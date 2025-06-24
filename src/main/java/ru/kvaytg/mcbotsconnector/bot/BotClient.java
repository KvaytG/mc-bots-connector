package ru.kvaytg.mcbotsconnector.bot;

import com.github.steveice10.mc.auth.service.SessionService;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.packetlib.ProxyInfo;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import ru.kvaytg.mcbotsconnector.util.PauseUtils;
import ru.kvaytg.mcbotsconnector.util.RandomUtils;
import java.net.Proxy;

public class BotClient {

    private final String name;
    private final String password;

    private String message = null;

    private Session session;
    private String ip;
    private int port;
    private Proxy proxy;
    private ProxyInfo proxyInfo;

    public BotClient() {
        name = RandomUtils.generateRandomString(14);
        password = RandomUtils.generateRandomString(10);
    }

    public String getPassword() {
        return password;
    }

    public void setMessage(String message) {
        this.message = message;
        if (session instanceof TcpClientSession) {
            session.getListeners().forEach(listener -> {
                if (listener instanceof BotSession) {
                    ((BotSession) listener).setMessage(message);
                }
            });
        }
    }

    public void connect(String ip, int port, Proxy proxy, ProxyInfo proxyInfo) {
        this.ip = ip;
        this.port = port;
        this.proxy = proxy;
        this.proxyInfo = proxyInfo;
        MinecraftProtocol protocol = new MinecraftProtocol(name);
        SessionService sessionService = new SessionService();
        sessionService.setProxy(proxy);
        session = new TcpClientSession(ip, port, protocol, proxyInfo);
        session.setFlag(MinecraftConstants.SESSION_SERVICE_KEY, sessionService);
        BotSession botSession = new BotSession(this);
        botSession.setMessage(message);
        session.addListener(botSession);
        session.connect();
    }

    public void disconnect() {
        if (session != null && session.isConnected()) {
            session.disconnect("Manual disconnect.");
        }
    }

    public void reconnect() {
        disconnect();
        PauseUtils.wait(3000);
        connect(ip, port, proxy, proxyInfo);
    }

}