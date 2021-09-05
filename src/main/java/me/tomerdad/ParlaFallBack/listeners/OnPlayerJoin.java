package me.tomerdad.ParlaFallBack.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.tomerdad.ParlaFallBack.utilities.Config;
import me.tomerdad.ParlaFallBack.utilities.Utilities;


public class OnPlayerJoin {

    private final ProxyServer server;

    public OnPlayerJoin(ProxyServer server) {
        this.server = server;
    }

    @Subscribe(order = PostOrder.EARLY)
    public void onPlayerJoin(PlayerChooseInitialServerEvent event) {
        if (!Config.getrootNode().getNode("FallBack", "enable").getBoolean()) {
            return;
        }

        Player player = event.getPlayer();

        RegisteredServer serverToTp = new Utilities(server).getServer("FallBack");
        if (serverToTp != null) {
            event.setInitialServer(serverToTp);
            player.createConnectionRequest(serverToTp);
        } else {
            player.disconnect(Utilities.msgBuilder(Config.getrootNode().getNode("messages", "randomError").getString()));
        }

    }
}
