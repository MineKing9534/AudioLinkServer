package de.mineking.audiolink.server.processing;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.mineking.audiolink.server.main.AudioLinkServer;
import de.mineking.audiolink.server.processing.data.EventType;
import de.mineking.audiolink.server.processing.data.MessageType;
import io.javalin.websocket.WsContext;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class AudioConnection {
	private final AudioLinkServer<?> main;
	private final WsContext context;
	private final AudioServer.ClientConfiguration config;

	private boolean shutdown = false;
	private Future<?> stream;

	private final AudioPlayer player1;
	private final AudioPlayer player2;

	private long bufferDifference = 0;

	public AudioConnection(AudioLinkServer<?> main, WsContext context, AudioServer.ClientConfiguration config) {
		this.main = main;
		this.context = context;
		this.config = config;

		player1 = main.manager.manager.createPlayer();
		player2 = main.manager.manager.createPlayer();

		player1.setVolume(10);
		player2.setVolume(10);

		player1.addListener(new EventListener(this, (byte) 0));
		player2.addListener(new EventListener(this, (byte) 1));
	}

	public void updateBufferDifference(long difference) {
		this.bufferDifference = difference;
	}

	public void startStream() {
		if(stream != null) {
			return;
		}

		stream = main.audioServer.scheduler.scheduleAtFixedRate(() -> {
			if(bufferDifference > 0) {
				bufferDifference--;
				return;
			}

			sendAudioData();

			for(; bufferDifference < 0; bufferDifference++) {
				sendAudioData();
			}
		}, 0, 20, TimeUnit.MILLISECONDS);
	}

	private void sendAudioData() {
		var frame1 = player1.provide();
		var frame2 = player2.provide();

		var data1 = frame1 != null ? frame1.getData() : new byte[0];
		var data2 = frame2 != null ? frame2.getData() : new byte[0];

		var pcm1 = new short[data1.length / 2];
		var pcm2 = new short[data2.length / 2];

		ByteBuffer.wrap(data1).order(ByteOrder.BIG_ENDIAN).asShortBuffer().get(pcm1);
		ByteBuffer.wrap(data2).order(ByteOrder.BIG_ENDIAN).asShortBuffer().get(pcm2);

		var mixed = mixPcmData(pcm1, pcm2);
		var buffer = ByteBuffer.allocate(mixed.length * 2);

		for(var s : mixed) {
			buffer.putShort(s);
		}

		sendData(MessageType.AUDIO, out -> out.write(buffer.array()));
	}

	private static short[] mixPcmData(short[] pcm1, short[] pcm2) {
		int length = Math.min(pcm1.length, pcm2.length);
		short[] mixedPcm = new short[length];

		for(int i = 0; i < length; i++) {
			mixedPcm[i] = (short) ((pcm1[i] + pcm2[i]) / 2);
		}

		if(pcm1.length > length) {
			mixedPcm = Arrays.copyOf(mixedPcm, pcm1.length);
			System.arraycopy(pcm1, length, mixedPcm, length, pcm1.length - length);
		}

		else if(pcm2.length > length) {
			mixedPcm = Arrays.copyOf(mixedPcm, pcm2.length);
			System.arraycopy(pcm2, length, mixedPcm, length, pcm2.length - length);
		}

		return mixedPcm;
	}

	public synchronized void sendData(MessageType type, DataCreator creator) {
		try {
			var baos = new ByteArrayOutputStream();
			var out = new DataOutputStream(baos);

			out.writeByte(type.id);
			creator.createData(out);

			context.send(ByteBuffer.wrap(baos.toByteArray()));
		} catch(IOException e) {
			disconnect();
		}
	}

	public synchronized void sendEvent(EventType type, byte id, DataCreator creator) {
		sendData(MessageType.EVENT, out -> {
			out.writeByte(type.id);
			out.writeByte(id);
			creator.createData(out);
		});
	}

	public AudioPlayer getPlayer1() {
		return player1;
	}

	public AudioPlayer getPlayer2() {
		return player2;
	}

	public void disconnect() {
		if(shutdown) {
			return;
		}

		shutdown = true;

		context.closeSession();

		AudioLinkServer.log.info("Client disconnect; Client Info: " + config.clientInfo());
		player1.destroy();

		if(stream != null) {
			stream.cancel(true);
		}
	}
}
