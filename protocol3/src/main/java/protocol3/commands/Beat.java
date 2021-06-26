package protocol3.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.Config;

// funny command haha

public class Beat implements CommandExecutor {

	public static boolean canBeat = true;
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!Bukkit.getOfflinePlayer(UUID.fromString(Config.getValue("funny.beat.uuid"))).isOnline()) {
			sender.spigot().sendMessage(new TextComponent("§cPlayer is not online right now."));
			return true;
		}
		
		if(!canBeat) {
			sender.spigot().sendMessage(new TextComponent("§cPlayer has been beat too recently. Try again later."));
			return true;
		}
		
		Player toBeat = Bukkit.getPlayer(UUID.fromString(Config.getValue("funny.beat.uuid")));
		toBeat.removePotionEffect(PotionEffectType.ABSORPTION);
		toBeat.removePotionEffect(PotionEffectType.HEALTH_BOOST);
		toBeat.removePotionEffect(PotionEffectType.HEAL);
		toBeat.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
		toBeat.removePotionEffect(PotionEffectType.REGENERATION);
		toBeat.damage(Integer.parseInt(Config.getValue("funny.beat.damage")));
		
		String beaterName = "";
		if(sender instanceof ConsoleCommandSender) {
			beaterName = "CONSOLE";
		}
		else {
			Player beater = (Player)sender;
			beater.sendMessage(new TextComponent("§aSuccessfully beat §a§l"+toBeat.getName()));
			beaterName = beater.getName();
		}
		
		toBeat.spigot().sendMessage(new TextComponent("§6§l"+beaterName+"§r§6 just beat you!"));
		
		canBeat = false;
		return true;
	}

}
