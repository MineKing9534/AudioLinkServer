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
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class AudioConnection {
	private final AudioLinkServer<?> main;

	private boolean shutdown = false;
	private final WsContext context;
	private Future<?> stream;

	private final AudioPlayer player;

	private long bufferDifference = 0;

	public AudioConnection(AudioLinkServer<?> main, WsContext context, long buffer) {
		this.main = main;
		this.context = context;

		player = main.manager.manager.createPlayer();

		player.addListener(new EventListener(this));

		for(long i = 0; i < buffer; i++) {
			sendAudioData();
		}
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
		var frame = player.provide();

		if(frame != null) {
			sendData(MessageType.AUDIO, out -> out.write(frame.getData()));
		}

		else {
			sendData(MessageType.AUDIO, out -> {});
		}
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

	public synchronized void sendEvent(EventType type, DataCreator creator) {
		sendData(MessageType.EVENT, out -> {
			out.writeByte(type.id);
			creator.createData(out);
		});
	}

	public AudioPlayer getPlayer() {
		return player;
	}

	public void disconnect() {
		if(shutdown) {
			return;
		}

		shutdown = true;


		AudioLinkServer.log.info("Client disconnect");
		player.destroy();

		if(stream != null) {
			stream.cancel(true);
		}
	}
}
