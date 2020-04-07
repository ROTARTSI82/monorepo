package io.github.rotartsi82.mcpp.common.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class MCPPItemGroup extends ItemGroup {
    public static final MCPPItemGroup INSTANCE = new MCPPItemGroup("MC++");

    public MCPPItemGroup(String label) {
        super(label);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(MCPPItems.ASCENDIUM_DUST.get());
    }
}
