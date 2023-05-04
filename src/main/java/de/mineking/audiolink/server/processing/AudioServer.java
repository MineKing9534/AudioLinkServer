package de.mineking.audiolink.server.processing;

import de.mineking.audiolink.server.main.AudioLinkServer;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class AudioServer implements Consumer<WsConfig> {
	public final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(0);

	private final AudioLinkServer<?> main;
	public final Map<WsContext, AudioConnection> connections = new HashMap<>();

	public AudioServer(AudioLinkServer<?> main) {
		this.main = main;
	}

	public record ClientConfiguration(String password, long buffer) {
	}

	@Override
	public void accept(WsConfig config) {
		config.onConnect(context -> context.enableAutomaticPings(5, TimeUnit.SECONDS));

		config.onMessage(context -> {
			if(connections.containsKey(context)) {
				main.commandManager.handleCommand(connections.get(context), context.message());
				return;
			}

			try {
				var client = AudioLinkServer.gson.fromJson(context.message(), ClientConfiguration.class);

				if(!client.password.equals(main.config.password)) {
					context.closeSession();
					AudioLinkServer.log.info("Disconnect: Wrong password");
					return;
				}

				AudioLinkServer.log.info("New Connection");

				connections.put(context, new AudioConnection(main, context, client.buffer));
			} catch(Exception e) {
				AudioLinkServer.log.error("Error handling new connection", e);
				context.closeSession();
			}
		});

		config.onClose(context -> {
			if(connections.containsKey(context)) {
				connections.get(context).disconnect();
				connections.remove(context);
			}
		});
	}
}
