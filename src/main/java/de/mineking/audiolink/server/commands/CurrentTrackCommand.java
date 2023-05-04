package de.mineking.audiolink.server.commands;

import de.mineking.audiolink.server.commands.types.Command;
import de.mineking.audiolink.server.commands.types.Context;
import de.mineking.audiolink.server.processing.data.CurrentTrackData;
import de.mineking.audiolink.server.processing.data.MessageType;

public class CurrentTrackCommand extends Command {
	@Override
	public void performCommand(Context context) {
		context.connection.sendData(MessageType.TRACK_INFO, out -> {
			var player = context.getPlayer();

			if(player.getPlayingTrack() != null) {
				out.write(new CurrentTrackData(player.getPlayingTrack()).getData());
			}
		});
	}
}
