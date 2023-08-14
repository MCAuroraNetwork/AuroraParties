package club.aurorapvp.auroraparties.commands;

import club.aurorapvp.auroraduels.modules.duels.Duel;
import club.aurorapvp.auroraparties.config.Lang;
import club.aurorapvp.auroraparties.modules.party.Party;
import club.aurorapvp.auroraparties.modules.party.PartyIndexGUI;
import club.aurorapvp.auroraparties.modules.party.PartyInvite;
import club.aurorapvp.auroraparties.modules.party.PartyJoinRequest;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("party|parties")
public class PartyCommands extends BaseCommand {
  @Default
  @Description("Opens the party menu")
  @SuppressWarnings("unused")
  public void onOpenGUI(Player p) {
    if (!Party.inParty(p)) {
      PartyIndexGUI.open(p);
      return;
    }

    if (Duel.inDuel(p)) {
      return;
    }

    Party party = Party.getParty(p);

    assert party != null;

    if (party.isLeader(p)) {
      p.openInventory(party.getLeaderGUI().getInventory());
    } else {
      p.openInventory(party.getMemberGUI().getInventory());
    }
  }

  @Subcommand("create")
  @Description("Creates a new party")
  @SuppressWarnings("unused")
  public void onCreate(Player p) {
    if (Party.inParty(p)) {
      p.sendMessage(Lang.getComponent("already-in-party"));
      return;
    }

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
    } else if (!party.isLeader(p)) {
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
    }

    Player invited = Bukkit.getPlayer(player);

    if (invited == null) {
      p.sendMessage(Lang.getComponent("unknown-player"));
      return;
    } else if (party.contains(invited)) {
      p.sendMessage(Lang.getComponent("already-in-party"));
      return;
    }

    new PartyInvite(p, invited);
  }

  @Subcommand("accept")
  @Syntax("[player]")
  @CommandCompletion("@players")
  @Description("Accepts a party invite")
  @SuppressWarnings("unused")
  public void onAccept(Player p, @Optional String inviterName) {
    if (PartyInvite.hasPendingInvite(p)) {
      Player inviter = null;

      if (inviterName != null) {
        inviter = Bukkit.getPlayer(inviterName);
      }

      if (inviter == null) {
        PartyInvite invite = PartyInvite.getInvite(p);

        for (PartyInvite pendingInvite : PartyInvite.getInvites(p)) {
          if (pendingInvite != invite) {
            pendingInvite.deny();
          }
        }

        assert invite != null;
        invite.accept();
        return;
      }

      if (!PartyInvite.hasPendingInvite(inviter, p)) {
        p.sendMessage(Lang.getComponent("no-invites"));
        return;
      }

      PartyInvite invite = PartyInvite.getInvite(inviter, p);

      for (PartyInvite pendingInvite : PartyInvite.getInvites(p)) {
        if (pendingInvite != invite) {
          pendingInvite.deny();
        }
      }

      assert invite != null;
      invite.accept();
    } else if (PartyJoinRequest.hasRequest(Party.getParty(p))) {
      Party party = Party.getParty(p);

      assert party != null;
      if (!party.isLeader(p)) {
        p.sendMessage(Lang.getComponent("no-permission"));
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
        p.sendMessage(Lang.getComponent("no-requests"));
        return;
      }

      Objects.requireNonNull(PartyJoinRequest.getRequest(party, requester)).accept();
    }
  }

  @Subcommand("deny")
  @Syntax("[player]")
  @CommandCompletion("@players")
  @Description("Denies a party invite")
  @SuppressWarnings("unused")
  public void onDeny(Player p, @Optional String inviterName) {
    if (PartyInvite.hasPendingInvite(p)) {
      Player inviter = null;

      if (inviterName != null) {
        inviter = Bukkit.getPlayer(inviterName);
      }

      if (inviter == null) {
        Objects.requireNonNull(PartyInvite.getInvite(p)).deny();
        return;
      }

      if (!PartyInvite.hasPendingInvite(inviter, p)) {
        p.sendMessage(Lang.getComponent("no-invites"));
        return;
      }

      Objects.requireNonNull(PartyInvite.getInvite(inviter, p)).deny();
    } else if (PartyJoinRequest.hasRequest(Party.getParty(p))) {
      Party party = Party.getParty(p);

      assert party != null;
      if (!party.isLeader(p)) {
        p.sendMessage(Lang.getComponent("no-permission"));
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
        p.sendMessage(Lang.getComponent("no-requests"));
        return;
      }

      Objects.requireNonNull(PartyJoinRequest.getRequest(party, requester)).deny();
    }
  }
}
