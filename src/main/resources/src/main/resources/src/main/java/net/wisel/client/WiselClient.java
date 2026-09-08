package net.wisel.client;

import net.fabricmc.api.ClientModInitializer;
import net.wisel.client.manager.ModuleManager;

public class WiselClient implements ClientModInitializer {
    public static final String MOD_ID = "wisel";

    @Override
    public void onInitializeClient() {
        ModuleManager.init();
    }
}
