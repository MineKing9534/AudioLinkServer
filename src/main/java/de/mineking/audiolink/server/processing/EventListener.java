package de.mineking.audiolink.server.processing;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import de.mineking.audiolink.server.processing.data.EventType;
import de.mineking.audiolink.server.processing.data.TrackData;

public class EventListener extends AudioEventAdapter {
	private final AudioConnection connection;
	private final byte id;

	public EventListener(AudioConnection connection, byte id) {
		this.connection = connection;
		this.id = id;
	}

	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		connection.sendEvent(EventType.START, id, out -> out.write(new TrackData(track.getInfo()).getData()));
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		connection.sendEvent(EventType.END, id, out -> out.writeByte(endReason.ordinal()));
	}

	@Override
	public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
		connection.sendEvent(EventType.STUCK, id, out -> {});
	}

	@Override
	public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
		connection.sendEvent(EventType.EXCEPTION, id, out -> out.writeUTF(exception.getMessage()));
	}
}
