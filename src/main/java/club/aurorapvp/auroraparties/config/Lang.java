package club.aurorapvp.auroraparties.config;

import club.aurorapvp.auroraparties.AuroraParties;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.YamlConfiguration;

public class Lang {

  private static final HashMap<String, String> PLACEHOLDERS = new HashMap<>();
  private static final File FILE = new File(AuroraParties.INSTANCE.getDataFolder(), "lang.yml");
  private static YamlConfiguration lang;

  public static void init() {
    Lang.reload();
    Lang.generateDefaults();
  }

  public static void generateDefaults() {
    final HashMap<String, String> DEFAULTS = new HashMap<>();

    for (Object path : get().getKeys(false).toArray()) {
      if (Objects.requireNonNull(get().getString((String) path)).startsWith("~")
          && Objects.requireNonNull(get().getString((String) path)).endsWith("~")) {
        PLACEHOLDERS.put(
            (String) path, Objects.requireNonNull(get().getString((String) path)).replace("~", ""));
      }
    }

    DEFAULTS.put("prefix", "~<gradient:#FFAA00:#FF55FF><bold>AuroraParties ><reset>~");
    DEFAULTS.put("party-created", "prefix <green>Party created!");
    DEFAULTS.put("invite-sent", "prefix <green>Invite sent!");
    DEFAULTS.put(
        "invite-received",
        "prefix <yellow>%1$s invited you to join %2$s! Type <bold>/party accept<reset><yellow> to join!");
    DEFAULTS.put("invite-timed-out", "prefix <red>Invite expired");
    DEFAULTS.put("invite-accepted", "prefix <green>Invite accepted!");
    DEFAULTS.put("invite-denied", "prefix <red>Invite denied");
    DEFAULTS.put("no-invites", "prefix <red>No pending invites");
    DEFAULTS.put("already-sent-invite", "prefix <red>Invite already sent!");
    DEFAULTS.put("request-sent", "prefix <green>Request sent!");
    DEFAULTS.put(
        "request-received",
        "prefix <yellow>%s requested to join your party! Type <bold>/party accept<reset><yellow> to let them join!");
    DEFAULTS.put("request-timed-out", "prefix <red>Join request expired");
    DEFAULTS.put("request-accepted", "prefix <green>Request accepted!");
    DEFAULTS.put("request-denied", "prefix <red>Request denied");
    DEFAULTS.put("request-cancelled", "prefix <red>Party join request cancelled");
    DEFAULTS.put("no-requests", "prefix <red>No pending requests!");
    DEFAULTS.put("no-permission", "prefix <red>You don't have permission to do that");
    DEFAULTS.put("party-disbanded", "prefix <red>Party disbanded by %s");
    DEFAULTS.put("leave-party", "<red><bold>Leave Party");
    DEFAULTS.put("disband-party", "<red><bold>Disband Party");
    DEFAULTS.put("exit", "<red><bold>Exit");
    DEFAULTS.put("status", "<yellow><bold>Party Status");
    DEFAULTS.put("party-name", "<yellow><bold>%s's Party");
    DEFAULTS.put("parties-gui", "<yellow><bold>All Parties");
    DEFAULTS.put("create-team-duel", "<yellow><bold>Team Duel");
    DEFAULTS.put("start-team-duel", "<yellow><bold>Start Duel");
    DEFAULTS.put("start-ffa-duel", "<yellow><bold>FFA Duel");
    DEFAULTS.put("start-random-team-duel", "<yellow><bold>Random Team Duel");
    DEFAULTS.put("manage-party", "<yellow><bold>Manage Party");
    DEFAULTS.put("duel-kit-gui", "<yellow><bold>Select Duel Kit");
    DEFAULTS.put("next-page", "<yellow><bold>Next page");
    DEFAULTS.put("previous-page", "<yellow><bold>Previous page");
    DEFAULTS.put("kit-invalid-name", "prefix <red>Invalid kit name");
    DEFAULTS.put("unknown-player", "prefix <red>Player not found");
    DEFAULTS.put("already-in-party", "prefix <red>Player already in this party!");
    DEFAULTS.put("not-in-party", "prefix <red>You're not in a party!");
    DEFAULTS.put("unknown-party", "prefix <red>Party not found!");
    DEFAULTS.put("not-enough-players", "prefix <red>Not enough players in this party!");
    DEFAULTS.put("removed-from-party", "prefix <red>You have been removed from the party");
    DEFAULTS.put("player-removed-from-party", "prefix <red>%s removed from the party");
    DEFAULTS.put("left-party", "prefix <red>You have left the party");
    DEFAULTS.put("player-left-party", "prefix <red>%s left the party");
    DEFAULTS.put("player-joined-party", "prefix <green>%s joined the party!");
    DEFAULTS.put("party-full", "prefix <red>This party is full!");
    DEFAULTS.put("unknown-party-command", "prefix <red>Unknown Party Command! Use /help");
    DEFAULTS.put(
        "help-command",
        """
            prefix <yellow><bold>All Commands:
                    
            <blue><bold>/parties: <reset><yellow>Opens the GUI for all public parties
            <blue><bold>/party: <reset><yellow>Opens the party GUI
            <blue><bold>/party create [name]: <reset><yellow>Creates a party
            <blue><bold>/party disband: <reset><yellow>Disbands your party
            <blue><bold>/party invite [player]: <reset><yellow>Invites a player to your party
            <blue><bold>/partyduel [player]: <reset><yellow>Sends a duel request to someone else's party
            """);

    for (String path : DEFAULTS.keySet()) {
      if (!get().contains(path) || get().getString(path) == null) {
        get().set(path, DEFAULTS.get(path));
      }
    }

    try {
      get().save(FILE);
    } catch (IOException e) {
      AuroraParties.INSTANCE.getLogger().log(Level.SEVERE, "Failed to save lang file", e);
    }

    for (Object path : get().getKeys(false).toArray()) {
      if (Objects.requireNonNull(get().getString((String) path)).startsWith("~")
          && Objects.requireNonNull(get().getString((String) path)).endsWith("~")) {
        PLACEHOLDERS.put(
            (String) path, Objects.requireNonNull(get().getString((String) path)).replace("~", ""));
      }
    }
  }

  public static Component formatComponent(String message, Object... args) {
    String pathString = get().getString(message);
    assert pathString != null;
    for (String placeholder : PLACEHOLDERS.keySet()) {
      if (pathString.contains(placeholder)) {
        pathString = pathString.replace(placeholder, PLACEHOLDERS.get(placeholder));
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
        pathString = pathString.replace(placeholder, PLACEHOLDERS.get(placeholder));
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

        lang = YamlConfiguration.loadConfiguration(FILE);

        Lang.generateDefaults();
      } catch (IOException e) {
        AuroraParties.INSTANCE.getLogger().log(Level.SEVERE, "Failed to generate lang file", e);
      }
    }
    lang = YamlConfiguration.loadConfiguration(FILE);
    AuroraParties.INSTANCE.getLogger().info("Lang reloaded!");
  }
}
