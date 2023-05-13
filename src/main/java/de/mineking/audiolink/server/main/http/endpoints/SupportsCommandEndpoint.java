package de.mineking.audiolink.server.main.http.endpoints;

import de.mineking.audiolink.server.main.AudioLinkServer;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class SupportsCommandEndpoint implements Handler {
	private final AudioLinkServer<?> main;

	public SupportsCommandEndpoint(AudioLinkServer<?> main) {
		this.main = main;
	}

	@Override
	public void handle(@NotNull Context ctx) throws Exception {
		var array = new ByteArrayOutputStream();
		var stream = new DataOutputStream(array);

		stream.writeBoolean(main.commandManager.hasCommand(ctx.queryParam("command")));

		ctx.result(new ByteArrayInputStream(array.toByteArray()));
	}
}
