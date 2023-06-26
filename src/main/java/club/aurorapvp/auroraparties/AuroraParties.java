package club.aurorapvp.auroraparties;

import club.aurorapvp.auroraparties.commands.CommandManager;
import club.aurorapvp.auroraparties.config.Config;
import club.aurorapvp.auroraparties.config.Lang;
import club.aurorapvp.auroraparties.events.EventManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuroraParties extends JavaPlugin {
    public static JavaPlugin INSTANCE;

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();

        INSTANCE = this;

        // Initialize configs
        Config.init();
        Lang.init();

        // Initialize classes
        CommandManager.init();
        EventManager.init();

        getLogger().info(
            "AuroraParties loaded in " + (System.currentTimeMillis() - startTime) +
                "ms");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("AuroraParties unloaded");
    }
}
