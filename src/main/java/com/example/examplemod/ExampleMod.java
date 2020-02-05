package com.example.examplemod;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Mod(modid = ExampleMod.MODID, name = ExampleMod.NAME, version = ExampleMod.VERSION)
public class ExampleMod {
    public static final String MODID = "examplemod";
    public static final String NAME = "Example Mod";
    public static final String VERSION = "1.0";

    @GameRegistry.ObjectHolder("minecraft:mob_spawner")
    public static final Block mob_spawner = null;

    @GameRegistry.ObjectHolder("minecraft:chest")
    public static final Block chest = null;

    private IForgeRegistry blockRegistry;
    private Set<Block> blocksToIgnore;

    private TelegramBot lithium;
    private long chatId;
    private boolean hasChatId = false;


    @EventHandler
    public void preInit(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        blockRegistry = GameRegistry.findRegistry(Block.class);

        blocksToIgnore = new HashSet<>();
        blocksToIgnore.add(mob_spawner);
        blocksToIgnore.add(chest);

        lithium = new TelegramBot("***REMOVED***");
        lithium.setUpdatesListener(updates -> {
            for (Update update : updates) {

                Message msg = update.message();
                if (msg != null && msg.text().equals("observe") && !hasChatId) {
                    chatId = msg.chat().id();
                    hasChatId = true;
                    lithium.execute(new SendMessage(chatId, "Will send updates to this chat"));
                }
                if (msg != null)
                    System.out.println(msg.toString());
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    @SubscribeEvent
    public void chunkLoad(ChunkDataEvent.Save e) {
        Chunk c = e.getChunk();
        Map<BlockPos, TileEntity> tileEntityMap = c.getTileEntityMap();
        Set<Map.Entry<BlockPos, TileEntity>> entrySet = tileEntityMap.entrySet();

        if (entrySet.size() <= 0)
            return;

        Map.Entry<BlockPos, TileEntity> first = entrySet.iterator().next();
        int combinedX = 0,
                combinedY = 0,
                combinedZ = 0;

        StringBuilder entityNames = new StringBuilder();
        for (Map.Entry<BlockPos, TileEntity> entry : entrySet) {
            TileEntity entity = entry.getValue();
            if (blocksToIgnore.contains(entity.getBlockType())) {
                //skip
                continue;
            }

            BlockPos pos = entry.getKey();
            combinedX += pos.getX();
            combinedY += pos.getY();
            combinedZ += pos.getZ();

            entityNames.append(entry.getValue().getBlockType().getLocalizedName() + "\n");
        }
        if(entityNames.length() == 0)
            return;

        int avgX = combinedX / entrySet.size();
        int avgY = combinedY / entrySet.size();
        int avgZ = combinedZ / entrySet.size();

        ChunkPos cPos = c.getPos();

        String msg = "[" + cPos.getXStart() + ", " + cPos.getZStart() + "] Found "
                + entrySet.size() + " tile entities:\n"
                + entityNames.toString();

        if (hasChatId) {
            lithium.execute(new SendMessage(chatId, msg));
        }
        System.out.println(msg);
    }

}
