package betterquesting.client;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class BQ_Keybindings {
    public static KeyBinding openQuests;

    public static void RegisterKeys() {
        openQuests = new KeyBinding("key.betterquesting.quests", Keyboard.KEY_GRAVE);

        KeyBindingRegistry.registerKeyBinding(new BQKeyHandler(
                new KeyBinding[]{openQuests},
                new boolean[]{false}
        ));
    }
}
