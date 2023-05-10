# AudioLink Server
The server library for the AudioLink system. It uses [Walkyst's lavaplayer-fork](https://github.com/Walkyst/lavaplayer-fork) under the hood the get the audio data.

# Clients
Currently, there only exists a [java client](https://github.com/MineKing9534/AudioLinkClient) for this. 

# Download
```xml
<repository>
	<id>jitpack.io</id>
	<url>https://jitpack.io</url>
</repository>

<dependency>
	<groupId>com.github.MineKing9534</groupId>
	<artifactId>AudioLinkServer</artifactId>
	<version>VERSION</version>
</dependency>
```

# Usage
The server library contains the basic foundation of a AudioLinkServer. It does not have any default implementation to register any lavaplayer sources. To run a AudioLinkServer you have to create your own implementation that does this configuration.

## Basics
To create a basic setup you need to create a class that extends `de.mineking.audiolink.server.main.AudioLinkServer`.
```java
public class MyAudioLinkServer extends AudioLinkServer<Config> {
	//...
}
```

## Config
AudioLinkServer uses a json file for configuration. To make it easier for you to add custom config properties, it was made so that you can inherit from the `de.mineking.audiolink.server.main.Config` class and just add java fields with your required properties. They will be read from the config with the same name they have in your `Config` class using [Gson](https://github.com/google/gson).
```java
public class MyConfig extends Config {
	public String myTestProperty;
}

public class MyAudioLinkServer extends AudioLinkServer<MyConfig> {
	public MyAudioLinkServer() {
		super("config", MyConfig.class); //Read config from file "config"
		
		System.out.println(config.myTestProperty);
	}
}
```
config:
```json
{
  "port": 1234,
  "password": "my-very-secure-password",

  "cleanupThreshold": 10000,
  "bufferDuration": 1000,
  
  "myTestProperty": "Super cool test property value"
}
```

As you can see, there are also some config entries that are needed by the library itself:

| name             | description                                                                                              |
|------------------|----------------------------------------------------------------------------------------------------------|
| port             | The port that the server will listen on                                                                  |
| password	        | The password that client have to use if they wan't to connect to this server                             |
| cleanupThreshold | The threshold for clearing an audio player when it has not been queried for the specified amount of time |
| bufferDuration   | length of the internal buffer for audio in milliseconds                                                  |

## Registering Sources
Registering sources is done by overriding the setup methods of AudioLinkServer. Here is an example:

```java
public class MyConfig extends Config {
	public Config.AudioSourceConfig youtube;
}

public class MyAudioLinkServer extends AudioLinkServer<MyConfig> {
	public MyAudioLinkServer() {
		super("config", MyConfig.class);
	}

	@Override
	public void configureSourceManagers(AudioPlayerManager manager) {
		manager.registerSourceManager(
			new YoutubeAudioSourceManager(
				true,
				config.youtube.id,
				config.youtube.secret
			)
		);
	}
}
```
config:
```json
{
  "port": 1234,
  "password": "my-very-secure-password",

  "cleanupThreshold": 10000,
  "bufferDuration": 1000,
  
  "youtube": {
	"id": "your youtube account email",
	"secret": "your youtube account password"
  }
}
```

## Custom commands
The AudioLinkServer library also allows you to create custom websocket commands that can be used by the client for more advanced things.

```java
public class MyAudioLinkServer extends AudioLinkServer<Config> {
	@Override
	public Map<String, Command> customAudioCommands() {
		return Map.of("my cool command", new MyCoolCommand());
	}
}

public class MyCoolCommand extends Command {
	@Override
	public void performCommand(Context context) {
		//Do something cool...
		System.out.println(context.json);
	}
}
```
The Context gives you access to the following things:

| name	      | description                                                                                       |
|------------|---------------------------------------------------------------------------------------------------|
| json	      | A JsonObject containing the parameters                                                            |
| connection | The AudioLinkConnection that this command was called on	                                          |
| main	      | A AudioLinkServer object that holds the main class (for example to give you access to the config) |