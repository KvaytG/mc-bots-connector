package ru.kvaytg.mcbotsconnector;

import ru.kvaytg.mcbotsconnector.bot.BotManager;

public class Main {

    public static void main(String[] args) {
        MinecraftServer server = new MinecraftServer("127.0.0.1");
        BotManager.INSTANCE.init(server, 1);
        BotManager.INSTANCE.setMessage("Testing");
        BotManager.INSTANCE.connect();
    }

}