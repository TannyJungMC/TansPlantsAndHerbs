package tannyjung.tansplantsandherbs_handcode.systems.living_mechanics;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import tannyjung.tansplantsandherbs_core.game.GameUtils;
import tannyjung.tansplantsandherbs_core.outside.ConfigDynamic;

import java.util.List;
import java.util.Map;

public class PlantBlock {

    public static void whenClick (LevelAccessor level_accessor, Entity entity, BlockPos pos) {

        if (level_accessor.isClientSide() == true) {

            return;

        }

        ServerLevel level_server = (ServerLevel) level_accessor;
        ItemStack item = GameUtils.Item.getSlot(entity, EquipmentSlot.MAINHAND);

        if (GameUtils.Item.isTaggedAs(item, "minecraft:shovels") == true) {

            GameUtils.Item.setCooldown(entity, item, 100);

            if (GameUtils.Mob.isCreativeMode(entity) == false) {

                GameUtils.Item.addDamage(item, 1);

            }

            GameUtils.Tile.removeDrop(level_accessor, level_server, pos);
            GameUtils.Misc.playSound(level_server, pos, 2.0, 0.0, "minecraft:item.shovel.flatten");
            GameUtils.Misc.spawnParticle(level_server, pos.getCenter().add(0.0, -0.25, 0.0), 0.25, 0.25, 0.25, 0.01, 10, "minecraft:campfire_cosy_smoke");

            if (level_accessor.getBlockState(pos.below()).getBlock() == Blocks.GRASS_BLOCK) {

                GameUtils.Tile.set(level_accessor, pos.below(), Blocks.DIRT.defaultBlockState());

            }

        }

    }

    public static void whenPlace (LevelAccessor level_accessor, Entity entity, BlockPos pos) {

        if (level_accessor.isClientSide() == true) {

            return;

        }

        testPlace(level_accessor, pos, true, GameUtils.Mob.isCreativeMode(entity) == false);

    }

    public static void whenNeighbourUpdate (LevelAccessor level_accessor, BlockPos pos) {

        testPlace(level_accessor, pos, false, false);

        // This will not summon message

    }

    private static void testPlace (LevelAccessor level_accessor, BlockPos pos, boolean message, boolean drop) {

        ServerLevel level_server = (ServerLevel) level_accessor;
        String error = "";

        // Unloaded Area
        {

            if (level_server.isLoaded(pos.offset(16, 0, 16)) == false || level_server.isLoaded(pos.offset(16, 0, -16)) == false || level_server.isLoaded(pos.offset(-16, 0, 16)) == false || level_server.isLoaded(pos.offset(-16, 0, -16)) == false) {

                error = "unloaded area";

            }

        }

        if (error.isEmpty() == true) {

            // Test
            {

                String id = GameUtils.Tile.toText(level_accessor.getBlockState(pos))[0].replace(":", "-");
                Map<String, Map<String, String>> data = ConfigDynamic.getData("settings", "id").get("");

                if (data.containsKey(id) == false) {

                    error = "data not found";

                } else {

                    Object[] surrounding_area_data = LivingMechanics.getSurroundingAreaData(level_accessor, level_server, (pos.getX() >> 4) * 16, (pos.getZ() >> 4) * 16);
                    Map<String, Integer> height = (Map<String, Integer>) surrounding_area_data[0];
                    List<BlockPos> water_locations = (List<BlockPos>) surrounding_area_data[1];
                    Map<BlockPos, Holder<Biome>> land_biomes = (Map<BlockPos, Holder<Biome>>) surrounding_area_data[2];

                    String type = data.get(id).get("type");
                    String area_type = LivingMechanics.getAreaType(level_accessor, pos, height.get(pos.getX() + "/" + pos.getZ()), water_locations.isEmpty() == false, land_biomes.isEmpty() == false);

                    if (area_type.isEmpty() == true || area_type.contains("|" + type + "|") == false) {

                        error = "unsupported environment";

                    } else {

                        BlockState ceil_block = null;
                        BlockPos cail_pos = null;

                        if (type.equals("cave") == true) {

                            for (int scan = 1; scan < 32; scan++) {

                                cail_pos = new BlockPos(pos.getX(), pos.getY() + scan, pos.getZ());

                                if (level_accessor.getBlockState(cail_pos).getCollisionShape(level_accessor, cail_pos).isEmpty() == false) {

                                    ceil_block = level_accessor.getBlockState(cail_pos);
                                    break;

                                }

                            }

                        }

                        error = LivingMechanics.test(level_accessor, data, height, water_locations, land_biomes, pos, ceil_block, id, false);

                    }

                }

            }

        }

        if (error.isEmpty() == false) {

            if (message == true) {

                GameUtils.Misc.summonText(level_server, pos.getCenter(), 0.5, "Can not place here because " + error + " / red", true);

            }

            if (drop == true) {

                GameUtils.Tile.removeDrop(level_accessor, level_server, pos);

            } else {

                GameUtils.Tile.remove(level_accessor, pos);

            }

        }

    }

}
