package io.github.rotartsi82.mcpp.common.item;

import io.github.rotartsi82.mcpp.common.MCPP;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MCPPItems {
    public static final DeferredRegister<Item> REGISTER = new DeferredRegister<>(ForgeRegistries.ITEMS, MCPP.MODID);

    public static final RegistryObject<Item> ASCENDIUM_DUST = REGISTER.register("ascendium_dust", () ->
            new Item(new Item.Properties().group(ItemGroup.BREWING).food(
                    new Food.Builder().setAlwaysEdible().saturation(0).hunger(0)
                            .effect(new EffectInstance(Effects.LEVITATION, 200), 100)
                            .effect(new EffectInstance(Effects.POISON, 450, 3), 100)
                            .effect(new EffectInstance(Effects.NAUSEA, 450, 3), 100)
                            .effect(new EffectInstance(Effects.HUNGER, 450, 3), 100)
                            .effect(new EffectInstance(Effects.WEAKNESS, 4000, 1), 100)
                            .effect(new EffectInstance(Effects.MINING_FATIGUE, 4000, 1), 100)
                            .effect(new EffectInstance(Effects.SLOWNESS, 4000, 1), 100)
                            .effect(new EffectInstance(Effects.BLINDNESS, 450, 3), 100)
                            .build())));
}
