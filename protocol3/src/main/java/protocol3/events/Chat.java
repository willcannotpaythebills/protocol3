package protocol3.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.PlayerMeta;
import protocol3.commands.Admin;

// Chat Events
// protocol3. ~~DO NOT REDISTRIBUTE!~~ n/a 3/6/2021

public class Chat implements Listener {
	private static Set<String> adminCommands = new HashSet<>(Arrays.asList(
		"mute", "lagfag", "setdonator", "restart", "op", "deop"
	));
	// yes dupehand belongs below, it sends a rude message to non-admins if they try to use it
	private static Set<String> allUserCommands = new HashSet<>(Arrays.asList(
		"about", "admin", "discord", "dupehand", "help", "kill", "kit", "kys", "msg", "r",
		"redeem", "server", "sign", "stats", "suicide", "tdm", "tjm", "tps", "vm", "vote", "w"
	));

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		// Cancel this event so we can override vanilla chat
		e.setCancelled(true);

		// Don't execute period if the player is muted
		if (PlayerMeta.isMuted(e.getPlayer()) || (PlayerMeta.MuteAll && !e.getPlayer().isOp()))
			return;

		// -- CREATE PROPERTIES --

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

		if (doSend) {
			String username = e.getPlayer().getName();

			TextComponent finalCom = new TextComponent(
					"§f<" + usernameColor + username + "§f> " + color + finalMessage);

			Bukkit.getServer().spigot().broadcast(finalCom);

			Bukkit.getLogger().log(Level.INFO, "§f<" + usernameColor + username + "§f> " + color + finalMessage);
		}
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
		e.getCommands().clear();
		e.getCommands().addAll(allUserCommands);
		if (e.getPlayer().isOp())
			e.getCommands().addAll(adminCommands);
	}

	private boolean isBlank(String check) {
		return check == null || check.isEmpty() || check.length() == 0 || check.trim().isEmpty();
	}
}
