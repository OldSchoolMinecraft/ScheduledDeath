package net.oldschoolminecraft.sd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.concurrent.TimeUnit;

public class AdminCommands implements CommandExecutor
{
    private ScheduledDeath plugin;

    public AdminCommands(ScheduledDeath plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!(sender.hasPermission("sd.admin") || sender.isOp()))
        {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            return true;
        }

        if (args.length == 0)
        {
            sendMultiLine(sender,
                    String.format("&eScheduled Death &cv%s&e - &dHelp", plugin.getDescription().getVersion()),
                    "&a/sd reload - Reload config",
                    "&a/sd cancel - Reset restart timer",
                    "&a/sd timer - Check the restart timer",
                    "&a/sd postpone <time> - Postpone restart",
                    "&a/sd set <time> - Override the preconfigured timer");
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload"))
        {
            plugin.getConfig().reload();
            Bukkit.getScheduler().cancelTask(plugin.getRestartTaskID());
            plugin.reinitializeRestartTask();
            System.out.println("[Scheduled Death] Configuration has been reloaded");
            sendMultiLine(sender, "&Configuration reloaded.");
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("cancel"))
        {
            System.out.println("[Scheduled Death] The automatic restart has been cancelled by " + sender.getName());
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&dThe automatic restart has been cancelled by the system administrator."));
            plugin.resetTaskTimer();
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("timer"))
        {
            double timeToLive = plugin.getTimeToLive();
            Time time = new Time((long) timeToLive, TimeUnit.SECONDS);
            sendMultiLine(sender, String.format("&eThere is currently &c%s &eleft until restart.", time));
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("postpone"))
        {
            try
            {
                Time time = Time.parseString(args[1]);
                plugin.incrementTaskTimer(time.toSeconds());
                String unit = time.toString();
                System.out.println("[Scheduled Death] The automatic restart has been postponed for " + unit + " by " + sender.getName());
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&dThe automatic restart has been postponed for &c" + unit + " &dby the system administrator."));
            } catch (Time.TimeParseException ex) {
                sendMultiLine(sender, "&cYou provided an invalid time! Try using one of these for seconds, minutes, or hours: &b30s, 5m, 1h");
            }
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("set"))
        {
            try
            {
                Time time = Time.parseString(args[1]);
                plugin.setTaskTimer(time.toSeconds());
                System.out.println("[Scheduled Death] The automatic restart has been reassigned by " + sender.getName() + ". The server will now restart in: " + time);
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&dThe automatic restart has been altered by the system administrator. The server will now restart in: &c" + time));
            } catch (Time.TimeParseException ex) {
                sendMultiLine(sender, "&cYou provided an invalid time! Try using one of these for seconds, minutes, or hours: &b30s, 5m, 1h");
            }
        }

        return true;
    }

    private void sendMultiLine(CommandSender sender, String... messages)
    {
        for (String msg : messages)
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }
}
