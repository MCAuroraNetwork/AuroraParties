package club.aurorapvp.auroraparties.commands;

import club.aurorapvp.auroraparties.AuroraParties;
import co.aikar.commands.PaperCommandManager;

public class Commands {
  public static PaperCommandManager MANAGER = new PaperCommandManager(AuroraParties.INSTANCE);

  public static void init() {
    MANAGER.registerCommand(new PluginCommands());
    MANAGER.registerCommand(new PartyCommands());

    // TODO command completions
  }
}
