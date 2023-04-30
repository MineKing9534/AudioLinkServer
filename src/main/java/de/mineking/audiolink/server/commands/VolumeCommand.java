package de.mineking.audiolink.server.commands;

import de.mineking.audiolink.server.commands.types.Command;
import de.mineking.audiolink.server.commands.types.Context;

public class VolumeCommand extends Command {
	@Override
	public void performCommand(Context context) {
		context.connection.getPlayer().setVolume((int) (double) context.getOption("volume"));
	}
}
