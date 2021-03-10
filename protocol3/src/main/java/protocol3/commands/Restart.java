package protocol3.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.Main;

// INTERNAL USE ONLY

public class Restart implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (sender instanceof Player)
		{
			if (!sender.isOp())
			{
				sender.spigot().sendMessage(new TextComponent("§cYou can't run this."));
				return true;
			}
		}
		if (args.length != 0)
		{
			Main.cleanRestart();
			return true;
		} else
		{
			Main.quickRestart();
			return true;
		}
	}

}
