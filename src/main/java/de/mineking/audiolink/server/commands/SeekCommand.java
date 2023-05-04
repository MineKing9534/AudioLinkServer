package de.mineking.audiolink.server.commands;

import com.google.gson.JsonElement;
import de.mineking.audiolink.server.commands.types.Command;
import de.mineking.audiolink.server.commands.types.Context;

public class SeekCommand extends Command {
	@Override
	public void performCommand(Context context) throws Exception {
		var player = context.getPlayer();

		if(player.getPlayingTrack() != null) {
			player.getPlayingTrack().setPosition(context.getParameter("position", JsonElement::getAsLong));
		}
	}
}
