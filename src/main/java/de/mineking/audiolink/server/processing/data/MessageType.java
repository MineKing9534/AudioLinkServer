package de.mineking.audiolink.server.processing.data;

public enum MessageType {
	AUDIO(0),
	EVENT(1),
	TRACK_INFO(2);
	public final byte id;

	MessageType(int id) {
		this.id = (byte) id;
	}
}
