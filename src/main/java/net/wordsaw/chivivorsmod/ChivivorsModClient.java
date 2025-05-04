package net.wordsaw.chivivorsmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.wordsaw.chivivorsmod.talent.TalentTreeData;
import org.lwjgl.glfw.GLFW;

public class ChivivorsModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModKeybinds.OPEN_TALENT_TREE_KEYBIND = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.chivivorsmod.open_talent_tree",
                        GLFW.GLFW_KEY_N,
                        "Chivivors Mod"));

        ClientTickCallback.EVENT.register(client -> {
            while (ModKeybinds.OPEN_TALENT_TREE_KEYBIND.isPressed()) {
                openTalentTree();
            }
        });
    }

    private void openTalentTree() {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player != null) {
            TalentTreeData talentTreeData = ChivivorsMod.PLAYER_TALENT_DATA.get(player.getUuid());
            client.setScreen(talentTreeData.getTalentScreen());
        }
    }
}
