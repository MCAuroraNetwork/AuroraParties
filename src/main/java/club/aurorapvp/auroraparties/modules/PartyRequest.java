package club.aurorapvp.auroraparties.modules;

import club.aurorapvp.auroraparties.configs.Config;
import club.aurorapvp.auroraparties.configs.Lang;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.entity.Player;

public class PartyRequest {
  private final static Set<PartyRequest> PENDING_REQUESTS = new HashSet<>();
  private final Player partyLeader;
  private final Player invited;
  private final Party party;

  public PartyRequest(Player partyLeader, Player invited) {
    this.partyLeader = partyLeader;
    this.invited = invited;
    this.party = Party.getParty(partyLeader);

    PENDING_REQUESTS.add(this);

    assert party != null;
    partyLeader.sendMessage(Lang.getComponent("invite-sent"));
    invited.sendMessage(
        Lang.formatComponent("invite-received", partyLeader.getName(), party.getName()));

    PartyRequest thisRequest = this;

    new Timer().schedule(new TimerTask() {
                           @Override
                           public void run() {
                             if (PENDING_REQUESTS.contains(thisRequest)) {
                               partyLeader.sendMessage(Lang.getComponent("invite-timed-out"));
                               invited.sendMessage(Lang.getComponent("invite-timed-out"));
                               PENDING_REQUESTS.remove(thisRequest);
                               this.cancel();
                             }
                           }
                         }, Config.get().getInt("invites.timeout-time")
    );
  }

  public Player getInvited() {
    return invited;
  }

  public void accept() {
    party.addMember(invited);
    PENDING_REQUESTS.remove(this);

    partyLeader.sendMessage(Lang.getComponent("invite-accepted"));
    invited.sendMessage(Lang.getComponent("invite-accepted"));
  }

  public void deny() {
    PENDING_REQUESTS.remove(this);

    partyLeader.sendMessage(Lang.getComponent("invite-denied"));
    invited.sendMessage(Lang.getComponent("invite-denied"));
  }

  public static PartyRequest getRequest(Player p) {
    for (PartyRequest request : PENDING_REQUESTS) {
      if (request.getInvited() == p) {
        return request;
      }
    }
    return null;
  }
}
