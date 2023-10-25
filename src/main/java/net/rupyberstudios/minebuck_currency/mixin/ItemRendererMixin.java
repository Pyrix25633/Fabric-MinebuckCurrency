package net.rupyberstudios.minebuck_currency.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.rupyberstudios.minebuck_currency.MinebuckCurrency;
import net.rupyberstudios.minebuck_currency.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @ModifyVariable(method = "renderItem", at = @At(value = "HEAD"), argsOnly = true)
    public BakedModel use3DModel(BakedModel value, ItemStack stack, ModelTransformationMode renderMode,
                                 boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                 int light, int overlay) {
        if(renderMode != ModelTransformationMode.GUI) {
            if(stack.isOf(ModItems.COIN_1)) {
                return ((ItemRendererAccessor)this).getModels().getModelManager()
                        .getModel(new ModelIdentifier(MinebuckCurrency.MOD_ID, "coin_1_3d", "inventory"));
            }
            if(stack.isOf(ModItems.COIN_2)) {
                return ((ItemRendererAccessor)this).getModels().getModelManager()
                        .getModel(new ModelIdentifier(MinebuckCurrency.MOD_ID, "coin_2_3d", "inventory"));
            }
            if(stack.isOf(ModItems.BANKNOTE_5)) {
                return ((ItemRendererAccessor)this).getModels().getModelManager()
                        .getModel(new ModelIdentifier(MinebuckCurrency.MOD_ID, "banknote_5_3d", "inventory"));
            }
            if(stack.isOf(ModItems.BANKNOTE_10)) {
                return ((ItemRendererAccessor)this).getModels().getModelManager()
                        .getModel(new ModelIdentifier(MinebuckCurrency.MOD_ID, "banknote_10_3d", "inventory"));
            }
            if(stack.isOf(ModItems.BANKNOTE_20)) {
                return ((ItemRendererAccessor)this).getModels().getModelManager()
                        .getModel(new ModelIdentifier(MinebuckCurrency.MOD_ID, "banknote_20_3d", "inventory"));
            }
            if(stack.isOf(ModItems.BANKNOTE_50)) {
                return ((ItemRendererAccessor)this).getModels().getModelManager()
                        .getModel(new ModelIdentifier(MinebuckCurrency.MOD_ID, "banknote_50_3d", "inventory"));
            }
            if(stack.isOf(ModItems.BANKNOTE_100)) {
                return ((ItemRendererAccessor)this).getModels().getModelManager()
                        .getModel(new ModelIdentifier(MinebuckCurrency.MOD_ID, "banknote_100_3d", "inventory"));
            }
            if(stack.isOf(ModItems.BANKNOTE_200)) {
                return ((ItemRendererAccessor)this).getModels().getModelManager()
                        .getModel(new ModelIdentifier(MinebuckCurrency.MOD_ID, "banknote_200_3d", "inventory"));
            }
            if(stack.isOf(ModItems.CARD)) {
                return ((ItemRendererAccessor)this).getModels().getModelManager()
                        .getModel(new ModelIdentifier(MinebuckCurrency.MOD_ID, "card_3d", "inventory"));
            }
        }
        return value;
    }
}