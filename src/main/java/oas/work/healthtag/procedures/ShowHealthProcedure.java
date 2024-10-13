package oas.work.healthtag.procedures;

import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.BuiltInRegistries;

import javax.annotation.Nullable;

@EventBusSubscriber
public class ShowHealthProcedure {
	@SubscribeEvent
	public static void onEntityTick(EntityTickEvent.Pre event) {
		execute(event, event.getEntity());
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;
		String color = "";
		if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) / 3) {
			color = "\u00A74";
		} else if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) / 1.5) {
			color = "\u00A76";
		}
		entity.setCustomName(Component.literal((BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString() + " : " + color + (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) + " / "
				+ (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1))));
		entity.setCustomNameVisible(true);
	}
}
