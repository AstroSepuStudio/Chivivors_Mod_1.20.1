package net.wordsaw.chivivorsmod.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.wordsaw.chivivorsmod.ChivivorsMod;

public class ModItems
{
    public static final Item Hand = RegisterItem("hand", new Item(new FabricItemSettings()));

    private static void AddItemsToIngredientsItemGroup(FabricItemGroupEntries entries)
    {
        entries.add(Hand);
    }

    private  static Item RegisterItem(String name, Item item)
    {
        return Registry.register(Registries.ITEM, new Identifier(ChivivorsMod.MOD_ID, name ), item);
    }

    public  static  void RegisterModItems()
    {
        ChivivorsMod.LOGGER.info("Registering Mod Items for " + ChivivorsMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::AddItemsToIngredientsItemGroup);
    }
}
