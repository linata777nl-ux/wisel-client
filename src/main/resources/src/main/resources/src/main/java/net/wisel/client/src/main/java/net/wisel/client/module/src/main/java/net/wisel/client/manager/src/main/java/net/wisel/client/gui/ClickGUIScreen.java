package net.wisel.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.wisel.client.manager.ModuleManager;
import net.wisel.client.module.Module;

public class ClickGUIScreen extends Screen {

    public ClickGUIScreen() { super(Text.literal("Wisel ClickGUI")); }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        int startX = 50, startY = 40, width = 160, height = 22;

        context.drawTextWithShadow(this.textRenderer, "§b§lWisel Client", startX, startY - 15, 0xFFFFFFFF);

        for (Module module : ModuleManager.getModules()) {
            boolean hovered = mouseX >= startX && mouseX <= startX + width && mouseY >= startY && mouseY <= startY + height;
            int bgColor = module.isEnabled() ? (hovered ? 0xC000FF88 : 0x9000AA55) : (hovered ? 0xC0444444 : 0x90222222);
            context.fill(startX, startY, startX + width, startY + height, bgColor);
            context.drawTextWithShadow(this.textRenderer, "§f" + module.getName() + (module.isEnabled() ? " §a[ON]" : " §c[OFF]"), startX + 8, startY + 7, 0xFFFFFFFF);
            startY += height + 4;
        }
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int startX = 50, startY = 40, width = 160, height = 22;
            for (Module module : ModuleManager.getModules()) {
                if (mouseX >= startX && mouseX <= startX + width && mouseY >= startY && mouseY <= startY + height) {
                    module.toggle();
                    return true;
                }
                startY += height + 4;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
