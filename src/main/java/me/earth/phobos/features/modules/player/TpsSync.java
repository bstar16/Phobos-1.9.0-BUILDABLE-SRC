
package me.earth.phobos.features.modules.player;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;

public class TpsSync
extends Module {
    public Setting<Boolean> mining = this.register(new Setting<Boolean>("Mining", true));
    public Setting<Boolean> attack = this.register(new Setting<Boolean>("Attack", false));
    private static TpsSync INSTANCE = new TpsSync();

    public TpsSync() {
        super("TpsSync", "Syncs your client with the TPS.", Module.Category.PLAYER, true, false, false);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static TpsSync getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TpsSync();
        }
        return INSTANCE;
    }
}

