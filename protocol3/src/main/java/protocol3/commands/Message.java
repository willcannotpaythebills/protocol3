package protocol3.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.AntiSpam;
import protocol3.backend.PlayerMeta;

// Message

public class Message implements CommandExecutor {

	public static HashMap<UUID, UUID> Replies = new HashMap();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (args.length < 2) {
			sender.spigot().sendMessage(new TextComponent("§cIncorrect syntax. Syntax: /msg [player] [message]"));
			return true;
		}

		String sendName;

		if (sender instanceof Player) {
			Player p = ((Player) sender);
			sendName = p.getName();
		} else {
			sendName = "Console";
		}

		// Get recipient
		final Player recv = Bukkit.getPlayer(args[0]);
		// Name to use [for stealth]
		String recvName = "";
		// Can't send to offline players
		if (recv == null) {
			sender.spigot().sendMessage(new TextComponent("§cPlayer is no longer online."));
			return true;
		}

		if (recvName.equals("")) {
			recvName = recv.getName();
		}

		// Concatenate all messages
		String finalMsg = "";
		int x = 0;
		
		for(String s : args) {
			if(x == 0) { x++; continue; }
			finalMsg += s + " ";
		}
		
		finalMsg = finalMsg.trim();


		Player player = (Player) sender;
		if (PlayerMeta.isMuted(player)) {
			sender.spigot().sendMessage(new TextComponent("§cYou can't send messages."));
			return true;
		}
		if (PlayerMeta.isMuted(recv) || (Admin.MsgToggle.contains(recv.getUniqueId()) && !player.isOp())) {
			sender.spigot().sendMessage(new TextComponent("§cYou can't send messages to this person."));
			return true;
		}
		if(PlayerMeta.isIgnoring(player.getUniqueId(), recv.getUniqueId())) {
			sender.spigot().sendMessage(new TextComponent("§cYou can't send messages to this person."));
			return true;
		}
		if(PlayerMeta.isIgnoring(recv.getUniqueId(), player.getUniqueId())) {
			sender.spigot().sendMessage(new TextComponent("§cYou can't send messages to this person."));
			return true;
		}
		
		if(!AntiSpam.doSendMessage(finalMsg, player)) {
			Bukkit.getLogger().log(Level.INFO, "§4"+player.getName()+" -> "+recv.getName()+": " + finalMsg + " [vl="+AntiSpam.violationLevels.get(player.getUniqueId())+"]");
			return true;
		}
		else {
			Bukkit.getLogger().log(Level.INFO, "§d"+player.getName()+" -> "+recv.getName()+": " + finalMsg);
		}

		// Cycle through online players & if they're an admin with spy enabled, send
		// them a copy of this message
		String finalRecvName = recvName;
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(Admin.Spies.contains(p.getUniqueId())) {
				p.spigot().sendMessage(new TextComponent("§5" + sendName + " to " + finalRecvName + ": " + finalMsg));
			}
		}

		if (!Admin.Spies.contains(recv.getUniqueId())) {
			recv.spigot().sendMessage(new TextComponent("§dfrom " + sendName + ": " + finalMsg));
		}
		if (!Admin.Spies.contains(((Player) sender).getUniqueId())) {
			sender.spigot().sendMessage(new TextComponent("§dto " + recvName + ": " + finalMsg));
		}
		Replies.put(recv.getUniqueId(), ((Player) sender).getUniqueId());
		Replies.put(((Player) sender).getUniqueId(), recv.getUniqueId());

		return true;
	}

}
