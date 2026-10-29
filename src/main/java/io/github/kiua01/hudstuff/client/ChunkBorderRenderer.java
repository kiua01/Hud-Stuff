package io.github.kiua01.hudstuff.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

/** Draws vertical lines at the edges of the player's current chunk. */
public final class ChunkBorderRenderer {
    private static final int COLOR = 0xFF00E5FF;
    private static final int Y_MIN = -64;
    private static final int Y_MAX = 320;

    private ChunkBorderRenderer() {}

    public static void render(PoseStack poseStack, MultiBufferSource buffers, float partialTick) {
        if (!HudConfig.enabled || !HudConfig.chunkBorders) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) return;
        if (mc.getDebugOverlay().showDebugScreen()) return;

        BlockPos pos = player.blockPosition();
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        int minX = chunkX << 4;
        int minZ = chunkZ << 4;
        int maxX = minX + 16;
        int maxZ = minZ + 16;

        Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();
        poseStack.pushPose();
        poseStack.translate(-cam.x, -cam.y, -cam.z);

        VertexConsumer buffer = buffers.getBuffer(RenderType.lines());
        Matrix4f matrix = poseStack.last().pose();

        line(buffer, matrix, minX, Y_MIN, minZ, minX, Y_MAX, minZ);
        line(buffer, matrix, maxX, Y_MIN, minZ, maxX, Y_MAX, minZ);
        line(buffer, matrix, minX, Y_MIN, maxZ, minX, Y_MAX, maxZ);
        line(buffer, matrix, maxX, Y_MIN, maxZ, maxX, Y_MAX, maxZ);

        for (int y = 0; y <= 256; y += 16) {
            line(buffer, matrix, minX, y, minZ, maxX, y, minZ);
            line(buffer, matrix, maxX, y, minZ, maxX, y, maxZ);
            line(buffer, matrix, maxX, y, maxZ, minX, y, maxZ);
            line(buffer, matrix, minX, y, maxZ, minX, y, minZ);
        }

        poseStack.popPose();
    }

    private static void line(
        VertexConsumer buffer, Matrix4f matrix,
        float x1, float y1, float z1, float x2, float y2, float z2
    ) {
        float r = ((COLOR >> 16) & 0xFF) / 255f;
        float g = ((COLOR >> 8) & 0xFF) / 255f;
        float b = (COLOR & 0xFF) / 255f;
        float a = ((COLOR >> 24) & 0xFF) / 255f;

        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;
        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len < 1e-6f) return;
        float nx = dx / len;
        float ny = dy / len;
        float nz = dz / len;

        buffer.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a).setNormal(nx, ny, nz);
        buffer.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a).setNormal(nx, ny, nz);
    }
}
