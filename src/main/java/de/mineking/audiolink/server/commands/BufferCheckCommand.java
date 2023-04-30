package de.mineking.audiolink.server.commands;

import de.mineking.audiolink.server.commands.types.Command;
import de.mineking.audiolink.server.commands.types.Context;

public class BufferCheckCommand extends Command {
	@Override
	public void performCommand(Context context) throws Exception {
		context.connection.updateBufferDifference((long) (double) context.getOption("difference"));
	}
}
