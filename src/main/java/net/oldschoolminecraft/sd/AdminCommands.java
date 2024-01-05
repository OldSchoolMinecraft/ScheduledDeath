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
        if (!(sender.hasPermission("srvdeath.admin") || sender.isOp()))
        {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            return true;
        }

        if (args.length == 0)
        {
            sendMultiLine(sender,
                    "&eScheduled Death - &dHelp",
                    "&a/sd reload - Reload config",
                    "&a/sd cancel - Reset restart timer",
                    "&a/sd timer - Check the restart timer",
                    "&a/sd postpone <time> - Postpone restart");
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

            long days = TimeUnit.SECONDS.toDays((long) timeToLive);
            timeToLive -= TimeUnit.DAYS.toSeconds(days);
            long hours = TimeUnit.SECONDS.toHours((long) timeToLive);
            timeToLive -= TimeUnit.HOURS.toSeconds(hours);
            long minutes = TimeUnit.SECONDS.toMinutes((long) timeToLive);
            timeToLive -= TimeUnit.MINUTES.toSeconds(minutes);
            long seconds = (long) timeToLive;

            StringBuilder sb = new StringBuilder();
            if (days != 0) sb.append(days).append(" day(s) ");
            if (hours != 0) sb.append(hours).append(" hour(s) ");
            if (minutes != 0) sb.append(minutes).append(" minute(s) ");
            if (seconds != 0) sb.append(seconds).append(" second(s) ");
            String unitsRemaining = sb.toString().trim();

            sendMultiLine(sender, String.format("&eThere is currently &c%s &eleft until restart.", unitsRemaining));
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
                sendMultiLine(sender, "&cYou provided an invalid time! Try using of these for seconds, minutes, or hours: &b30s, 5m, 1h");
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
