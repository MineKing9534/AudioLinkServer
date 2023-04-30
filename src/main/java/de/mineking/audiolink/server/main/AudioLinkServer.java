package de.mineking.audiolink.server.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import de.mineking.audiolink.server.commands.types.Command;
import de.mineking.audiolink.server.commands.types.CommandManager;
import de.mineking.audiolink.server.main.http.HttpServer;
import de.mineking.audiolink.server.processing.AudioManager;
import de.mineking.audiolink.server.processing.AudioServer;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

public class AudioLinkServer<T extends Config> {
	public final static Gson gson = new GsonBuilder()
			.create();
	public final static Logger log = LoggerFactory.getLogger("AudioLink Server");

	public final T config;

	public final AudioManager manager;
	public final CommandManager commandManager;

	public final AudioServer audioServer;
	public final HttpServer httpServer;

	public AudioLinkServer(String config, Class<T> type) throws Exception {
		this(Config.readFromFile(config, type));
	}

	public AudioLinkServer(T config) throws Exception {
		this.config = config;

		this.manager = new AudioManager(this);
		this.commandManager = new CommandManager(this);

		this.audioServer = new AudioServer(this);
		this.httpServer = new HttpServer(this);

		this.httpServer.start();
	}

	public void configureSourceManagers(AudioPlayerManager manager) {
	}

	public Map<String, Command> customAudioCommands() {
		return Collections.emptyMap();
	}

	public void setupHttpServer(Javalin server) {
	}
}
