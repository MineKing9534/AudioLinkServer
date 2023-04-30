package de.mineking.audiolink.server.commands;

import de.mineking.audiolink.server.commands.types.Command;
import de.mineking.audiolink.server.commands.types.Context;

public class SeekCommand extends Command {
	@Override
	public void performCommand(Context context) throws Exception {
		if(context.connection.getPlayer().getPlayingTrack() != null) {
			context.connection.getPlayer().getPlayingTrack().setPosition((long) (double) context.getOption("position"));
		}
	}
}
