package dev.duzo.mobspawner;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class MobSpawnerNeoForge {

    public MobSpawnerNeoForge(IEventBus eventBus) {
        MobSpawnerMod.init();
    }
}