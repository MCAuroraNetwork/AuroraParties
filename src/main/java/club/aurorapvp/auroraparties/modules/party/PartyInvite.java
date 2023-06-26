package club.aurorapvp.auroraparties.modules.party;

import club.aurorapvp.auroraparties.config.Config;
import club.aurorapvp.auroraparties.config.Lang;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.entity.Player;

public class PartyInvite {
  private final static LinkedList<PartyInvite> PARTY_INVITES = new LinkedList<>();
  private final Player partyLeader;
  private final Player invited;
  private final Party party;

  public PartyInvite(Player partyLeader, Player invited) {
    this.partyLeader = partyLeader;
    this.invited = invited;
    this.party = Party.getParty(partyLeader);

    PARTY_INVITES.add(this);

    assert party != null;
    partyLeader.sendMessage(Lang.getComponent("invite-sent"));
    invited.sendMessage(
        Lang.formatComponent("invite-received", partyLeader.getName(), party.getName()));

    PartyInvite thisRequest = this;

    new Timer().schedule(new TimerTask() {
                           @Override
                           public void run() {
                             if (PARTY_INVITES.contains(thisRequest)) {
                               partyLeader.sendMessage(Lang.getComponent("invite-timed-out"));
                               invited.sendMessage(Lang.getComponent("invite-timed-out"));
                               PARTY_INVITES.remove(thisRequest);
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
    PARTY_INVITES.remove(this);

    partyLeader.sendMessage(Lang.getComponent("invite-accepted"));
    invited.sendMessage(Lang.getComponent("invite-accepted"));
  }

  public void deny() {
    PARTY_INVITES.remove(this);

    partyLeader.sendMessage(Lang.getComponent("invite-denied"));
    invited.sendMessage(Lang.getComponent("invite-denied"));
  }

  public static PartyInvite getInvite(Player p) {
    for (PartyInvite invite : PARTY_INVITES) {
      if (invite.getInvited() == p) {
        return invite;
      }
    }
    return null;
  }
}
