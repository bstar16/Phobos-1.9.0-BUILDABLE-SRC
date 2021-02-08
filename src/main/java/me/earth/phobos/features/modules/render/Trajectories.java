

package me.earth.phobos.features.modules.render;

import java.util.ArrayList;
import java.util.List;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSnowball;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

public class Trajectories
extends Module {
    public Trajectories() {
        super("Trajectories", "Shows the way of projectiles.", Module.Category.RENDER, false, false, false);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (Trajectories.mc.world == null || Trajectories.mc.player == null) {
            return;
        }
        this.drawTrajectories((EntityPlayer)Trajectories.mc.player, event.getPartialTicks());
    }

    public void enableGL3D(float lineWidth) {
        GL11.glDisable((int)3008);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)3553);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glEnable((int)2884);
        Trajectories.mc.entityRenderer.disableLightmap();
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glHint((int)3155, (int)4354);
        GL11.glLineWidth((float)lineWidth);
    }

    public void disableGL3D() {
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)3008);
        GL11.glDepthMask((boolean)true);
        GL11.glCullFace((int)1029);
        GL11.glDisable((int)2848);
        GL11.glHint((int)3154, (int)4352);
        GL11.glHint((int)3155, (int)4352);
    }

    private void drawTrajectories(EntityPlayer player, float partialTicks) {
        float pow;
        double renderPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)partialTicks;
        double renderPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)partialTicks;
        double renderPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)partialTicks;
        player.getHeldItem(EnumHand.MAIN_HAND);
        if (!(Trajectories.mc.gameSettings.thirdPersonView == 0 && (player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBow || player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemFishingRod || player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemEnderPearl || player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemEgg || player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSnowball || player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemExpBottle))) {
            return;
        }
        GL11.glPushMatrix();
        Item item = player.getHeldItem(EnumHand.MAIN_HAND).getItem();
        double posX = renderPosX - (double)(MathHelper.cos((float)(player.rotationYaw / 180.0f * (float)Math.PI)) * 0.16f);
        double posY = renderPosY + (double)player.getEyeHeight() - 0.1000000014901161;
        double posZ = renderPosZ - (double)(MathHelper.sin((float)(player.rotationYaw / 180.0f * (float)Math.PI)) * 0.16f);
        double motionX = (double)(-MathHelper.sin((float)(player.rotationYaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(player.rotationPitch / 180.0f * (float)Math.PI))) * (item instanceof ItemBow ? 1.0 : 0.4);
        double motionY = (double)(-MathHelper.sin((float)(player.rotationPitch / 180.0f * (float)Math.PI))) * (item instanceof ItemBow ? 1.0 : 0.4);
        double motionZ = (double)(MathHelper.cos((float)(player.rotationYaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(player.rotationPitch / 180.0f * (float)Math.PI))) * (item instanceof ItemBow ? 1.0 : 0.4);
        int var6 = 72000 - player.getItemInUseCount();
        float power = (float)var6 / 20.0f;
        power = (power * power + power * 2.0f) / 3.0f;
        if (power > 1.0f) {
            power = 1.0f;
        }
        float distance = MathHelper.sqrt((double)(motionX * motionX + motionY * motionY + motionZ * motionZ));
        motionX /= (double)distance;
        motionY /= (double)distance;
        motionZ /= (double)distance;
        float f = item instanceof ItemBow ? power * 2.0f : (item instanceof ItemFishingRod ? 1.25f : (pow = player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.EXPERIENCE_BOTTLE ? 0.9f : 1.0f));
        motionX *= (double)(pow * (item instanceof ItemFishingRod ? 0.75f : (player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.EXPERIENCE_BOTTLE ? 0.75f : 1.5f)));
        motionY *= (double)(pow * (item instanceof ItemFishingRod ? 0.75f : (player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.EXPERIENCE_BOTTLE ? 0.75f : 1.5f)));
        motionZ *= (double)(pow * (item instanceof ItemFishingRod ? 0.75f : (player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.EXPERIENCE_BOTTLE ? 0.75f : 1.5f)));
        this.enableGL3D(2.0f);
        if (power > 0.6f) {
            GlStateManager.color((float)0.0f, (float)1.0f, (float)0.0f, (float)1.0f);
        } else {
            GlStateManager.color((float)0.8f, (float)0.5f, (float)0.0f, (float)1.0f);
        }
        GL11.glEnable((int)2848);
        float size = (float)(item instanceof ItemBow ? 0.3 : 0.25);
        boolean hasLanded = false;
        Entity landingOnEntity = null;
        RayTraceResult landingPosition = null;
        while (!hasLanded && posY > 0.0) {
            Vec3d present = new Vec3d(posX, posY, posZ);
            Vec3d future = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
            RayTraceResult possibleLandingStrip = Trajectories.mc.world.rayTraceBlocks(present, future, false, true, false);
            if (possibleLandingStrip != null && possibleLandingStrip.typeOfHit != RayTraceResult.Type.MISS) {
                landingPosition = possibleLandingStrip;
                hasLanded = true;
            }
            AxisAlignedBB arrowBox = new AxisAlignedBB(posX - (double)size, posY - (double)size, posZ - (double)size, posX + (double)size, posY + (double)size, posZ + (double)size);
            List entities = this.getEntitiesWithinAABB(arrowBox.offset(motionX, motionY, motionZ).expand(1.0, 1.0, 1.0));
            for (Object entity : entities) {
                Entity boundingBox = (Entity)entity;
                if (!boundingBox.canBeCollidedWith() || boundingBox == player) continue;
                float var7 = 0.3f;
                AxisAlignedBB var8 = boundingBox.getEntityBoundingBox().expand((double)var7, (double)var7, (double)var7);
                RayTraceResult possibleEntityLanding = var8.calculateIntercept(present, future);
                if (possibleEntityLanding == null) continue;
                hasLanded = true;
                landingOnEntity = boundingBox;
                landingPosition = possibleEntityLanding;
            }
            if (landingOnEntity != null) {
                GlStateManager.color((float)1.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            }
            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            float motionAdjustment = 0.99f;
            motionX *= (double)motionAdjustment;
            motionY *= (double)motionAdjustment;
            motionZ *= (double)motionAdjustment;
            motionY -= item instanceof ItemBow ? 0.05 : 0.03;
        }
        if (landingPosition != null && landingPosition.typeOfHit == RayTraceResult.Type.BLOCK) {
            GlStateManager.translate((double)(posX - renderPosX), (double)(posY - renderPosY), (double)(posZ - renderPosZ));
            int side = landingPosition.sideHit.getIndex();
            if (side == 2) {
                GlStateManager.rotate((float)90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
            } else if (side == 3) {
                GlStateManager.rotate((float)90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
            } else if (side == 4) {
                GlStateManager.rotate((float)90.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            } else if (side == 5) {
                GlStateManager.rotate((float)90.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            }
            Cylinder c = new Cylinder();
            GlStateManager.rotate((float)-90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
            c.setDrawStyle(100011);
            if (landingOnEntity != null) {
                GlStateManager.color((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glLineWidth((float)2.5f);
                c.draw(0.6f, 0.3f, 0.0f, 4, 1);
                GL11.glLineWidth((float)0.1f);
                GlStateManager.color((float)1.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            }
            c.draw(0.6f, 0.3f, 0.0f, 4, 1);
        }
        this.disableGL3D();
        GL11.glPopMatrix();
    }

    private List getEntitiesWithinAABB(AxisAlignedBB bb) {
        ArrayList list = new ArrayList();
        int chunkMinX = MathHelper.floor((double)((bb.minX - 2.0) / 16.0));
        int chunkMaxX = MathHelper.floor((double)((bb.maxX + 2.0) / 16.0));
        int chunkMinZ = MathHelper.floor((double)((bb.minZ - 2.0) / 16.0));
        int chunkMaxZ = MathHelper.floor((double)((bb.maxZ + 2.0) / 16.0));
        for (int x = chunkMinX; x <= chunkMaxX; ++x) {
            for (int z = chunkMinZ; z <= chunkMaxZ; ++z) {
                if (Trajectories.mc.world.getChunkProvider().getLoadedChunk(x, z) == null) continue;
                Trajectories.mc.world.getChunk(x, z).getEntitiesWithinAABBForEntity((Entity)Trajectories.mc.player, bb, list, null);
            }
        }
        return list;
    }
}

