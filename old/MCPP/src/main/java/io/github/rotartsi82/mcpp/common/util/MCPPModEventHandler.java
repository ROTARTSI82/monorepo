package io.github.rotartsi82.mcpp.common.util;

import io.github.rotartsi82.mcpp.common.MCPP;
import io.github.rotartsi82.mcpp.common.block.MCPPBlocks;
import io.github.rotartsi82.mcpp.common.item.MCPPItems;
import io.github.rotartsi82.mcpp.common.potion.MCPPPotions;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = MCPP.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MCPPModEventHandler {
    private static final Logger LOGGER = LogManager.getLogger(MCPPModEventHandler.class);

    @SubscribeEvent
    public static void onRegisterPotions(RegistryEvent.Register<Potion> event) {
        addStandardBrewSet(Potions.AWKWARD, MCPPPotions.LEVITATION.get(), MCPPPotions.LONG_LEVITATION.get(), MCPPPotions.STRONG_LEVITATION.get(), MCPPItems.ASCENDIUM_DUST.get());
    }

    private static void addPotionRecipeForAllTypes(Potion bottomSlot, Item ingredient, Potion result) {
        BrewingRecipeRegistry.addRecipe(Ingredient.fromStacks(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), bottomSlot)),
                Ingredient.fromItems(ingredient), PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), result));
        BrewingRecipeRegistry.addRecipe(Ingredient.fromStacks(PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), bottomSlot)),
                Ingredient.fromItems(ingredient), PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), result));
        BrewingRecipeRegistry.addRecipe(Ingredient.fromStacks(PotionUtils.addPotionToItemStack(new ItemStack(Items.LINGERING_POTION), bottomSlot)),
                Ingredient.fromItems(ingredient), PotionUtils.addPotionToItemStack(new ItemStack(Items.LINGERING_POTION), result));

        BrewingRecipeRegistry.addRecipe(Ingredient.fromStacks(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), result)),
                Ingredient.fromItems(Items.GUNPOWDER), PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), result));

        BrewingRecipeRegistry.addRecipe(Ingredient.fromStacks(PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), result)),
                Ingredient.fromItems(Items.DRAGON_BREATH), PotionUtils.addPotionToItemStack(new ItemStack(Items.LINGERING_POTION), result));
    }

    private static void addStandardBrewSet(Potion bottomSlot, Potion baselineResult, Potion longResult, Potion strongResult, Item ingredient) {
        addPotionRecipeForAllTypes(bottomSlot, ingredient, baselineResult);

        if (longResult != null) {
            addPotionRecipeForAllTypes(baselineResult, Items.GLOWSTONE_DUST, longResult);
        }

        if (strongResult != null) {
            addPotionRecipeForAllTypes(baselineResult, Items.REDSTONE, strongResult);
        }
    }

    @SubscribeEvent
    public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {

    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        MCPPBlocks.REGISTER.getEntries().forEach((element) -> {
            event.getRegistry().register(new BlockItem(element.get(), new BlockItem.Properties().group(ItemGroup.BUILDING_BLOCKS)).setRegistryName(element.get().getRegistryName()));
        });
    }

    @SubscribeEvent
    public static void addCustomOreGen(FMLLoadCompleteEvent event) {
        for (Biome biome : ForgeRegistries.BIOMES) {
            ConfiguredPlacement config = Placement.COUNT_RANGE.configure(new CountRangeConfig(11, 0, 0, 32));
            biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, MCPPBlocks.RUBY_ORE.get().getDefaultState(), 1)).withPlacement(config));
        }
    }
}
