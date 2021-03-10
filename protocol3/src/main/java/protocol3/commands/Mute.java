package protocol3.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.PlayerMeta;
import protocol3.backend.PlayerMeta.MuteType;

// Mute somebody. OPs only.

public class Mute implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		String mode = args[0];
		Player player = (Player) sender;

		if (!player.isOp())
		{
			player.sendMessage("§cYou can't use this.");
			return true;
		}

		if (mode.equals("all"))
		{
			PlayerMeta.MuteAll = !PlayerMeta.MuteAll;
			if (PlayerMeta.MuteAll)
			{
				Bukkit.getServer().spigot()
						.broadcast(new TextComponent("§4§l" + player.getName() + " §r§4has silenced the chat."));
			} else
			{
				Bukkit.getServer().spigot()
						.broadcast(new TextComponent("§a§l" + player.getName() + " §r§ahas unsilenced the chat."));
			}
			return true;
		}

		if (args.length != 2)
		{
			player.sendMessage("§cIncorrect syntax. Syntax: /mute [perm/temp/none] [player]");
			return true;
		}
		Player toMute = Bukkit.getPlayer(args[1]);
		if (toMute == null)
		{
			player.sendMessage("§cPlayer is not online.");
			return true;
		}
		if (toMute.isOp())
		{
			player.sendMessage("§cYou can't mute this person.");
			return true;
		}

		if (mode.toUpperCase().equals("PERM"))
		{
			Bukkit.getServer().spigot().broadcast(new TextComponent(
					"§4§l" + player.getName() + " §r§4has permanently muted §4§l" + toMute.getName() + " §r§4."));
			PlayerMeta.setMuteType(toMute, MuteType.PERMANENT);
		} else if (mode.toUpperCase().equals("TEMP"))
		{
			Bukkit.getServer().spigot().broadcast(new TextComponent(
					"§c§l" + player.getName() + " §r§chas temporarily muted §c§l" + toMute.getName() + " §r§c."));
			PlayerMeta.setMuteType(toMute, MuteType.TEMPORARY);
		} else if (mode.toUpperCase().equals("NONE"))
		{
			Bukkit.getServer().spigot().broadcast(new TextComponent(
					"§a§l" + player.getName() + " §r§ahas unmuted §a§l" + toMute.getName() + "§r§a."));
			PlayerMeta.setMuteType(toMute, MuteType.NONE);
		} else
		{
			player.sendMessage("§cIncorrect syntax. Syntax: /mute [perm/temp/none] [player]");
			return true;
		}
		return true;
	}

}
