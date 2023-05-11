package de.mineking.audiolink.server.commands.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.mineking.audiolink.server.main.AudioLinkServer;
import de.mineking.audiolink.server.processing.AudioConnection;

import java.util.function.Function;

public class Context {
	public final AudioLinkServer<?> main;
	public final AudioConnection connection;
	public final JsonObject json;

	public Context(AudioLinkServer<?> main, AudioConnection connection, JsonObject json) {
		this.main = main;
		this.connection = connection;
		this.json = json;
	}

	/**
	 * @param name the name of the parameter
	 * @param handler a generator for the parameter
	 * @return the parameter value
	 * @param <T> the parameter type
	 */
	public <T> T getParameter(String name, Function<JsonElement, T> handler) {
		return getParameter(name, null, handler);
	}

	/**
	 * @param name the name of the parameter
	 * @param def the default value
	 * @param handler a generator for the parameter
	 * @return the parameter value
	 * @param <T> the parameter type
	 */
	public <T> T getParameter(String name, T def, Function<JsonElement, T> handler) {
		return json.get(name) != null
				? handler.apply(json.get(name))
				: def;
	}

	/**
	 * @return The targeted player layer
	 */
	public byte getLayer() {
		return getParameter("layer", (byte) 0, JsonElement::getAsByte);
	}

	/**
	 * @return the targeted {@link AudioPlayer}
	 */
	public AudioPlayer getPlayer() {
		return getLayer() % 2 == 0
				? connection.getPlayer1()
				: connection.getPlayer2();
	}
}
