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

	public EventListener(AudioConnection connection) {
		this.connection = connection;
	}

	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		connection.sendEvent(EventType.START, out -> out.write(new TrackData(track.getInfo()).getData()));
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		connection.sendEvent(EventType.END, out -> out.writeByte(endReason.ordinal()));
	}

	@Override
	public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
		connection.sendEvent(EventType.STUCK, out -> {});
	}

	@Override
	public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
		connection.sendEvent(EventType.EXCEPTION, out -> out.writeUTF(exception.getMessage()));
	}
}
