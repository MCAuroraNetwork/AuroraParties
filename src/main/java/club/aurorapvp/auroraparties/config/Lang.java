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

  private final HashMap<String, String> PLACEHOLDERS = new HashMap<>();
  private final File FILE = new File(AuroraParties.getInstance().getDataFolder(), "lang.yml");
  private YamlConfiguration lang;

  public Lang() {
    this.reload();
    this.generateDefaults();
  }

  public void generateDefaults() {
    final HashMap<String, String> DEFAULTS = new HashMap<>();

    for (Object path : getYaml().getKeys(false).toArray()) {
      if (Objects.requireNonNull(getYaml().getString((String) path)).startsWith("~")
          && Objects.requireNonNull(getYaml().getString((String) path)).endsWith("~")) {
        PLACEHOLDERS.put(
            (String) path, Objects.requireNonNull(getYaml().getString((String) path)).replace("~", ""));
      }
    }

    DEFAULTS.put("prefix", "~<gradient:#FFAA00:#FF55FF><bold>AuroraParties ><reset>~");
    DEFAULTS.put("reloaded", "prefix <green>Reloaded");
    DEFAULTS.put("party-created", "prefix <green>Party created! Use <bold>/party<reset><green> to manage it!");
    DEFAULTS.put("invite-sent", "prefix <green>Invite sent!");
    DEFAULTS.put(
        "invite-received",
        "prefix <yellow>%s invited you to join their party! Type <bold>/party accept<reset><yellow> to join!");
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
    DEFAULTS.put("in-queue", "prefix <yellow>Player is already in a queue");
    DEFAULTS.put("you-have-request", "prefix <red>You already have a pending request");
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
    DEFAULTS.put("team-duel", "<yellow><bold>New Team Duel");
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
      if (!getYaml().contains(path) || getYaml().getString(path) == null) {
        getYaml().set(path, DEFAULTS.get(path));
      }
    }

    try {
      getYaml().save(FILE);
    } catch (IOException e) {
      AuroraParties.getInstance().getLogger().log(Level.SEVERE, "Failed to save lang file", e);
    }

    for (Object path : getYaml().getKeys(false).toArray()) {
      if (Objects.requireNonNull(getYaml().getString((String) path)).startsWith("~")
          && Objects.requireNonNull(getYaml().getString((String) path)).endsWith("~")) {
        PLACEHOLDERS.put(
            (String) path, Objects.requireNonNull(getYaml().getString((String) path)).replace("~", ""));
      }
    }
  }

  public Component formatComponent(String message, Object... args) {
    String pathString = getYaml().getString(message);
    assert pathString != null;
    for (String placeholder : PLACEHOLDERS.keySet()) {
      if (pathString.contains(placeholder)) {
        pathString = pathString.replace(placeholder, PLACEHOLDERS.get(placeholder));
      }
    }

    pathString = String.format(pathString, args);

    return MiniMessage.miniMessage().deserialize(pathString);
  }

  public Component getComponent(String message) {
    String pathString = getYaml().getString(message);
    assert pathString != null;

    for (String placeholder : PLACEHOLDERS.keySet()) {
      if (pathString.contains(placeholder)) {
        pathString = pathString.replace(placeholder, PLACEHOLDERS.get(placeholder));
      }
    }
    return MiniMessage.miniMessage().deserialize(pathString);
  }

  public YamlConfiguration getYaml() {
    return lang;
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void reload() {
    if (!FILE.exists()) {
      try {
        FILE.getParentFile().mkdirs();
        FILE.createNewFile();

        lang = YamlConfiguration.loadConfiguration(FILE);

        this.generateDefaults();
      } catch (IOException e) {
        AuroraParties.getInstance().getLogger().log(Level.SEVERE, "Failed to generate lang file", e);
      }
    }
    lang = YamlConfiguration.loadConfiguration(FILE);
    AuroraParties.getInstance().getLogger().info("Lang reloaded!");
  }
}
