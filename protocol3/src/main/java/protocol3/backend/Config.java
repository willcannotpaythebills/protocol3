package protocol3.backend;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import protocol3.events.Connection;

public class Config {

	private static HashMap<String, String> _values = new HashMap<String, String>();

	public static int version = 28;

	public static String getValue(String key)
	{
		return _values.getOrDefault(key, "false");
	}

	public static void load() throws IOException {
		Files.readAllLines(Paths.get("plugins/protocol3/config.txt")).stream()
				.filter(cases -> !cases.startsWith("//"))
				.filter(cases -> !(cases.length() == 0)).forEach( val ->
					_values.put(val.split("=")[0].trim(), val.split("=")[1].trim()));
	}
	
	public static void reload() throws IOException {
		Config.load();
		
		final String plugin_work_path = "plugins/protocol3/";
		File on_join_announce = new File(plugin_work_path + "onjoin.txt");
		File motd_message_list = new File(plugin_work_path + "motds.txt");
		
		if(on_join_announce.exists()) {
			Connection.doJoinAnnounce = true;
			Connection.joinAnnounceText = String.join("\n", Files.readAllLines(on_join_announce.toPath()));
		}
		else {
			Connection.doJoinAnnounce = false;
		}
		
		Files.readAllLines(motd_message_list.toPath()).forEach(val ->
			Connection.Motds.add(val)
		);
	
	    if(Connection.Motds.size() == 0) { Connection.Motds.add("i dont have any motds :("); }
		
		ProxyFilter.loadWhitelist();
	}
}
