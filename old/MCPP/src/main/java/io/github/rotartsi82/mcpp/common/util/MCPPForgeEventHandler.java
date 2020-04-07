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
        clusterBreak(event.getPlayer(), event.getPos(), event.getPlayer().world.getBlockState(event.getPos()),
                EnchantmentHelper.getEnchantmentLevel(MCPPEnchants.VEIN_MINER.get(), event.getPlayer().getHeldItemMainhand()));
    }

    private static void clusterBreak(PlayerEntity player, BlockPos pos, BlockState targetBlock, int lvl) {
        player.addExhaustion(0.125f);

        if (player.getHeldItemMainhand().canHarvestBlock(player.world.getBlockState(pos)) || player.world.getBlockState(pos).canHarvestBlock(player.world, pos, player)) {
            if (player.world.getBlockState(pos) == targetBlock && player.world.getBlockState(pos).canEntityDestroy(player.world, pos, player)) {
                player.world.destroyBlock(pos, true);
                player.getHeldItemMainhand().attemptDamageItem(1, MCPP.rand, null);
            }
        }

        if (lvl > 0) {
            clusterBreak(player, pos.add(1, 0, 0), targetBlock, lvl - 1);
            clusterBreak(player, pos.add(-1, 0, 0), targetBlock, lvl - 1);

            clusterBreak(player, pos.add(0, -1, 0), targetBlock, lvl - 1);
            clusterBreak(player, pos.add(0, 1, 0), targetBlock, lvl - 1);

            clusterBreak(player, pos.add(0, 0, 1), targetBlock, lvl - 1);
            clusterBreak(player, pos.add(0, 0, -1), targetBlock, lvl - 1);

        }
    }

}
