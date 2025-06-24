package ru.kvaytg.mcbotsconnector;

public class MinecraftServer {

    private final String host;
    private final int port;

    public MinecraftServer(String host, int port) {
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