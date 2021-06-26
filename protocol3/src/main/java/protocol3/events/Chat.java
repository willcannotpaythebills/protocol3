package protocol3.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.AntiSpam;
import protocol3.backend.Config;
import protocol3.backend.PlayerMeta;
import protocol3.backend.ServerMeta;
import protocol3.backend.PlayerMeta.MuteType;
import protocol3.commands.Admin;

// Chat Events
// protocol3. ~~DO NOT REDISTRIBUTE!~~ n/a 3/6/2021

public class Chat implements Listener {
	// yes dupehand belongs below, it sends a rude message to non-admins if they try to use it
	private static Set<String> allUserCommands = new HashSet<>(Arrays.asList(
		"about", "admin", "discord", "dupehand", "help", "kill", "kit", "kys", "msg", "r",
		"redeem", "server", "sign", "stats", "suicide", "tdm", "tjm", "tps", "vm", "vote", "w", "ignore"
	));

	@EventHandler
	public void onChat(PlayerChatEvent e) {
		
		// Cancel this event so we can override vanilla chat
		e.setCancelled(true);

		// Don't execute period if the player is muted
		if (PlayerMeta.isMuted(e.getPlayer()) || (PlayerMeta.MuteAll && !e.getPlayer().isOp()))
			return;

		// -- CREATE PROPERTIES -- //

		// Chat color to send the message with.
		String color;
		// The final edited message.
		String finalMessage = e.getMessage();
		// If we should send the final message.
		boolean doSend = true;
		// The username color.
		String usernameColor;

		// -- SET CHAT COLORS -- //

		switch (e.getMessage().charAt(0)) {
			case '>':
				color = "§a"; // Greentext
				break;
			case '$':
				if (PlayerMeta.isDonator(e.getPlayer())) {
					color = "§6"; // Donator text
					break;
				}
			default:
				color = "§f"; // Normal text
				break;
		}

		if (PlayerMeta.isDonator(e.getPlayer()) && !Admin.UseRedName.contains(e.getPlayer().getUniqueId())) {
			usernameColor = "§6";
		} else if (Admin.UseRedName.contains(e.getPlayer().getUniqueId())) {
			usernameColor = "§c";
		} else {
			usernameColor = "§f";
		}

		// -- STRING MODIFICATION -- //

		// Remove section symbols
		finalMessage = finalMessage.replace('§', ' ');

		// -- CHECKS -- //

		if (isBlank(finalMessage))
			doSend = false;

		if (PlayerMeta.isLagfag(e.getPlayer())) {
			finalMessage = finalMessage + "; i am a registered lagfag";
		}

		// -- SEND FINAL MESSAGE -- //

		if (!doSend) { return; }
		
		String username = e.getPlayer().getName();
		TextComponent finalCom = new TextComponent("§f<" + usernameColor + username + "§f> " + color + finalMessage);
		doSend = AntiSpam.doSendMessage(finalMessage, e.getPlayer());	
		
		if(!doSend) {
			Bukkit.getLogger().log(Level.INFO, "§4<" + username + "> " + finalMessage + " [vl="+AntiSpam.violationLevels.get(e.getPlayer().getUniqueId())+"]");
			return;
		}
		else {
			Bukkit.getLogger().log(Level.INFO, "§f<" + usernameColor + username + "§f> " + color + finalMessage);
		}
			
		for(Player pl : Bukkit.getOnlinePlayers()) {
			if(!PlayerMeta.isIgnoring(pl.getUniqueId(), e.getPlayer().getUniqueId())) {
				pl.spigot().sendMessage(finalCom);
			}
		}
	}
	
	public double similarity(String s1, String s2) {
		    String longer = s1, shorter = s2;
		    if (s1.length() < s2.length()) { // longer should always have greater length
		      longer = s2; shorter = s1;
		    }
		    int longerLength = longer.length();
		    if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
		    /* // If you have Apache Commons Text, you can use it to calculate the edit distance:
		    LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
		    return (longerLength - levenshteinDistance.apply(longer, shorter)) / (double) longerLength; */
		    return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

	}
	
	public int editDistance(String s1, String s2) {
	    s1 = s1.toLowerCase();
	    s2 = s2.toLowerCase();

	    int[] costs = new int[s2.length() + 1];
	    for (int i = 0; i <= s1.length(); i++) {
	      int lastValue = i;
	      for (int j = 0; j <= s2.length(); j++) {
	        if (i == 0)
	          costs[j] = j;
	        else {
	          if (j > 0) {
	            int newValue = costs[j - 1];
	            if (s1.charAt(i - 1) != s2.charAt(j - 1))
	              newValue = Math.min(Math.min(newValue, lastValue),
	                  costs[j]) + 1;
	            costs[j - 1] = lastValue;
	            lastValue = newValue;
	          }
	        }
	      }
	      if (i > 0)
	        costs[s2.length()] = lastValue;
	    }
	    return costs[s2.length()];
	  }

	@EventHandler
	public boolean onCommand(PlayerCommandPreprocessEvent e) {
		if (e.getMessage().split(" ")[0].contains(":") && !e.getPlayer().isOp()) {
			e.getPlayer().spigot().sendMessage(new TextComponent("§cUnknown command."));
			e.setCancelled(true);
		}
		return true;
	}

	@EventHandler
	public void onPlayerTab(PlayerCommandSendEvent e) {
		if (!e.getPlayer().isOp()) {
			e.getCommands().clear();
			e.getCommands().addAll(allUserCommands);
		}
	}

	private boolean isBlank(String check) {
		return check == null || check.isEmpty() || check.length() == 0 || check.trim().isEmpty();
	}
}
