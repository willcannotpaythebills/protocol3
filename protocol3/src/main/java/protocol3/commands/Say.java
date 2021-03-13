package protocol3.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import net.md_5.bungee.api.chat.TextComponent;

// OP-only say command.

public class Say implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.isOp() && !(sender instanceof ConsoleCommandSender)) {
			sender.spigot().sendMessage(new TextComponent("§cUnknown command."));
			return true;
		} else {
			String data = "";
			for (String arg : args)
				data += arg + " ";
			data = data.trim();
			data = data.replace("§", "");
			if (data.equals("") || data.equals(" ")) {
				sender.spigot().sendMessage(new TextComponent("§cNo message specified."));
				return true;
			}
			Bukkit.spigot().broadcast(new TextComponent("§d[Server] " + data));
			System.out.println("§d[Server] " + data);
			return true;
		}
	}

}
