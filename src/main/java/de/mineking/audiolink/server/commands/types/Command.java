package de.mineking.audiolink.server.commands.types;

public abstract class Command {
	/**
	 * Execute the command
	 * @param context the {@link Context} of the command execution
	 * @throws Exception if something went wrong
	 */
	public abstract void performCommand(Context context) throws Exception;
}
