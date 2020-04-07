package io.github.rotartsi82.mcpp.common;

import io.github.rotartsi82.mcpp.common.block.MCPPBlocks;
import io.github.rotartsi82.mcpp.common.item.MCPPItems;
import io.github.rotartsi82.mcpp.common.potion.MCPPPotions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("mcpp")
public class MCPP {
    public static final String MODID = "mcpp";
    private static final Logger LOGGER = LogManager.getLogger();

    public MCPP() {
        MCPPItems.REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
        MCPPPotions.REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
        MCPPBlocks.REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());

        MinecraftForge.EVENT_BUS.register(this);
    }
}
