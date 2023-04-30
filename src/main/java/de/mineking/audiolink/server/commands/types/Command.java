package de.mineking.audiolink.server.commands.types;

public abstract class Command {
	public abstract void performCommand(Context context) throws Exception;
}
