package net.oldschoolminecraft.sd;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;

public class CommandPreProcessor extends PlayerListener
{
    private ScheduledDeath plugin;

    public CommandPreProcessor(ScheduledDeath plugin)
    {
        this.plugin = plugin;
    }

    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        if (plugin.restartImminent && !(event.getPlayer().hasPermission("sd.admin") || event.getPlayer().isOp()))
        {
            event.getPlayer().sendMessage(ChatColor.RED + "Commands are disabled as a restart is imminent.");
            event.setCancelled(true);
        }
    }
}
