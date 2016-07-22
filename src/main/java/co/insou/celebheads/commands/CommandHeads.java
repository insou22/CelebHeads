package co.insou.celebheads.commands;

import co.insou.colorchar.ColorChar;
import co.insou.commands.CommandConsumer;
import co.insou.gui.GUIPlayer;
import co.insou.celebheads.Heads;
import co.insou.celebheads.database.HeadPlayer;
import co.insou.celebheads.gui.CategorySelectPage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CommandHeads extends CommandConsumer {

    private final Heads plugin;

    public CommandHeads(Heads plugin) {
        super("heads", true);
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        if (!player.hasPermission("heads.use")) {
            player.sendMessage(ColorChar.color("&cYou don't have permission to do this!"));
            return;
        }
        final HeadPlayer headPlayer = plugin.getPlayerManager().getPlayer(player);
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("confirm")) {
                if (!headPlayer.isConfirmed()) {
                    headPlayer.sendMessage("&cYour request timed out, please do \"/heads\" again");
                    return;
                }
                if (!headPlayer.takeEmeralds()) {
                    headPlayer.sendMessage("&cYou don't have 5 emeralds!");
                    headPlayer.setConfirmed(false);
                    return;
                }
                headPlayer.sendMessage("&aYou have reset your cooldown!");
                headPlayer.setLastClaim(new Date(System.currentTimeMillis() - (plugin.getHeadDatabase().getCooldownMillis() + 1000)));
                headPlayer.player().performCommand("heads");
                return;
            }
            headPlayer.sendMessage(String.format("&cUnknown argument \"%s\"", args[0]));
            return;
        }
        if (!headPlayer.isClaimable()) {
            if (headPlayer.isConfirmed()) {
                headPlayer.sendMessage("&cPlease wait the 10 seconds before using this command again");
                return;
            }
            long millis = headPlayer.timeRemaining();
            headPlayer.sendMessage(
                    String.format(
                            "&cYou have to wait another %d hours, %d minutes, %d seconds to claim another head!",
                            TimeUnit.MILLISECONDS.toHours(millis),
                            TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                            TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
                    )
            );
            headPlayer.sendMessage(String.format("&bWould you like to pay %dx %s to reset your cooldown? You have 10 seconds to type \"/heads confirm\" to do so.", plugin.getHeadDatabase().getBuyAmount(), plugin.getHeadDatabase().getBuyMaterial() + (plugin.getHeadDatabase().getBuyData() == 0 ? "" : ":" + plugin.getHeadDatabase().getBuyData())));
            headPlayer.setConfirmed(true);
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                // Yes. I know. This is subject to overwrite conditions. I don't care. If you really do, fix it yourself and PR.
                public void run() {
                    headPlayer.setConfirmed(false);
                }
            }, 200);
            return;
        }
        GUIPlayer guiPlayer = plugin.getGuiManager().getPlayer(player);
        guiPlayer.openPage(new CategorySelectPage(plugin, guiPlayer), true);
    }

}
