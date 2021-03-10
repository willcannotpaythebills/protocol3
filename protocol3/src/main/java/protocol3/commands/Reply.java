package protocol3.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.PlayerMeta;

public class Reply implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Player p = (Player) sender;

		String sendName = p.getName();

		if (args.length < 1)
		{
			sender.spigot().sendMessage(new TextComponent("§cIncorrect syntax. Syntax: /r [message]"));
			return true;
		}
		if (!Message.Replies.containsKey(p.getUniqueId()))
		{
			sender.spigot().sendMessage(new TextComponent("§cNobody to reply to."));
			return true;
		}
		if (Message.Replies.get(p.getUniqueId()) == null)
		{
			sender.spigot().sendMessage(new TextComponent("§cCan't reply to Console."));
			return true;
		}

		// Get recipient
		Player recv = Bukkit.getPlayer(Message.Replies.get(p.getUniqueId()));
		// Name to use [for stealth]
		String recvName = "";
		// Can't send to offline players
		if (recv == null)
		{
			sender.spigot().sendMessage(new TextComponent("§cPlayer is not online."));
			return true;
		}

		if (recvName.equals(""))
		{
			recvName = recv.getName();
		}

		// Muted players can't send or recieve messages.
		if (PlayerMeta.isMuted(p))
		{
			sender.spigot().sendMessage(new TextComponent("§cYou can't send messages."));
			return true;
		} else if (PlayerMeta.isMuted(recv))
		{
			sender.spigot().sendMessage(new TextComponent("§cYou can't send messages to this person."));
			return true;
		}

		// Concatenate
		String msg = "";
		for (String s : args)
		{
			msg += s + " ";
		}
		msg = msg.trim();

		for (Player pl : Bukkit.getOnlinePlayers())
		{
			if (Admin.Spies.contains(pl.getUniqueId()))
			{
				pl.spigot().sendMessage(new TextComponent("§5" + sendName + " to " + recvName + ": " + msg));
			}
		}

		if (!Admin.Spies.contains(recv.getUniqueId()))
		{
			recv.spigot().sendMessage(new TextComponent("§dfrom " + sendName + ": " + msg));
		}
		if (!Admin.Spies.contains(((Player) sender).getUniqueId()))
		{
			sender.spigot().sendMessage(new TextComponent("§dto " + recvName + ": " + msg));
		}
		Message.Replies.put(recv.getUniqueId(), ((Player) sender).getUniqueId());
		Message.Replies.put(((Player) sender).getUniqueId(), recv.getUniqueId());
		return true;
	}

}
