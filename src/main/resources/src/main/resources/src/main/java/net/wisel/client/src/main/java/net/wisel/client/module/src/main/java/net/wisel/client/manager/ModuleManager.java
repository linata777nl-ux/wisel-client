package net.wisel.client.manager;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.wisel.client.gui.ClickGUIScreen;
import net.wisel.client.module.Module;
import net.wisel.client.module.world.PrimeChunkFinderModule;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private static final List<Module> modules = new ArrayList<>();
    private static KeyBinding clickGuiKey;

    public static void init() {
        clickGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.wisel.clickgui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.wisel"
        ));

        // Module laden
        modules.add(new PrimeChunkFinderModule());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            while (clickGuiKey.wasPressed()) client.setScreen(new ClickGUIScreen());
            
            for (Module mod : modules) {
                while (mod.getKeyBinding().wasPressed()) mod.toggle();
                if (mod.isEnabled()) mod.onTick(client);
            }
        });
    }
    public static List<Module> getModules() { return modules; }
}
