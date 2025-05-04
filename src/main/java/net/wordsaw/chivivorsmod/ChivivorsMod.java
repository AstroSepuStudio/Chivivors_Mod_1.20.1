package net.wordsaw.chivivorsmod;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.wordsaw.chivivorsmod.item.ModItemGroups;
import net.wordsaw.chivivorsmod.item.ModItems;
import net.wordsaw.chivivorsmod.talent.TalentScreen;
import net.wordsaw.chivivorsmod.talent.TalentTreeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class ChivivorsMod implements ModInitializer {
	public static final String MOD_ID = "chivivorsmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final HashMap<UUID, TalentTreeData> PLAYER_TALENT_DATA = new HashMap<>();

	@Override
	public void onInitialize() {
		ModItemGroups.RegisterItemGroup();
		ModItems.RegisterModItems();

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity player = handler.getPlayer();
			TalentScreen talentScreen = new TalentScreen();
			TalentTreeData talentData = new TalentTreeData(player, talentScreen);
			talentScreen.setUp(talentData);

			PLAYER_TALENT_DATA.put(player.getUuid(), talentData);
			LOGGER.info("Initialized talent data for " + player.getEntityName());
		});

		UseItemCallback.EVENT.register((player, world, hand) -> {
			ItemStack stack = player.getStackInHand(hand);

			if (!world.isClient) {
				if (stack.getItem() == Items.GLASS_BOTTLE) {
					HitResult hitResult = player.raycast(5.0D, 0.0F, false);
					if (hitResult.getType() == HitResult.Type.MISS) {
						int xpCost = 10;
						if (player.totalExperience >= xpCost) {
							player.addExperience(-xpCost);
							stack.decrement(1);

							ItemStack xpBottle = new ItemStack(Items.EXPERIENCE_BOTTLE);
							if (!player.getInventory().insertStack(xpBottle)) {
								player.dropItem(xpBottle, false);
							}

							return TypedActionResult.success(player.getStackInHand(hand), true);
						}
					}
				}
			}

			return TypedActionResult.pass(player.getStackInHand(hand));
		});
	}
}