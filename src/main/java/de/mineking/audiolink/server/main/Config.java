package de.mineking.audiolink.server.main;

import java.io.FileReader;

public class Config {
	public static class AudioSourceConfig {
		public String id;
		public String secret;
	}

	public int port;

	public String password;

	public int cleanupThreshold;
	public int bufferDuration;

	public static <T extends Config> T readFromFile(String path, Class<T> type) throws Exception {
		try(var r = new FileReader(path)) {
			return AudioLinkServer.gson.fromJson(r, type);
		} catch(Exception e) {
			throw new Exception("Failed to read config file from '" + path + "' - Please check for correct syntax", e);
		}
	}
}
