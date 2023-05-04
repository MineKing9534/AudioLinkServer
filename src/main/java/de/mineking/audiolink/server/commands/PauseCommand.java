package de.mineking.audiolink.server.commands;

import com.google.gson.JsonElement;
import de.mineking.audiolink.server.commands.types.Command;
import de.mineking.audiolink.server.commands.types.Context;

public class PauseCommand extends Command {
	@Override
	public void performCommand(Context context) {
		context.getPlayer().setPaused(context.getParameter("state", JsonElement::getAsBoolean));
	}
}
