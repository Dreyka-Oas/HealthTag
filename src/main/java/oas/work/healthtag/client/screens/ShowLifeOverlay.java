package oas.work.healthtag.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.EntityHitResult;

import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.api.distmarker.Dist;

@EventBusSubscriber(Dist.CLIENT)
public class ShowLifeOverlay {

    private static final double DISPLAY_DISTANCE = 10.0;
    private static float lastHealth = -1;
    private static float alpha = 1.0f;
    private static float lastFilledWidth = 0; // Pour l'animation progressive de la barre verte

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void eventHandler(RenderGuiEvent.Pre event) {
        GuiGraphics gui = event.getGuiGraphics();
        int x = 10;
        int y = 10;
        int overlayWidth = 130;
        int overlayHeight = 40; // Taille ajustée du rectangle

        Player player = Minecraft.getInstance().player;
        if (player == null)
            return;

        Entity targetEntity = null;
        HitResult hit = Minecraft.getInstance().hitResult;
        if (hit instanceof EntityHitResult) {
            targetEntity = ((EntityHitResult) hit).getEntity();
        }

        if (targetEntity instanceof LivingEntity targetLivingEntity) {
            double distance = player.distanceTo(targetEntity);
            if (distance <= DISPLAY_DISTANCE) {
                // Fond du rectangle
                gui.fill(x, y, x + overlayWidth, y + overlayHeight, 0xAA2C2F40);

                // Affichage du nom complet de l'entité
                Component entityName = targetEntity.getName();
                gui.drawString(Minecraft.getInstance().font, entityName.getString(), x + 5, y + 5, -1, false);

                float health = targetLivingEntity.getHealth();
                float maxHealth = targetLivingEntity.getMaxHealth();
                int barX = x + 5;
                int barY = y + 20;
                int barWidth = overlayWidth - 10;
                int barHeight = 12;

                // Couleur dynamique du contour en fonction de la santé
                int contourColor = 0xFF00FF00; // Vert par défaut
                if (health < maxHealth * 0.25f) {
                    contourColor = 0xFFFF0000; // Rouge si vie < 25%
                } else if (health < maxHealth * 0.5f) {
                    contourColor = 0xFFFFA500; // Orange si vie < 50%
                }
                // Contour de la barre
                gui.fill(barX - 1, barY - 1, barX + barWidth + 1, barY + barHeight + 1, contourColor);

                // Fond de la barre de vie (couleur constante)
                int barColor = 0xFF2C2F40;
                gui.fill(barX, barY, barX + barWidth, barY + barHeight, barColor);

                int filledWidth = (int) ((health / maxHealth) * barWidth);

                // Animation progressive de la barre verte
                if (filledWidth > lastFilledWidth) {
                    lastFilledWidth = filledWidth;
                } else if (lastFilledWidth > filledWidth) {
                    lastFilledWidth -= 1;
                    if (lastFilledWidth < 0) {
                        lastFilledWidth = 0;
                    }
                }
                // Barre de vie (toujours en vert)
                int lifeBarColor = 0xFF3BB13C;
                gui.fill(barX, barY, barX + (int) lastFilledWidth, barY + barHeight, lifeBarColor);

                if (lastHealth > health) {
                    alpha = 1.0f;
                }
                if (alpha > 0 && lastHealth > health) {
                    int lostWidth = (int) (((lastHealth - health) / maxHealth) * barWidth);
                    int lostStart = barX + filledWidth;
                    int fadeColor = ((int) (alpha * 255) << 24) | 0xDF4444;
                    gui.fill(lostStart, barY, lostStart + lostWidth, barY + barHeight, fadeColor);
                    alpha -= 0.02f;
                    if (alpha <= 0) {
                        lastHealth = health;
                    }
                }

                String healthText = String.format("%.0f / %.0f", health, maxHealth);
                gui.drawString(Minecraft.getInstance().font, healthText, barX + 2, barY + 2, -1, false);
            }
        }
    }
}
