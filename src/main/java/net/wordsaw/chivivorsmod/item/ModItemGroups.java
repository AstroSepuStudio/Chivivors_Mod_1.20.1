package net.wordsaw.chivivorsmod.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.wordsaw.chivivorsmod.ChivivorsMod;

public class ModItemGroups
{
    public static final ItemGroup Chivivors_Group = Registry.register(Registries.ITEM_GROUP,
            new Identifier(ChivivorsMod.MOD_ID, "ruby"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.chivivorstab"))
                    .icon(() -> new ItemStack(ModItems.Hand)).entries((displayContext, entries) -> {
                        entries.add(ModItems.Hand);

                    }).build());

    public static void RegisterItemGroup()
    {
        ChivivorsMod.LOGGER.info("Registering Items Group for " + ChivivorsMod.MOD_ID);
    }
}
