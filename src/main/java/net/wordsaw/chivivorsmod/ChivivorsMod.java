package net.wordsaw.chivivorsmod;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.wordsaw.chivivorsmod.command.TeamCommand;
import net.wordsaw.chivivorsmod.enchantment.MagicalProtectionEnchantment;
import net.wordsaw.chivivorsmod.item.ModItemGroups;
import net.wordsaw.chivivorsmod.item.ModItems;
import net.wordsaw.chivivorsmod.talent.TalentTreeData;
import net.wordsaw.chivivorsmod.team.TeamData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;

import java.util.HashMap;
import java.util.UUID;

public class ChivivorsMod implements ModInitializer {
	public static final String MOD_ID = "chivivorsmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final HashMap<UUID, TalentTreeData> PLAYER_TALENT_DATA = new HashMap<>();

	public static final Enchantment MAGICAL_PROTECTION = new MagicalProtectionEnchantment();
	public static final Identifier ID_MAGICAL_PROT = new Identifier(MOD_ID, "magical_protection");

	public static final TagKey<DamageType> MAGIC_DAMAGE_TAG = TagKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("minecraft", "magic"));

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register(this::registerCommands);
		ModItemGroups.RegisterItemGroup();
		ModItems.RegisterModItems();

		Registry.register(Registries.ENCHANTMENT, ID_MAGICAL_PROT, MAGICAL_PROTECTION);

		RegisterExperienceBottleCreation();
		registerFriendlyFireCheck();
	}

	private void registerFriendlyFireCheck() {
		ServerLivingEntityEvents.ALLOW_DAMAGE.register((LivingEntity entity, DamageSource source, float amount) -> {
			if (!(entity instanceof ServerPlayerEntity victim)) return false;
			if (!(source.getAttacker() instanceof ServerPlayerEntity attacker)) return false;

			TeamData.Team victimTeam = TeamData.get(victim.getServerWorld()).getPlayerTeam(victim.getUuid());
			TeamData.Team attackerTeam = TeamData.get(attacker.getServerWorld()).getPlayerTeam(attacker.getUuid());
			if (victimTeam == null || attackerTeam == null)
				return true;

			if (victimTeam == attackerTeam) {
                return victimTeam.friendlyFire;
			}

			return true;
		});

	}

	private void registerCommands(
			CommandDispatcher<ServerCommandSource> dispatcher,
			CommandRegistryAccess registryAccess,
			CommandManager.RegistrationEnvironment environment
	) {
		TeamCommand.register(dispatcher);
	}

	private void RegisterExperienceBottleCreation(){
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