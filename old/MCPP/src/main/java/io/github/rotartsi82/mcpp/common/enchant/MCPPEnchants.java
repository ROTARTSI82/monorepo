package io.github.rotartsi82.mcpp.common.enchant;

import io.github.rotartsi82.mcpp.common.MCPP;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MCPPEnchants {
    public final static DeferredRegister<Enchantment> REGISTER = new DeferredRegister<>(ForgeRegistries.ENCHANTMENTS, MCPP.MODID);

    public final static RegistryObject<Enchantment> VEIN_MINER = REGISTER.register("vein_miner", VeinMinerEnchant::new);
}
