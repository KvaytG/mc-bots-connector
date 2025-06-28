package ru.kvaytg.mcbotsconnector;

public class MinecraftServer {

    private final String host;
    private final int port;

    public MinecraftServer(String host, int port) {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException(
                    "Invalid port number: " + port + ". Port must be between 1 and 65535."
            );
        }
        this.host = host;
        this.port = port;
    }

    public MinecraftServer(String host) {
        this(host, 25565);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

}