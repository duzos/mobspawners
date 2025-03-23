package dev.duzo.mobspawner.mixin;

import dev.duzo.mobspawner.Constants;
import dev.duzo.mobspawner.api.SpawnerData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.core.jmx.Server;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpawnerBlockEntity.class)
public class SpawnerBlockEntityMixin implements SpawnerData {
	@Unique private boolean mobspawner$forceLoaded = false;

	@Inject(method="saveAdditional", at=@At("HEAD"))
	private void saveAdditional(CompoundTag data, HolderLookup.Provider provider, CallbackInfo ci) {
		data.putBoolean("mobspawner:forceLoaded", this.mobspawner$isForceLoaded());
	}

	@Inject(method="loadAdditional", at=@At("HEAD"))
	private void loadAdditional(CompoundTag data, HolderLookup.Provider provider, CallbackInfo ci) {
		this.mobspawner$setForceLoaded(data.getBoolean("mobspawner:forceLoaded"));
	}

	@Inject(method = "serverTick", at = @At("HEAD"))
	private static void serverTick(Level level, BlockPos pos, BlockState state, SpawnerBlockEntity be, CallbackInfo ci) {
		if (!(be instanceof SpawnerData data) || !data.mobspawner$isForceLoaded()) return;
		
		ServerLevel sLevel = (ServerLevel) level;

		if (sLevel.getBlockTicks().count() % 120 == 0) {
			ChunkPos chunkPos = new ChunkPos(pos);
			sLevel.setChunkForced(chunkPos.x, chunkPos.z, true);
		}

		// randomly spawn particles
		RandomSource random = sLevel.random;
		if (!random.nextBoolean()) return;

		Vec3 pPos = Vec3.atCenterOf(pos);

		pPos.add(((random.nextInt(0, 100) - 50) / 100f), 0.1, (random.nextInt(0, 100) - 50) / 100f);

		sLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pPos.x, pPos.y, pPos.z, 10, 0.1, 0.1, 0.1, 0.1);
	}

	@Override
	public boolean mobspawner$isForceLoaded() {
		return mobspawner$forceLoaded;
	}

	@Override
	public void mobspawner$setForceLoaded(boolean forceLoaded) {
		mobspawner$forceLoaded = forceLoaded;

		SpawnerBlockEntity be = ((SpawnerBlockEntity) (Object) this);

		if (!be.hasLevel()) return;

		if (be.getLevel().isClientSide()) {
			Constants.LOG.error("This mod is not supposed to be installed on the client side!");
			return;
		}

		ServerLevel level = (ServerLevel) be.getLevel();
		BlockPos pos = be.getBlockPos();
		ChunkPos chunkPos = new ChunkPos(pos);

		level.setChunkForced(chunkPos.x, chunkPos.z, forceLoaded);
	}
}
