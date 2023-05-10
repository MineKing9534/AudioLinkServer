package de.mineking.audiolink.server.processing.data;

public enum EventType {
	/**
	 * A track started playing
	 */
	START(0),
	/**
	 * A track ended
	 */
	END(1),
	/**
	 * A track was stuck
	 */
	STUCK(2),
	/**
	 * An exception occurred during playback
	 */
	EXCEPTION(3),
	/**
	 * A track marker was triggered
	 */
	MARKER(4);

	public final byte id;

	EventType(int id) {
		this.id = (byte) id;
	}
}