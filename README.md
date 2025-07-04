# mc-bots-connector

![Java](https://img.shields.io/badge/Java-8-blue?logo=java)

A Minecraft bot connector via proxy with message sending capability.

## ✨ Features
- **Bot Management**: Centralized multi-bot control
- **Proxy Handling**: Auto-loading and validation
- **Auto-Registration**: Automatic server sign-up
- **Scheduled Messages**: Timed messaging
- **Fault Tolerance**: Auto-respawn & reconnect
- **Async Connections**: Multi-threaded bots

## 📚 Usage
```java
// Create a server object (if port is not specified, 25565 is used)
MinecraftServer server = new MinecraftServer("127.0.0.1", 25566);

// Initialize BotManager (specify server and the number of bots per proxy)
int botsPerProxy = 1;
BotManager.INSTANCE.init(server, botsPerProxy);

// Set a common message for all bots
BotManager.INSTANCE.setMessage("Testing");

// Start connecting all bots
BotManager.INSTANCE.connect();
```

## ❗ Important Notes
- Works only on servers with online-mode=false
- Compatible only with Minecraft version 1.16.5
- This project is intended for localhost/private networks only.

## ⚠️ Legal Disclaimer
This project is provided for educational purposes only. Using this software may violate:
- Minecraft EULA
- Microsoft Terms of Service
- Computer Misuse Act (various jurisdictions)

By using this software you agree:
- To use only on servers you own/control
- Not to disrupt third-party services
- To assume all legal responsibility

## 📜 License
mc-bots-connector is licensed under the **[MIT license](https://opensource.org/license/mit)**.

This project uses open-source components. For license details see **[pom.xml](pom.xml)** and dependencies' official websites.
