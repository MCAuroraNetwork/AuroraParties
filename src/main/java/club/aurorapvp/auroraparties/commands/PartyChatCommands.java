package club.aurorapvp.auroraparties.commands;

import club.aurorapvp.aurorachat.modules.ChatGroup;
import club.aurorapvp.auroraparties.AuroraParties;
import club.aurorapvp.auroraparties.modules.party.Party;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import org.bukkit.entity.Player;

@CommandAlias("partychat")
@CommandPermission("auroraparties.command.partychat")
public class PartyChatCommands extends BaseCommand {

  @Default
  @Description("Enables or disables party chat")
  @SuppressWarnings("unused")
  public void partyChat(Player player) {
    if (!Party.inParty(player)) {
      return;
    }

    Party party = Party.getParty(player);

    assert party != null;

    ChatGroup group = ChatGroup.getChatGroup(player);

    if (group.getMode() == ChatGroup.ChatMode.SELECT && group.getAllowedGroup() == party) {
      group.setMode(ChatGroup.ChatMode.ALL);
      player.sendMessage(AuroraParties.getInstance().getLang().getComponent("party-chat-disabled"));
      return;
    }

    group.setChatTarget(party);
    player.sendMessage(AuroraParties.getInstance().getLang().getComponent("party-chat-enabled"));
  }
}