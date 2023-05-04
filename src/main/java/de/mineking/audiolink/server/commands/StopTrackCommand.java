package de.mineking.audiolink.server.commands;

import de.mineking.audiolink.server.commands.types.Command;
import de.mineking.audiolink.server.commands.types.Context;

public class StopTrackCommand extends Command {
	@Override
	public void performCommand(Context context) {
		context.getPlayer().stopTrack();
	}
}
