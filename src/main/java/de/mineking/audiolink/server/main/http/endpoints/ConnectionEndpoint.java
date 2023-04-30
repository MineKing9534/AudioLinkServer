package de.mineking.audiolink.server.main.http.endpoints;

import de.mineking.audiolink.server.main.AudioLinkServer;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class ConnectionEndpoint implements Handler {
	private final AudioLinkServer<?> main;

	public ConnectionEndpoint(AudioLinkServer<?> main) {
		this.main = main;
	}

	@Override
	public void handle(@NotNull Context context) {
		context.result(String.valueOf(main.audioServer.connections.size()));
	}
}
