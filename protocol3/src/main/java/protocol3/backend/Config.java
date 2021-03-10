package protocol3.backend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Config
{

	private static HashMap<String, String> _values = new HashMap<String, String>();

	public static int version = 11;

	public static String getValue(String key)
	{
		return _values.getOrDefault(key, "0");
	}

	public static void load() throws IOException
	{
		for (String val : Files.readAllLines(Paths.get("plugins/protocol3/config.txt")))
		{
			if (val.startsWith("//") || val.length() == 0)
				continue;
			else
				_values.put(val.split("=")[0].trim(), val.split("=")[1].trim());
		}
	}
}
