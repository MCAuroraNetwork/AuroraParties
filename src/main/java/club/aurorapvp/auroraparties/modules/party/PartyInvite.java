package club.aurorapvp.auroraparties.modules.party;

import club.aurorapvp.auroraparties.AuroraParties;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.entity.Player;

public class PartyInvite {

  private static final HashMap<Party, LinkedList<PartyInvite>> PENDING_INVITES = new HashMap<>();
  private final Player partyLeader;
  private final Player invited;
  private final Party party;

  public PartyInvite(Player partyLeader, Player invited) {
    this.partyLeader = partyLeader;
    this.invited = invited;
    this.party = Party.getParty(partyLeader);

    if (party == null) {
      partyLeader.sendMessage(AuroraParties.getInstance().getLang().getComponent("not-in-party"));
      return;
    } else if (hasPendingInvite(partyLeader, invited)) {
      partyLeader.sendMessage(
          AuroraParties.getInstance().getLang().getComponent("already-sent-invite"));
      return;
    } else if (Party.inParty(invited)) {
      partyLeader.sendMessage(
          AuroraParties.getInstance().getLang().getComponent("already-in-party"));
      return;
    } else if (!party.isLeader(partyLeader)) {
      partyLeader.sendMessage(AuroraParties.getInstance().getLang().getComponent("no-permission"));
      return;
    } else if (PartyJoinRequest.hasRequest(party, invited)) {
      party.addMember(invited);
      return;
    }

    if (!PENDING_INVITES.containsKey(party)) {
      PENDING_INVITES.put(party, new LinkedList<>());
    }

    PENDING_INVITES.get(party).add(this);

    partyLeader.sendMessage(AuroraParties.getInstance().getLang().getComponent("invite-sent"));
    invited.sendMessage(
        AuroraParties.getInstance().getLang()
            .formatComponent("invite-received", partyLeader.getName()));

    PartyInvite thisRequest = this;

    new Timer()
        .schedule(
            new TimerTask() {
              @Override
              public void run() {
                if (PENDING_INVITES.get(party).contains(thisRequest)) {
                  partyLeader.sendMessage(
                      AuroraParties.getInstance().getLang().getComponent("invite-timed-out"));
                  invited.sendMessage(
                      AuroraParties.getInstance().getLang().getComponent("invite-timed-out"));
                  PENDING_INVITES.get(party).remove(thisRequest);
                  this.cancel();
                }
              }
            },
            AuroraParties.getInstance().getConfig().getInt("invites.timeout-time"));
  }

  public static PartyInvite getInvite(Player player) {
    for (LinkedList<PartyInvite> inviteList : PENDING_INVITES.values()) {
      for (PartyInvite invite : inviteList) {
        if (invite.partyLeader == player || invite.invited == player) {
          return invite;
        }
      }
    }

    return null;
  }

  public static PartyInvite getInvite(Player player, Party party) {
    for (PartyInvite invite : PENDING_INVITES.get(party)) {
      if (invite.partyLeader == player || invite.invited == player) {
        return invite;
      }
    }
    return null;
  }

  public static PartyInvite getInvite(Player inviter, Player invited) {
    for (PartyInvite invite : PENDING_INVITES.get(Party.getParty(inviter))) {
      if (invite.partyLeader == inviter && invite.invited == invited) {
        return invite;
      }
    }
    return null;
  }

  public static Set<PartyInvite> getInvites(Player player, Party party) {
    Set<PartyInvite> matchingRequests = new HashSet<>();

    for (PartyInvite invite : PENDING_INVITES.get(party)) {
      if (invite.partyLeader == player && invite.invited == player) {
        matchingRequests.add(invite);
      }
    }

    return matchingRequests;
  }

  public static boolean hasPendingInvite(Player invited, Party party) {
    LinkedList<PartyInvite> inviteList = PENDING_INVITES.get(party);

    for (PartyInvite invite : inviteList) {
      if (invite.invited == invited) {
        return true;
      }
    }

    return false;
  }

  public static boolean hasPendingInvite(Player inviter, Player invited) {
    for (PartyInvite invite : PENDING_INVITES.get(Party.getParty(inviter))) {
      if (invite.partyLeader == inviter && invite.invited == invited) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasPendingInvite(Player invited) {

    for (LinkedList<PartyInvite> inviteList : PENDING_INVITES.values()) {
      for (PartyInvite invite : inviteList) {
        if (invite.invited == invited) {
          return true;
        }
      }
    }

    return false;
  }

  public void accept() {
    party.addMember(invited);
    PENDING_INVITES.get(party).remove(this);

    for (LinkedList<PartyInvite> inviteList : PENDING_INVITES.values()) {
      for (PartyInvite invite : inviteList) {
        if (invite.invited == invited) {
          invite.deny();
        }
      }
    }

    partyLeader.sendMessage(AuroraParties.getInstance().getLang().getComponent("invite-accepted"));
    invited.sendMessage(AuroraParties.getInstance().getLang().getComponent("invite-accepted"));
  }

  public void deny() {
    PENDING_INVITES.get(party).remove(this);

    partyLeader.sendMessage(AuroraParties.getInstance().getLang().getComponent("invite-denied"));
    invited.sendMessage(AuroraParties.getInstance().getLang().getComponent("invite-denied"));
  }
}
