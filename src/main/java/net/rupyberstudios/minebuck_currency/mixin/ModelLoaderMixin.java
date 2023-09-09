package net.rupyberstudios.minebuck_currency.mixin;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.rupyberstudios.minebuck_currency.MinebuckCurrency;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {
    @Shadow
    protected abstract void addModel(ModelIdentifier modelId);

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader;addModel(Lnet/minecraft/client/util/ModelIdentifier;)V", ordinal = 3, shift = At.Shift.AFTER))
    public void add3DModels(BlockColors blockColors, Profiler profiler, Map<Identifier, JsonUnbakedModel> jsonUnbakedModels, Map<Identifier, List<ModelLoader.SourceTrackedData>> blockStates, CallbackInfo ci) {
        this.addModel(new ModelIdentifier(MinebuckCurrency.MOD_ID, "coin_1_3d", "inventory"));
        this.addModel(new ModelIdentifier(MinebuckCurrency.MOD_ID, "coin_2_3d", "inventory"));
        this.addModel(new ModelIdentifier(MinebuckCurrency.MOD_ID, "banknote_5_3d", "inventory"));
        this.addModel(new ModelIdentifier(MinebuckCurrency.MOD_ID, "banknote_10_3d", "inventory"));
        this.addModel(new ModelIdentifier(MinebuckCurrency.MOD_ID, "banknote_20_3d", "inventory"));
        this.addModel(new ModelIdentifier(MinebuckCurrency.MOD_ID, "banknote_50_3d", "inventory"));
        this.addModel(new ModelIdentifier(MinebuckCurrency.MOD_ID, "banknote_100_3d", "inventory"));
        this.addModel(new ModelIdentifier(MinebuckCurrency.MOD_ID, "banknote_200_3d", "inventory"));
        this.addModel(new ModelIdentifier(MinebuckCurrency.MOD_ID, "card_3d", "inventory"));
    }
}