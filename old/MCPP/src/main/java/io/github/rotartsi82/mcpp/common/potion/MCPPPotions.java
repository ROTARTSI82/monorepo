package io.github.rotartsi82.mcpp.common.potion;

import io.github.rotartsi82.mcpp.common.MCPP;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MCPPPotions {
    public static final DeferredRegister<Potion> REGISTER = new DeferredRegister<>(ForgeRegistries.POTION_TYPES, MCPP.MODID);
    public static final RegistryObject<Potion> BASE_LEVITATION = REGISTER.register("base_levitation", () -> new Potion(new EffectInstance(Effects.LEVITATION, 900)));
    public static final RegistryObject<Potion> LONG_LEVITATION = REGISTER.register("long_levitation", () -> new Potion(new EffectInstance(Effects.LEVITATION, 1800)));
    public static final RegistryObject<Potion> STRONG_LEVITATION = REGISTER.register("strong_levitation", () -> new Potion(new EffectInstance(Effects.LEVITATION, 450, 1)));

}
