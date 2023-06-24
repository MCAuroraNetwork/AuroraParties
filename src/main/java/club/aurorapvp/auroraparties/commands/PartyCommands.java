package club.aurorapvp.auroraparties.commands;

import club.aurorapvp.auroraparties.configs.Lang;
import club.aurorapvp.auroraparties.modules.Party;
import club.aurorapvp.auroraparties.modules.PartyRequest;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("party")
public class PartyCommands extends BaseCommand {
  @Default
  @Description("Opens the party menu")
  @SuppressWarnings("unused")
  public void onOpenGUI(Player p) {
    if (!Party.inParty(p)) {
      p.sendMessage(Lang.getComponent("not-in-party"));
      return;
    }

    Party party = Party.getParty(p);

    assert party != null;
    party.getGUI().open(p);
  }

  @Subcommand("create")
  @Description("Creates a new party")
  @SuppressWarnings("unused")
  public void onCreate(Player p) {
    new Party(p);
  }

  @Subcommand("disband")
  @Description("Disbands the current party")
  @SuppressWarnings("unused")
  public void onDisband(Player p) {
    Party party = Party.getParty(p);

    if (party == null) {
      p.sendMessage(Lang.getComponent("not-in-party"));
      return;
    } else if (!party.hasPermission(p, "can-disband")) {
      p.sendMessage(Lang.getComponent("no-permission"));
      return;
    }

    party.disband();
  }

  @Subcommand("invite")
  @CommandCompletion("@players")
  @Syntax("[player]")
  @Description("Invites a player to a party")
  @SuppressWarnings("unused")
  public void onInvite(Player p, String player) {
    Party party = Party.getParty(p);

    if (party == null) {
      p.sendMessage(Lang.getComponent("not-in-party"));
      return;
    } else if (!party.hasPermission(p, "can-invite")) {
      p.sendMessage(Lang.getComponent("no-permission"));
      return;
    } else if (party.getMembers().size() >= 27) {
      p.sendMessage(Lang.getComponent("party-full"));
      return;
    }

    Player invited = Bukkit.getPlayer(player);

    if (invited == null) {
      p.sendMessage(Lang.getComponent("unknown-player"));
      return;
    } else if (party.contains(invited)) {
      p.sendMessage(Lang.getComponent("already-in-party"));
      return;
    }

    new PartyRequest(p, invited);
  }

  @Subcommand("accept")
  @Description("Accepts a party invite")
  @SuppressWarnings("unused")
  public void onAccept(Player p) {
    PartyRequest request = PartyRequest.getRequest(p);

    if (request == null) {
      p.sendMessage(Lang.getComponent("no-invites"));
      return;
    }

    request.accept();
  }

  @Subcommand("deny")
  @Description("Denies a party invite")
  @SuppressWarnings("unused")
  public void onDeny(Player p) {
    PartyRequest request = PartyRequest.getRequest(p);

    if (request == null) {
      p.sendMessage(Lang.getComponent("no-invites"));
      return;
    }

    request.deny();
  }
}
