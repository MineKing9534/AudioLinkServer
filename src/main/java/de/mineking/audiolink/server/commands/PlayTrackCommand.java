package de.mineking.audiolink.server.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.TrackMarker;
import de.mineking.audiolink.server.commands.types.Command;
import de.mineking.audiolink.server.commands.types.Context;
import de.mineking.audiolink.server.processing.data.CurrentTrackData;
import de.mineking.audiolink.server.processing.data.EventType;

public class PlayTrackCommand extends Command {
	@Override
	public void performCommand(Context context) {
		context.main.manager.manager.loadItem((String) context.getOption("url"), new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack track) {
				if(context.getOption("marker") != null) {
					track.setMarker(new TrackMarker((long) (double) context.getOption("marker"), state ->
							context.connection.sendEvent(EventType.MARKER, out -> {
								out.writeByte(state.ordinal());
								out.write(new CurrentTrackData(track).getData());
							})
					));
				}

				if(context.getOption("position") != null) {
					track.setPosition((long) (double) context.getOption("position"));
				}

				context.connection.getPlayer().playTrack(track);
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {}

			@Override
			public void noMatches() {}

			@Override
			public void loadFailed(FriendlyException exception) {}
		});
	}
}
