package club.aurorapvp.auroraparties.modules.party;

import club.aurorapvp.auroraparties.config.Config;
import club.aurorapvp.auroraparties.config.Lang;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.entity.Player;

public class PartyJoinRequest {

  private static final LinkedList<PartyJoinRequest> PENDING_REQUESTS = new LinkedList<>();
  private final Party party;
  private final Player requester;

  public PartyJoinRequest(Player requester, Party party) {
    this.party = party;
    this.requester = requester;

    if (PartyInvite.hasPendingInvite(requester)) {
      party.addMember(requester);
      return;
    }

    requester.sendMessage(Lang.getComponent("request-sent"));
    party.getLeader().sendMessage(Lang.formatComponent("request-received", requester.getName()));

    PartyJoinRequest thisRequest = this;

    new Timer()
        .schedule(
            new TimerTask() {
              @Override
              public void run() {
                if (PENDING_REQUESTS.contains(thisRequest)) {
                  requester.sendMessage(Lang.getComponent("request-timed-out"));
                  party.getLeader().sendMessage(Lang.getComponent("request-timed-out"));
                  PENDING_REQUESTS.remove(thisRequest);
                  this.cancel();
                }
              }
            },
            Config.get().getInt("join-requests.timeout-time"));

    PENDING_REQUESTS.add(this);
  }

  public static Set<PartyJoinRequest> getRequests(Player player) {
    Set<PartyJoinRequest> matchingRequests = new HashSet<>();

    for (PartyJoinRequest request : PENDING_REQUESTS) {
      if (request.requester == player) {
        matchingRequests.add(request);
      }
    }

    return matchingRequests;
  }

  public static PartyJoinRequest getRequest(Party party) {
    for (PartyJoinRequest request : PENDING_REQUESTS) {
      if (request.getParty() == party) {
        return request;
      }
    }
    return null;
  }

  public static boolean hasRequest(Party party) {
    for (PartyJoinRequest request : PENDING_REQUESTS) {
      if (request.getParty() == party) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasRequest(Party party, Player requester) {
    for (PartyJoinRequest request : PENDING_REQUESTS) {
      if (request.requester == requester && request.party == party) {
        return false;
      }
    }
    return true;
  }

  public static PartyJoinRequest getRequest(Party party, Player requester) {
    for (PartyJoinRequest request : PENDING_REQUESTS) {
      if (request.requester == requester && request.party == party) {
        return request;
      }
    }
    return null;
  }

  public Party getParty() {
    return party;
  }

  public void accept() {
    for (PartyJoinRequest request : PartyJoinRequest.getRequests(requester)) {
      if (request != this) {
        request.cancel();
      }
    }

    requester.sendMessage(Lang.getComponent("request-accepted"));

    party.addMember(requester);
    PENDING_REQUESTS.remove(this);
  }

  public void deny() {
    requester.sendMessage(Lang.getComponent("request-denied"));
    PENDING_REQUESTS.remove(this);
  }

  public void cancel() {
    party.getLeader().sendMessage(Lang.getComponent("request-cancelled"));
    PENDING_REQUESTS.remove(this);
  }
}
