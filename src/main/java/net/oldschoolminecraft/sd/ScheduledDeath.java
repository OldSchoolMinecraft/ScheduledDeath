package net.oldschoolminecraft.sd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

public class ScheduledDeath extends JavaPlugin
{
    private SDConfig config;
    private double secondsTTL;
    private int restartTaskID;
    public boolean restartImminent;

    public void onEnable()
    {
        config = new SDConfig(this);

        resetTaskTimer();

        getCommand("sd").setExecutor(new AdminCommands(this));
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, new CommandPreProcessor(this), Event.Priority.Normal, this);

        reinitializeRestartTask();

        System.out.println("ScheduledDeath has been enabled");
    }

    public void resetTaskTimer()
    {
        long timeToLive = config.getConfigLong("timeToLive");
        secondsTTL = (int) (timeToLive / 20);
        System.out.println("[Scheduled Death] Server will die after " + Math.round((float) timeToLive / (20 * 60 * 60)) + " hours");
    }

    public void reinitializeRestartTask()
    {
        restartTaskID = getServer().getScheduler().scheduleAsyncRepeatingTask(this, () ->
        {
            if (secondsTTL == (60 * 5))
            {
                System.out.println("[Scheduled Death] 5 minutes until scheduled death");
                Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Server will auto-restart in 5 minutes.");
            }

            if (secondsTTL == 30)
            {
                System.out.println("[Scheduled Death] 30 seconds until scheduled death");
                Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Server will auto-restart in 30 seconds.");
            }

            if (secondsTTL == 10)
            {
                System.out.println("[Scheduled Death] 10 seconds until scheduled death");
                Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Server will auto-restart in 10 seconds.");
            }

            if (secondsTTL <= 5)
            {
                restartImminent = true;
                System.out.println("[Scheduled Death] " + secondsTTL + " until scheduled death");
                Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Server will auto-restart in " + secondsTTL + " seconds...");
            }

            if (secondsTTL == 0)
            {
                System.out.println(">>> INITIATING SERVER'S SCHEDULED DEATH <<<");
                Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "The server is now restarting!");
                for (World world : getServer().getWorlds())
                    world.save();
                for (Player player : getServer().getOnlinePlayers())
                    player.kickPlayer(ChatColor.translateAlternateColorCodes('&', String.valueOf(config.getConfigOption("kickMessage"))));
                getServer().getScheduler().cancelTask(restartTaskID);
                getServer().savePlayers();
                getServer().shutdown();
            }

            secondsTTL--;
        }, 0L, 20);
    }

    public void incrementTaskTimer(double seconds)
    {
        secondsTTL += seconds;
    }

    public void setTaskTimer(double seconds)
    {
        secondsTTL = seconds;
    }

    public double getTimeToLive()
    {
        return secondsTTL;
    }

    public int getRestartTaskID()
    {
        return restartTaskID;
    }

    public SDConfig getConfig()
    {
        return config;
    }

    public void onDisable()
    {
        System.out.println("[Scheduled Death] Server has now successfully committed die. See you next time!");
    }
}
