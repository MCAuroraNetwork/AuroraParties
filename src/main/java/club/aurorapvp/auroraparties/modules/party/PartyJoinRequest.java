package club.aurorapvp.auroraparties.modules.party;

import club.aurorapvp.auroraparties.AuroraParties;
import club.aurorapvp.auroraparties.config.Config;
import club.aurorapvp.auroraparties.config.Lang;
import java.util.HashSet;
import java.util.Iterator;
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

    if (PartyInvite.hasPendingInvite(requester, party)) {
      party.addMember(requester);
      return;
    }

    requester.sendMessage(AuroraParties.getInstance().getLang().getComponent("request-sent"));
    party
        .getLeader()
        .sendMessage(
            AuroraParties.getInstance()
                .getLang()
                .getformattedComponentCommand(
                    "request-received",
                    "Click to Accept Join Request",
                    "/party accept " + requester.getName(),
                    requester.getName()));

    PartyJoinRequest thisRequest = this;

    new Timer()
        .schedule(
            new TimerTask() {
              @Override
              public void run() {
                if (PENDING_REQUESTS.contains(thisRequest)) {
                  requester.sendMessage(
                      AuroraParties.getInstance().getLang().getComponent("request-timed-out"));
                  party
                      .getLeader()
                      .sendMessage(
                          AuroraParties.getInstance().getLang().getComponent("request-timed-out"));
                  PENDING_REQUESTS.remove(thisRequest);
                  this.cancel();
                }
              }
            },
            AuroraParties.getInstance().getConfig().getInt("join-requests.timeout-time"));

    PENDING_REQUESTS.add(this);
  }

  public static Set<PartyJoinRequest> getRequests(Player player) {
    Set<PartyJoinRequest> matchingRequests = new HashSet<>();

    for (PartyJoinRequest request : PENDING_REQUESTS) {
      if (request.requester.equals(player)) {
        matchingRequests.add(request);
      }
    }

    return matchingRequests;
  }

  public static PartyJoinRequest getRequest(Party party) {
    for (Iterator<PartyJoinRequest> it = PENDING_REQUESTS.descendingIterator(); it.hasNext(); ) {
      PartyJoinRequest request = it.next();
      if (request.getParty().equals(party)) {
        return request;
      }
    }
    return null;
  }

  public static boolean hasRequest(Party party) {
    for (PartyJoinRequest request : PENDING_REQUESTS) {
      if (request.getParty().equals(party)) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasRequest(Player player) {
    for (PartyJoinRequest request : PENDING_REQUESTS) {
      if (request.requester.equals(player)) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasRequest(Party party, Player requester) {
    for (PartyJoinRequest request : PENDING_REQUESTS) {
      if (request.requester.equals(requester) && request.party.equals(party)) {
        return true;
      }
    }
    return false;
  }

  public static PartyJoinRequest getRequest(Party party, Player requester) {
    for (PartyJoinRequest request : PENDING_REQUESTS) {
      if (request.requester.equals(requester) && request.party.equals(party)) {
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

    requester.sendMessage(AuroraParties.getInstance().getLang().getComponent("request-accepted"));

    party.addMember(requester);
    PENDING_REQUESTS.remove(this);
  }

  public void deny() {
    requester.sendMessage(AuroraParties.getInstance().getLang().getComponent("request-denied"));
    PENDING_REQUESTS.remove(this);
  }

  public void cancel() {
    party
        .getLeader()
        .sendMessage(AuroraParties.getInstance().getLang().getComponent("request-cancelled"));
    PENDING_REQUESTS.remove(this);
  }
}
