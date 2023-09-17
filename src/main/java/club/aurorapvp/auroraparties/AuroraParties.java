package club.aurorapvp.auroraparties;

import club.aurorapvp.auroraparties.commands.CommandManager;
import club.aurorapvp.auroraparties.config.Config;
import club.aurorapvp.auroraparties.config.Lang;
import club.aurorapvp.auroraparties.events.EventManager;
import club.aurorapvp.auroraparties.modules.guis.PartySelectGUI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuroraParties extends JavaPlugin {

  private static AuroraParties INSTANCE;
  private static boolean auroraCombatInstalled = false;
  private static boolean auroraWarpsInstalled = false;
  private static boolean auroraKitsInstalled = false;
  private Config config;
  private Lang lang;

  public static AuroraParties getInstance() {
    return INSTANCE;
  }

  public static boolean isAuroraCombatInstalled() {
    return auroraCombatInstalled;
  }

  public static boolean isAuroraWarpsInstalled() {
    return auroraWarpsInstalled;
  }

  public static boolean isAuroraKitsInstalled() {
    return auroraKitsInstalled;
  }

  public YamlConfiguration getConfig() {
    return config.getYaml();
  }

  public Lang getLang() {
    return lang;
  }

  @Override
  public void onEnable() {
    long startTime = System.currentTimeMillis();

    INSTANCE = this;

    // Initialize configs
    config = new Config();
    lang = new Lang();

    if (Bukkit.getPluginManager().getPlugin("AuroraCombat") != null
        && getConfig().getBoolean("optional-plugins.auroracombat-compatibility")) {
      auroraCombatInstalled = true;
    }

    if (Bukkit.getPluginManager().getPlugin("AuroraWarps") != null
        && getConfig().getBoolean("optional-plugins.aurorawarps-compatibility")) {
      auroraWarpsInstalled = true;
    }

    if ((Bukkit.getPluginManager().getPlugin("AuroraKits") != null)
        && getConfig().getBoolean("optional-plugins.aurorakits-compatibility")) {
      auroraKitsInstalled = true;
    }

    // Initialize classes
    CommandManager.init();
    EventManager.init();
    PartySelectGUI.init();

    getLogger().info("AuroraParties enabled in " + (System.currentTimeMillis() - startTime) + "ms");
  }

  @Override
  public void onDisable() {
    getLogger().info("AuroraParties disabled");
  }

  public void reloadConfig() {
    config.reload();
  }
}
