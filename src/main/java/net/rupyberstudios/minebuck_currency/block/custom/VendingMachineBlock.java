package net.rupyberstudios.minebuck_currency.block.custom;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.rupyberstudios.minebuck_currency.block.entity.VendingMachineBlockEntity;
import net.rupyberstudios.minebuck_currency.block.property.VendingMachineOpenScreen;
import net.rupyberstudios.minebuck_currency.block.property.VendingMachineOpenScreenProperty;
import net.rupyberstudios.minebuck_currency.item.ModItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VendingMachineBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty ON = BooleanProperty.of("on");
    public static final VendingMachineOpenScreenProperty OPEN_SCREEN = VendingMachineOpenScreenProperty.of("open_screen");

    public VendingMachineBlock(Settings settings) {
        super(settings);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, @NotNull World world, BlockPos pos, @NotNull PlayerEntity player,
                              Hand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getStackInHand(hand);
        if(world.isClient || heldItem.getItem() != ModItems.CARD || state.get(ON))
            return ActionResult.FAIL;
        state = state.with(ON, true);
        VendingMachineOpenScreen screen = (heldItem.getNbt() == null || !heldItem.getNbt().contains("id")) ?
                VendingMachineOpenScreen.VENDING_MACHINE : VendingMachineOpenScreen.CONFIG;
        world.setBlockState(pos, state.with(OPEN_SCREEN, screen));
        ExtendedScreenHandlerFactory screenHandlerFactory = (VendingMachineBlockEntity)world.getBlockEntity(pos);
        if(screenHandlerFactory != null) player.openHandledScreen(screenHandlerFactory);
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState getPlacementState(@NotNull ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing()).with(ON, false)
                .with(OPEN_SCREEN, VendingMachineOpenScreen.OFF);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if(state != newState) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof VendingMachineBlockEntity vendingMachineBlockEntity) {
                ItemScatterer.spawn(world, pos, vendingMachineBlockEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected void appendProperties(StateManager.@NotNull Builder<Block, BlockState> builder) {
        builder.add(FACING, ON, OPEN_SCREEN);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(@NotNull BlockState state, @NotNull BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(@NotNull BlockState state, @NotNull BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new VendingMachineBlockEntity(pos, state);
    }
}