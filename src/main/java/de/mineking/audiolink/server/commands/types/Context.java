package de.mineking.audiolink.server.commands.types;

import de.mineking.audiolink.server.main.AudioLinkServer;
import de.mineking.audiolink.server.processing.AudioConnection;

import java.util.Map;

public class Context {
	public final AudioLinkServer<?> main;
	public final AudioConnection connection;
	public final Map<String, Object> args;

	public Context(AudioLinkServer<?> main, AudioConnection connection, Map<String, Object> args) {
		this.main = main;
		this.connection = connection;
		this.args = args;
	}

	@SuppressWarnings("unchecked")
	public <T> T getOption(String name) {
		return (T) args.get(name);
	}
}
