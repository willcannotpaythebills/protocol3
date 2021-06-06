package protocol3.events;

import net.md_5.bungee.api.chat.TextComponent;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import protocol3.backend.Config;
import protocol3.backend.ItemCheck;
import protocol3.backend.PlayerMeta;
import protocol3.commands.Admin;

import java.util.Arrays;
import java.util.Random;

public class ItemCheckTriggers implements Listener {

	static Material[] lagItems = { Material.REDSTONE, Material.REDSTONE_BLOCK, Material.ARMOR_STAND,
			Material.STICKY_PISTON, Material.PISTON, Material.REDSTONE_WALL_TORCH, Material.COMPARATOR,
			Material.REDSTONE_WIRE, Material.REPEATER, Material.OBSERVER, Material.LEVER, Material.TRIPWIRE_HOOK, Material.DISPENSER, Material.DROPPER };

	static Random r = new Random();

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		
		// No roof placement
		if(e.getBlock().getLocation().getY() > 127 && e.getBlock().getLocation().getWorld().getName().equals("world_nether") 
				&& Config.getValue("movement.block.roof").equals("true")) {
			e.setCancelled(true);
			return;
		}
		
		// Exempt ender portal frames; they are illegal but this event
		// gets
		// triggered when player adds eye of ender to portal to fire it.
		if (e.getItemInHand().getType().equals(Material.ENDER_EYE))
			return;

		if (PlayerMeta.isLagfag(e.getPlayer())) {
			Arrays.stream(lagItems).filter(m -> e.getBlock().getType().equals(m)).forEach(m -> e.setCancelled(true));

			int randomNumber = r.nextInt(9);

			if (randomNumber == 5 || randomNumber == 6) {
				e.getPlayer().spigot().sendMessage(new TextComponent("§cThis is what you get for being a lagfag!"));
				e.setCancelled(true);
				return;
			}
		}
		
		for(Material m : Move.ChunkbanItems) {
			if(e.getBlock().getType().equals(m)) {
				int lagCount = 0;
				Chunk c = e.getBlock().getLocation().getChunk();
				int X = c.getX() * 16;
				int Z = c.getZ() * 16;
				for (int x = 0; x < 16; x++)
				{
					for (int z = 0; z < 16; z++)
					{
						for (int y = 0; y < 256; y++)
						{
							Block block = e.getBlock().getWorld().getBlockAt(X + x, y, Z + z);
							for(Material mat : Move.ChunkbanItems) {
								if(block.getType().equals(mat)) {
									if(lagCount == 1024) {
										block.getLocation().getWorld().dropItem(block.getLocation(), new ItemStack(block.getType(),1));
										block.setType(Material.AIR);
										break;
									}
									lagCount++;
								}
							}
						}
					}
				}
			}
		}
		
		for(Material m : lagItems) {
			if(e.getBlock().getType().equals(m)) {
				for(Player p : Bukkit.getOnlinePlayers()) {
					if(Admin.LagMachineNotifs.contains(p.getUniqueId()) && !Admin.LagMachineNotifsExcept.contains(p.getUniqueId())) {
						String word = WordUtils.capitalizeFully(e.getBlock().getType().toString().replaceAll("_", " "));
						Location loc = e.getBlock().getLocation();
						String coords = loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();
						p.spigot().sendMessage(new TextComponent("§cLaggy Block - "+e.getPlayer().getName()+" - " + word + " - [" + coords + "]"));
					}
				}
			}
		}

		if (Config.getValue("place.illegal").equals("true")) {
			if (Config.getValue("place.illegal.ops").equals("false") && e.getPlayer().isOp()) {
				return;
			}

			// Check if item isn't placeable

			ItemCheck.Banned.stream().filter(m -> e.getBlock().getType().equals(m)).forEach(m -> {
				e.setCancelled(true);
				ItemCheck.IllegalCheck(e.getItemInHand(), "ILLEGAL_BLOCK_PLACED", e.getPlayer());
				if (Config.getValue("item.illegal.agro").equals("true")) {
					e.getPlayer().getInventory().forEach(itemStack -> ItemCheck.IllegalCheck(itemStack, "ILLEGAL_BLOCK_PLACED_AGGRESSIVE", e.getPlayer()));
				}
			});

		}
	}

	@EventHandler
	public void onDispense(BlockDispenseArmorEvent e) {
		ItemCheck.IllegalCheck(e.getItem(), "DISPENSED_ARMOR", null);
	}

	@EventHandler
	public void onOpenInventory(InventoryOpenEvent event) {
		event.getInventory().forEach(itemStack -> ItemCheck.IllegalCheck(itemStack, "INVENTORY_OPENED", (Player)event.getPlayer()));
	}

	// Prevents hopper exploits.
	@EventHandler
	public void onInventoryMovedItem(InventoryMoveItemEvent event) {
		if (Config.getValue("item.illegal.agro").equals("true")) {
			ItemCheck.IllegalCheck(event.getItem(), "INVENTORY_MOVED_ITEM", null);
			event.getSource().forEach(itemStack -> ItemCheck.IllegalCheck(itemStack, "INVENTORY_MOVED_ITEM_INVENTORY", null));
		}
	}

	@EventHandler
	public void onPickupItem(EntityPickupItemEvent e) {
		if (Config.getValue("item.illegal.agro").equals("true"))
		{
			if (e.getEntityType().equals(EntityType.PLAYER)) {
				Player player = (Player) e.getEntity();
				ItemCheck.IllegalCheck(e.getItem().getItemStack(), "ITEM_PICKUP", player);
				player.getInventory().forEach(itemStack -> ItemCheck.IllegalCheck(itemStack, "ITEM_PICKUP_INVENTORY", null));
			}
			else {
				ItemCheck.IllegalCheck(e.getItem().getItemStack(), "ITEM_PICKUP", null);
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (Config.getValue("item.illegal.agro").equals("true")) {
			ItemCheck.IllegalCheck(e.getCurrentItem(), "INVENTORY_CLICK", (Player)e.getWhoClicked());
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		// No roof breaking
		if(e.getBlock().getLocation().getY() > 127 && e.getBlock().getLocation().getWorld().getName().equals("world_nether")) {
				e.setCancelled(true);
				return;
		}
	}
	
	@EventHandler
	public void onBookEdit(PlayerEditBookEvent e) {
		BookMeta bm = e.getNewBookMeta();
		ItemStack i = new ItemStack(Material.WRITTEN_BOOK,1);
		i.setItemMeta(bm);
		ItemCheck.IllegalCheck(i, "BOOK_EDITED", e.getPlayer());
		e.setNewBookMeta((BookMeta)i.getItemMeta());
	}

}
