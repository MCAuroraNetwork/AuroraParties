package club.aurorapvp.auroraparties.modules.party;

import club.aurorapvp.auroraparties.AuroraParties;
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

    if (party == null) {
      partyLeader.sendMessage(AuroraParties.INSTANCE.getLang().getComponent("not-in-party"));
      return;
    } else if (hasPendingInvite(partyLeader, invited)) {
      partyLeader.sendMessage(
          club.aurorapvp.auroraduels.config.Lang.getComponent("already-sent-invite"));
      return;
    } else if (Party.inParty(invited)) {
      partyLeader.sendMessage(
          club.aurorapvp.auroraduels.config.Lang.getComponent("already-in-party"));
      return;
    } else if (!party.isLeader(partyLeader)) {
      partyLeader.sendMessage(AuroraParties.INSTANCE.getLang().getComponent("no-permission"));
      return;
    } else if (party.getMembers().size() >= 28) {
      partyLeader.sendMessage(AuroraParties.INSTANCE.getLang().getComponent("party-full"));
      return;
    } else if (PartyJoinRequest.hasRequest(party, invited)) {
      party.addMember(invited);
      return;
    }

    PENDING_INVITES.add(this);

    partyLeader.sendMessage(AuroraParties.INSTANCE.getLang().getComponent("invite-sent"));
    invited.sendMessage(
        AuroraParties.INSTANCE.getLang().formatComponent("invite-received", partyLeader.getName()));

    PartyInvite thisRequest = this;

    new Timer()
        .schedule(
            new TimerTask() {
              @Override
              public void run() {
                if (PENDING_INVITES.contains(thisRequest)) {
                  partyLeader.sendMessage(AuroraParties.INSTANCE.getLang().getComponent("invite-timed-out"));
                  invited.sendMessage(AuroraParties.INSTANCE.getLang().getComponent("invite-timed-out"));
                  PENDING_INVITES.remove(thisRequest);
                  this.cancel();
                }
              }
            },
            AuroraParties.INSTANCE.getConfig().getInt("invites.timeout-time"));
  }

  public static PartyInvite getInvite(Party party) {
    for (PartyInvite invite : PENDING_INVITES) {
      if (invite.party == party) {
        return invite;
      }
    }
    return null;
  }

  public static PartyInvite getInvite(Player player) {
    for (PartyInvite invite : PENDING_INVITES) {
      if (invite.partyLeader == player || invite.invited == player) {
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

  public static Set<PartyInvite> getInvites(Player player) {
    Set<PartyInvite> matchingRequests = new HashSet<>();

    for (PartyInvite invite : PENDING_INVITES) {
      if (invite.partyLeader == player && invite.invited == player) {
        matchingRequests.add(invite);
      }
    }

    return matchingRequests;
  }

  public static boolean hasPendingInvite(Player invited, Party party) {
    PartyInvite invite = PartyInvite.getInvite(party);

    return invite != null && invite.invited == invited;
  }

  public static boolean hasPendingInvite(Player inviter, Player invited) {
    for (PartyInvite invite : PENDING_INVITES) {
      if (invite.partyLeader == inviter && invite.invited == invited) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasPendingInvite(Player player) {
    for (PartyInvite invite : PENDING_INVITES) {
      if (invite.partyLeader == player || invite.invited == player) {
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

    partyLeader.sendMessage(AuroraParties.INSTANCE.getLang().getComponent("invite-accepted"));
    invited.sendMessage(AuroraParties.INSTANCE.getLang().getComponent("invite-accepted"));
  }

  public void deny() {
    PENDING_INVITES.remove(this);

    partyLeader.sendMessage(AuroraParties.INSTANCE.getLang().getComponent("invite-denied"));
    invited.sendMessage(AuroraParties.INSTANCE.getLang().getComponent("invite-denied"));
  }
}
