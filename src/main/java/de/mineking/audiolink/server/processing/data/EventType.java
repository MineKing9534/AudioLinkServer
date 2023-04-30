package de.mineking.audiolink.server.processing.data;

public enum EventType {
	START(0),
	END(1),
	STUCK(2),
	EXCEPTION(3),
	MARKER(4);

	public final byte id;

	EventType(int id) {
		this.id = (byte) id;
	}
}