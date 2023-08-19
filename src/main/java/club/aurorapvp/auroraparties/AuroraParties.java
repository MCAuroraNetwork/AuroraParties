package club.aurorapvp.auroraparties;

import club.aurorapvp.auroraparties.commands.CommandManager;
import club.aurorapvp.auroraparties.config.Config;
import club.aurorapvp.auroraparties.config.Lang;
import club.aurorapvp.auroraparties.events.EventManager;
import club.aurorapvp.auroraparties.modules.guis.PartySelectGUI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuroraParties extends JavaPlugin {
  public static JavaPlugin INSTANCE;private static boolean auroraCombatInstalled = false;
  private static boolean auroraWarpsInstalled = false;
  private static boolean auroraKitsInstalled = false;

  public static boolean isAuroraCombatInstalled() {
    return auroraCombatInstalled;
  }
  public static boolean isAuroraWarpsInstalled() {
    return auroraWarpsInstalled;
  }
  public static boolean isAuroraKitsInstalled() {
    return auroraKitsInstalled;
  }

  @Override
  public void onEnable() {
    long startTime = System.currentTimeMillis();

    INSTANCE = this;

    // Initialize configs
    Config.init();
    Lang.init();

    if (Bukkit.getPluginManager().getPlugin("AuroraCombat") != null
        && Config.get().getBoolean("optional-plugins.auroracombat-compatibility")) {
      auroraCombatInstalled = true;
    }

    if (Bukkit.getPluginManager().getPlugin("AuroraWarps") != null
        && Config.get().getBoolean("optional-plugins.aurorawarps-compatibility")) {
      auroraWarpsInstalled = true;
    }

    if ((Bukkit.getPluginManager().getPlugin("AuroraKits") != null)
        && Config.get().getBoolean("optional-plugins.aurorakits-compatibility")) {
      auroraKitsInstalled = true;
    }

    // Initialize classes
    CommandManager.init();
    EventManager.init();
    PartySelectGUI.reload();

    getLogger().info("AuroraParties enabled in " + (System.currentTimeMillis() - startTime) + "ms");
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
    getLogger().info("AuroraParties disabled");
  }
}
