package io.github.rotartsi82.mcpp.common.util;

import io.github.rotartsi82.mcpp.common.MCPP;
import io.github.rotartsi82.mcpp.common.item.MCPPItems;
import io.github.rotartsi82.mcpp.common.potion.MCPPPotions;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = MCPP.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MCPPModEventHandler {
    private static final Logger LOGGER = LogManager.getLogger(MCPPModEventHandler.class);

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event) {
        addStandardBrewSet(Potions.AWKWARD, MCPPPotions.BASE_LEVITATION.get(), MCPPPotions.LONG_LEVITATION.get(), MCPPPotions.STRONG_LEVITATION.get(), MCPPItems.ASCENDIUM_DUST.get());
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
}
