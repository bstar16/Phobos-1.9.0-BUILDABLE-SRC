
package me.earth.phobos.features.modules.client;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.Render2DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.combat.AutoCrystal;
import me.earth.phobos.features.modules.combat.Killaura;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.ColorUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Components
extends Module {
    private static final ResourceLocation box = new ResourceLocation("textures/gui/container/shulker_box.png");
    public static ResourceLocation logo = new ResourceLocation("textures/phobos.png");
    private static final double HALF_PI = 1.5707963267948966;
    private Map<EntityPlayer, Map<Integer, ItemStack>> hotbarMap = new HashMap<EntityPlayer, Map<Integer, ItemStack>>();
    public Setting<Boolean> inventory = this.register(new Setting<Boolean>("Inventory", false));
    public Setting<Integer> invX = this.register(new Setting<Object>("InvX", Integer.valueOf(564), Integer.valueOf(0), Integer.valueOf(1000), v -> this.inventory.getValue()));
    public Setting<Integer> invY = this.register(new Setting<Object>("InvY", Integer.valueOf(467), Integer.valueOf(0), Integer.valueOf(1000), v -> this.inventory.getValue()));
    public Setting<Integer> fineinvX = this.register(new Setting<Object>("InvFineX", Integer.valueOf(0), v -> this.inventory.getValue()));
    public Setting<Integer> fineinvY = this.register(new Setting<Object>("InvFineY", Integer.valueOf(0), v -> this.inventory.getValue()));
    public Setting<Boolean> renderXCarry = this.register(new Setting<Object>("RenderXCarry", Boolean.valueOf(false), v -> this.inventory.getValue()));
    public Setting<Integer> invH = this.register(new Setting<Object>("InvH", Integer.valueOf(3), v -> this.inventory.getValue()));
    public Setting<Boolean> holeHud = this.register(new Setting<Boolean>("HoleHUD", false));
    public Setting<Integer> holeX = this.register(new Setting<Object>("HoleX", Integer.valueOf(279), Integer.valueOf(0), Integer.valueOf(1000), v -> this.holeHud.getValue()));
    public Setting<Integer> holeY = this.register(new Setting<Object>("HoleY", Integer.valueOf(485), Integer.valueOf(0), Integer.valueOf(1000), v -> this.holeHud.getValue()));
    public Setting<Compass> compass = this.register(new Setting<Compass>("Compass", Compass.NONE));
    public Setting<Integer> compassX = this.register(new Setting<Object>("CompX", Integer.valueOf(472), Integer.valueOf(0), Integer.valueOf(1000), v -> this.compass.getValue() != Compass.NONE));
    public Setting<Integer> compassY = this.register(new Setting<Object>("CompY", Integer.valueOf(424), Integer.valueOf(0), Integer.valueOf(1000), v -> this.compass.getValue() != Compass.NONE));
    public Setting<Integer> scale = this.register(new Setting<Object>("Scale", Integer.valueOf(3), Integer.valueOf(0), Integer.valueOf(10), v -> this.compass.getValue() != Compass.NONE));
    public Setting<Boolean> playerViewer = this.register(new Setting<Boolean>("PlayerViewer", false));
    public Setting<Integer> playerViewerX = this.register(new Setting<Object>("PlayerX", Integer.valueOf(752), Integer.valueOf(0), Integer.valueOf(1000), v -> this.playerViewer.getValue()));
    public Setting<Integer> playerViewerY = this.register(new Setting<Object>("PlayerY", Integer.valueOf(497), Integer.valueOf(0), Integer.valueOf(1000), v -> this.playerViewer.getValue()));
    public Setting<Float> playerScale = this.register(new Setting<Object>("PlayerScale", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(2.0f), v -> this.playerViewer.getValue()));
    public Setting<Boolean> imageLogo = this.register(new Setting<Boolean>("ImageLogo", false));
    public Setting<Integer> imageX = this.register(new Setting<Object>("ImageX", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(1000), v -> this.imageLogo.getValue()));
    public Setting<Integer> imageY = this.register(new Setting<Object>("ImageY", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(1000), v -> this.imageLogo.getValue()));
    public Setting<Integer> imageWidth = this.register(new Setting<Object>("ImageWidth", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(1000), v -> this.imageLogo.getValue()));
    public Setting<Integer> imageHeight = this.register(new Setting<Object>("ImageHeight", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(1000), v -> this.imageLogo.getValue()));
    public Setting<Boolean> targetHud = this.register(new Setting<Boolean>("TargetHud", false));
    public Setting<Boolean> targetHudBackground = this.register(new Setting<Object>("TargetHudBackground", Boolean.valueOf(true), v -> this.targetHud.getValue()));
    public Setting<Integer> targetHudX = this.register(new Setting<Object>("TargetHudX", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(1000), v -> this.targetHud.getValue()));
    public Setting<Integer> targetHudY = this.register(new Setting<Object>("TargetHudY", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(1000), v -> this.targetHud.getValue()));
    public Setting<TargetHudDesign> design = this.register(new Setting<Object>("Design", (Object)TargetHudDesign.NORMAL, v -> this.targetHud.getValue()));
    public Setting<Boolean> clock = this.register(new Setting<Boolean>("Clock", true));
    public Setting<Boolean> clockFill = this.register(new Setting<Boolean>("ClockFill", true));
    public Setting<Float> clockX = this.register(new Setting<Object>("ClockX", Float.valueOf(2.0f), Float.valueOf(0.0f), Float.valueOf(1000.0f), v -> this.clock.getValue()));
    public Setting<Float> clockY = this.register(new Setting<Object>("ClockY", Float.valueOf(2.0f), Float.valueOf(0.0f), Float.valueOf(1000.0f), v -> this.clock.getValue()));
    public Setting<Float> clockRadius = this.register(new Setting<Object>("ClockRadius", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(100.0f), v -> this.clock.getValue()));
    public Setting<Float> clockLineWidth = this.register(new Setting<Object>("ClockLineWidth", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(5.0f), v -> this.clock.getValue()));
    public Setting<Integer> clockSlices = this.register(new Setting<Object>("ClockSlices", Integer.valueOf(360), Integer.valueOf(1), Integer.valueOf(720), v -> this.clock.getValue()));
    public Setting<Integer> clockLoops = this.register(new Setting<Object>("ClockLoops", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(720), v -> this.clock.getValue()));

    public Components() {
        super("Components", "HudComponents", Module.Category.CLIENT, false, false, true);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        if (Components.fullNullCheck()) {
            return;
        }
        if (this.playerViewer.getValue().booleanValue()) {
            this.drawPlayer();
        }
        if (this.compass.getValue() != Compass.NONE) {
            this.drawCompass();
        }
        if (this.holeHud.getValue().booleanValue()) {
            this.drawOverlay(event.partialTicks);
        }
        if (this.inventory.getValue().booleanValue()) {
            this.renderInventory();
        }
        if (this.imageLogo.getValue().booleanValue()) {
            this.drawImageLogo();
        }
        if (this.targetHud.getValue().booleanValue()) {
            this.drawTargetHud(event.partialTicks);
        }
        if (this.clock.getValue().booleanValue()) {
            RenderUtil.drawClock(this.clockX.getValue().floatValue(), this.clockY.getValue().floatValue(), this.clockRadius.getValue().floatValue(), this.clockSlices.getValue(), this.clockLoops.getValue(), this.clockLineWidth.getValue().floatValue(), this.clockFill.getValue(), new Color(255, 0, 0, 255));
        }
    }

    public void drawTargetHud(float partialTicks) {
        if (this.design.getValue() == TargetHudDesign.NORMAL) {
            EntityPlayer target = AutoCrystal.target != null ? AutoCrystal.target : (Killaura.target instanceof EntityPlayer ? (EntityPlayer)Killaura.target : Components.getClosestEnemy());
            if (target == null) {
                return;
            }
            if (this.targetHudBackground.getValue().booleanValue()) {
                RenderUtil.drawRectangleCorrectly(this.targetHudX.getValue(), this.targetHudY.getValue(), 210, 100, ColorUtil.toRGBA(20, 20, 20, 160));
            }
            GlStateManager.disableRescaleNormal();
            GlStateManager.setActiveTexture((int)OpenGlHelper.lightmapTexUnit);
            GlStateManager.disableTexture2D();
            GlStateManager.setActiveTexture((int)OpenGlHelper.defaultTexUnit);
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            try {
                GuiInventory.drawEntityOnScreen((int)(this.targetHudX.getValue() + 30), (int)(this.targetHudY.getValue() + 90), (int)45, (float)0.0f, (float)0.0f, (EntityLivingBase)target);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
            this.renderer.drawStringWithShadow(target.getName(), this.targetHudX.getValue() + 60, this.targetHudY.getValue() + 10, ColorUtil.toRGBA(255, 0, 0, 255));
            float health = target.getHealth() + target.getAbsorptionAmount();
            int healthColor = health >= 16.0f ? ColorUtil.toRGBA(0, 255, 0, 255) : (health >= 10.0f ? ColorUtil.toRGBA(255, 255, 0, 255) : ColorUtil.toRGBA(255, 0, 0, 255));
            DecimalFormat df = new DecimalFormat("##.#");
            this.renderer.drawStringWithShadow(df.format(target.getHealth() + target.getAbsorptionAmount()), this.targetHudX.getValue() + 60 + this.renderer.getStringWidth(target.getName() + "  "), this.targetHudY.getValue() + 10, healthColor);
            Integer ping = EntityUtil.isFakePlayer(target) ? 0 : (mc.getConnection().getPlayerInfo(target.getUniqueID()) == null ? 0 : mc.getConnection().getPlayerInfo(target.getUniqueID()).getResponseTime());
            int color = ping >= 100 ? ColorUtil.toRGBA(0, 255, 0, 255) : (ping > 50 ? ColorUtil.toRGBA(255, 255, 0, 255) : ColorUtil.toRGBA(255, 0, 0, 255));
            this.renderer.drawStringWithShadow("Ping: " + (ping == null ? 0 : ping), this.targetHudX.getValue() + 60, this.targetHudY.getValue() + this.renderer.getFontHeight() + 20, color);
            this.renderer.drawStringWithShadow("Pops: " + Phobos.totemPopManager.getTotemPops(target), this.targetHudX.getValue() + 60, this.targetHudY.getValue() + this.renderer.getFontHeight() * 2 + 30, ColorUtil.toRGBA(255, 0, 0, 255));
            GlStateManager.enableTexture2D();
            int iteration = 0;
            int i = this.targetHudX.getValue() + 50;
            int y = this.targetHudY.getValue() + this.renderer.getFontHeight() * 3 + 44;
            for (ItemStack is : target.inventory.armorInventory) {
                ++iteration;
                if (is.isEmpty()) continue;
                int x = i - 90 + (9 - iteration) * 20 + 2;
                GlStateManager.enableDepth();
                RenderUtil.itemRender.zLevel = 200.0f;
                RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);
                RenderUtil.itemRender.renderItemOverlayIntoGUI(Components.mc.fontRenderer, is, x, y, "");
                RenderUtil.itemRender.zLevel = 0.0f;
                GlStateManager.enableTexture2D();
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                String s = is.getCount() > 1 ? is.getCount() + "" : "";
                this.renderer.drawStringWithShadow(s, x + 19 - 2 - this.renderer.getStringWidth(s), y + 9, 0xFFFFFF);
                int dmg = 0;
                int itemDurability = is.getMaxDamage() - is.getItemDamage();
                float green = ((float)is.getMaxDamage() - (float)is.getItemDamage()) / (float)is.getMaxDamage();
                float red = 1.0f - green;
                dmg = 100 - (int)(red * 100.0f);
                this.renderer.drawStringWithShadow(dmg + "", (float)(x + 8) - (float)this.renderer.getStringWidth(dmg + "") / 2.0f, y - 5, ColorUtil.toRGBA((int)(red * 255.0f), (int)(green * 255.0f), 0));
            }
            this.drawOverlay(partialTicks, (Entity)target, this.targetHudX.getValue() + 150, this.targetHudY.getValue() + 6);
            this.renderer.drawStringWithShadow("Strength", this.targetHudX.getValue() + 150, this.targetHudY.getValue() + 60, target.isPotionActive(MobEffects.STRENGTH) ? ColorUtil.toRGBA(0, 255, 0, 255) : ColorUtil.toRGBA(255, 0, 0, 255));
            this.renderer.drawStringWithShadow("Weakness", this.targetHudX.getValue() + 150, this.targetHudY.getValue() + this.renderer.getFontHeight() + 70, target.isPotionActive(MobEffects.WEAKNESS) ? ColorUtil.toRGBA(0, 255, 0, 255) : ColorUtil.toRGBA(255, 0, 0, 255));
        } else if (this.design.getValue() == TargetHudDesign.COMPACT) {
            // empty if block
        }
    }

    @SubscribeEvent
    public void onReceivePacket(PacketEvent.Receive event) {
    }

    public static EntityPlayer getClosestEnemy() {
        EntityPlayer closestPlayer = null;
        for (EntityPlayer player : Components.mc.world.playerEntities) {
            if (player == Components.mc.player || Phobos.friendManager.isFriend(player)) continue;
            if (closestPlayer == null) {
                closestPlayer = player;
                continue;
            }
            if (!(Components.mc.player.getDistanceSq((Entity)player) < Components.mc.player.getDistanceSq((Entity)closestPlayer))) continue;
            closestPlayer = player;
        }
        return closestPlayer;
    }

    public void drawImageLogo() {
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        mc.getTextureManager().bindTexture(logo);
        Components.drawCompleteImage(this.imageX.getValue(), this.imageY.getValue(), this.imageWidth.getValue(), this.imageHeight.getValue());
        mc.getTextureManager().deleteTexture(logo);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
    }

    public void drawCompass() {
        ScaledResolution sr = new ScaledResolution(mc);
        if (this.compass.getValue() == Compass.LINE) {
            float playerYaw = Components.mc.player.rotationYaw;
            float rotationYaw = MathUtil.wrap(playerYaw);
            RenderUtil.drawRect(this.compassX.getValue().intValue(), this.compassY.getValue().intValue(), this.compassX.getValue() + 100, this.compassY.getValue() + this.renderer.getFontHeight(), 1963986960);
            RenderUtil.glScissor(this.compassX.getValue().intValue(), this.compassY.getValue().intValue(), this.compassX.getValue() + 100, this.compassY.getValue() + this.renderer.getFontHeight(), sr);
            GL11.glEnable((int)3089);
            float zeroZeroYaw = MathUtil.wrap((float)(Math.atan2(0.0 - Components.mc.player.posZ, 0.0 - Components.mc.player.posX) * 180.0 / Math.PI) - 90.0f);
            RenderUtil.drawLine((float)this.compassX.getValue().intValue() - rotationYaw + 50.0f + zeroZeroYaw, this.compassY.getValue() + 2, (float)this.compassX.getValue().intValue() - rotationYaw + 50.0f + zeroZeroYaw, this.compassY.getValue() + this.renderer.getFontHeight() - 2, 2.0f, -61424);
            RenderUtil.drawLine((float)this.compassX.getValue().intValue() - rotationYaw + 50.0f + 45.0f, this.compassY.getValue() + 2, (float)this.compassX.getValue().intValue() - rotationYaw + 50.0f + 45.0f, this.compassY.getValue() + this.renderer.getFontHeight() - 2, 2.0f, -1);
            RenderUtil.drawLine((float)this.compassX.getValue().intValue() - rotationYaw + 50.0f - 45.0f, this.compassY.getValue() + 2, (float)this.compassX.getValue().intValue() - rotationYaw + 50.0f - 45.0f, this.compassY.getValue() + this.renderer.getFontHeight() - 2, 2.0f, -1);
            RenderUtil.drawLine((float)this.compassX.getValue().intValue() - rotationYaw + 50.0f + 135.0f, this.compassY.getValue() + 2, (float)this.compassX.getValue().intValue() - rotationYaw + 50.0f + 135.0f, this.compassY.getValue() + this.renderer.getFontHeight() - 2, 2.0f, -1);
            RenderUtil.drawLine((float)this.compassX.getValue().intValue() - rotationYaw + 50.0f - 135.0f, this.compassY.getValue() + 2, (float)this.compassX.getValue().intValue() - rotationYaw + 50.0f - 135.0f, this.compassY.getValue() + this.renderer.getFontHeight() - 2, 2.0f, -1);
            this.renderer.drawStringWithShadow("n", (float)this.compassX.getValue().intValue() - rotationYaw + 50.0f + 180.0f - (float)this.renderer.getStringWidth("n") / 2.0f, this.compassY.getValue().intValue(), -1);
            this.renderer.drawStringWithShadow("n", (float)this.compassX.getValue().intValue() - rotationYaw + 50.0f - 180.0f - (float)this.renderer.getStringWidth("n") / 2.0f, this.compassY.getValue().intValue(), -1);
            this.renderer.drawStringWithShadow("e", (float)this.compassX.getValue().intValue() - rotationYaw + 50.0f - 90.0f - (float)this.renderer.getStringWidth("e") / 2.0f, this.compassY.getValue().intValue(), -1);
            this.renderer.drawStringWithShadow("s", (float)this.compassX.getValue().intValue() - rotationYaw + 50.0f - (float)this.renderer.getStringWidth("s") / 2.0f, this.compassY.getValue().intValue(), -1);
            this.renderer.drawStringWithShadow("w", (float)this.compassX.getValue().intValue() - rotationYaw + 50.0f + 90.0f - (float)this.renderer.getStringWidth("w") / 2.0f, this.compassY.getValue().intValue(), -1);
            RenderUtil.drawLine(this.compassX.getValue() + 50, this.compassY.getValue() + 1, this.compassX.getValue() + 50, this.compassY.getValue() + this.renderer.getFontHeight() - 1, 2.0f, -7303024);
            GL11.glDisable((int)3089);
        } else {
            double centerX = this.compassX.getValue().intValue();
            double centerY = this.compassY.getValue().intValue();
            for (Direction dir : Direction.values()) {
                double rad = Components.getPosOnCompass(dir);
                this.renderer.drawStringWithShadow(dir.name(), (float)(centerX + this.getX(rad)), (float)(centerY + this.getY(rad)), dir == Direction.N ? -65536 : -1);
            }
        }
    }

    public void drawPlayer(EntityPlayer player, int x, int y) {
        EntityPlayer ent = player;
        GlStateManager.pushMatrix();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.shadeModel((int)7424);
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.rotate((float)0.0f, (float)0.0f, (float)5.0f, (float)0.0f);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)(this.playerViewerX.getValue() + 25), (float)(this.playerViewerY.getValue() + 25), (float)50.0f);
        GlStateManager.scale((float)(-50.0f * this.playerScale.getValue().floatValue()), (float)(50.0f * this.playerScale.getValue().floatValue()), (float)(50.0f * this.playerScale.getValue().floatValue()));
        GlStateManager.rotate((float)180.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        GlStateManager.rotate((float)135.0f, (float)0.0f, (float)1.0f, (float)0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate((float)-135.0f, (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.rotate((float)(-((float)Math.atan((float)this.playerViewerY.getValue().intValue() / 40.0f)) * 20.0f), (float)1.0f, (float)0.0f, (float)0.0f);
        GlStateManager.translate((float)0.0f, (float)0.0f, (float)0.0f);
        RenderManager rendermanager = mc.getRenderManager();
        rendermanager.setPlayerViewY(180.0f);
        rendermanager.setRenderShadow(false);
        try {
            rendermanager.renderEntity((Entity)ent, 0.0, 0.0, 0.0, 0.0f, 1.0f, false);
        }
        catch (Exception exception) {
            // empty catch block
        }
        rendermanager.setRenderShadow(true);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture((int)OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture((int)OpenGlHelper.defaultTexUnit);
        GlStateManager.depthFunc((int)515);
        GlStateManager.resetColor();
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();
    }

    public void drawPlayer() {
        EntityPlayerSP ent = Components.mc.player;
        GlStateManager.pushMatrix();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.shadeModel((int)7424);
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.rotate((float)0.0f, (float)0.0f, (float)5.0f, (float)0.0f);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)(this.playerViewerX.getValue() + 25), (float)(this.playerViewerY.getValue() + 25), (float)50.0f);
        GlStateManager.scale((float)(-50.0f * this.playerScale.getValue().floatValue()), (float)(50.0f * this.playerScale.getValue().floatValue()), (float)(50.0f * this.playerScale.getValue().floatValue()));
        GlStateManager.rotate((float)180.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        GlStateManager.rotate((float)135.0f, (float)0.0f, (float)1.0f, (float)0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate((float)-135.0f, (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.rotate((float)(-((float)Math.atan((float)this.playerViewerY.getValue().intValue() / 40.0f)) * 20.0f), (float)1.0f, (float)0.0f, (float)0.0f);
        GlStateManager.translate((float)0.0f, (float)0.0f, (float)0.0f);
        RenderManager rendermanager = mc.getRenderManager();
        rendermanager.setPlayerViewY(180.0f);
        rendermanager.setRenderShadow(false);
        try {
            rendermanager.renderEntity((Entity)ent, 0.0, 0.0, 0.0, 0.0f, 1.0f, false);
        }
        catch (Exception exception) {
            // empty catch block
        }
        rendermanager.setRenderShadow(true);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture((int)OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture((int)OpenGlHelper.defaultTexUnit);
        GlStateManager.depthFunc((int)515);
        GlStateManager.resetColor();
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();
    }

    private double getX(double rad) {
        return Math.sin(rad) * (double)(this.scale.getValue() * 10);
    }

    private double getY(double rad) {
        double epicPitch = MathHelper.clamp((float)(Components.mc.player.rotationPitch + 30.0f), (float)-90.0f, (float)90.0f);
        double pitchRadians = Math.toRadians(epicPitch);
        return Math.cos(rad) * Math.sin(pitchRadians) * (double)(this.scale.getValue() * 10);
    }

    private static double getPosOnCompass(Direction dir) {
        double yaw = Math.toRadians(MathHelper.wrapDegrees((float)Components.mc.player.rotationYaw));
        int index = dir.ordinal();
        return yaw + (double)index * 1.5707963267948966;
    }

    public void drawOverlay(float partialTicks) {
        BlockPos westPos;
        Block west;
        BlockPos eastPos;
        Block east;
        BlockPos southPos;
        Block south;
        float yaw = 0.0f;
        int dir = MathHelper.floor((double)((double)(Components.mc.player.rotationYaw * 4.0f / 360.0f) + 0.5)) & 3;
        switch (dir) {
            case 1: {
                yaw = 90.0f;
                break;
            }
            case 2: {
                yaw = -180.0f;
                break;
            }
            case 3: {
                yaw = -90.0f;
                break;
            }
        }
        BlockPos northPos = this.traceToBlock(partialTicks, yaw);
        Block north = this.getBlock(northPos);
        if (north != null && north != Blocks.AIR) {
            int damage = this.getBlockDamage(northPos);
            if (damage != 0) {
                RenderUtil.drawRect(this.holeX.getValue() + 16, this.holeY.getValue().intValue(), this.holeX.getValue() + 32, this.holeY.getValue() + 16, 0x60FF0000);
            }
            this.drawBlock(north, this.holeX.getValue() + 16, this.holeY.getValue().intValue());
        }
        if ((south = this.getBlock(southPos = this.traceToBlock(partialTicks, yaw - 180.0f))) != null && south != Blocks.AIR) {
            int damage = this.getBlockDamage(southPos);
            if (damage != 0) {
                RenderUtil.drawRect(this.holeX.getValue() + 16, this.holeY.getValue() + 32, this.holeX.getValue() + 32, this.holeY.getValue() + 48, 0x60FF0000);
            }
            this.drawBlock(south, this.holeX.getValue() + 16, this.holeY.getValue() + 32);
        }
        if ((east = this.getBlock(eastPos = this.traceToBlock(partialTicks, yaw + 90.0f))) != null && east != Blocks.AIR) {
            int damage = this.getBlockDamage(eastPos);
            if (damage != 0) {
                RenderUtil.drawRect(this.holeX.getValue() + 32, this.holeY.getValue() + 16, this.holeX.getValue() + 48, this.holeY.getValue() + 32, 0x60FF0000);
            }
            this.drawBlock(east, this.holeX.getValue() + 32, this.holeY.getValue() + 16);
        }
        if ((west = this.getBlock(westPos = this.traceToBlock(partialTicks, yaw - 90.0f))) != null && west != Blocks.AIR) {
            int damage = this.getBlockDamage(westPos);
            if (damage != 0) {
                RenderUtil.drawRect(this.holeX.getValue().intValue(), this.holeY.getValue() + 16, this.holeX.getValue() + 16, this.holeY.getValue() + 32, 0x60FF0000);
            }
            this.drawBlock(west, this.holeX.getValue().intValue(), this.holeY.getValue() + 16);
        }
    }

    public void drawOverlay(float partialTicks, Entity player, int x, int y) {
        BlockPos westPos;
        Block west;
        BlockPos eastPos;
        Block east;
        BlockPos southPos;
        Block south;
        float yaw = 0.0f;
        int dir = MathHelper.floor((double)((double)(player.rotationYaw * 4.0f / 360.0f) + 0.5)) & 3;
        switch (dir) {
            case 1: {
                yaw = 90.0f;
                break;
            }
            case 2: {
                yaw = -180.0f;
                break;
            }
            case 3: {
                yaw = -90.0f;
                break;
            }
        }
        BlockPos northPos = this.traceToBlock(partialTicks, yaw, player);
        Block north = this.getBlock(northPos);
        if (north != null && north != Blocks.AIR) {
            int damage = this.getBlockDamage(northPos);
            if (damage != 0) {
                RenderUtil.drawRect(x + 16, y, x + 32, y + 16, 0x60FF0000);
            }
            this.drawBlock(north, x + 16, y);
        }
        if ((south = this.getBlock(southPos = this.traceToBlock(partialTicks, yaw - 180.0f, player))) != null && south != Blocks.AIR) {
            int damage = this.getBlockDamage(southPos);
            if (damage != 0) {
                RenderUtil.drawRect(x + 16, y + 32, x + 32, y + 48, 0x60FF0000);
            }
            this.drawBlock(south, x + 16, y + 32);
        }
        if ((east = this.getBlock(eastPos = this.traceToBlock(partialTicks, yaw + 90.0f, player))) != null && east != Blocks.AIR) {
            int damage = this.getBlockDamage(eastPos);
            if (damage != 0) {
                RenderUtil.drawRect(x + 32, y + 16, x + 48, y + 32, 0x60FF0000);
            }
            this.drawBlock(east, x + 32, y + 16);
        }
        if ((west = this.getBlock(westPos = this.traceToBlock(partialTicks, yaw - 90.0f, player))) != null && west != Blocks.AIR) {
            int damage = this.getBlockDamage(westPos);
            if (damage != 0) {
                RenderUtil.drawRect(x, y + 16, x + 16, y + 32, 0x60FF0000);
            }
            this.drawBlock(west, x, y + 16);
        }
    }

    private int getBlockDamage(BlockPos pos) {
        for (DestroyBlockProgress destBlockProgress : Components.mc.renderGlobal.damagedBlocks.values()) {
            if (destBlockProgress.getPosition().getX() != pos.getX() || destBlockProgress.getPosition().getY() != pos.getY() || destBlockProgress.getPosition().getZ() != pos.getZ()) continue;
            return destBlockProgress.getPartialBlockDamage();
        }
        return 0;
    }

    private BlockPos traceToBlock(float partialTicks, float yaw) {
        Vec3d pos = EntityUtil.interpolateEntity((Entity)Components.mc.player, partialTicks);
        Vec3d dir = MathUtil.direction(yaw);
        return new BlockPos(pos.x + dir.x, pos.y, pos.z + dir.z);
    }

    private BlockPos traceToBlock(float partialTicks, float yaw, Entity player) {
        Vec3d pos = EntityUtil.interpolateEntity(player, partialTicks);
        Vec3d dir = MathUtil.direction(yaw);
        return new BlockPos(pos.x + dir.x, pos.y, pos.z + dir.z);
    }

    private Block getBlock(BlockPos pos) {
        Block block = Components.mc.world.getBlockState(pos).getBlock();
        if (block == Blocks.BEDROCK || block == Blocks.OBSIDIAN) {
            return block;
        }
        return Blocks.AIR;
    }

    private void drawBlock(Block block, float x, float y) {
        ItemStack stack = new ItemStack(block);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.translate((float)x, (float)y, (float)0.0f);
        Components.mc.getRenderItem().zLevel = 501.0f;
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
        Components.mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableBlend();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.popMatrix();
    }

    public void renderInventory() {
        this.boxrender(this.invX.getValue() + this.fineinvX.getValue(), this.invY.getValue() + this.fineinvY.getValue());
        this.itemrender((NonNullList<ItemStack>)Components.mc.player.inventory.mainInventory, this.invX.getValue() + this.fineinvX.getValue(), this.invY.getValue() + this.fineinvY.getValue());
    }

    private static void preboxrender() {
        GL11.glPushMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.disableAlpha();
        GlStateManager.clear((int)256);
        GlStateManager.enableBlend();
        GlStateManager.color((float)255.0f, (float)255.0f, (float)255.0f, (float)255.0f);
    }

    private static void postboxrender() {
        GlStateManager.disableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
        GL11.glPopMatrix();
    }

    private static void preitemrender() {
        GL11.glPushMatrix();
        GL11.glDepthMask((boolean)true);
        GlStateManager.clear((int)256);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.scale((float)1.0f, (float)1.0f, (float)0.01f);
    }

    private static void postitemrender() {
        GlStateManager.scale((float)1.0f, (float)1.0f, (float)1.0f);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale((double)0.5, (double)0.5, (double)0.5);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        GlStateManager.scale((float)2.0f, (float)2.0f, (float)2.0f);
        GL11.glPopMatrix();
    }

    private void boxrender(int x, int y) {
        Components.preboxrender();
        Components.mc.renderEngine.bindTexture(box);
        RenderUtil.drawTexturedRect(x, y, 0, 0, 176, 16, 500);
        RenderUtil.drawTexturedRect(x, y + 16, 0, 16, 176, 54 + this.invH.getValue(), 500);
        RenderUtil.drawTexturedRect(x, y + 16 + 54, 0, 160, 176, 8, 500);
        Components.postboxrender();
    }

    private void itemrender(NonNullList<ItemStack> items, int x, int y) {
        int iX;
        int i;
        for (i = 0; i < items.size() - 9; ++i) {
            iX = x + i % 9 * 18 + 8;
            int iY = y + i / 9 * 18 + 18;
            ItemStack itemStack = (ItemStack)items.get(i + 9);
            Components.preitemrender();
            Components.mc.getRenderItem().zLevel = 501.0f;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(itemStack, iX, iY);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(Components.mc.fontRenderer, itemStack, iX, iY, null);
            Components.mc.getRenderItem().zLevel = 0.0f;
            Components.postitemrender();
        }
        if (this.renderXCarry.getValue().booleanValue()) {
            for (i = 1; i < 5; ++i) {
                iX = x + (i + 4) % 9 * 18 + 8;
                ItemStack itemStack = ((Slot)Components.mc.player.inventoryContainer.inventorySlots.get(i)).getStack();
                if (itemStack == null || itemStack.isEmpty) continue;
                Components.preitemrender();
                Components.mc.getRenderItem().zLevel = 501.0f;
                RenderUtil.itemRender.renderItemAndEffectIntoGUI(itemStack, iX, y + 1);
                RenderUtil.itemRender.renderItemOverlayIntoGUI(Components.mc.fontRenderer, itemStack, iX, y + 1, null);
                Components.mc.getRenderItem().zLevel = 0.0f;
                Components.postitemrender();
            }
        }
    }

    public static void drawCompleteImage(int posX, int posY, int width, int height) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)posX, (float)posY, (float)0.0f);
        GL11.glBegin((int)7);
        GL11.glTexCoord2f((float)0.0f, (float)0.0f);
        GL11.glVertex3f((float)0.0f, (float)0.0f, (float)0.0f);
        GL11.glTexCoord2f((float)0.0f, (float)1.0f);
        GL11.glVertex3f((float)0.0f, (float)height, (float)0.0f);
        GL11.glTexCoord2f((float)1.0f, (float)1.0f);
        GL11.glVertex3f((float)width, (float)height, (float)0.0f);
        GL11.glTexCoord2f((float)1.0f, (float)0.0f);
        GL11.glVertex3f((float)width, (float)0.0f, (float)0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public static enum TargetHudDesign {
        NORMAL,
        COMPACT;

    }

    public static enum Compass {
        NONE,
        CIRCLE,
        LINE;

    }

    private static enum Direction {
        N,
        W,
        S,
        E;

    }
}

