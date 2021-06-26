package protocol3.backend;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import protocol3.Main;
import protocol3.events.Connection;

public class FileManager {

	public static void setup() throws IOException {
		final String plugin_work_path = "plugins/protocol3/";

		// Create initial directory
		File plugin_work_directory = new File(plugin_work_path);
		File donor_code_directory = new File(plugin_work_path + "codes");
		File donor_list = new File(plugin_work_path + "donator.db");
		File muted_user_database = new File(plugin_work_path + "muted.db");
		File protocol3_server_config = new File(plugin_work_path + "config.txt");
		File lagfag_user_database = new File(plugin_work_path + "lagfag.db");
		File playtime_user_database = new File(plugin_work_path + "playtime.db");
		File motd_message_list = new File(plugin_work_path + "motds.txt");
		File uuid_resolution_list = new File(plugin_work_path + "uuid.db");
		File ip_username_list = new File(plugin_work_path + "ip.db");
		File on_join_announce = new File(plugin_work_path + "onjoin.txt");

		//
		if (!plugin_work_directory.exists()) plugin_work_directory.mkdir();
		if (!donor_code_directory.exists()) donor_code_directory.mkdir();
		if (!donor_list.exists()) donor_list.createNewFile();
		if (!muted_user_database.exists()) muted_user_database.createNewFile();
		if (!motd_message_list.exists()) motd_message_list.createNewFile();
		if (!ip_username_list.exists()) ip_username_list.createNewFile();
		
		if(on_join_announce.exists()) {
			Connection.doJoinAnnounce = true;
			Connection.joinAnnounceText = String.join("\n", Files.readAllLines(on_join_announce.toPath()));
		}

		if (!protocol3_server_config.exists()) {
			InputStream protocol3_server_config_template = (Main.class.getResourceAsStream("/config.txt"));
			Files.copy(protocol3_server_config_template, Paths.get(plugin_work_path + "config.txt"));
		}

		Config.load();

		if (Integer.parseInt(Config.getValue("config.version")) < Config.version) {
			protocol3_server_config.delete();
			InputStream protocol3_server_config_template = (Main.class.getResourceAsStream("/config.txt"));
			Files.copy(protocol3_server_config_template, Paths.get(plugin_work_path + "config.txt"));
		}

		if (!lagfag_user_database.exists()) lagfag_user_database.createNewFile();


		if (!playtime_user_database.exists()) playtime_user_database.createNewFile();

		Config.load();

		Files.readAllLines(playtime_user_database.toPath()).forEach(val ->
				PlayerMeta.Playtimes.put(UUID.fromString(val.split(":")[0]), Double.parseDouble(val.split(":")[1]))
		);
		
		Files.readAllLines(uuid_resolution_list.toPath()).forEach(val ->
			PlayerMeta.UUIDResolutions.put(val.split(":")[0], UUID.fromString(val.split(":")[1]))
		);
		
		Files.readAllLines(ip_username_list.toPath()).forEach(val ->
			PlayerMeta.IPResolutions.put(val.split(":")[0], val.split(":")[1])
		);
	

	}
}
