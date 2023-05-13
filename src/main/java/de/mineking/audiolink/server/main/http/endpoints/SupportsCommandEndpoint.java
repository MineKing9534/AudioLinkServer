package de.mineking.audiolink.server.main.http.endpoints;

import de.mineking.audiolink.server.main.AudioLinkServer;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class SupportsCommandEndpoint implements Handler {
	private final AudioLinkServer<?> main;

	public SupportsCommandEndpoint(AudioLinkServer<?> main) {
		this.main = main;
	}

	public record Response(boolean supports) {}

	@Override
	public void handle(@NotNull Context ctx) {
		ctx.json(new Response(main.commandManager.hasCommand(ctx.queryParam("command"))));
	}
}
