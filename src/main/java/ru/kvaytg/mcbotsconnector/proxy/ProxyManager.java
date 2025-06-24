package ru.kvaytg.mcbotsconnector.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ProxyManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyManager.class);

    private static final String PROXY_URL = "https://raw.githubusercontent.com/TheSpeedX/PROXY-List/master/socks5.txt";
    private static final String FILE_PATH = "socks5.list.txt";

    private final List<Proxy> proxies = new ArrayList<>();

    public ProxyManager(String targetHost, int targetPort) {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            LOGGER.info("Loading proxies from file: " + FILE_PATH);
            loadProxyListFromFile();
        } else {
            LOGGER.info("File not found, downloading proxies from URL and verifying...");
            List<String> downloaded = downloadProxyList();
            List<String> validProxies = new ArrayList<>();
            for (String proxyStr : downloaded) {
                String[] parts = proxyStr.split(":");
                if (parts.length != 2) continue;
                String proxyHost = parts[0];
                int proxyPort;
                try {
                    proxyPort = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    continue;
                }
                if (isProxyAlive(proxyHost, proxyPort, targetHost, targetPort)) {
                    validProxies.add(proxyStr);
                }
            }
            saveProxiesToFile(validProxies);
            loadProxyListFromFile();
        }
    }

    private List<String> downloadProxyList() {
        List<String> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(PROXY_URL).openStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.contains(":")) {
                    list.add(line);
                }
            }
        } catch (IOException ex) {
            LOGGER.error("Failed to download proxy list", ex);
        }
        LOGGER.info("Proxies downloaded: {}", list.size());
        return list;
    }

    public static boolean isProxyAlive(String proxyHost, int proxyPort, String targetHost, int targetPort) {
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort));
        try (Socket socket = new Socket(proxy)) {
            socket.connect(new InetSocketAddress(targetHost, targetPort), 3000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void saveProxiesToFile(List<String> proxies) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String proxy : proxies) {
                writer.write(proxy);
                writer.newLine();
            }
            LOGGER.info("Valid proxies saved to file " + ProxyManager.FILE_PATH);
        } catch (IOException ex) {
            LOGGER.error("Error saving proxies", ex);
        }
    }

    private void loadProxyListFromFile() {
        proxies.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(":");
                if (parts.length == 2) {
                    String host = parts[0];
                    int port = Integer.parseInt(parts[1]);
                    proxies.add(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(host, port)));
                }
            }
            LOGGER.info("Proxies loaded from file: {}", proxies.size());
        } catch (IOException | NumberFormatException ex) {
            LOGGER.error("Error loading proxies", ex);
        }
    }

    public Proxy takeRandomProxy() {
        if (proxies.isEmpty()) return null;
        int index = ThreadLocalRandom.current().nextInt(proxies.size());
        return proxies.remove(index);
    }

    public int getProxyListSize() {
        return proxies.size();
    }

}