package com.example.examplemod;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scala.Int;
import scala.collection.parallel.ParIterableLike;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mod(modid = ExampleMod.MODID, name = ExampleMod.NAME, version = ExampleMod.VERSION, clientSideOnly = true)
public class ExampleMod {
    public static final String MODID = "examplemod";
    public static final String NAME = "Example Mod";
    public static final String VERSION = "1.0";

    private static final Logger LOGGER = LogManager.getLogger();

    private IForgeRegistry blockRegistry;
    private Set<String> blocksToSearchFor;

    private TelegramBot lithium;
    private long chatId;
    private boolean hasChatId = false;

    private boolean takeScreenshot;
    private boolean sendMsg;
    private String cmd;

    @EventHandler
    public void preInit(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("Startup");
        blockRegistry = GameRegistry.findRegistry(Block.class);

        blocksToSearchFor = new HashSet<>();
        blocksToSearchFor.add("crafting_table");
        blocksToSearchFor.add("anvil");
        blocksToSearchFor.add("armor_stand");
        blocksToSearchFor.add("banner");
        blocksToSearchFor.add("beacon");
        blocksToSearchFor.add("bed");
        blocksToSearchFor.add("black_glazed_terracotta");
        blocksToSearchFor.add("black_shulker_box");
        blocksToSearchFor.add("blue_glazed_terracotta");
        blocksToSearchFor.add("blue_shulker_box");
        blocksToSearchFor.add("brewing_stand");
        blocksToSearchFor.add("brown_glazed_terracotta");
        blocksToSearchFor.add("brown_shulker_box");
        blocksToSearchFor.add("carpet");
        blocksToSearchFor.add("cauldron");
        blocksToSearchFor.add("concrete");
        blocksToSearchFor.add("concrete_powder");
        blocksToSearchFor.add("cyan_glazed_terracotta");
        blocksToSearchFor.add("cyan_shulker_box");
        blocksToSearchFor.add("daylight_detector");
        blocksToSearchFor.add("diamond_block");
        blocksToSearchFor.add("dispenser");
        blocksToSearchFor.add("dropper");
        blocksToSearchFor.add("dragon_egg");
        blocksToSearchFor.add("enchanting_table");
        blocksToSearchFor.add("end_portal_frame");
        blocksToSearchFor.add("ender_chest");
        blocksToSearchFor.add("furnace");
        blocksToSearchFor.add("gold_block");
        blocksToSearchFor.add("gray_glazed_terracotta");
        blocksToSearchFor.add("gray_shulker_box");
        blocksToSearchFor.add("green_glazed_terracotta");
        blocksToSearchFor.add("green_shulker_box");
        blocksToSearchFor.add("hopper");
        blocksToSearchFor.add("heavy_weighted_pressure_plate");
        blocksToSearchFor.add("iron_block");
        blocksToSearchFor.add("iron_door");
        blocksToSearchFor.add("iron_trapdoor");
        blocksToSearchFor.add("item_frame");
        blocksToSearchFor.add("jukebox");
        blocksToSearchFor.add("light_blue_glazed_terracotta");
        blocksToSearchFor.add("light_blue_shulker_box");
        blocksToSearchFor.add("lime_glazed_terracotta");
        blocksToSearchFor.add("lime_shulker_box");
        blocksToSearchFor.add("lit_pumpkin");
        blocksToSearchFor.add("magenta_glazed_terracotta");
        blocksToSearchFor.add("magenta_shulker_box");
        blocksToSearchFor.add("noteblock");
        blocksToSearchFor.add("observer");
        blocksToSearchFor.add("orange_shulker_box");
        blocksToSearchFor.add("painting");
        blocksToSearchFor.add("pink_shulker_box");
        blocksToSearchFor.add("piston");
        blocksToSearchFor.add("purple_shulker_box");
        blocksToSearchFor.add("red_shulker_box");
        blocksToSearchFor.add("redstone_block");
        blocksToSearchFor.add("redstone_lamp");
        blocksToSearchFor.add("repeater");
        blocksToSearchFor.add("sign");
        blocksToSearchFor.add("silver_shulker_box");
        blocksToSearchFor.add("sticky_piston");
        blocksToSearchFor.add("tnt");
        blocksToSearchFor.add("white_shulker_box");
        blocksToSearchFor.add("yellow_shulker_box");
        blocksToSearchFor.add("nether_portal");
        blocksToSearchFor.add("portal");


        lithium = new TelegramBot("YOUR TELEGRAM API KEY HERE");
        lithium.setUpdatesListener(updates -> {
            for (Update update : updates) {

                Message msg = update.message();
                if (msg != null && msg.text() != null) {

                    if (hasChatId) {
                        if (msg.text().startsWith(".")) {
                            sendMsg = true;
                            cmd = msg.text();
                        } else if (msg.text().toLowerCase().equals("pos")) {
                            EntityPlayerSP player = Minecraft.getMinecraft().player;
                            if (player != null)
                                lithium.execute(new SendMessage(chatId, "Player Location: " + ((int) player.posX) + ", " + ((int) player.posY) + ", " + ((int) player.posZ)));
                        } else if (msg.text().toLowerCase().equals("s")) {
                            takeScreenshot = true;
                        }
                    } else {
                        if (msg.text().toLowerCase().equals("observe")) {
                            chatId = msg.chat().id();
                            hasChatId = true;
                            LOGGER.info("Will send updates to " + msg.chat().id().toString());
                            lithium.execute(new SendMessage(chatId, "Will send updates to this chat"));
                        }
                    }
                }
                if (msg != null)
                    LOGGER.info(msg.toString());
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    /*
    Set<Class> knownEvents = new HashSet<>();
    @SubscribeEvent
    public void event(Event event)
    {
        boolean known = false;
        for (Class ke: knownEvents) {
            if(event.getClass() == ke)
            {
                known = true;
                break;
            }
        }
        if(!known)
        {
            knownEvents.add(event.getClass());
            LOGGER.info(event.toString());
        }
    }
*/
    private HashSet<Chunk> chunksToInspect = new HashSet<>();
    private Queue<BaseRequest> executeQueue = new ArrayDeque<>();
    private long lastSend = 0;

    @SubscribeEvent
    public void update(TickEvent.RenderTickEvent tick) {

        if(System.currentTimeMillis() - lastSend > 1000){
            lastSend = System.currentTimeMillis();
            while(executeQueue.size() > 0)
            {
                lithium.execute(executeQueue.poll());
            }
        }

        if (sendMsg) {
            sendMsg = false;
            takeScreenshot = true;
            Minecraft.getMinecraft().player.sendChatMessage(cmd);
        } else if (takeScreenshot) {
            takeScreenshot = false;
            try {
                Minecraft minecraft = Minecraft.getMinecraft();
                BufferedImage image = ScreenShotHelper.createScreenshot(minecraft.displayWidth, minecraft.displayHeight, minecraft.getFramebuffer());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                try {
                    ImageIO.write(image, "jpg", baos);
                    baos.flush();
                    byte[] imageInByte = baos.toByteArray();
                    executeQueue.add(new SendPhoto(chatId, imageInByte));
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        baos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                lithium.execute(new SendMessage(chatId, e.toString()));
            }
        } else if (chunksToInspect.size() > 0) {
            Iterator<Chunk> iter = chunksToInspect.iterator();
            while(iter.hasNext()) {
                Chunk c = chunksToInspect.iterator().next();
                if (!c.isLoaded()) {
                    continue;
                }
                LOGGER.info(c);
                LOGGER.info(c.isEmpty());
                chunksToInspect.remove(c);
                iter = chunksToInspect.iterator();
                if(Math.abs(c.x) < 500 && Math.abs(c.z) < 500)
                    continue;

                int totalFoundBlockCount = 0;

                HashMap<Block, IntVal> foundBlockCount = new HashMap<Block, IntVal>();
                for (int y = 0; y < 256; y++) {
                    for (int z = 0; z < 16; z++) {
                        for (int x = 0; x < 16; x++) {
                            Block b = c.getBlockState(x, y, z).getBlock();
                            if (blocksToSearchFor.contains(b.getRegistryName().getResourcePath())) {
                                totalFoundBlockCount++;
                                try {
                                    IntVal count = foundBlockCount.get(b);
                                    count.increase();
                                } catch (NullPointerException ex) {
                                    foundBlockCount.put(b, new IntVal());
                                }
                            }
                        }
                    }
                }
                if (totalFoundBlockCount == 0) {
                    continue;
                }
                StringBuilder entityNames = new StringBuilder();
                Set<Block> keys = foundBlockCount.keySet();
                for (Block key : keys) {
                    entityNames.append(key.getUnlocalizedName() + ": " + foundBlockCount.get(key).val + "\n");
                }

                ChunkPos cPos = c.getPos();
                String msg = "[" + cPos.getXStart() + ", " + cPos.getZStart() + "] Found "
                        + totalFoundBlockCount + " blocks:\n"
                        + entityNames.toString();

                takeScreenshot = true;

                if (hasChatId) {
                    executeQueue.add(new SendMessage(chatId, msg));
                }
                LOGGER.info(msg);
            }
        }
    }

    @SubscribeEvent
    public void chunkLoad(ChunkEvent.Load e) {
        Chunk c = e.getChunk();
        chunksToInspect.add(c);
    }

    class IntVal {
        int val = 1;

        public void increase() {
            val++;
        }
    }
}
