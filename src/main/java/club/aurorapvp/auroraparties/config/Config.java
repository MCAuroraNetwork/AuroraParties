package club.aurorapvp.auroraparties.config;

import club.aurorapvp.auroraparties.AuroraParties;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {

  private static final File FILE = new File(AuroraParties.INSTANCE.getDataFolder(), "config.yml");
  private static YamlConfiguration config;

  public static void init() {
    Config.reload();
    Config.generateDefaults();
  }

  public static void generateDefaults() {
    final HashMap<String, Object> DEFAULTS = new HashMap<>();

    DEFAULTS.put("optional-plugins.auroracombat-compatibility", true);
    DEFAULTS.put("optional-plugins.aurorakits-compatibility", true);
    DEFAULTS.put("optional-plugins.aurorawarps-compatibility", true);
    DEFAULTS.put("invites.timeout-time", 600000);
    DEFAULTS.put("join-requests.timeout-time", 600000);

    for (String path : DEFAULTS.keySet()) {
      if (!get().isSet(path) || get().getString(path) == null) {
        get().set(path, DEFAULTS.get(path));
      }
    }

    try {
      get().save(FILE);
    } catch (IOException e) {
      AuroraParties.INSTANCE.getLogger().log(Level.SEVERE, "Failed to save config file", e);
    }
  }

  public static YamlConfiguration get() {
    return config;
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public static void reload() {
    if (!FILE.exists()) {
      try {
        FILE.getParentFile().mkdirs();
        FILE.createNewFile();

        config = YamlConfiguration.loadConfiguration(FILE);

        Config.generateDefaults();
      } catch (IOException e) {
        AuroraParties.INSTANCE.getLogger().log(Level.SEVERE, "Failed to generate config file", e);
      }
    }
    config = YamlConfiguration.loadConfiguration(FILE);
    AuroraParties.INSTANCE.getLogger().info("Config reloaded!");
  }
}
