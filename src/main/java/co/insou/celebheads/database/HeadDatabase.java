package co.insou.celebheads.database;

import co.insou.celebheads.Heads;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class HeadDatabase {

    private final Heads plugin;
    private final FileConfiguration config;
    private final File databaseFile;
    private final YamlConfiguration database;

    private long cooldownMillis;

    private Material buyMaterial;
    private short buyData;
    private int buyAmount;

    public HeadDatabase(Heads plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        cooldownMillis = config.getLong("cooldown-seconds") * 1000;
        buyMaterial = Material.valueOf(config.getString("buy-item.material"));
        buyData = (short) config.getInt("buy-item.data");
        buyAmount = config.getInt("buy-item.amount");

        databaseFile = new File(plugin.getDataFolder(), "database.yml");
        if (!databaseFile.exists()) {
            try {
                databaseFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        database = YamlConfiguration.loadConfiguration(databaseFile);
    }

    public long getCooldownMillis() {
        return cooldownMillis;
    }

    public YamlConfiguration getDatabase() {
        return database;
    }

    public void saveDatabase() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                save();
            }
        });
    }

    private synchronized void save() {
        try {
            database.save(databaseFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Material getBuyMaterial() {
        return buyMaterial;
    }

    public void setBuyMaterial(Material buyMaterial) {
        this.buyMaterial = buyMaterial;
    }

    public short getBuyData() {
        return buyData;
    }

    public void setBuyData(short buyData) {
        this.buyData = buyData;
    }

    public int getBuyAmount() {
        return buyAmount;
    }

    public void setBuyAmount(int buyAmount) {
        this.buyAmount = buyAmount;
    }
}
