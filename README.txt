This is a **forge mod for minecraft 12.2.**

It will search through every newly loaded chunk for certain blocks indicating player build structures.
If it finds instances of these blocks, it will send a telegram message including a screenshot.

This mod is designed to automate searching for bases on 9b9t. You can let a pathfinder like baritone move the player,
and get notification on your phone, if something of importance is found. No need to stare on that screen for hours anymore.
You can now go and do other stuff, while searching for dupe stashes!


**Blocks it searches for are:**

- crafting_table
- anvil
- armor_stand
- banner
- beacon
- bed
- black_glazed_terracotta
- black_shulker_box
- blue_glazed_terracotta
- blue_shulker_box
- brewing_stand
- brown_glazed_terracotta
- brown_shulker_box
- carpet
- cauldron
- concrete
- concrete_powder
- cyan_glazed_terracotta
- cyan_shulker_box
- daylight_detector
- diamond_block
- dispenser
- dropper
- dragon_egg
- enchanting_table
- end_portal_frame
- ender_chest
- furnace
- gold_block
- gray_glazed_terracotta
- gray_shulker_box
- green_glazed_terracotta
- green_shulker_box
- hopper
- heavy_weighted_pressure_plate
- iron_block
- iron_door
- iron_trapdoor
- item_frame
- jukebox
- light_blue_glazed_terracotta
- light_blue_shulker_box
- lime_glazed_terracotta
- lime_shulker_box
- lit_pumpkin
- magenta_glazed_terracotta
- magenta_shulker_box
- noteblock
- observer
- orange_shulker_box
- painting
- pink_shulker_box
- piston
- purple_shulker_box
- red_shulker_box
- redstone_block
- redstone_lamp
- repeater
- sign
- silver_shulker_box
- sticky_piston
- tnt
- white_shulker_box
- yellow_shulker_box
- nether_portal
- portal


# How to install

Change API key placeholder in /src/main/java/com/example/examplemod/ExampleMod.java

>lithium = new TelegramBot("YOUR TELEGRAM API KEY HERE");


You can get a new API key here: https://core.telegram.org/bots#creating-a-new-bot

Build jar and place it in the mod folder. On windows: %appdata&/.minecraft/mods/1.12.2/
Additionally a jar for telegram is needed: https://github.com/pengrad/java-telegram-bot-api/releases
Place it in the same folder as the jar of this mod.

# How to use
After every start of minecraft, the bot needs to know which chat to write notifications to. Send 
>observe
    
to the bot. If everything works, the bot will reply with:
   >Will send updates to this chat

After this, the bot will send a notification about every block it finds. For example:

>[43488, -159440] Found 16 blocks:
tile.bed: 2
tile.furnace: 1
tile.workbench: 1
tile.woolCarpet: 12

Additional commands for remotely checking minecrafts status are:
>s

The mod will make and send a screenshot in reply.

>pos

The mod will send the current player position.

Every message starting with a "." will execute a chat command. This is useful to remotely give baritone commands.
For example, send:
>.b goto 43488 -159440

And the mod will pass that chat command on, causing baritone to move to the given coordinates. In reply to a chat command, the mod will send a screenshot.

# Warning
A high screenresulution will cause minecraft to freeze for the few seconds it takes to make and send a screenshot. Leaving minecraft in a smaller window will help with that.

The mod does not remember chunks. If a chunk contains something and gets unloaded, then loaded again, it will send notificaitons again. 

All coordinates given in the notification are from the chunks position in blocks. Not the coordinates of the found blocks themself.
