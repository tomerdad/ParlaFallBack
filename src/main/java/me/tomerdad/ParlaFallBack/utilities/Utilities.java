package me.tomerdad.ParlaFallBack.utilities;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public class Utilities {

    private final ProxyServer server;

    public Utilities(ProxyServer server) {
        this.server = server;
    }

    public RegisteredServer getServer(String method) {
        List<String> serversList = Config.nodeConfigList(method,"servers");
        String mode = Config.getrootNode().getNode(method, "mode").getString();
        if (Objects.equals(mode, "random")) {
            //random
            String serverName;
            Random random = new Random();
            while (serversList.size() > 0) {
                int serverNum = random.nextInt(serversList.size());
                serverName = serversList.get(serverNum);
                if (!CheckServer(server.getServer(serverName))) {
                    serversList.remove(serverName);
                } else {
                    return server.getServer(serverName).get();
                }
            }

        }
        else if (Objects.equals(mode, "order")) {
            //order
            for (String serverName : serversList) {
                if (CheckServer(server.getServer(serverName))) {
                    return server.getServer(serverName).get();
                }
            }
        }

        return null;
    }

    public static boolean CheckServer(Optional<RegisteredServer> server) {
        if (server.isPresent()) {
            try {
//                server.get().ping().join();
//                return true;
//                logger.info(String.valueOf(server.get().getPlayersConnected().size()));
//                logger.info(String.valueOf(server.get().ping().join().getPlayers().get().getMax()));
                if (server.get().getPlayersConnected().size() < server.get().ping().join().getPlayers().get().getMax()){
                    return true;
                } else {
                    return false;
                }
                //check if full

            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static TextComponent msgBuilder(String msg) {
        return LegacyComponentSerializer.legacySection().deserialize(msg);
    }
}
