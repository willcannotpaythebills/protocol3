package protocol3.commands;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import protocol3.backend.Config;
import protocol3.backend.PlayerMeta;

// INTERNAL USE ONLY

public class DupeHand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!PlayerMeta.isOp(sender)) {
			Player player = (Player) sender;
			player.kickPlayer("§6get fucked newfag [pog]");
			return true;
		} else {
			int rewardMultiplier = Integer.parseInt(Config.getValue("vote.multiplier"));
			Player player = Bukkit.getPlayer(args[0]);
			ItemStack itemInHand = player.getInventory().getItemInMainHand();
			if (Config.getValue("vote.heal").equals("true")) {
				player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			}
			for (int x = 0; x < rewardMultiplier; x++) {
				ItemStack modItemInHand = itemInHand;
				if (modItemInHand.getItemMeta() != null) {
					if (modItemInHand.getItemMeta().hasLore()) {
						ItemMeta im = modItemInHand.getItemMeta();
						im.setLore(null);
						modItemInHand.setItemMeta(im);
					}
				}
				HashMap<Integer, ItemStack> didntFit = player.getInventory().addItem(modItemInHand);
				if (!didntFit.values().isEmpty()) {
					for (Entry<Integer, ItemStack> entry : didntFit.entrySet()) {
						for (int y = 0; y > entry.getKey(); y++) {
							player.getWorld().dropItem(player.getLocation(), entry.getValue());
						}
					}
				}
			}
			return true;
		}
	}

}
