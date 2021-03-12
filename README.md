# protocol3 
Protocol3 is a Java plugin created by d2k11 (gcurtiss) to manage the shit chaos on the avas.cc Minecraft server. Currently tested/running on a Paper 1.16.5 server. 

### Commands  
| Internal Commands | Description |
| ----------------- | ----------- |
| /dupehand         | Copies whatever the player is holding in their hand after they have voted for the server.  |

| Admin Commands | Description |
| ----------------- | ----------- |
| /lagfag cam       | Used for detecting lagfags. sets gamemode of admin to spectator and teleports admin to each online players location for 0.5 secs  |
| /lagfag cancel    | Cancels the iteration through players when /lagfag cam is running |
| /lagfag next      | Cycles to the next online player while /lagfag cam is running |
| /lagfag [player]  | If player is online, displays lagfag message, IP address, and coords. also clears players ender chest, removes bed spawnpoint, and kills the player |
| /mute [perm\|temp\|none] [player] | mutes a player permanently, temporarily, or removes mute |
| /mute all         | Mutes all players |
| /restart slow     | Performs full server restart |
| /restart          | Performs quick restart |
| /say [message]    | Mimics vanilla server 'say' command |
| /setdonator [player] | Sets a player's donator status |

| Player Commands | Description |
| ----------------- | ----------- |
| /about | protocol3 description | 
| /admin | Displays current admins |  
| /discord | Displays discord link |
| /help | Help with additional info about these commands |
| /kill, /kys, /suicide | End it all. |  
| /kit | Troll, kicks player |
| /msg [player] [message] | Messages a player, sends copy to admins if spy is enabled |
| /redeem [code] | Redeems a code after a donation is made, sets donator status |
| /r [message] | Reply to a msg that you already received |
| /server | Displays server stats |
| /sign | Signs the item in you hand with coords, name, time, and verifable hash |
| /stats [player] | Displays a users playtime stats (join date, last login data, rank, and time played) |
| /tjm | toggles join messages |
| /tps | displays the current TPS |
| /vote | displays URL to vote for the server |
| /vm [player] | submits a vote to mute a player for a period of time (1 hour) |

### Installation
1. Install maven
2. Run `mvn package` in protocol3 directory
3. An .jar file will be produced in /target called `p3-3-shaded.jar` 
4. Place this .jar in your plugins directory on your server

