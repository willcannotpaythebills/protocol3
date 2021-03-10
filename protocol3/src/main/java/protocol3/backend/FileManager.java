package protocol3.backend;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import protocol3.Main;

public class FileManager
{

	public static void setup() throws IOException
	{
		final String path = "plugins/protocol3/";

		// Create initial directory
		File file = new File(path);
		if (!file.exists())
		{
			file.mkdir();
		}

		// Create codes directory
		File codeDir = new File(path + "codes");
		if (!codeDir.exists())
		{
			codeDir.mkdir();
		}

		// Create donator list
		File dlist = new File(path + "donator.db");
		if (!dlist.exists())
		{
			dlist.createNewFile();
		}

		// Create donator codes
		File codes = new File(path + "codes/all.db");
		if (!codes.exists())
		{
			codes.createNewFile();
		}

		// Create used donator codes
		File ucodes = new File(path + "codes/used.db");
		if (!ucodes.exists())
		{
			ucodes.createNewFile();
		}

		// Create permanent mute list
		File mlist = new File(path + "muted.db");
		if (!mlist.exists())
		{
			mlist.createNewFile();
		}

		// Create analytics csv
		File csv = new File(path + "analytics.csv");
		if (!csv.exists())
		{
			csv.createNewFile();
			Files.write(Paths.get(csv.getAbsolutePath()),
					"\"Average Playtime\",\"New Joins\", \"Unique Joins\"\n".getBytes());
		}

		// Write config
		File config = new File(path + "config.txt");
		if (!config.exists())
		{
			InputStream link = (Main.class.getResourceAsStream("/config.txt"));
			Files.copy(link, Paths.get(path + "config.txt"));
		}

		Config.load();

		// Update config if necessary
		if (Integer.parseInt(Config.getValue("config.version")) < Config.version)
		{
			config.delete();
			InputStream link = (Main.class.getResourceAsStream("/config.txt"));
			Files.copy(link, Paths.get(path + "config.txt"));
		}

		// Create lagfag list
		File llist = new File(path + "lagfag.db");
		if (!llist.exists())
		{
			llist.createNewFile();
		}

		// Create lagfag list
		File pt = new File(path + "playtime.db");
		if (!pt.exists())
		{
			pt.createNewFile();
		}

		Config.load();

		// Load donator codes

		for (String val : Files.readAllLines(Paths.get("plugins/protocol3/codes/all.db")))
		{
			PlayerMeta.DonorCodes.add(val.replace("\"", "").trim());
		}

		// Load used donator codes

		for (String val : Files.readAllLines(Paths.get("plugins/protocol3/codes/used.db")))
		{
			PlayerMeta.UsedDonorCodes.add(val);
		}

		// Load playtimes
		for (String val : Files.readAllLines(Paths.get("plugins/protocol3/playtime.db")))
		{
			PlayerMeta.Playtimes.put(UUID.fromString(val.split(":")[0]), Double.parseDouble(val.split(":")[1]));
		}
	}
}
