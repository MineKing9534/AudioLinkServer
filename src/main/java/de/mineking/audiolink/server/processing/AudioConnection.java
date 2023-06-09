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

	/**
	 * Start the audio data stream
	 */
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

	/**
	 * Gets the audio data of both layers and combine them. The data are sent to the client afterward.
	 */
	private void sendAudioData() {
		var frame2 = player2.provide();

		if(frame2 != null) {
			sendAudioData(frame2.getData());
		}

		else {
			var frame1 = player1.provide();

			if(frame1 != null) {
				sendAudioData(frame1.getData());
			}
		}
	}

	private void sendAudioData(byte[] data) {
		sendData(MessageType.AUDIO, out -> out.write(data));
	}

	/**
	 * Send any type of data to the client
	 * @param type the {@link MessageType} of this message
	 * @param creator a {@link DataCreator} to write the actual data of this message
	 */
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

	/**
	 * Send an event to the client
	 * @param type the {@link EventType}
	 * @param id the id of the player, either 0 or 1
	 * @param creator a {@link DataCreator} to write additional event parameters
	 */
	public synchronized void sendEvent(EventType type, byte id, DataCreator creator) {
		sendData(MessageType.EVENT, out -> {
			out.writeByte(type.id);
			out.writeByte(id);
			creator.createData(out);
		});
	}

	/**
	 * @return The first player layer. This is the primary {@link AudioPlayer} used for playing most of the tracks
	 */
	public AudioPlayer getPlayer1() {
		return player1;
	}

	/**
	 * @return The second player layer. This is a second {@link AudioPlayer} whose data will be overlayed on top of the primary layer.
	 */
	public AudioPlayer getPlayer2() {
		return player2;
	}

	/**
	 * Disconnect the client
	 */
	public void disconnect() {
		if(shutdown) {
			return;
		}

		shutdown = true;

		context.closeSession();

		AudioLinkServer.log.info("Client disconnect; Client Info: " + config.clientInfo());

		player1.destroy();
		player2.destroy();

		if(stream != null) {
			stream.cancel(true);
		}
	}
}
