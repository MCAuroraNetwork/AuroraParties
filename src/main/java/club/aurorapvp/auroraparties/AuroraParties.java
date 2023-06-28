package club.aurorapvp.auroraparties;

import club.aurorapvp.auroraduels.AuroraDuels;
import club.aurorapvp.auroraparties.commands.CommandManager;
import club.aurorapvp.auroraparties.config.Config;
import club.aurorapvp.auroraparties.config.Lang;
import club.aurorapvp.auroraparties.events.EventManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuroraParties extends JavaPlugin {
  public static JavaPlugin INSTANCE;
  private static boolean auroraCombatInstalled = false;
  private static boolean auroraKitsInstalled = false;

  @Override
  public void onEnable() {
    long startTime = System.currentTimeMillis();

    INSTANCE = this;

    // Initialize configs
    Config.init();
    Lang.init();

    if (Bukkit.getPluginManager().getPlugin("AuroraCombat") != null &&
        Config.get().getBoolean("optional-plugins.auroracombat-compatibility")) {
      auroraCombatInstalled = true;
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

  public static boolean isAuroraKitsInstalled() {
    return AuroraDuels.isAuroraKitsInstalled();
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
    getLogger().info("AuroraParties unloaded");
  }
}
