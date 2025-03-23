package dev.duzo.mobspawner.mixin;

import dev.duzo.mobspawner.api.SpawnerData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BaseSpawner.class)
public class BaseSpawnerMixin {
	@Inject(method="isNearPlayer", at=@At("HEAD"), cancellable = true)
	private void isNearPlayer(Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (!(level.getBlockEntity(pos) instanceof SpawnerData be)) return;
		if (!be.mobspawner$isForceLoaded()) return;

		cir.setReturnValue(true);
	}
}
