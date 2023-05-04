package de.mineking.audiolink.server.processing;

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import de.mineking.audiolink.server.main.AudioLinkServer;

public class AudioManager {
	public final AudioPlayerManager manager;

	public AudioManager(AudioLinkServer<?> main) {
		manager = new DefaultAudioPlayerManager();

		manager.getConfiguration().setFilterHotSwapEnabled(true);
		manager.getConfiguration().setOutputFormat(StandardAudioDataFormats.DISCORD_PCM_S16_BE);

		manager.setPlayerCleanupThreshold(main.config.cleanupThreshold);
		manager.setFrameBufferDuration(main.config.bufferDuration);

		main.configureSourceManagers(manager);
	}
}
