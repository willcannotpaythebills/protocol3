package protocol3.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.entity.Player;

public class Analytics
{

	private static HashMap<UUID, Integer> Playtime = new HashMap<UUID, Integer>();
	private static List<UUID> Joins = new ArrayList<UUID>();
	private static List<UUID> NewJoins = new ArrayList<UUID>();

	public static int avgPlaytime = 0;
	public static int newJoins = 0;
	public static int uniqueJoins = 0;

	public static void registerJoin(Player p, boolean isNewfag)
	{
		if (!Joins.contains(p.getUniqueId()))
		{
			Joins.add(p.getUniqueId());
			uniqueJoins = Joins.size();
		}
		if (!NewJoins.contains(p.getUniqueId()) && isNewfag)
		{
			NewJoins.add(p.getUniqueId());
			newJoins = NewJoins.size();
		}
	}

	public static void registerPlaytime(Player p)
	{
		if (Playtime.containsKey(p.getUniqueId()))
		{
			int pt = Playtime.get(p.getUniqueId()) + 1;
			Playtime.put(p.getUniqueId(), pt);
		} else
		{
			Playtime.put(p.getUniqueId(), 1);
		}
		int total = 0;
		for (Entry<UUID, Integer> entry : Playtime.entrySet())
		{
			total += entry.getValue();
		}
		avgPlaytime = (total / Playtime.entrySet().size());
	}

	public static void clear()
	{
		Playtime = new HashMap<UUID, Integer>();
		Joins = new ArrayList<UUID>();
		NewJoins = new ArrayList<UUID>();
		avgPlaytime = 0;
		newJoins = 0;
		uniqueJoins = 0;
	}
}
