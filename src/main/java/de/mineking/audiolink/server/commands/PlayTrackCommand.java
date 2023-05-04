package de.mineking.audiolink.server.commands;

import com.google.gson.JsonElement;
import com.sedmelluq.discord.lavaplayer.container.MediaContainer;
import com.sedmelluq.discord.lavaplayer.container.MediaContainerDescriptor;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.TrackMarker;
import de.mineking.audiolink.server.commands.types.Command;
import de.mineking.audiolink.server.commands.types.Context;
import de.mineking.audiolink.server.processing.StreamSeekableInputStream;
import de.mineking.audiolink.server.processing.data.CurrentTrackData;
import de.mineking.audiolink.server.processing.data.EventType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

public class PlayTrackCommand extends Command {
	private final MediaContainerDescriptor descriptor = new MediaContainerDescriptor(MediaContainer.MP3.probe, null);

	@Override
	public void performCommand(Context context) throws IOException {
		var data = context.getParameter("data", JsonElement::getAsString);

		if(data != null) {
			loadFromData(data, context);
		}

		else {
			loadFromRemote(context);
		}
	}

	private void loadTrack(AudioTrack track, Context context) {
		var marker = context.getParameter("marker", JsonElement::getAsLong);

		if(marker != null && marker > 0) {
			track.setMarker(new TrackMarker(marker, state ->
					context.connection.sendEvent(EventType.MARKER, context.getLayer(), out -> {
						out.writeByte(state.ordinal());
						out.write(new CurrentTrackData(track).getData());
					})
			));
		}

		if(context.getParameter("position", JsonElement::getAsLong) != null) {
			track.setPosition(context.getParameter("position", JsonElement::getAsLong));
		}

		context.getPlayer().playTrack(track);
	}

	private void loadFromRemote(Context context) {
		context.main.manager.manager.loadItem(context.getParameter("url", JsonElement::getAsString), new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack track) {
				loadTrack(track, context);
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {}

			@Override
			public void noMatches() {}

			@Override
			public void loadFailed(FriendlyException exception) {}
		});
	}

	private void loadFromData(String base64, Context context) throws IOException {
		var data = Base64.getDecoder().decode(base64);
		var stream = new StreamSeekableInputStream(new ByteArrayInputStream(data));

		loadTrack(
				descriptor.createTrack(
						new AudioTrackInfo("", "", stream.available(), "", false, ""),
						stream
				),
				context
		);
	}
}
