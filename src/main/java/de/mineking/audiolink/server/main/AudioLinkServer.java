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

	/**
	 * Initializes this AudioLinkServer by reading the config from a json file
	 * @param config the path to the config
	 * @param type the {@link Class} of the config, required from reading the file
	 * @throws Exception if something goes wrong
	 */
	public AudioLinkServer(String config, Class<T> type) throws Exception {
		this(Config.readFromFile(config, type));
	}

	/**
	 * Initialize this AudioLinkServer from an existing config object
	 * @param config the {@link Config} object
	 * @throws Exception if something goes wrong
	 */
	public AudioLinkServer(T config) throws Exception {
		this.config = config;

		this.manager = new AudioManager(this);
		this.commandManager = new CommandManager(this);

		this.audioServer = new AudioServer(this);
		this.httpServer = new HttpServer(this);

		this.httpServer.start();
	}

	/**
	 * Configure the sources managers of lavaplayer
	 * @param manager The {@link AudioPlayerManager} instance used internally
	 */
	public void configureSourceManagers(AudioPlayerManager manager) {
	}

	/**
	 * @return A {@link Map} of custom commands that the client can use
	 */
	public Map<String, Command> customAudioCommands() {
		return Collections.emptyMap();
	}

	/**
	 * Do some advanced {@link Javalin} configuration (used as http and websocket server)
	 * @param server the {@link Javalin} instance
	 */
	public void setupHttpServer(Javalin server) {
	}
}
