package club.aurorapvp.auroraparties.config;

import club.aurorapvp.auroraparties.AuroraParties;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
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

    for (var path : getYaml().getKeys(false).toArray()) {
      if (Objects.requireNonNull(getYaml().getString((String) path)).startsWith("~")
          && Objects.requireNonNull(getYaml().getString((String) path)).endsWith("~")) {
        PLACEHOLDERS.put(
            (String) path,
            Objects.requireNonNull(getYaml().getString((String) path)).replace("~", ""));
      }
    }

    DEFAULTS.put("prefix", "~<gradient:#FFAA00:#FF55FF><bold>AuroraParties ><reset>~");
    DEFAULTS.put("reloaded", "<green>✨ Reloaded!");
    DEFAULTS.put("party-created", "<green>🎉 Party created! Use <bold>/party<reset><green> to manage it!");
    DEFAULTS.put("invite-sent", "<green>📨 Invite sent!");
    DEFAULTS.put("invite-received", "<yellow>💌 %s invited you to their party!\nType <bold>/party accept<reset><yellow> to join 🎈");
    DEFAULTS.put("invite-timed-out", "<red>⌛ Invite expired");
    DEFAULTS.put("invite-accepted", "<green>✅ Invite accepted!");
    DEFAULTS.put("invite-denied", "<red>❌ Invite denied");
    DEFAULTS.put("no-invites", "<red>🚫 No pending invites");
    DEFAULTS.put("already-sent-invite", "<red>🛑 Invite already sent!");
    DEFAULTS.put("player-in-duel-queue", "<red>⚔️ That player is already in a duel queue!");
    DEFAULTS.put("you-in-duel-queue", "<red>⚔️ You're already in a duel queue!");
    DEFAULTS.put("request-sent", "<green>📤 Request sent!");
    DEFAULTS.put("request-received", "<yellow>📥 %s requested to join your party!\nType <bold>/party accept<reset><yellow> to let them in 🌟");
    DEFAULTS.put("request-timed-out", "<red>⌛ Request expired");
    DEFAULTS.put("request-accepted", "<green>✅ Request accepted!");
    DEFAULTS.put("request-denied", "<red>❌ Request denied");
    DEFAULTS.put("request-cancelled", "<red>❌ Join request cancelled");
    DEFAULTS.put("no-requests", "<red>🚫 No pending requests");
    DEFAULTS.put("no-permission", "<red>🚷 You don't have permission to do that");
    DEFAULTS.put("party-disbanded", "<red>💥 %s disbanded the party");
    DEFAULTS.put("in-queue", "<yellow>⏳ Already in a queue");
    DEFAULTS.put("you-have-request", "<red>🕓 You already have a pending request");
    DEFAULTS.put("leave-party", "<red><bold>🚪 Leave Party");
    DEFAULTS.put("disband-party", "<red><bold>💣 Disband Party");
    DEFAULTS.put("exit", "<red><bold>❌ Exit");
    DEFAULTS.put("status", "<yellow><bold>📊 Party Status");
    DEFAULTS.put("party-name", "<yellow><bold>🎉 %s's Party");
    DEFAULTS.put("parties-gui", "<yellow><bold>🌐 All Parties");
    DEFAULTS.put("create-team-duel", "<yellow><bold>⚔️ Team Duel");
    DEFAULTS.put("start-team-duel", "<yellow><bold>🔥 Start Duel");
    DEFAULTS.put("start-ffa-duel", "<yellow><bold>⚡ FFA Duel");
    DEFAULTS.put("start-random-team-duel", "<yellow><bold>🎲 Random Team Duel");
    DEFAULTS.put("manage-party", "<yellow><bold>🛠️ Manage Party");
    DEFAULTS.put("duel-kit-gui", "<yellow><bold>🧰 Select Duel Kit");
    DEFAULTS.put("team-duel", "<yellow><bold>👥 New Team Duel");
    DEFAULTS.put("shuffle-teams", "<green><bold>🔀 Shuffle Teams");
    DEFAULTS.put("next-page", "<yellow><bold>➡️ Next page");
    DEFAULTS.put("previous-page", "<yellow><bold>⬅️ Previous page");
    DEFAULTS.put("kit-invalid-name", "<red>❗ Invalid kit name");
    DEFAULTS.put("unknown-player", "<red>❓ Player not found");
    DEFAULTS.put("already-in-party", "<red>👤 Player already in the party!");
    DEFAULTS.put("not-in-party", "<red>🙅 You're not in a party!");
    DEFAULTS.put("unknown-party", "<red>🔍 Party not found!");
    DEFAULTS.put("not-enough-players", "<red>👥 Not enough players to start!");
    DEFAULTS.put("removed-from-party", "<red>🚫 You were removed from the party");
    DEFAULTS.put("player-removed-from-party", "<red>👋 %s was removed from the party");
    DEFAULTS.put("left-party", "<red>🏃 You left the party");
    DEFAULTS.put("player-left-party", "<red>👋 %s left the party");
    DEFAULTS.put("player-joined-party", "<green>🎉 %s joined the party!");
    DEFAULTS.put("party-full", "<red>📛 This party is full!");
    DEFAULTS.put("party-chat-disabled", "<yellow>🔇 Party chat disabled");
    DEFAULTS.put("party-chat-enabled", "<green>💬 Party chat enabled");
    DEFAULTS.put("party-status-gui", "<yellow><bold>🔧 Choose Party Status");
    DEFAULTS.put("public-party", "<green><bold>🌎 Public Party");
    DEFAULTS.put("invite-only-party", "<blue><bold>✉️ Invite Only Party");
    DEFAULTS.put("private-party", "<red><bold>🔒 Private Party");
    DEFAULTS.put(
            "help-command",
            """
            <yellow><bold>📖 All Commands:
        
            <blue><bold>/parties:<reset><yellow> Opens the public party list 🧭
        
            <blue><bold>/party:<reset><yellow> Opens your party menu 🛡️
        
            <blue><bold>/party create [name]:<reset><yellow> Create a brand new party 🎈
        
            <blue><bold>/party disband:<reset><yellow> Disbands your current party 💥
        
            <blue><bold>/party invite [player]:<reset><yellow> Invite someone to your party 💌
        
            <blue><bold>/partyduel [player]:<reset><yellow> Challenge another party to a duel ⚔️
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

    for (var path : getYaml().getKeys(false).toArray()) {
      if (Objects.requireNonNull(getYaml().getString((String) path)).startsWith("~")
          && Objects.requireNonNull(getYaml().getString((String) path)).endsWith("~")) {
        PLACEHOLDERS.put(
            (String) path,
            Objects.requireNonNull(getYaml().getString((String) path)).replace("~", ""));
      }
    }
  }

  public Component formatComponent(String message, Object... args) {
    String pathString = getYaml().getString(message);
    assert pathString != null;
    for (var placeholder : PLACEHOLDERS.entrySet()) {
      if (pathString.contains(placeholder.getKey())) {
        pathString = pathString.replace(placeholder.getKey(), placeholder.getValue());
      }
    }
    pathString = String.format(pathString, args);
    return MiniMessage.miniMessage().deserialize(pathString);
  }

  public Component getComponent(String message) {
    String pathString = getYaml().getString(message);
    assert pathString != null;
    for (var placeholder : PLACEHOLDERS.entrySet()) {
      if (pathString.contains(placeholder.getKey())) {
        pathString = pathString.replace(placeholder.getKey(), placeholder.getValue());
      }
    }
    return MiniMessage.miniMessage().deserialize(pathString);
  }

  public Component getformattedComponentCommand(
      String message, String hover, String command, Object... args) {
    String pathString = getYaml().getString(message);
    assert pathString != null;
    for (String placeholder : PLACEHOLDERS.keySet()) {
      if (pathString.contains(placeholder)) {
        pathString = pathString.replace(placeholder, PLACEHOLDERS.get(placeholder));
      }
    }
    pathString = String.format(pathString, args);
    Component component = MiniMessage.miniMessage().deserialize(pathString);
    return component
        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, command))
        .hoverEvent(HoverEvent.showText(Component.text(hover)));
  }

  public Component getComponentCommand(String message, String hover, String command) {
    String pathString = getYaml().getString(message);
    assert pathString != null;
    for (String placeholder : PLACEHOLDERS.keySet()) {
      if (pathString.contains(placeholder)) {
        pathString = pathString.replace(placeholder, PLACEHOLDERS.get(placeholder));
      }
    }
    Component component = MiniMessage.miniMessage().deserialize(pathString);
    return component
        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, command))
        .hoverEvent(HoverEvent.showText(Component.text(hover)));
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
        AuroraParties.getInstance()
            .getLogger()
            .log(Level.SEVERE, "Failed to generate lang file", e);
      }
    }
    lang = YamlConfiguration.loadConfiguration(FILE);
    AuroraParties.getInstance().getLogger().info("Lang reloaded!");
  }
}
