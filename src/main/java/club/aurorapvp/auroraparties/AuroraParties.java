package club.aurorapvp.auroraparties;

import club.aurorapvp.auroraduels.AuroraDuels;
import club.aurorapvp.auroraparties.commands.CommandManager;
import club.aurorapvp.auroraparties.config.Config;
import club.aurorapvp.auroraparties.config.Lang;
import club.aurorapvp.auroraparties.events.EventManager;
import club.aurorapvp.auroraparties.modules.party.PublicPartyGUI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuroraParties extends JavaPlugin {
  public static JavaPlugin INSTANCE;
  private static boolean auroraCombatInstalled = false;
  private static boolean auroraWarpsInstalled = false;


  public static boolean isAuroraCombatInstalled() {
    return auroraCombatInstalled;
  }

  public static boolean isAuroraWarpsInstalled() { return auroraWarpsInstalled; }

  public static boolean isAuroraKitsInstalled() {
    return AuroraDuels.isAuroraKitsInstalled();
  }

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

    if (Bukkit.getPluginManager().getPlugin("AuroraTeleports") != null &&
        Config.get().getBoolean("optional-plugins.aurorateleports-compatibility")) {
      auroraWarpsInstalled = true;
    }

    // Initialize classes
    CommandManager.init();
    EventManager.init();
    PublicPartyGUI.reload();

    getLogger().info(
        "AuroraParties enabled in " + (System.currentTimeMillis() - startTime) +
            "ms");
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
    getLogger().info("AuroraParties disabled");
  }
}
