package club.aurorapvp.auroraparties.modules.party;

import club.aurorapvp.auroraparties.config.Config;
import club.aurorapvp.auroraparties.config.Lang;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.entity.Player;

public class PartyInvite {
  private static final LinkedList<PartyInvite> PENDING_INVITES = new LinkedList<>();
  private final Player partyLeader;
  private final Player invited;
  private final Party party;

  public PartyInvite(Player partyLeader, Player invited) {
    this.partyLeader = partyLeader;
    this.invited = invited;
    this.party = Party.getParty(partyLeader);

    assert party != null;

    if (hasPendingInvite(partyLeader, invited)) {
      partyLeader.sendMessage(
          club.aurorapvp.auroraduels.config.Lang.getComponent("already-sent-invite"));
      return;
    } else if (Party.inParty(invited)) {
      partyLeader.sendMessage(
          club.aurorapvp.auroraduels.config.Lang.getComponent("already-in-party"));
      return;
    } else if (!party.isLeader(partyLeader)) {
      partyLeader.sendMessage(Lang.getComponent("no-permission"));
      return;
    } else if (party.getMembers().size() >= 28) {
      partyLeader.sendMessage(Lang.getComponent("party-full"));
      return;
    }

    PENDING_INVITES.add(this);

    partyLeader.sendMessage(Lang.getComponent("invite-sent"));
    invited.sendMessage(
        Lang.formatComponent("invite-received", partyLeader.getName(), party.getName()));

    PartyInvite thisRequest = this;

    new Timer()
        .schedule(
            new TimerTask() {
              @Override
              public void run() {
                if (PENDING_INVITES.contains(thisRequest)) {
                  partyLeader.sendMessage(Lang.getComponent("invite-timed-out"));
                  invited.sendMessage(Lang.getComponent("invite-timed-out"));
                  PENDING_INVITES.remove(thisRequest);
                  this.cancel();
                }
              }
            },
            Config.get().getInt("invites.timeout-time"));
  }

  public static PartyInvite getInvite(Player p) {
    for (PartyInvite invite : PENDING_INVITES) {
      if (invite.partyLeader == p || invite.invited == p) {
        return invite;
      }
    }
    return null;
  }

  public static PartyInvite getInvite(Player inviter, Player invited) {
    for (PartyInvite invite : PENDING_INVITES) {
      if (invite.partyLeader == inviter && invite.invited == invited) {
        return invite;
      }
    }
    return null;
  }

  public static Set<PartyInvite> getInvites(Player p) {
    Set<PartyInvite> matchingRequests = new HashSet<>();

    for (PartyInvite invite : PENDING_INVITES) {
      if (invite.partyLeader == p && invite.invited == p) {
        matchingRequests.add(invite);
      }
    }

    return matchingRequests;
  }

  public static boolean hasPendingInvite(Player inviter, Player invited) {
    for (PartyInvite invite : PENDING_INVITES) {
      if (invite.partyLeader == inviter && invite.invited == invited) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasPendingInvite(Player p) {
    for (PartyInvite invite : PENDING_INVITES) {
      if (invite.partyLeader == p || invite.invited == p) {
        return true;
      }
    }
    return false;
  }

  public void accept() {
    party.addMember(invited);
    PENDING_INVITES.remove(this);

    for (PartyInvite invite : PartyInvite.getInvites(invited)) {
      if (invite != this) {
        invite.deny();
      }
    }

    partyLeader.sendMessage(Lang.getComponent("invite-accepted"));
    invited.sendMessage(Lang.getComponent("invite-accepted"));
  }

  public void deny() {
    PENDING_INVITES.remove(this);

    partyLeader.sendMessage(Lang.getComponent("invite-denied"));
    invited.sendMessage(Lang.getComponent("invite-denied"));
  }
}
