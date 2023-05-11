package de.mineking.audiolink.server.processing.data;

public enum MessageType {
	/**
	 * A chunk of PCM data
	 */
	AUDIO(0),
	/**
	 * An {@link EventType} occurred
	 */
	EVENT(1),
	/**
	 * The response of a track info request
	 */
	TRACK_INFO(2);
	public final byte id;

	MessageType(int id) {
		this.id = (byte) id;
	}
}
