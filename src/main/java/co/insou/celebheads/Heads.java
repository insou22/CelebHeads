package co.insou.celebheads;

import co.insou.commands.CommandManager;
import co.insou.gui.GUIManager;
import co.insou.celebheads.commands.CommandHeads;
import co.insou.celebheads.database.HeadDatabase;
import co.insou.celebheads.database.PlayerManager;
import co.insou.celebheads.items.HeadItems;
import org.bukkit.plugin.java.JavaPlugin;

public class Heads extends JavaPlugin {

    private GUIManager guiManager;
    private HeadDatabase headDatabase;
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getDataFolder().mkdirs();

        HeadItems.load(this);
        guiManager = new GUIManager(this);

        this.headDatabase = new HeadDatabase(this);

        this.playerManager = new PlayerManager(this);

        CommandManager manager = new CommandManager(this);
        manager.register(new CommandHeads(this));
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }

    public HeadDatabase getHeadDatabase() {
        return headDatabase;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

}
