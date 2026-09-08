package net.wisel.client.module.world;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.BlockPos;
import net.minecraft.client.util.math.ChunkPos;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.wisel.client.module.Module;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PrimeChunkFinderModule extends Module {
    private final Map<ChunkPos, Boolean> pistonChunkCache = new HashMap<>();
    private int tickCounter = 0;

    public PrimeChunkFinderModule() {
        super("PrimeChunkFinder", GLFW.GLFW_KEY_H);
        WorldRenderEvents.END.register(this::onWorldRender);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client.world == null || client.player == null) return;
        tickCounter++;
        if (tickCounter % 10 != 0) return;

        pistonChunkCache.clear();
        ChunkPos playerChunk = client.player.getChunkPos();
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                ChunkPos cPos = new ChunkPos(playerChunk.x + x, playerChunk.z + z);
                if (scanChunkForPistons(client, cPos)) pistonChunkCache.put(cPos, true);
            }
        }
    }

    private boolean scanChunkForPistons(MinecraftClient client, ChunkPos cPos) {
        BlockPos start = cPos.getStartPos();
        for (int x = 0; x < 16; x += 2) {
            for (int z = 0; z < 16; z += 2) {
                for (int y = -60; y < 120; y += 4) {
                    Block block = client.world.getBlockState(start.add(x, y, z)).getBlock();
                    if (block instanceof PistonBlock || block instanceof PistonHeadBlock || block == Blocks.MOVING_PISTON) return true;
                }
            }
        }
        return false;
    }

    private void onWorldRender(WorldRenderContext context) {
        if (!isEnabled()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        Vec3d cam = context.camera().getPos();
        MatrixStack matrices = context.matrixStack();
        ChunkPos currentChunk = client.player.getChunkPos();

        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();

        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                int cX = currentChunk.x + x, cZ = currentChunk.z + z;
                ChunkPos checkPos = new ChunkPos(cX, cZ);

                if (pistonChunkCache.getOrDefault(checkPos, false)) {
                    renderChunkBox(matrices, cam, cX, cZ, 0.0f, 0.4f, 1.0f); // Blau
                } else if (isPrimeSlimeChunk(client.world.getSeed(), cX, cZ)) {
                    renderChunkBox(matrices, cam, cX, cZ, 0.0f, 1.0f, 0.0f); // Grün
                }
            }
        }
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private boolean isPrimeSlimeChunk(long seed, int x, int z) {
        Random rnd = new Random(seed + (long)(x * x * 0x4c1906) + (long)(x * 597399) + (long)(z * z * 0x09e1e0) + (long)(z * 357980) ^ 0x3ad8025fL);
        return rnd.nextInt(10) == 0;
    }

    private void renderChunkBox(MatrixStack matrices, Vec3d cam, int cX, int cZ, float r, float g, float b) {
        double minX = (cX * 16) - cam.x, minZ = (cZ * 16) - cam.z;
        double minY = -64 - cam.y, maxY = 320 - cam.y;

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrices.peek().getPositionMatrix(), (float) minX, (float) minY, (float) minZ).color(r, g, b, 0.8f).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), (float) minX, (float) maxY, (float) minZ).color(r, g, b, 0.8f).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), (float) (minX + 16), (float) minY, (float) minZ).color(r, g, b, 0.8f).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), (float) (minX + 16), (float) maxY, (float) minZ).color(r, g, b, 0.8f).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), (float) minX, (float) minY, (float) (minZ + 16)).color(r, g, b, 0.8f).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), (float) minX, (float) maxY, (float) (minZ + 16)).color(r, g, b, 0.8f).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), (float) (minX + 16), (float) minY, (float) (minZ + 16)).color(r, g, b, 0.8f).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), (float) (minX + 16), (float) maxY, (float) (minZ + 16)).color(r, g, b, 0.8f).next();
        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }
}
