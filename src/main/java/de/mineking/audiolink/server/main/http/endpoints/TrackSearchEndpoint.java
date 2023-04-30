package de.mineking.audiolink.server.main.http.endpoints;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.mineking.audiolink.server.main.AudioLinkServer;
import de.mineking.audiolink.server.processing.data.TrackData;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TrackSearchEndpoint implements Handler {
	private final AudioLinkServer<?> main;

	public TrackSearchEndpoint(AudioLinkServer<?> main) {
		this.main = main;
	}

	@Override
	public void handle(@NotNull Context context) throws Exception {
		main.manager.manager.loadItem(context.queryParam("query"), new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack track) {
				context.json(new TrackResponse(track));
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				context.json(new PlaylistResponse(playlist));
			}

			@Override
			public void noMatches() {
				context.json(new NoMatchesResponse());
			}

			@Override
			public void loadFailed(FriendlyException exception) {
				context.json(new FailedResponse(exception.getMessage()));
			}
		}).get();
	}

	public enum SearchResultType {
		TRACK,
		PLAYLIST,
		NONE,
		FAILED
	}

	private static class Response {
		private final SearchResultType type;

		public Response(SearchResultType type) {
			this.type = type;
		}
	}

	private static class TrackResponse extends TrackData {
		private final SearchResultType type = SearchResultType.TRACK;

		public TrackResponse(AudioTrack track) {
			super(track.getInfo());
		}
	}

	private static class PlaylistResponse extends Response {
		private final List<TrackData> tracks;
		private final String name;
		private final boolean isSearchResult;

		public PlaylistResponse(AudioPlaylist playlist) {
			super(SearchResultType.PLAYLIST);

			this.tracks = playlist.getTracks().stream()
					.map(track -> new TrackData(track.getInfo()))
					.toList();
			this.name = playlist.getName();
			this.isSearchResult = playlist.isSearchResult();
		}
	}

	private static class NoMatchesResponse extends Response {
		public NoMatchesResponse() {
			super(SearchResultType.NONE);
		}
	}

	private static class FailedResponse extends Response {
		private final String message;

		public FailedResponse(String message) {
			super(SearchResultType.FAILED);

			this.message = message;
		}
	}
}
