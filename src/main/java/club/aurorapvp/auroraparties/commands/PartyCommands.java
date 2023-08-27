package club.aurorapvp.auroraparties.commands;

import club.aurorapvp.auroraparties.config.Lang;
import club.aurorapvp.auroraparties.modules.PendingDuel;
import club.aurorapvp.auroraparties.modules.party.Party;
import club.aurorapvp.auroraparties.modules.party.PartyInvite;
import club.aurorapvp.auroraparties.modules.party.PartyJoinRequest;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("party")
@CommandPermission("auroraparties.command.party")
public class PartyCommands extends BaseCommand {

  @Default
  @Description("Opens the party menu")
  @SuppressWarnings("unused")
  public void onOpenGUI(Player player) {
    if (!Party.inParty(player)) {
      return;
    }

    Party party = Party.getParty(player);
    assert party != null;

    if (!party.isLeader(player)) {
      player.openInventory(party.getMemberGUI().getInventory());
    } else if (!PendingDuel.hasPendingDuel(player)) {
      player.openInventory(party.getLeaderGUI().getInventory());

    }
  }

  @Subcommand("create")
  @CommandPermission("auroraparties.command.party.create")
  @Description("Creates a new party")
  @SuppressWarnings("unused")
  public void onCreate(Player player) {
    if (Party.inParty(player)) {
      player.sendMessage(Lang.getComponent("already-in-party"));
      return;
    }

    new Party(player);
  }

  @Subcommand("disband")
  @CommandPermission("auroraparties.command.party.disband")
  @Description("Disbands the current party")
  @SuppressWarnings("unused")
  public void onDisband(Player player) {
    Party party = Party.getParty(player);

    if (party == null) {
      player.sendMessage(Lang.getComponent("not-in-party"));
      return;
    } else if (!party.isLeader(player)) {
      player.sendMessage(Lang.getComponent("no-permission"));
      return;
    }

    party.disband();
  }

  @Subcommand("invite")
  @CommandPermission("auroraparties.command.party.invite")
  @CommandCompletion("@players")
  @Syntax("<player>")
  @Description("Invites a player to a party")
  @SuppressWarnings("unused")
  public void onInvite(Player player, String playerName) {
    Party party = Party.getParty(player);

    if (party == null) {
      player.sendMessage(Lang.getComponent("not-in-party"));
      return;
    }

    Player invited = Bukkit.getPlayer(playerName);

    if (invited == null) {
      player.sendMessage(Lang.getComponent("unknown-player"));
      return;
    } else if (party.contains(invited)) {
      player.sendMessage(Lang.getComponent("already-in-party"));
      return;
    }

    new PartyInvite(player, invited);
  }

  @Subcommand("join")
  @CommandPermission("auroraparties.command.party.join")
  @CommandCompletion("@players")
  @Syntax("<player>")
  @Description("Invites a player to a party")
  @SuppressWarnings("unused")
  public void onJoinRequest(Player player, String playerName) {
    Player requested = Bukkit.getPlayer(playerName);

    if (requested == null) {
      player.sendMessage(Lang.getComponent("unknown-player"));
      return;
    }

    Party party = Party.getParty(requested);

    if (party == null) {
      player.sendMessage(Lang.getComponent("unknown-party"));
      return;
    }

    if (party.contains(player) || Party.inParty(player)) {
      player.sendMessage(Lang.getComponent("already-in-party"));
      return;
    }

    new PartyJoinRequest(player, party);
  }

  @Subcommand("accept")
  @CommandPermission("auroraparties.command.party.accept")
  @CommandCompletion("@players")
  @Syntax("[player]")
  @Description("Accepts a party invite")
  @SuppressWarnings("unused")
  public void onAccept(Player player, @Optional String inviterName) {
    if (PartyInvite.hasPendingInvite(player)) {
      Player inviter = null;

      if (inviterName != null) {
        inviter = Bukkit.getPlayer(inviterName);
      }

      if (inviter == null) {
        PartyInvite invite = PartyInvite.getInvite(player);

        for (PartyInvite pendingInvite : PartyInvite.getInvites(player)) {
          if (pendingInvite != invite) {
            pendingInvite.deny();
          }
        }

        assert invite != null;
        invite.accept();
        return;
      }

      if (!PartyInvite.hasPendingInvite(inviter, player)) {
        player.sendMessage(Lang.getComponent("no-invites"));
        return;
      }

      PartyInvite invite = PartyInvite.getInvite(inviter, player);

      for (PartyInvite pendingInvite : PartyInvite.getInvites(player)) {
        if (pendingInvite != invite) {
          pendingInvite.deny();
        }
      }

      assert invite != null;
      invite.accept();
    } else if (PartyJoinRequest.hasRequest(Party.getParty(player))) {
      Party party = Party.getParty(player);

      assert party != null;
      if (!party.isLeader(player)) {
        player.sendMessage(Lang.getComponent("no-permission"));
        return;
      }

      Player requester = null;

      if (inviterName != null) {
        requester = Bukkit.getPlayer(inviterName);
      }

      if (requester == null) {
        Objects.requireNonNull(PartyJoinRequest.getRequest(party)).accept();
        return;
      }

      if (!PartyJoinRequest.hasRequest(party, requester)) {
        player.sendMessage(Lang.getComponent("no-requests"));
        return;
      }

      Objects.requireNonNull(PartyJoinRequest.getRequest(party, requester)).accept();
    }
  }

  @Subcommand("deny")
  @CommandPermission("auroraparties.command.party.deny")
  @CommandCompletion("@players")
  @Syntax("[player]")
  @Description("Denies a party invite")
  @SuppressWarnings("unused")
  public void onDeny(Player player, @Optional String inviterName) {
    if (PartyInvite.hasPendingInvite(player)) {
      Player inviter = null;

      if (inviterName != null) {
        inviter = Bukkit.getPlayer(inviterName);
      }

      if (inviter == null) {
        Objects.requireNonNull(PartyInvite.getInvite(player)).deny();
        return;
      }

      if (!PartyInvite.hasPendingInvite(inviter, player)) {
        player.sendMessage(Lang.getComponent("no-invites"));
        return;
      }

      Objects.requireNonNull(PartyInvite.getInvite(inviter, player)).deny();
    } else if (PartyJoinRequest.hasRequest(Party.getParty(player))) {
      Party party = Party.getParty(player);

      assert party != null;
      if (!party.isLeader(player)) {
        player.sendMessage(Lang.getComponent("no-permission"));
        return;
      }

      Player requester = null;

      if (inviterName != null) {
        requester = Bukkit.getPlayer(inviterName);
      }

      if (requester == null) {
        Objects.requireNonNull(PartyJoinRequest.getRequest(party)).deny();
        return;
      }

      if (!PartyJoinRequest.hasRequest(party, requester)) {
        player.sendMessage(Lang.getComponent("no-requests"));
        return;
      }

      Objects.requireNonNull(PartyJoinRequest.getRequest(party, requester)).deny();
    }
  }

  // TODO
  @CatchUnknown
  @Subcommand("help")
  @CommandPermission("auroraparties.command.party.help")
  @SuppressWarnings("unused")
  public void onHelp(Player player) {
    player.sendMessage(Lang.getComponent("unknown-party-command"));
  }
}
