package tannyjung.tansplantsandherbs_handcode.systems.living_mechanics;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import tannyjung.tansplantsandherbs_core.game.GameUtils;

import java.util.List;

public class GardenSoil {

    public static void click (LevelAccessor level_accessor, Entity entity, BlockPos pos) {

        if (level_accessor.isClientSide() == true) {

            return;

        }

        ServerLevel level_server = (ServerLevel) level_accessor;
        Vec3 vec3 = pos.above().getCenter();
        List<Entity> entities = GameUtils.Mob.getAtArea(level_server, vec3, 1, true, 0, "minecraft:block_display", "TANSPLANTSANDHERBS-table_preview");

        if (entities.isEmpty() == false) {

            // Remove
            {

                for (Entity entity_scan : entities) {

                    GameUtils.Item.spawn(level_server, vec3, GameUtils.Item.fromID(GameUtils.Data.getEntityText(entity_scan, "id")));
                    GameUtils.Mob.remove(entity_scan, false);

                }

            }

        } else {

            // Place
            {

                String id = GameUtils.Item.toID(GameUtils.Item.getSlot(entity, EquipmentSlot.MAINHAND));

                if (id.startsWith("tansplantsandherbs:plant") == true) {

                    Entity entity_summon = null;
                    double offsetX = Mth.nextDouble(RandomSource.create(), -0.25, 0.25);
                    double offsetZ = Mth.nextDouble(RandomSource.create(), -0.25, 0.25);
                    double offsetY = Mth.nextDouble(RandomSource.create(), -0.1, 0.0);

                    if (id.startsWith("tansplantsandherbs:plant_") == false) {

                        entity_summon = GameUtils.Misc.summonBlock(level_server, vec3, "Table Preview", "TANSPLANTSANDHERBS-table_preview", offsetX, offsetY, offsetZ, 1.0, 1.0, 1.0, 0, 0, id);

                    }

                    GameUtils.Data.setEntityText(entity_summon, "id", id);
                    GameUtils.Item.addCount(entity, EquipmentSlot.MAINHAND, -1);

                }

            }

        }

    }

}
