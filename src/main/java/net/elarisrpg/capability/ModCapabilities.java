package net.elarisrpg.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;


/**
 * Registers our custom capability with Forge.
 */
public class ModCapabilities {

    public static Capability<IPlayerStats> PLAYER_STATS = null;

    public static void register(RegisterCapabilitiesEvent event) {
        // Register the capability interface
        event.register(IPlayerStats.class);
    }

}
