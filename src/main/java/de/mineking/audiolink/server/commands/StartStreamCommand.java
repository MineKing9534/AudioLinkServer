package de.mineking.audiolink.server.commands;

import de.mineking.audiolink.server.commands.types.Command;
import de.mineking.audiolink.server.commands.types.Context;

public class StartStreamCommand extends Command {
	@Override
	public void performCommand(Context context) {
		context.connection.startStream();
	}
}
