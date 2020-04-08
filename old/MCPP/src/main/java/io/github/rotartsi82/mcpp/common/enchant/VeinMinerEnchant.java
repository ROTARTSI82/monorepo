package io.github.rotartsi82.mcpp.common.enchant;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class VeinMinerEnchant extends Enchantment {
    public VeinMinerEnchant() {
        super(Rarity.COMMON, EnchantmentType.DIGGER, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }

    @Override
    public boolean isTreasureEnchantment() {
        return true;
    }

    @Override
    public int getMinEnchantability(int p_77321_1_) {
        return 0;
    }

    @Override
    public int getMaxEnchantability(int p_223551_1_) {
        return 40;
    }
}
