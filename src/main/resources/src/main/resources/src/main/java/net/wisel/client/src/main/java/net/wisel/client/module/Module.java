package net.wisel.client.module;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

public abstract class Module {
    private final String name;
    private final KeyBinding keyBinding;
    private boolean enabled;

    public Module(String name, int defaultKey) {
        this.name = name;
        this.enabled = false;
        this.keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.wisel." + name.toLowerCase(),
                InputUtil.Type.KEYSYM,
                defaultKey,
                "category.wisel"
        ));
    }

    public void toggle() {
        this.enabled = !this.enabled;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.literal("§7[§bWisel§7] " + name + " -> " + (enabled ? "§aON" : "§cOFF")), true);
        }
    }

    public abstract void onTick(MinecraftClient client);
    public String getName() { return name; }
    public boolean isEnabled() { return enabled; }
    public KeyBinding getKeyBinding() { return keyBinding; }
}
