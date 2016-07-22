package co.insou.celebheads.database;

import co.insou.colorchar.ColorChar;
import co.insou.celebheads.Heads;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.UUID;

public class HeadPlayer {

    private final Heads plugin;
    private final UUID uuid;

    private Date lastClaim;

    private boolean confirmed = false;

    public HeadPlayer(Heads plugin, Player player) {
        this.plugin = plugin;
        this.uuid = player.getUniqueId();
        YamlConfiguration database = plugin.getHeadDatabase().getDatabase();
        Long lastClaimLong = database.getLong(uuid + ".last-claim", -1);
        if (lastClaimLong != -1) {
            lastClaim = new Date(lastClaimLong);
        }
    }

    public void sendMessage(String message) {
        if (message == null) {
            return;
        }
        player().sendMessage(ColorChar.color(message));
    }

    public Player player() {
        return Bukkit.getPlayer(uuid);
    }

    public Date getLastClaim() {
        return lastClaim;
    }

    public void setLastClaim(Date lastClaim) {
        this.lastClaim = lastClaim;
        updateDatabase();
    }

    public boolean isClaimable() {
        return timeRemaining() <= 0;
    }

    private void updateDatabase() {
        YamlConfiguration database = plugin.getHeadDatabase().getDatabase();
        database.set(uuid + ".last-claim", lastClaim.getTime());
        plugin.getHeadDatabase().saveDatabase();
    }

    public long timeRemaining() {
        if (lastClaim == null) {
            return 0;
        }
        return Math.max(0, (lastClaim.getTime() + plugin.getHeadDatabase().getCooldownMillis()) - new Date().getTime());
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public boolean takeEmeralds() {
        if (count(plugin.getHeadDatabase().getBuyMaterial(), plugin.getHeadDatabase().getBuyData()) < plugin.getHeadDatabase().getBuyAmount()) {
            return false;
        }
        remove(plugin.getHeadDatabase().getBuyMaterial(), plugin.getHeadDatabase().getBuyData(), plugin.getHeadDatabase().getBuyAmount());
        return true;
    }

    public int count(Material material, int data) {
        int count = 0;
        for (int i = 0; i < player().getInventory().getSize(); i++) {
            ItemStack item = player().getInventory().getItem(i);
            if (item != null && item.getType() == material && item.getDurability() == data) {
                count += item.getAmount();
            }
        }
        return count;
    }

    public void remove(Material material, int data, int amount) {
        int count = amount;
        for (int i = 0; i < player().getInventory().getSize(); i++) {
            ItemStack item = player().getInventory().getItem(i);
            if (item != null && item.getType() == material && item.getDurability() == data) {
                if (item.getAmount() <= count) {
                    count -= item.getAmount();
                    player().getInventory().setItem(i, new ItemStack(Material.AIR));
                    if (count == 0) {
                        return;
                    }
                    continue;
                }
                player().getInventory().setItem(i, new ItemStack(material, item.getAmount() - count, (short) data));
                return;
            }
        }
    }

}
