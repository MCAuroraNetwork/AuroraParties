package club.aurorapvp.auroraparties.config;

import club.aurorapvp.auroraparties.AuroraParties;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.YamlConfiguration;

public class Lang {
  private static final HashMap<String, String> PLACEHOLDERS = new HashMap<>();
  private static final File FILE = new File(AuroraParties.INSTANCE.getDataFolder(), "lang.yml");
  private static YamlConfiguration lang;

  public static void init() {
    reload();
    generateDefaults();
  }

  public static void generateDefaults() {
    final HashMap<String, String> DEFAULTS = new HashMap<>();

    for (Object path : get().getKeys(false).toArray()) {
      if (Objects.requireNonNull(get().getString((String) path)).startsWith("~") &&
          Objects.requireNonNull(get().getString((String) path)).endsWith("~")) {
        PLACEHOLDERS.put((String) path, Objects.requireNonNull(get().getString((String) path))
            .replace("~", ""));
      }
    }

    DEFAULTS.put("prefix", "~<gradient:#FFAA00:#FF55FF><bold>AuroraParties ><reset>~");
    DEFAULTS.put("party-created", "prefix <gradient:#FFAA00:#FF55FF>Party successfully created!");
    DEFAULTS.put("invite-sent", "prefix <gradient:#FFAA00:#FF55FF>Invite successfully sent!");
    DEFAULTS.put("invite-received",
        "prefix <gradient:#FFAA00:#FF55FF>You have received a party invite from %1$s to join %2$s! Type /party accept to join!");
    DEFAULTS.put("invite-timed-out", "prefix <gradient:#FFAA00:#FF55FF>Invite timed out");
    DEFAULTS.put("invite-accepted", "prefix <gradient:#FFAA00:#FF55FF>Invite accepted!");
    DEFAULTS.put("invite-denied", "prefix <gradient:#FFAA00:#FF55FF>Invite denied");
    DEFAULTS.put("no-invites", "prefix <gradient:#FFAA00:#FF55FF>You have no pending invites!");
    DEFAULTS.put("already-sent-invite", "prefix <gradient:#FFAA00:#FF55FF>You have already sent an invite!");
    DEFAULTS.put("no-permission",
        "prefix <gradient:#FFAA00:#FF55FF>You don't have permission to do that!");
    DEFAULTS.put("party-disbanded",
        "prefix <gradient:#FFAA00:#FF55FF>%1$s has been disbanded by %2$s");
    DEFAULTS.put("leave-party", "<gradient:#FFAA00:#FF55FF><bold>Leave Party");
    DEFAULTS.put("disband-party", "<gradient:#FFAA00:#FF55FF><bold>Disband Party");
    DEFAULTS.put("exit", "<gradient:#FFAA00:#FF55FF><bold>Exit");
    DEFAULTS.put("create-team-duel", "<gradient:#FFAA00:#FF55FF><bold>Team Duel");
    DEFAULTS.put("start-team-duel", "<gradient:#FFAA00:#FF55FF><bold>Start Duel");
    DEFAULTS.put("start-ffa-duel", "<gradient:#FFAA00:#FF55FF><bold>FFA Duel");
    DEFAULTS.put("start-random-team-duel",
        "<gradient:#FFAA00:#FF55FF><bold>Random Team Duel");
    DEFAULTS.put("manage-party", "<gradient:#FFAA00:#FF55FF><bold>Manage Party");
    DEFAULTS.put("duel-kit-gui", "<gradient:#FFAA00:#FF55FF><bold>Select Duel Kit");
    DEFAULTS.put("next-page", "<gradient:#FFAA00:#FF55FF><bold>Next page");
    DEFAULTS.put("previous-page", "<gradient:#FFAA00:#FF55FF>Previous page");
    DEFAULTS.put("kit-invalid-name", "prefix <gradient:#FFAA00:#FF55FF>Invalid kit name!");
    DEFAULTS.put("unknown-player", "prefix <gradient:#FFAA00:#FF55FF>Unable to find player!");
    DEFAULTS.put("already-in-party",
        "prefix <gradient:#FFAA00:#FF55FF>Player is already in this party!");
    DEFAULTS.put("not-in-party", "prefix <gradient:#FFAA00:#FF55FF>You're not in a party!");
    DEFAULTS.put("not-enough-players",
        "prefix <gradient:#FFAA00:#FF55FF>There's not enough players in this party!");
    DEFAULTS.put("removed-from-party",
        "prefix <gradient:#FFAA00:#FF55FF>You have been removed from the party");
    DEFAULTS.put("player-removed-from-party",
        "prefix <gradient:#FFAA00:#FF55FF>%s has been removed from the party");
    DEFAULTS.put("left-party", "prefix <gradient:#FFAA00:#FF55FF>You have left the party");
    DEFAULTS.put("player-left-party", "prefix <gradient:#FFAA00:#FF55FF>%s has left the party");
    DEFAULTS.put("party-full", "prefix <gradient:#FFAA00:#FF55FF>This party is full!");
    // TODO
    DEFAULTS.put("help-command", """

        """);

    for (String path : DEFAULTS.keySet()) {
      if (!get().contains(path) || get().getString(path) == null) {
        get().set(path, DEFAULTS.get(path));
      }
    }

    try {
      get().save(FILE);
    } catch (IOException e) {
      AuroraParties.INSTANCE.getLogger().severe("Failed to save lang file");
    }

    for (Object path : get().getKeys(false).toArray()) {
      if (Objects.requireNonNull(get().getString((String) path)).startsWith("~") &&
          Objects.requireNonNull(get().getString((String) path)).endsWith("~")) {
        PLACEHOLDERS.put((String) path, Objects.requireNonNull(get().getString((String) path))
            .replace("~", ""));
      }
    }
  }

  public static String getString(String message) {
    String pathString = get().getString(message);
    for (String placeholder : PLACEHOLDERS.keySet()) {
      assert pathString != null;
      if (pathString.contains(placeholder)) {
        pathString = pathString.replace(placeholder,
            PLACEHOLDERS.get(placeholder));
      }
    }
    return pathString;
  }

  public static Component formatComponent(String message, Object... args) {
    String pathString = get().getString(message);
    assert pathString != null;
    for (String placeholder : PLACEHOLDERS.keySet()) {
      if (pathString.contains(placeholder)) {
        pathString = pathString.replace(placeholder,
            PLACEHOLDERS.get(placeholder));
      }
    }

    pathString = String.format(pathString, args);

    return MiniMessage.miniMessage().deserialize(pathString);
  }

  public static Component getComponent(String message) {
    String pathString = get().getString(message);
    assert pathString != null;

    for (String placeholder : PLACEHOLDERS.keySet()) {
      if (pathString.contains(placeholder)) {
        pathString = pathString.replace(placeholder,
            PLACEHOLDERS.get(placeholder));
      }
    }
    return MiniMessage.miniMessage().deserialize(pathString);
  }

  public static YamlConfiguration get() {
    return lang;
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public static void reload() {
    if (!FILE.exists()) {
      try {
        FILE.getParentFile().mkdirs();
        FILE.createNewFile();
      } catch (IOException e) {
        AuroraParties.INSTANCE.getLogger().severe("Failed to generate lang file");
      }
    }
    lang = YamlConfiguration.loadConfiguration(FILE);
    AuroraParties.INSTANCE.getLogger().info("Lang reloaded!");
  }
}
