package de.mineking.audiolink.server.main.http;

import de.mineking.audiolink.server.main.AudioLinkServer;
import de.mineking.audiolink.server.main.http.endpoints.ConnectionEndpoint;
import de.mineking.audiolink.server.main.http.endpoints.TrackSearchEndpoint;
import io.javalin.Javalin;
import io.javalin.http.ForbiddenResponse;
import io.javalin.json.JsonMapper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Objects;

public class HttpServer {
	private final AudioLinkServer<?> main;
	private final Javalin server;

	public HttpServer(AudioLinkServer<?> main) {
		this.main = main;

		server = Javalin.create(config ->
				config.jsonMapper(new JsonMapper() {
					@NotNull
					@Override
					public <T> T fromJsonString(@NotNull String json, @NotNull Type targetType) {
						return AudioLinkServer.gson.fromJson(json, targetType);
					}

					@NotNull
					@Override
					public String toJsonString(@NotNull Object obj, @NotNull Type type) {
						return AudioLinkServer.gson.toJson(obj, type);
					}
				})
		);

		server.before(ctx -> {
			if(!Objects.equals(ctx.header("Authorization"), main.config.password)) {
				throw new ForbiddenResponse();
			}
		});

		server.get("track", new TrackSearchEndpoint(main));
		server.get("connection", new ConnectionEndpoint(main));

		server.ws("gateway", main.audioServer);

		main.setupHttpServer(server);
	}

	public void start() {
		server.start(main.config.port);
	}
}
