package net.rupyberstudios.minebuck_currency.block.entity.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.rupyberstudios.minebuck_currency.MinebuckCurrency;
import net.rupyberstudios.minebuck_currency.block.custom.ComputerBlock;
import net.rupyberstudios.minebuck_currency.block.entity.ComputerBlockEntity;

public class ComputerBlockEntityRenderer implements BlockEntityRenderer<ComputerBlockEntity> {
    public ComputerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(ComputerBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack itemStack = entity.getRenderStack();
        matrices.push();
        switch(entity.getCachedState().get(ComputerBlock.FACING)) {
            case NORTH -> matrices.translate(0.5F, 0.7F, 0.155F);
            case EAST -> matrices.translate(0.845F, 0.7F, 0.5F);
            case SOUTH -> matrices.translate(0.5F, 0.7F, 0.845F);
            case WEST -> matrices.translate(0.155F, 0.7F, 0.5F);
        }
        switch(entity.getCachedState().get(ComputerBlock.FACING)) {
            case NORTH -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0));
            case EAST -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270));
            case SOUTH -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            case WEST -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
        }
        matrices.scale(0.5F, 0.5F, 0.5F);

        World world = entity.getWorld();
        assert world != null;
        itemRenderer.renderItem(itemStack, ModelTransformationMode.FIXED, getLightLevel(world, entity.getPos()),
                OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, world, 1);
        matrices.pop();
    }

    private int getLightLevel(World world, BlockPos pos) {
        int bLight = world.getLightLevel(LightType.BLOCK, pos);
        int sLight = world.getLightLevel(LightType.SKY, pos);
        return LightmapTextureManager.pack(bLight, sLight);
    }
}