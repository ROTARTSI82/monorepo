package io.github.rotartsi82.mcpp.common.util;

import io.github.rotartsi82.mcpp.common.MCPP;
import io.github.rotartsi82.mcpp.common.enchant.MCPPEnchants;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = MCPP.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MCPPForgeEventHandler {
    private static final Logger LOGGER = LogManager.getLogger(MCPPForgeEventHandler.class);

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        int enchLvl = EnchantmentHelper.getEnchantmentLevel(MCPPEnchants.VEIN_MINER.get(), event.getPlayer().getHeldItemMainhand());
        if (enchLvl > 0) {
            BlockState target = event.getPlayer().world.getBlockState(event.getPos());
            breakBlock(event.getPlayer(), event.getPos(), target);
            clusterBreak(event.getPlayer(), event.getPos(), target, enchLvl);
        }
    }

    private static void clusterBreak(PlayerEntity player, BlockPos pos, BlockState targetBlock, int lvl) {
        boolean px = breakBlock(player, pos.add(1, 0, 0), targetBlock);
        boolean nx = breakBlock(player, pos.add(-1, 0, 0), targetBlock);

        boolean py = breakBlock(player, pos.add(0, 1, 0), targetBlock);
        boolean ny = breakBlock(player, pos.add(0, -1, 0), targetBlock);

        boolean pz = breakBlock(player, pos.add(0, 0, 1), targetBlock);
        boolean nz = breakBlock(player, pos.add(0, 0, -1), targetBlock);

        if (lvl > 0) {
            if (px) {
                clusterBreak(player, pos.add(1, 0, 0), targetBlock, lvl - 1);
            }
            if (nx) {
                clusterBreak(player, pos.add(-1, 0, 0), targetBlock, lvl - 1);
            }

            if (py) {
                clusterBreak(player, pos.add(0, 1, 0), targetBlock, lvl - 1);
            }
            if (ny) {
                clusterBreak(player, pos.add(0, -1, 0), targetBlock, lvl - 1);
            }

            if (pz) {
                clusterBreak(player, pos.add(0, 0, 1), targetBlock, lvl - 1);
            }
            if (nz) {
                clusterBreak(player, pos.add(0, 0, -1), targetBlock, lvl - 1);
            }
        }
    }

    private static boolean breakBlock(PlayerEntity player, BlockPos pos, BlockState targetBlock) {
        if (player.world.getBlockState(pos) == targetBlock && player.canHarvestBlock(player.world.getBlockState(pos))) {
            player.world.destroyBlock(pos, true, player);
            player.getHeldItemMainhand().attemptDamageItem(1, MCPP.rand, null);
            player.addExhaustion(0.125f);
            return true;
        }
        return false;
    }
}

