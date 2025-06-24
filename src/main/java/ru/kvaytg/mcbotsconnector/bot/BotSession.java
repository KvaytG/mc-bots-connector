package ru.kvaytg.mcbotsconnector.bot;

import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import ru.kvaytg.mcbotsconnector.util.PauseUtils;
import ru.kvaytg.mcbotsconnector.util.StringUtils;
import java.util.Timer;
import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotSession extends SessionAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BotSession.class);

    private static final int MAX_RECONNECT_ATTEMPTS = 5;
    private int reconnectAttempts = 0;

    private final BotClient botClient;

    private boolean registered = false;
    private String message = null;

    public BotSession(BotClient botClient) {
        this.botClient = botClient;
    }

    public synchronized void setMessage(String newMessage) {
        this.message = newMessage;
    }

    @Override
    public void packetReceived(PacketReceivedEvent event) {
        try {
            Packet packet = event.getPacket();
            if (packet instanceof ServerChatPacket) {
                if (registered) return;
                String password = botClient.getPassword();
                String command = "/register " + password + " " + password;
                event.getSession().send(new ClientChatPacket(command));
                registered = true;
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (event.getSession().isConnected()) {
                            synchronized (BotSession.this) {
                                if (!StringUtils.isNullOrBlank(message)) {
                                    event.getSession().send(new ClientChatPacket(message));
                                }
                            }
                        } else {
                            this.cancel();
                        }
                    }
                }, 0, 1000);
            } else if (packet instanceof ServerPlayerPositionRotationPacket) {
                ServerPlayerPositionRotationPacket posPacket = (ServerPlayerPositionRotationPacket) packet;
                event.getSession().send(new ClientTeleportConfirmPacket(posPacket.getTeleportId()));
            } else if (packet instanceof ServerPlayerHealthPacket) {
                ServerPlayerHealthPacket healthPacket = (ServerPlayerHealthPacket) packet;
                if (healthPacket.getHealth() == 0) {
                    PauseUtils.wait(1000);
                    event.getSession().send(new ClientRequestPacket(ClientRequest.RESPAWN));
                }
            }
        } catch (Throwable ex) {
            LOGGER.error("Error processing packet: ", ex);
        }
    }

    @Override
    public void disconnected(DisconnectedEvent event) {
        String reason = event.getReason();
        if (reason.contains("The server is full!")) {
            LOGGER.info("Disconnected: The server is full");
        } else if (reason.toLowerCase().contains("antibot")) {
            LOGGER.info("Disconnected: AntiBot protection");
        } else {
            LOGGER.info("Disconnected: {}", reason);
            if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                reconnectAttempts++;
                PauseUtils.wait(5000);
                botClient.reconnect();
            } else {
                LOGGER.info("Maximum reconnect attempts reached");
            }
        }
    }

}