package protocol3.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.PlayerMeta;
import protocol3.backend.Utilities;

// INTERNAL USE ONLY

public class Restart implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!PlayerMeta.isOp(sender)) {
			sender.spigot().sendMessage(new TextComponent("§cYou can't run this."));
			return true;
		}
		if(args.length == 0) {
			Utilities.restart();
		}
		else if(args.length == 1) {
			if(args[0].equalsIgnoreCase("fast")) {
				Utilities.restart();
				return true;
			}
			else if(args[0].equalsIgnoreCase("slow")) {
				Utilities.restart(true);
				return true;
			}
		}
		else {
			String kickMessage = "§6";
			for(int x = 1; x < args.length; x++) {
				kickMessage += args[x] + " ";
			}
			if(args[0].equalsIgnoreCase("fast")) {
				Utilities.restart(false, kickMessage);
				return true;
			}
			else if(args[0].equalsIgnoreCase("slow")) {
				Utilities.restart(true, kickMessage);
				return true;
			}
		}
		Utilities.restart(args.length != 0);
		return true;
	}

}
