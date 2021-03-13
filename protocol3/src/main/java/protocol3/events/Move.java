package protocol3.events;

import java.util.*;

import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.Config;
import protocol3.backend.ItemCheck;
import protocol3.backend.PlayerMeta;

public class Move implements Listener
{

	public static HashMap<UUID, Chunk> lastChunks = new HashMap<UUID, Chunk>();

	static Random r = new Random();

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		// This method is actually fired upon head rotate to; if the player's coords did
		// not change,
		// don't fire this event

		if (event.getFrom().getBlockX() == event.getTo().getBlockX()
				&& event.getFrom().getBlockY() == event.getTo().getBlockY()
				&& event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;

		if (event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
			event.getPlayer().setInvulnerable(false);
		}

		// lagfag torture
		if (PlayerMeta.isLagfag(event.getPlayer())) {
			int randomNumber = r.nextInt(9);

			if (randomNumber == 5 || randomNumber == 6) {
				event.getPlayer().spigot().sendMessage(new TextComponent("§cThis is what you get for being a lagfag!"));
				event.setCancelled(true);
				return;
			}

			randomNumber = r.nextInt(250);

			if (randomNumber == 21) {
				event.getPlayer().kickPlayer("§6fuck you lol");
				return;
			}
		}

		Player p = event.getPlayer();
		boolean needsCheck = false;

		// -- ILLEGAL PLACEMENT PATCH -- //

		// Check every chunk the player enters

		if (!lastChunks.containsKey(event.getPlayer().getUniqueId())) {
			lastChunks.put(p.getUniqueId(), p.getLocation().getChunk());
			needsCheck = true;
		} else {
			if (lastChunks.get(p.getUniqueId()) != p.getLocation().getChunk()) {
				lastChunks.put(p.getUniqueId(), p.getLocation().getChunk());
				needsCheck = true;
			}
		}

		if (Config.getValue("movement.block.chunkcheck").equals("false")) needsCheck = false;

		if (needsCheck) {
			boolean containsSpawner = false;
			boolean noPortals = false;
			Chunk c = p.getLocation().getChunk();

			// Portals dont spawn within a 25000 block radius of spawn

			int X = c.getX() * 16;
			int Z = c.getZ() * 16;
			if ((c.getX() >= 25000 || c.getX() <= -25000) || (c.getZ() >= 25000 || c.getZ() <= -25000)) {
				noPortals = true;
			}

			// Create an array of frames because a certain amount of frames are necessary
			// for an end portal
			// If the number of end portals is exactly 12, allow it to exist
			// Also consider silverfish spawners, they don't occur naturally anywhere except
			// for strongholds

			List<Block> frames = new ArrayList();

			// Todo : Read Docs on what the server considers tile entities. From there we can check even quicker at containers and other blocks.
			//Arrays.stream(c.getTileEntities()).filter(tileEntities -> tileEntities instanceof Container)

			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					for (int y = 0; y < 256; y++) {
						Block block = p.getWorld().getBlockAt(X + x, y, Z + z);

						if (Config.getValue("item.illegal.agro").equals("true")) {
							// Containers.
							Arrays.stream(c.getTileEntities()).filter(tileEntities -> tileEntities instanceof Container).forEach(blockState -> ((Container) blockState).getInventory().forEach(itemStack -> ItemCheck.IllegalCheck(itemStack)));
						}

						// Too difficult to anti-illegal the end
						if (block.getWorld().getName().equals("world_the_end")) continue;

						if (block.getType().getHardness() == -1) {

							if (block.getType().equals(Material.PISTON_HEAD)
									|| block.getType().equals(Material.MOVING_PISTON)) continue;

							if (block.getType().equals(Material.NETHER_PORTAL)) continue;

							if (noPortals && (block.getType().equals(Material.END_PORTAL_FRAME)
									|| block.getType().equals(Material.END_GATEWAY)
									|| block.getType().equals(Material.END_PORTAL))) {
								block.setType(Material.AIR);
								continue;
							}

							if (block.getType().equals(Material.BEDROCK) && y <= 4) continue;

							if (block.getType().equals(Material.BEDROCK)
									&& block.getWorld().getName().equals("world_nether") && y >= 123) continue;

							if (block.getType().equals(Material.SPAWNER)) {
								CreatureSpawner cs = ((CreatureSpawner) block.getState());
								if (cs.getSpawnedType().equals(EntityType.SILVERFISH)) {
									containsSpawner = true;
								}
							}

							if (block.getType().equals(Material.END_PORTAL_FRAME)
									|| block.getType().equals(Material.END_PORTAL)) {
								frames.add(block);
								continue;
							}

							block.setType(Material.AIR);
						}

						if (!(block.getType().equals(Material.BEDROCK)) && y == 1) {
							block.setType(Material.BEDROCK);
							continue;
						}

						if (!(block.getType().equals(Material.BEDROCK)) && y == 127
								&& block.getWorld().getName().equals("world_nether")) {
							block.setType(Material.BEDROCK);
							continue;
						}
					}
				}
			}

			// If frames and no spawner, make sure there's exactly 12, then allow it to
			// exist

			if (!frames.isEmpty() && !containsSpawner) {
				frames.forEach(block -> {
					if (!block.getType().equals(Material.END_PORTAL_FRAME)) frames.remove(block);
				});
				if (frames.size() > 12) {
					frames.forEach(block -> block.setType(Material.AIR));
				}
			}
		}

		// -- ROOF AND FLOOR PATCH -- //

		if ((p.getLocation().getY() > 127 && p.getLocation().getWorld().getName().equals("world_nether")
				&& Config.getValue("movement.block.roof").equals("true"))) p.setHealth(0);


		if ((p.getLocation().getY() <= 0 && !p.getLocation().getWorld().getName().equals("world_the_end")
				&& Config.getValue("movement.block.floor").equals("true"))) p.setHealth(0);
	}

}
