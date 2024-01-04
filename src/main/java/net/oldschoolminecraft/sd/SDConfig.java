package net.oldschoolminecraft.sd;

import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;

import java.io.File;

public class SDConfig extends Configuration
{
    public SDConfig(Plugin plugin)
    {
        super(new File(plugin.getDataFolder(), "config.yml"));
        this.reload();
    }

    private void write()
    {
        generateConfigOption("timeToLive", 864000L); // 12 hours default
        generateConfigOption("kickMessage", "&cServer is restarting! &aTry reconnecting in a few seconds.");
    }

    private void generateList(String key, Object... values)
    {
        generateConfigOption(key, values);
    }

    private void generateConfigOption(String key, Object defaultValue)
    {
        if (this.getProperty(key) == null)
        {
            this.setProperty(key, defaultValue);
        }
        final Object value = this.getProperty(key);
        this.removeProperty(key);
        this.setProperty(key, value);
    }

    //Getters Start
    public Object getConfigOption(String key)
    {
        return this.getProperty(key);
    }

    public String getConfigString(String key)
    {
        return String.valueOf(getConfigOption(key));
    }

    public Integer getConfigInteger(String key)
    {
        return Integer.valueOf(getConfigString(key));
    }

    public Long getConfigLong(String key)
    {
        return Long.valueOf(getConfigString(key));
    }

    public Double getConfigDouble(String key)
    {
        return Double.valueOf(getConfigString(key));
    }

    public Boolean getConfigBoolean(String key)
    {
        return Boolean.valueOf(getConfigString(key));
    }


    //Getters End


    public void reload()
    {
        this.load();
        this.write();
        this.save();
    }
}
