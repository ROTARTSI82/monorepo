package io.github.rotartsi82.mcpp.common.util;

import io.github.rotartsi82.mcpp.common.MCPP;
import io.github.rotartsi82.mcpp.common.enchant.MCPPEnchants;
import io.github.rotartsi82.mcpp.common.enchant.VeinMinerEnchant;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;

@Mod.EventBusSubscriber(modid = MCPP.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MCPPForgeEventHandler {
    private static final Logger LOGGER = LogManager.getLogger(MCPPForgeEventHandler.class);

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer().isCreative()) {
            return;
        }

        int enchLvl = EnchantmentHelper.getEnchantmentLevel(MCPPEnchants.VEIN_MINER.get(), event.getPlayer().getHeldItemMainhand());
        if (enchLvl > VeinMinerEnchant.MAX_LVL) {
            event.getPlayer().sendMessage(ITextComponent.Serializer.fromJson("{\"text\":\"No stop please don't do that\"}"));
            return;
        }
        if (enchLvl > 0) {
            BlockState target = event.getPlayer().world.getBlockState(event.getPos());
            if (event.getPlayer().canHarvestBlock(event.getPlayer().world.getBlockState(event.getPos()))) {
                veinMine(event.getPlayer(), event.getPos(), event.getPos(), target, new LinkedList<>(), enchLvl);
            }
        }
    }

    private static float getManhattanDistance(BlockPos a, BlockPos b) {
        return Math.abs(b.getX() - a.getX()) + Math.abs(b.getY() - a.getY()) + Math.abs(b.getZ() - a.getZ());
    }

    private static double getSphereDistance(BlockPos a, BlockPos b) {
        return Math.sqrt(Math.pow(b.getX() - a.getX(), 2) + Math.pow(b.getY() - a.getY(), 2) + Math.pow(b.getZ() - a.getZ(), 2));
    }

    private static void veinMine(PlayerEntity player, BlockPos origin, BlockPos pos, BlockState targetBlock, LinkedList<BlockPos> visited, int lvl) {
        if (getSphereDistance(origin, pos) <= lvl && player.world.getBlockState(pos) == targetBlock && !visited.contains(pos)) {
            player.world.destroyBlock(pos, true, player);
            player.getHeldItemMainhand().attemptDamageItem(1, MCPP.rand, null);
            player.addExhaustion(1f);
            visited.add(pos);

            veinMine(player, origin, pos.add(1, 0, 0), targetBlock, visited, lvl);
            veinMine(player, origin, pos.add(-1, 0, 0), targetBlock, visited, lvl);

            veinMine(player, origin, pos.add(0, 1, 0), targetBlock, visited, lvl);
            veinMine(player, origin, pos.add(0, -1, 0), targetBlock, visited, lvl);

            veinMine(player, origin, pos.add(0, 0, 1), targetBlock, visited, lvl);
            veinMine(player, origin, pos.add(0, 0, -1), targetBlock, visited, lvl);
        }
    }
}

