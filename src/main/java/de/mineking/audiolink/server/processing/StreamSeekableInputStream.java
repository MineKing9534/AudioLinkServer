package de.mineking.audiolink.server.processing;

import com.sedmelluq.discord.lavaplayer.tools.io.SeekableInputStream;
import com.sedmelluq.discord.lavaplayer.track.info.AudioTrackInfoProvider;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class StreamSeekableInputStream extends SeekableInputStream {
	private final InputStream stream;
	private long position = 0;

	public StreamSeekableInputStream(InputStream stream) throws IOException {
		super(stream.available(), 0);
		this.stream = stream;
	}

	@Override
	public int read() throws IOException {
		var result = stream.read();

		if(result >= 0) {
			position++;
		}

		return result;
	}

	@Override
	public int read(@NotNull byte[] b, int off, int len) throws IOException {
		var read = stream.read(b, off, len);

		position += read;

		return read;
	}

	@Override
	public long skip(long n) throws IOException {
		var skipped = stream.skip(n);

		position += skipped;

		return skipped;
	}

	@Override
	public int available() throws IOException {
		return stream.available();
	}

	@Override
	public synchronized void reset() {}

	@Override
	public void close() throws IOException {
		stream.close();
	}

	@Override
	public long getPosition() {
		return position;
	}

	@Override
	public List<AudioTrackInfoProvider> getTrackInfoProviders() {
		return Collections.emptyList();
	}

	@Override
	public boolean canSeekHard() {
		return false;
	}

	@Override
	protected void seekHard(long position) {}
}
