package tfar.classicbar.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static tfar.classicbar.ColorUtilities.cU;
import static tfar.classicbar.config.ModConfig.*;
import static tfar.classicbar.ModUtils.*;

/*
    Class handles the drawing of the health bar
 */

public class HealthBarMountRenderer {
    private final Minecraft mc = Minecraft.getMinecraft();

    private int updateCounter = 0;
    private double mountHealth = 0;
    private double lasMountHealth = 0;
    private long healthUpdateCounter = 0;
    private long lastSystemTime = 0;

    private boolean forceUpdateIcons = false;

    public HealthBarMountRenderer() {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void renderHealthBarMount(RenderGameOverlayEvent.Pre event) {

        Entity renderViewEnity = this.mc.getRenderViewEntity();
        if (event.getType() != RenderGameOverlayEvent.ElementType.HEALTHMOUNT
                || event.isCanceled()
                || !(renderViewEnity instanceof EntityPlayer)) return;
        int scaledWidth = event.getResolution().getScaledWidth();
        int scaledHeight = event.getResolution().getScaledHeight();
        //Push to avoid lasting changes
        ;

        updateCounter = mc.ingameGUI.getUpdateCounter();

        EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();

        if (player.getRidingEntity() == null)return;
        if (!(player.getRidingEntity() instanceof EntityLivingBase))return;

        EntityLivingBase mount = (EntityLivingBase) player.getRidingEntity();
        if (mount.isDead)return;
        event.setCanceled(true);
        double mountHealth = mount.getHealth();

        boolean highlight = healthUpdateCounter > (long) updateCounter && (healthUpdateCounter - (long) updateCounter) / 3L % 2L == 1L;

        if (mountHealth < this.mountHealth && player.hurtResistantTime > 0) {
            lastSystemTime = Minecraft.getSystemTime();
            healthUpdateCounter = (long) (updateCounter + 20);
        } else if (mountHealth > this.mountHealth && player.hurtResistantTime > 0) {
            lastSystemTime = Minecraft.getSystemTime();
            healthUpdateCounter = (long) (updateCounter + 10);
        }
        if (mountHealth != this.mountHealth || forceUpdateIcons) {
            forceUpdateIcons = false;
        }

        this.mountHealth = mountHealth;
        IAttributeInstance maxHealthAttribute = mount.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        int xStart = scaledWidth / 2 + 10;
        int yStart = scaledHeight - 39;
        double maxHealth = maxHealthAttribute.getAttributeValue();

        mc.profiler.startSection("mountHealth");
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        int i4 = (highlight) ? 18 : 0;


        //Bind our Custom bar
        mc.getTextureManager().bindTexture(ICON_BAR);
        //Bar background
        drawTexturedModalRect(xStart, yStart, 0, i4, 81, 9,general.style,false,false);

        //is the bar changing
        //Pass 1, draw bar portion

        //calculate bar color
        cU.color2Gl(cU.calculateScaledColor(mountHealth, maxHealth));
        float f = xStart+80-getWidth(mountHealth,maxHealth);
        //draw portion of bar based on mountHealth remaining
        drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(mountHealth, maxHealth), 7,general.style,true,false);
        //draw mountHealth amount
        int h1 = (int) Math.ceil(mountHealth);

        int i3 = general.displayIcons ? 1 : 0;
        if (numbers.showPercent)h1 = (int)(100*mountHealth/maxHealth);
        drawStringOnHUD(h1+"", xStart + 9 * i3 + rightTextOffset, yStart - 1, cU.colorToText(cU.calculateScaledColor(mountHealth,maxHealth)));

        //Reset back to normal settings
        GlStateManager.color(1, 1, 1, 1);

        mc.getTextureManager().bindTexture(ICON_VANILLA);
        GuiIngameForge.left_height += 10;

        if (general.displayIcons) {
            //Draw mountHealth icon
            //heart background
            drawTexturedModalRect(xStart + 82, yStart, 16, 0, 9, 9,0,true,false);
            //heart
            drawTexturedModalRect(xStart + 82, yStart, 88, 9, 9, 9,0,true,false);

        }

        //Reset back to normal settings

        GlStateManager.disableBlend();
        //Revert our state back
        GlStateManager.popMatrix();
        mc.profiler.endSection();
        event.setCanceled(true);
    }

}
