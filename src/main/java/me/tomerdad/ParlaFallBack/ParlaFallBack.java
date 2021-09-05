package me.tomerdad.ParlaFallBack;

import com.google.inject.Inject;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.velocitypowered.api.command.CommandManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.tomerdad.ParlaFallBack.listeners.OnPlayerJoin;
import me.tomerdad.ParlaFallBack.listeners.OnPlayerKick;
import me.tomerdad.ParlaFallBack.utilities.Config;
import me.tomerdad.ParlaFallBack.utilities.Utilities;
import net.kyori.adventure.text.Component;
import java.nio.file.Path;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.slf4j.Logger;

@Plugin(id = "parlafallback", name = "Parla FallBack", version = "1.0",
        description = "Announcer plugin", authors = {"tomerdad"})
public class ParlaFallBack {

    private final ProxyServer server;
    private final Logger logger;
    public static Path configpath;

    @Inject
    public ParlaFallBack(ProxyServer server, Logger logger, @DataDirectory Path userConfigDirectory) {
        logger.info("Loading ParlaFallBack");
        this.server = server;
        this.logger = logger;
        configpath = userConfigDirectory;

        Config.setupConfig();

        logger.info("ParlaFallBack loaded");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        CommandManager commandManager = server.getCommandManager();

        LiteralCommandNode<CommandSource> help = LiteralArgumentBuilder
                .<CommandSource>literal("parlafallback")
                .requires(ctx -> ctx.hasPermission("parlafallback.help"))
                .executes(ctx -> {
                    Component deserialized = Component.text()
                            .append(LegacyComponentSerializer.legacyAmpersand().deserialize("HELP \n1. /parlafallback - Send this help message\n2. /parlafallback-reload - reloads the config\n3. /hub - teleports you to the hub"))
                            .build();
                    ctx.getSource().sendMessage(deserialized);
                    return 1;
                }).build();

        LiteralCommandNode<CommandSource> reload = LiteralArgumentBuilder
                .<CommandSource>literal("parlafallback-reload")
                .requires(ctx -> ctx.hasPermission("parlafallback.reload"))
                .executes(ctx -> {
                    Config.setupConfig();
                    ctx.getSource().sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&aReload Completed"));
                    return 1;
                }).build();

        LiteralCommandNode<CommandSource> hub = LiteralArgumentBuilder
                .<CommandSource>literal("hub")
                .requires(ctx -> ctx.hasPermission("parlafallback.hub"))
                .executes(ctx -> {
                    if (!Config.getrootNode().getNode("Command", "enable").getBoolean()) {
                        return 1;
                    }

                    RegisteredServer serverToTp = new Utilities(server).getServer("Command");
                    if (ctx.getSource() instanceof Player){
                        Player player = (Player) ctx.getSource();
                        if (player.getCurrentServer().isPresent()) {
                            if (Config.nodeConfigList("servers").contains(player.getCurrentServer().get().getServerInfo().getName())) {
                                ctx.getSource().sendMessage(Utilities.msgBuilder(Config.getrootNode().getNode("messages", "alreadyInHub").getString()));
                                return 1;
                            }
                        }
                        if (serverToTp != null) {
                            player.createConnectionRequest(serverToTp).fireAndForget();
                        } else {
                            player.disconnect(Utilities.msgBuilder(Config.getrootNode().getNode("messages", "randomError").getString()));
                        }
                    }
                    return 1;
                }).build();



        commandManager.register(
                commandManager.metaBuilder("parlafallback").build(),
                new BrigadierCommand(help)
        );

        commandManager.register(
                commandManager.metaBuilder("parlafallback-reload").build(),
                new BrigadierCommand(reload)
        );

        String[] aliases = Config.nodeConfigList("command", "aliases").toArray(new String[0]);
        commandManager.register(
                commandManager.metaBuilder("hub").aliases(aliases).build(),
                new BrigadierCommand(hub)
        );

        EventManager eventManager = server.getEventManager();

        eventManager.register(this, new OnPlayerKick(server, logger));
        eventManager.register(this, new OnPlayerJoin(server));

        logger.info("Commands loaded.");
    }
}