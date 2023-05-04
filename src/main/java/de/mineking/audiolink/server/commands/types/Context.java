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

	public <T> T getParameter(String name, Function<JsonElement, T> handler) {
		return getParameter(name, null, handler);
	}

	public <T> T getParameter(String name, T def, Function<JsonElement, T> handler) {
		return json.get(name) != null
				? handler.apply(json.get(name))
				: def;
	}

	public byte getLayer() {
		return getParameter("layer", (byte) 0, JsonElement::getAsByte);
	}

	public AudioPlayer getPlayer() {
		return getLayer() % 2 == 0
				? connection.getPlayer1()
				: connection.getPlayer2();
	}
}
