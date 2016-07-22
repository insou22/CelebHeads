package co.insou.celebheads.database;

import co.insou.celebheads.Heads;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private final Heads plugin;
    private final Map<UUID, HeadPlayer> players = new HashMap<>();

    public PlayerManager(Heads plugin) {
        this.plugin = plugin;
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            register(player);
        }
        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(), plugin);
    }

    private HeadPlayer register(Player player) {
        return players.put(player.getUniqueId(), new HeadPlayer(plugin, player));
    }

    private HeadPlayer deregister(Player player) {
        return players.remove(player.getUniqueId());
    }

    public HeadPlayer getPlayer(Player player) {
        HeadPlayer headPlayer = players.get(player.getUniqueId());
        if (headPlayer == null) {
            headPlayer = register(player);
        }
        return headPlayer;
    }

    private class PlayerListener implements Listener {

        @EventHandler
        public void on(PlayerJoinEvent event) {
            register(event.getPlayer());
        }

        @EventHandler
        public void on(PlayerQuitEvent event) {
            deregister(event.getPlayer());
        }

    }

}
