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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.rupyberstudios.minebuck_currency.block.entity.ComputerBlockEntity;
import net.rupyberstudios.minebuck_currency.block.property.ComputerOpenScreen;
import net.rupyberstudios.minebuck_currency.block.property.ComputerOpenScreenProperty;
import net.rupyberstudios.minebuck_currency.item.ModItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ComputerBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty ON = BooleanProperty.of("on");
    public static final ComputerOpenScreenProperty OPEN_SCREEN = ComputerOpenScreenProperty.of("open_screen");
    private static final VoxelShape NORTH_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(0, 0, 3, 16, 16, 7),
            Block.createCuboidShape(1, 0, 7, 15, 1, 14));
    private static final VoxelShape EAST_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(9, 0, 0, 13, 16, 16),
            Block.createCuboidShape(2, 0, 1, 9, 1, 15));
    private static final VoxelShape SOUTH_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(0, 0, 9, 16, 16, 13),
            Block.createCuboidShape(1, 0, 2, 15, 1, 9));
    private static final VoxelShape WEST_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(3, 0, 0, 7, 16, 16),
            Block.createCuboidShape(7, 0, 1, 14, 1, 15));

    public ComputerBlock(Settings settings) {
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
        boolean openScreen = false;
        if(heldItem.getNbt() == null || !heldItem.getNbt().contains("id")) {
            world.setBlockState(pos, state.with(OPEN_SCREEN, ComputerOpenScreen.ACTIVATE_CARD));
            openScreen = true;
        }
        if(openScreen) {
            ExtendedScreenHandlerFactory screenHandlerFactory = (ComputerBlockEntity)world.getBlockEntity(pos);
            if(screenHandlerFactory != null) player.openHandledScreen(screenHandlerFactory);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState getPlacementState(@NotNull ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing()).with(ON, false);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if(state != newState) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof ComputerBlockEntity computerBlockEntity) {
                ItemScatterer.spawn(world, pos, computerBlockEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(@NotNull BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            default -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
        };
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
        return new ComputerBlockEntity(pos, state);
    }
}