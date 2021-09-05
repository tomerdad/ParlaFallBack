package me.tomerdad.ParlaFallBack.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.*;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.tomerdad.ParlaFallBack.utilities.Config;
import me.tomerdad.ParlaFallBack.utilities.Utilities;
import org.slf4j.Logger;

public class OnPlayerKick {

    private final ProxyServer server;
    private final Logger logger;

    public OnPlayerKick(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe(order = PostOrder.EARLY)
    public void onPlayerKick(KickedFromServerEvent event) {
        if (!Config.getrootNode().getNode("FallBack", "enable").getBoolean()) {
            return;
        }

        logger.info(event.getServerKickReason().toString());

        Player player = event.getPlayer();

        RegisteredServer serverToTp = new Utilities(server).getServer("FallBack");
        if (serverToTp != null) {
//            player.createConnectionRequest(serverToTp);
            event.setResult(KickedFromServerEvent.RedirectPlayer.create(serverToTp, Utilities.msgBuilder(Config.getrootNode().getNode("messages", "randomError").getString())));
        } else {
            player.disconnect(Utilities.msgBuilder(Config.getrootNode().getNode("messages", "randomError").getString()));
        }
    }
}
