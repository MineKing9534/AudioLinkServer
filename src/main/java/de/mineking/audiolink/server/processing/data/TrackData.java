package de.mineking.audiolink.server.processing.data;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackData {
	public final String url;
	public final String title;
	public final String author;
	public final String artworkUrl;

	public final long length;
	public final boolean isStream;

	public final String identifier;
	public final String isrc;

	public TrackData(AudioTrackInfo track) {
		this.url = track.uri;
		this.title = track.title;
		this.author = track.author;
		this.artworkUrl = track.artworkUrl;
		this.length = track.length;
		this.isStream = track.isStream;
		this.identifier = track.identifier;
		this.isrc = track.isrc;
	}

	/**
	 * Write the data of this track to a {@link DataOutputStream}.
	 * @param out the target {@link DataOutputStream}
	 * @throws IOException if something went wrong
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeUTF(url);
		out.writeUTF(title);
		out.writeUTF(author);
		out.writeUTF(artworkUrl != null ? artworkUrl : "");
		out.writeLong(length);
		out.writeBoolean(isStream);
		out.writeUTF(identifier);
		out.writeUTF(isrc != null ? isrc : "");
	}

	/**
	 * @return a byte array of the track data
	 * @throws IOException if something went wrong
	 */
	public byte[] getData() throws IOException {
		var baos = new ByteArrayOutputStream();
		var temp = new DataOutputStream(baos);

		write(temp);

		return baos.toByteArray();
	}
}
