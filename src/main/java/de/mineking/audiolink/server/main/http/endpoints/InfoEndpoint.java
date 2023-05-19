package de.mineking.audiolink.server.main.http.endpoints;

import de.mineking.audiolink.server.main.AudioLinkServer;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class InfoEndpoint implements Handler {
	private final static String html;

	static {
		try(var input = AudioLinkServer.class.getResourceAsStream("/info.html")) {
			html = IOUtils.toString(input, StandardCharsets.UTF_8);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void handle(@NotNull Context ctx) {
		ctx.html(html);
	}
}
