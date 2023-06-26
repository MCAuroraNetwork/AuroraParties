package club.aurorapvp.auroraparties;

import club.aurorapvp.auroraparties.commands.CommandManager;
import club.aurorapvp.auroraparties.config.Config;
import club.aurorapvp.auroraparties.config.Lang;
import club.aurorapvp.auroraparties.events.EventManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuroraParties extends JavaPlugin {
    public static JavaPlugin INSTANCE;
    private static boolean auroraDuelsInstalled = false;
    private static boolean auroraCombatInstalled = false;
    private static boolean auroraKitsInstalled = false;

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();

        INSTANCE = this;

        // Initialize configs
        Config.init();
        Lang.init();

        // Check if soft depends are installed
        if (Bukkit.getPluginManager().getPlugin("AuroraDuels") != null &&
            Config.get().getBoolean("optional-plugins.auroraduels-compatibility")) {
            auroraDuelsInstalled = true;
        }
        if (Bukkit.getPluginManager().getPlugin("AuroraCombat") != null &&
            Config.get().getBoolean("optional-plugins.auroracombat-compatibility")) {
            auroraCombatInstalled = true;
        }
        if (Bukkit.getPluginManager().getPlugin("AuroraKits") != null &&
            Config.get().getBoolean("optional-plugins.aurorakits-compatibility")) {
            auroraKitsInstalled = true;
        }

        // Initialize classes
        CommandManager.init();
        EventManager.init();

        getLogger().info(
            "AuroraParties loaded in " + (System.currentTimeMillis() - startTime) +
                "ms");
    }

    public static boolean isAuroraCombatInstalled() {
        return auroraCombatInstalled;
    }

    public static boolean isAuroraDuelsInstalled() {
        return auroraDuelsInstalled;
    }

    public static boolean isAuroraKitsInstalled() {
        return auroraKitsInstalled;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("AuroraParties unloaded");
    }
}
