package protocol3.events;

import com.google.common.base.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import protocol3.backend.Config;

import java.util.List;
import java.util.stream.Collectors;

public class LagPrevention implements Listener
{
	public static int currentWithers = 0;

	public void onEntitySpawn(EntitySpawnEvent e)
	{
		int witherLimit = Integer.parseInt(Config.getValue("wither.limit"));
		if (e.getEntityType().equals(EntityType.WITHER))
		{
			if (e.getEntity().getTicksLived() > 200)
			{
				return;
			}
			if (currentWithers + 1 > witherLimit)
			{
				e.setCancelled(true);
				return;
			}
			currentWithers = getWithers();
		}
	}

	public static int getWithers() {
		String[] worldTypes = new String[]{"world", "world_nether", "world_the_end"};
		int toRet = 0;
		int witherLimit = Integer.parseInt(Config.getValue("wither.limit"));
		List<Entity> entities;
		for (String worldType : worldTypes) {
			entities = Bukkit.getWorld(worldType).getEntities().stream().filter(e -> (e instanceof Wither)).collect(Collectors.toList());
			for (Entity e : entities) {
				if (e.getType().equals(EntityType.WITHER) && e.getCustomName() == null) {
					toRet++;
					if (toRet > witherLimit) {
						toRet--;
						Wither w = (Wither) e;
						w.setHealth(0);
					}
				}
			}
		}
		return toRet;
	}

}
