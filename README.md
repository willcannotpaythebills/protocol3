# protocol3 
protocol3 is the plugin that runs avas.cc. It manages things like the speed limit, anti-illegals, and commands like vote mute. It also provides quality of life commands and debug information for server administrators.

### Dependencies
At this time, only [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/) and [ArmorEquipEvent](https://www.spigotmc.org/resources/lib-armorequipevent.5478/) is required for the plugin to run correctly.

### Setup
1. Download ProtocolLib and the latest build of Paper for the latest version of Minecraft.
2. Compile protocol3 as shown in the Testing section.
3. Place the protocol3 and ProtocolLib jars into your plugins directory.
4. Launch the server.
Note: For testing illegals and speed limit, you cannot be opped. For testing admin commands, you must be opped.

### Testing (Eclipse)
1. Clone this repo.
2. Use Eclipse to import the repo in a workspace. 
3. Right click pom.xml, and use "Run as -> Maven build.."
4. Type "package" in the Goals box. This will save the package configuration. You can later run it with "Run as -> Maven build" and select the configuration.
5. An .jar file will be produced in /target called `p3-3-shaded.jar` 
6. Place this .jar in your plugins directory on your server.

### Testing (IntelliJ)
TODO i dont use intellij fuck intellij

### Contributing
Please comment any lines that may be unclear to someone who is less experienced with programming, and follow standard Java convention. If you are on Eclipse, please DISABLE auto-formatting on save. It is disabled by default; if you manually enabled it, disable it.

### License
This software is licensed under [GNU GPL v3](https://www.gnu.org/licenses/gpl-3.0.en.html).
