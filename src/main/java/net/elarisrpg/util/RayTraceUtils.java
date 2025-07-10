package net.elarisrpg.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;

import java.util.List;

public class RayTraceUtils {

    /**
     * Traces entities the player is looking at, filtering:
     * - LivingEntity (mobs, players, animals)
     * - ItemEntity (dropped items)
     *
     * @param player   The player
     * @param distance How far to trace
     * @return EntityHitResult if found, else null
     */
    public static EntityHitResult rayTraceEntities(Player player, double distance) {
        Vec3 eyePos = player.getEyePosition(1.0F);
        Vec3 lookVec = player.getViewVector(1.0F);
        Vec3 endVec = eyePos.add(lookVec.scale(distance));

        AABB searchBox = player.getBoundingBox().expandTowards(lookVec.scale(distance)).inflate(1.0D);

        List<Entity> entities = player.level().getEntities(player, searchBox);

        Entity closestEntity = null;
        double closestDistanceSq = distance * distance;

        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity || entity instanceof ItemEntity)) {
                continue;
            }

            AABB entityBox = entity.getBoundingBox().inflate(0.3D);
            var optional = entityBox.clip(eyePos, endVec);
            if (optional.isPresent()) {
                double distSq = eyePos.distanceToSqr(optional.get());
                if (distSq < closestDistanceSq) {
                    closestEntity = entity;
                    closestDistanceSq = distSq;
                }
            }
        }

        if (closestEntity != null) {
            Vec3 hitPos = eyePos.add(lookVec.scale(Math.sqrt(closestDistanceSq)));
            return new EntityHitResult(closestEntity, hitPos);
        }

        return null;
    }

    /**
     * Traces entities but respects blocks. Only returns an entity
     * if no solid block is in the way.
     *
     * @param player   The player
     * @param distance Max trace distance
     * @return EntityHitResult or null
     */
    public static EntityHitResult rayTraceEntitiesRespectingBlocks(Player player, double distance) {
        // Trace blocks first
        HitResult blockHit = player.pick(distance, 0.0F, false);

        if (blockHit.getType() == HitResult.Type.BLOCK) {
            return null;
        }

        // No block in the way, trace entities
        return rayTraceEntities(player, distance);
    }
}
