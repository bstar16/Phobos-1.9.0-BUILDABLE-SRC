
package me.earth.phobos.features.modules.render;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.BossInfoClient;
import net.minecraft.client.gui.GuiBossOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BossInfo;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class NoRender
extends Module {
    public Setting<Boolean> fire = this.register(new Setting<Boolean>("Fire", Boolean.valueOf(false), "Removes the portal overlay."));
    public Setting<Boolean> portal = this.register(new Setting<Boolean>("Portal", Boolean.valueOf(false), "Removes the portal overlay."));
    public Setting<Boolean> pumpkin = this.register(new Setting<Boolean>("Pumpkin", Boolean.valueOf(false), "Removes the pumpkin overlay."));
    public Setting<Boolean> totemPops = this.register(new Setting<Boolean>("TotemPop", Boolean.valueOf(false), "Removes the Totem overlay."));
    public Setting<Boolean> items = this.register(new Setting<Boolean>("Items", Boolean.valueOf(false), "Removes items on the ground."));
    public Setting<Boolean> nausea = this.register(new Setting<Boolean>("Nausea", Boolean.valueOf(false), "Removes Portal Nausea."));
    public Setting<Boolean> hurtcam = this.register(new Setting<Boolean>("HurtCam", Boolean.valueOf(false), "Removes shaking after taking damage."));
    public Setting<Fog> fog = this.register(new Setting<Fog>("Fog", Fog.NONE, "Removes Fog."));
    public Setting<Boolean> noWeather = this.register(new Setting<Boolean>("Weather", Boolean.valueOf(false), "AntiWeather"));
    public Setting<Boss> boss = this.register(new Setting<Boss>("BossBars", Boss.NONE, "Modifies the bossbars."));
    public Setting<Float> scale = this.register(new Setting<Object>("Scale", Float.valueOf(0.0f), Float.valueOf(0.5f), Float.valueOf(1.0f), v -> this.boss.getValue() == Boss.MINIMIZE || this.boss.getValue() != Boss.STACK, "Scale of the bars."));
    public Setting<Boolean> bats = this.register(new Setting<Boolean>("Bats", Boolean.valueOf(false), "Removes bats."));
    public Setting<NoArmor> noArmor = this.register(new Setting<NoArmor>("NoArmor", NoArmor.NONE, "Doesnt Render Armor on players."));
    public Setting<Boolean> glint = this.register(new Setting<Object>("Glint", Boolean.valueOf(false), v -> this.noArmor.getValue() != NoArmor.NONE));
    public Setting<Skylight> skylight = this.register(new Setting<Skylight>("Skylight", Skylight.NONE));
    public Setting<Boolean> barriers = this.register(new Setting<Boolean>("Barriers", Boolean.valueOf(false), "Barriers"));
    public Setting<Boolean> blocks = this.register(new Setting<Boolean>("Blocks", Boolean.valueOf(false), "Blocks"));
    public Setting<Boolean> advancements = this.register(new Setting<Boolean>("Advancements", false));
    public Setting<Boolean> pigmen = this.register(new Setting<Boolean>("Pigmen", false));
    public Setting<Boolean> timeChange = this.register(new Setting<Boolean>("TimeChange", false));
    public Setting<Integer> time = this.register(new Setting<Object>("Time", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(23000), v -> this.timeChange.getValue()));
    private static NoRender INSTANCE = new NoRender();

    public NoRender() {
        super("NoRender", "Allows you to stop rendering stuff", Module.Category.RENDER, true, false, false);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (this.items.getValue().booleanValue()) {
            NoRender.mc.world.loadedEntityList.stream().filter(EntityItem.class::isInstance).map(EntityItem.class::cast).forEach(Entity::setDead);
        }
        if (this.noWeather.getValue().booleanValue() && NoRender.mc.world.isRaining()) {
            NoRender.mc.world.setRainStrength(0.0f);
        }
        if (this.timeChange.getValue().booleanValue()) {
            NoRender.mc.world.setWorldTime((long)this.time.getValue().intValue());
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketTimeUpdate & this.timeChange.getValue()) {
            event.setCanceled(true);
        }
    }

    public void doVoidFogParticles(int posX, int posY, int posZ) {
        int i = 32;
        Random random = new Random();
        ItemStack itemstack = NoRender.mc.player.getHeldItemMainhand();
        boolean flag = this.barriers.getValue() == false || NoRender.mc.playerController.getCurrentGameType() == GameType.CREATIVE && !itemstack.isEmpty() && itemstack.getItem() == Item.getItemFromBlock((Block)Blocks.BARRIER);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        for (int j = 0; j < 667; ++j) {
            this.showBarrierParticles(posX, posY, posZ, 16, random, flag, blockpos$mutableblockpos);
            this.showBarrierParticles(posX, posY, posZ, 32, random, flag, blockpos$mutableblockpos);
        }
    }

    public void showBarrierParticles(int x, int y, int z, int offset, Random random, boolean holdingBarrier, BlockPos.MutableBlockPos pos) {
        int i = x + NoRender.mc.world.rand.nextInt(offset) - NoRender.mc.world.rand.nextInt(offset);
        int j = y + NoRender.mc.world.rand.nextInt(offset) - NoRender.mc.world.rand.nextInt(offset);
        int k = z + NoRender.mc.world.rand.nextInt(offset) - NoRender.mc.world.rand.nextInt(offset);
        pos.setPos(i, j, k);
        IBlockState iblockstate = NoRender.mc.world.getBlockState((BlockPos)pos);
        iblockstate.getBlock().randomDisplayTick(iblockstate, (World)NoRender.mc.world, (BlockPos)pos, random);
        if (!holdingBarrier && iblockstate.getBlock() == Blocks.BARRIER) {
            NoRender.mc.world.spawnParticle(EnumParticleTypes.BARRIER, (double)((float)i + 0.5f), (double)((float)j + 0.5f), (double)((float)k + 0.5f), 0.0, 0.0, 0.0, new int[0]);
        }
    }

    @SubscribeEvent
    public void onRenderPre(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.BOSSINFO && this.boss.getValue() != Boss.NONE) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderPost(RenderGameOverlayEvent.Post event) {
        block7: {
            block8: {
                if (event.getType() != RenderGameOverlayEvent.ElementType.BOSSINFO || this.boss.getValue() == Boss.NONE) break block7;
                if (this.boss.getValue() != Boss.MINIMIZE) break block8;
                Map map = NoRender.mc.ingameGUI.getBossOverlay().mapBossInfos;
                if (map == null) {
                    return;
                }
                ScaledResolution scaledresolution = new ScaledResolution(mc);
                int i = scaledresolution.getScaledWidth();
                int j = 12;
                for (Map.Entry entry : map.entrySet()) {
                    BossInfoClient info = (BossInfoClient)entry.getValue();
                    String text = info.getName().getFormattedText();
                    int k = (int)((float)i / this.scale.getValue().floatValue() / 2.0f - 91.0f);
                    GL11.glScaled((double)this.scale.getValue().floatValue(), (double)this.scale.getValue().floatValue(), (double)1.0);
                    if (!event.isCanceled()) {
                        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                        mc.getTextureManager().bindTexture(GuiBossOverlay.GUI_BARS_TEXTURES);
                        NoRender.mc.ingameGUI.getBossOverlay().render(k, j, (BossInfo)info);
                        NoRender.mc.fontRenderer.drawStringWithShadow(text, (float)i / this.scale.getValue().floatValue() / 2.0f - (float)(NoRender.mc.fontRenderer.getStringWidth(text) / 2), (float)(j - 9), 0xFFFFFF);
                    }
                    GL11.glScaled((double)(1.0 / (double)this.scale.getValue().floatValue()), (double)(1.0 / (double)this.scale.getValue().floatValue()), (double)1.0);
                    j += 10 + NoRender.mc.fontRenderer.FONT_HEIGHT;
                }
                break block7;
            }
            if (this.boss.getValue() != Boss.STACK) break block7;
            Map map = NoRender.mc.ingameGUI.getBossOverlay().mapBossInfos;
            HashMap to = new HashMap();
            for (Map.Entry entry : map.entrySet()) {
                Pair p;
                String s = ((BossInfoClient)entry.getValue()).getName().getFormattedText();
                if (to.containsKey(s)) {
                    p = (Pair)to.get(s);
                    p = new Pair(p.getKey(), p.getValue() + 1);
                    to.put(s, p);
                    continue;
                }
                p = new Pair(entry.getValue(), 1);
                to.put(s, p);
            }
            ScaledResolution scaledresolution = new ScaledResolution(mc);
            int i = scaledresolution.getScaledWidth();
            int j = 12;
            for (Map.Entry entry : to.entrySet()) {
                String text = (String)entry.getKey();
                BossInfoClient info = (BossInfoClient)((Pair)entry.getValue()).getKey();
                int a = (Integer)((Pair)entry.getValue()).getValue();
                text = text + " x" + a;
                int k = (int)((float)i / this.scale.getValue().floatValue() / 2.0f - 91.0f);
                GL11.glScaled((double)this.scale.getValue().floatValue(), (double)this.scale.getValue().floatValue(), (double)1.0);
                if (!event.isCanceled()) {
                    GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                    mc.getTextureManager().bindTexture(GuiBossOverlay.GUI_BARS_TEXTURES);
                    NoRender.mc.ingameGUI.getBossOverlay().render(k, j, (BossInfo)info);
                    NoRender.mc.fontRenderer.drawStringWithShadow(text, (float)i / this.scale.getValue().floatValue() / 2.0f - (float)(NoRender.mc.fontRenderer.getStringWidth(text) / 2), (float)(j - 9), 0xFFFFFF);
                }
                GL11.glScaled((double)(1.0 / (double)this.scale.getValue().floatValue()), (double)(1.0 / (double)this.scale.getValue().floatValue()), (double)1.0);
                j += 10 + NoRender.mc.fontRenderer.FONT_HEIGHT;
            }
        }
    }

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Pre<?> event) {
        if (this.bats.getValue().booleanValue() && event.getEntity() instanceof EntityBat) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlaySound(PlaySoundAtEntityEvent event) {
        if (this.bats.getValue().booleanValue() && event.getSound().equals(SoundEvents.ENTITY_BAT_AMBIENT) || event.getSound().equals(SoundEvents.ENTITY_BAT_DEATH) || event.getSound().equals(SoundEvents.ENTITY_BAT_HURT) || event.getSound().equals(SoundEvents.ENTITY_BAT_LOOP) || event.getSound().equals(SoundEvents.ENTITY_BAT_TAKEOFF)) {
            event.setVolume(0.0f);
            event.setPitch(0.0f);
            event.setCanceled(true);
        }
    }

    public static NoRender getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoRender();
        }
        return INSTANCE;
    }

    public static class Pair<T, S> {
        private T key;
        private S value;

        public Pair(T key, S value) {
            this.key = key;
            this.value = value;
        }

        public T getKey() {
            return this.key;
        }

        public S getValue() {
            return this.value;
        }

        public void setKey(T key) {
            this.key = key;
        }

        public void setValue(S value) {
            this.value = value;
        }
    }

    public static enum NoArmor {
        NONE,
        ALL,
        HELMET;

    }

    public static enum Boss {
        NONE,
        REMOVE,
        STACK,
        MINIMIZE;

    }

    public static enum Fog {
        NONE,
        AIR,
        NOFOG;

    }

    public static enum Skylight {
        NONE,
        WORLD,
        ENTITY,
        ALL;

    }
}

