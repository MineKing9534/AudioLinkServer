package de.mineking.audiolink.server.processing.data;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.io.DataOutputStream;
import java.io.IOException;

public class CurrentTrackData extends TrackData {
	public final long position;

	public CurrentTrackData(AudioTrack track) {
		super(track.getInfo());

		this.position = track.getPosition();
	}

	@Override
	public void write(DataOutputStream out) throws IOException {
		super.write(out);

		out.writeLong(position);
	}
}
