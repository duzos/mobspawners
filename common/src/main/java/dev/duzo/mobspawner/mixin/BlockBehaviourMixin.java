package dev.duzo.mobspawner.mixin;

import dev.duzo.mobspawner.api.SpawnerData;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {
	@Inject(method="useItemOn", at=@At("HEAD"), cancellable = true)
	private void useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
		if (!(level.getBlockEntity(pos) instanceof SpawnerData be)) return;

		if (be.mobspawner$isForceLoaded()) {
			if (stack.getItem() instanceof SpawnEggItem) return;

			be.mobspawner$setForceLoaded(false);

			level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.NETHERITE_INGOT)));
			level.playSound(null, pos, SoundEvents.CREAKING_HEART_BREAK, SoundSource.BLOCKS);

			cir.setReturnValue(InteractionResult.SUCCESS);
		}

		if (!stack.is(Items.NETHERITE_INGOT)) return;

		be.mobspawner$setForceLoaded(true);

		level.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS);

		cir.setReturnValue(InteractionResult.SUCCESS);
	}
}
