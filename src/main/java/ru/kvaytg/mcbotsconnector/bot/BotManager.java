package ru.kvaytg.mcbotsconnector.bot;

import com.github.steveice10.packetlib.ProxyInfo;
import ru.kvaytg.mcbotsconnector.MinecraftServer;
import ru.kvaytg.mcbotsconnector.proxy.ProxyManager;
import ru.kvaytg.mcbotsconnector.util.PauseUtils;
import ru.kvaytg.mcbotsconnector.util.StringUtils;
import java.net.Proxy;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public enum BotManager {

    INSTANCE;

    private String host = null;
    private int port = 0;
    private int botsPerProxy;
    private ProxyManager proxyManager;

    private final Set<BotClient> botClients = ConcurrentHashMap.newKeySet();

    public void init(MinecraftServer server, int botsPerProxy) {
        this.host = server.getHost();
        this.port = server.getPort();
        this.botsPerProxy = Math.max(1, botsPerProxy);
        proxyManager = new ProxyManager(host, port);
    }

    public void setMessage(String message) {
        if (StringUtils.isNullOrBlank(message) || botClients.isEmpty()) return;
        synchronized (botClients) {
            for (BotClient botClient : botClients) {
                botClient.setMessage(message);
            }
        }
    }

    public void connect() {
        if (host == null || port == 0) {
            throw new IllegalStateException("Server IP address and port must be set before connecting.");
        }
        for (int i = 0; i < proxyManager.getProxyListSize(); i++) {
            Proxy proxy = proxyManager.takeRandomProxy();
            ProxyInfo proxyInfo = new ProxyInfo(ProxyInfo.Type.SOCKS5, proxy.address());
            for (int j = 0; j < botsPerProxy; j++) {
                BotClient botClient = new BotClient();
                botClients.add(botClient);
                botClient.connect(host, port, proxy, proxyInfo);
                PauseUtils.wait(5000);
            }
        }
    }

    @SuppressWarnings("unused")
    public void disconnect() {
        synchronized (botClients) {
            for (BotClient botClient : botClients) {
                botClient.disconnect();
            }
            botClients.clear();
        }
    }

}