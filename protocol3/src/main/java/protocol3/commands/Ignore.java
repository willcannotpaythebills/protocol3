package protocol3.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;

public class Ignore implements CommandExecutor
{

	public static HashMap<UUID, List<UUID>> Ignores = new HashMap<UUID, List<UUID>>();
	
	private Random r = new Random();
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player)sender;
			if(args.length != 1) {
				player.sendMessage(new TextComponent("§cIncorrect syntax. Syntax: /ignore [player]"));
				return true;
			}
			if(Bukkit.getPlayer(args[0]) == null) {
				player.sendMessage(new TextComponent("§cPlayer is not online."));
				return true;
			}
			Player toIgnore = Bukkit.getPlayer(args[0]);
			if(toIgnore.isOp()) {
				player.sendMessage(new TextComponent("§cYou can't ignore this person."));
				return true;
			}
			if(Ignores.containsKey(player.getUniqueId())) {
				List<UUID> existing = Ignores.get(player.getUniqueId());
				if(existing.contains(toIgnore.getUniqueId())) {
					existing.remove(toIgnore.getUniqueId());
					player.sendMessage(new TextComponent("§6No longer ignoring "+toIgnore.getName()+"."));
				}
				else {
					existing.add(toIgnore.getUniqueId());
					player.sendMessage(new TextComponent("§6Now ignoring "+toIgnore.getName()+". This will persist until the server restarts."));
					int rnd = r.nextInt(10);
					if(rnd == 5) {
						
					}
				}
				Ignores.put(player.getUniqueId(), existing);
				return true;
			}
			else {
				List<UUID> ignores = new ArrayList<UUID>();
				ignores.add(toIgnore.getUniqueId());
				Ignores.put(player.getUniqueId(), ignores);
				player.sendMessage(new TextComponent("§6Now ignoring "+toIgnore.getName()+". This will persist until the server restarts."));
				int rnd = r.nextInt(10);
				if(rnd == 5) {
					
				}
				return true;
			}
		}
		else {
			sender.sendMessage(new TextComponent("§cConsole can't run this command."));
			return true;
		}
	}
	
}
