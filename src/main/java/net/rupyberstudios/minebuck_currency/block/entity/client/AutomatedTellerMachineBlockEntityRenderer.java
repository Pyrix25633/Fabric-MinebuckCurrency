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
import net.rupyberstudios.minebuck_currency.block.custom.AutomatedTellerMachineBlock;
import net.rupyberstudios.minebuck_currency.block.entity.AutomatedTellerMachineBlockEntity;
import net.rupyberstudios.minebuck_currency.item.ModItems;
import org.jetbrains.annotations.NotNull;

public class AutomatedTellerMachineBlockEntityRenderer implements BlockEntityRenderer<AutomatedTellerMachineBlockEntity> {
    public AutomatedTellerMachineBlockEntityRenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(@NotNull AutomatedTellerMachineBlockEntity entity, float tickDelta, @NotNull MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack itemStack = entity.getRenderStack();
        matrices.push();
        switch(entity.getCachedState().get(AutomatedTellerMachineBlock.FACING)) {
            case NORTH -> matrices.translate(0.078125F, 0.3F, 0.75F);
            case EAST -> matrices.translate(0.25F, 0.3F, 0.078125F);
            case SOUTH -> matrices.translate(0.921875F, 0.3F, 0.25F);
            case WEST -> matrices.translate(0.75F, 0.3F, 0.921875F);
        }
        switch(entity.getCachedState().get(AutomatedTellerMachineBlock.FACING)) {
            case NORTH -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
            case EAST -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0));
            case SOUTH -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270));
            case WEST -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        }
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(270));
        matrices.scale(0.5F, 0.5F, 0.5F);

        World world = entity.getWorld();
        assert world != null;
        itemRenderer.renderItem(itemStack, ModelTransformationMode.FIXED, getLightLevel(world, entity.getPos()),
                OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, world, 1);
        matrices.pop();
    }

    private int getLightLevel(@NotNull World world, BlockPos pos) {
        int bLight = world.getLightLevel(LightType.BLOCK, pos);
        int sLight = world.getLightLevel(LightType.SKY, pos);
        return LightmapTextureManager.pack(bLight, sLight);
    }
}