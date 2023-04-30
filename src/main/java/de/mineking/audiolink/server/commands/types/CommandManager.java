package de.mineking.audiolink.server.commands.types;

import de.mineking.audiolink.server.commands.*;
import de.mineking.audiolink.server.main.AudioLinkServer;
import de.mineking.audiolink.server.processing.AudioConnection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandManager {
	private final AudioLinkServer<?> main;
	private final Map<String, Command> commands = new ConcurrentHashMap<>();

	public CommandManager(AudioLinkServer<?> main) {
		this.main = main;

		commands.put("stream", new StartStreamCommand());
		commands.put("bufferCheck", new BufferCheckCommand());
		commands.put("current", new CurrentTrackCommand());

		commands.put("play", new PlayTrackCommand());
		commands.put("stop", new StopTrackCommand());
		commands.put("pause", new PauseCommand());
		commands.put("volume", new VolumeCommand());
		commands.put("seek", new SeekCommand());

		commands.putAll(main.customAudioCommands());
	}

	public void handleCommand(AudioConnection connection, String input) {
		var data = AudioLinkServer.gson.fromJson(input, CommandData.class);

		try {
			commands.get(data.command).performCommand(new Context(main, connection, data.args));
		} catch(Exception e) {
			AudioLinkServer.log.error("Error performing command '" + data.command + "'", e);
		}
	}
}
