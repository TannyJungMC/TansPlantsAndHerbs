package tannyjung.tansplantsandherbs_handcode.systems.world_gen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import tannyjung.tansplantsandherbs_core.game.GameUtils;
import tannyjung.tansplantsandherbs_core.game.TXTFunction;
import tannyjung.tansplantsandherbs_core.outside.ConfigWorldGen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PlantPlacer {

    public static void start (LevelAccessor level_accessor, ServerLevel level_server, ChunkPos chunk_pos) {

        scan(level_accessor, level_server, chunk_pos, ConfigWorldGen.getData());

    }

    private static void scan (LevelAccessor level_accessor, ServerLevel level_server, ChunkPos chunk_pos, Map<String, Map<String, Map<String, String>>> data) {

        int start_posX = chunk_pos.x * 16;
        int start_posZ = chunk_pos.z * 16;
        int testX = 0;
        int testZ = 0;
        int height = 0;
        List<BlockPos> list_pos_land = new ArrayList<>();
        List<BlockPos> list_pos_water = new ArrayList<>();

        // Get Land and Water Pos
        {

            BlockPos pos = null;

            for (int scanX = -16; scanX < 32; scanX++) {

                for (int scanZ = -16; scanZ < 32; scanZ++) {

                    testX = start_posX + scanX;
                    testZ = start_posZ + scanZ;
                    height = level_accessor.getHeight(Heightmap.Types.OCEAN_FLOOR, testX, testZ);

                    for (int scanY = 16; scanY > -16; scanY--) {

                        pos = new BlockPos(testX, height + scanY, testZ);

                        if (level_accessor.getBlockState(pos).canBeReplaced() == true) {

                            if (level_accessor.getBlockState(pos.above()).canBeReplaced() == true && level_accessor.isWaterAt(pos.above()) == false) {

                                if (level_accessor.isWaterAt(pos) == false) {

                                    if (level_accessor.getBlockState(pos.below()).canBeReplaced() == false) {

                                        list_pos_land.add(pos);

                                    }

                                } else {

                                    list_pos_water.add(pos);

                                }

                            }

                        }

                    }

                }

            }

        }

        // Select and Place
        {

            boolean can_waterside = list_pos_water.isEmpty() == false && data.containsKey("waterside") == true;
            boolean can_landside = list_pos_land.isEmpty() == false && data.containsKey("landside") == true;
            double distance_water = 0.0;
            double distance_land = 0.0;
            BlockPos nearest_land = null;

            for (int scanX = 0; scanX < 16; scanX++) {

                for (int scanZ = 0; scanZ < 16; scanZ++) {

                    testX = start_posX + scanX;
                    testZ = start_posZ + scanZ;

                    for (int scanY = 16; scanY > -16; scanY--) {

                        BlockPos pos = new BlockPos(testX, height + scanY, testZ);

                        if (level_accessor.getBlockState(pos).canBeReplaced() == true) {

                            if (level_accessor.getBlockState(pos.below()).canBeReplaced() == false) {

                                if (level_accessor.getBlockState(pos.above()).canBeReplaced() == true && level_accessor.isWaterAt(pos.above()) == false) {

                                    // Convert Land and Water Pos
                                    {

                                        if (can_waterside == true) {

                                            distance_water = list_pos_water.stream().mapToDouble(sort -> sort.getCenter().distanceTo(pos.getCenter())).min().getAsDouble();

                                        }

                                        if (can_landside == true) {

                                            nearest_land = list_pos_land.stream().min(Comparator.comparingDouble(sort -> sort.getCenter().distanceTo(pos.getCenter()))).get();
                                            distance_land = pos.getCenter().distanceTo(nearest_land.getCenter());

                                        }

                                    }

                                    if (level_accessor.isWaterAt(pos) == false) {

                                        if (can_waterside == true) {

                                            // Waterside
                                            {

                                                if (place(level_accessor, level_server, pos, pos, data, "waterside", true, distance_water) == true) {

                                                    continue;

                                                }

                                            }

                                        }

                                        // Normal
                                        {

                                            place(level_accessor, level_server, pos, pos, data, "normal", false, 0);

                                        }

                                    } else {

                                        if (can_landside == true) {

                                            // Landside
                                            {

                                                if (place(level_accessor, level_server, pos, nearest_land, data, "landside", true, distance_land) == true) {

                                                    continue;

                                                }

                                            }

                                        }

                                    }

                                }

                            } else {

                                if (level_accessor.isWaterAt(pos.below()) == true) {

                                    if (level_accessor.isWaterAt(pos) == false) {

                                        if (can_landside == true) {

                                            // Convert Land and Water Pos
                                            {

                                                if (can_waterside == true) {

                                                    distance_water = list_pos_water.stream().mapToDouble(sort -> sort.getCenter().distanceTo(pos.getCenter())).min().getAsDouble();

                                                }

                                                if (can_landside == true) {

                                                    nearest_land = list_pos_land.stream().min(Comparator.comparingDouble(sort -> sort.getCenter().distanceTo(pos.getCenter()))).get();
                                                    distance_land = pos.getCenter().distanceTo(nearest_land.getCenter());

                                                }

                                            }

                                            // Floating Landside
                                            {

                                                place(level_accessor, level_server, pos, nearest_land, data, "floating_landside", true, distance_land);

                                            }

                                        }

                                    }

                                }

                            }

                        }

                    }

                }

            }

        }

    }

    private static boolean place (LevelAccessor level_accessor, ServerLevel level_server, BlockPos pos, BlockPos pos_biome, Map<String, Map<String, Map<String, String>>> data, String spawn_type, boolean side_test, double side_distance) {

        if (data.containsKey(spawn_type) == true) {

            double distance_test = 0;

            for (Map.Entry<String, Map<String, String>> entry : data.get(spawn_type).entrySet()) {

                if (entry.getValue().get("enable").equals("true") == false) {

                    continue;

                }

                if (side_test == true) {

                    distance_test = Double.parseDouble(entry.getValue().get("land_water_distance")) + 1.0;

                    if (side_distance > distance_test) {

                        continue;

                    }

                    if (side_distance > 2.0 && Math.random() > 1.0 - (side_distance / distance_test)) {

                        continue;

                    }

                }

                // Test
                {

                    if (Math.random() >= Double.parseDouble(entry.getValue().get("rarity")) * 0.01) {

                        continue;

                    } else if (GameUtils.Misc.testCustomBiome(GameUtils.Space.getBiomeAt(level_server, pos_biome), entry.getValue().get("biome")) == false) {

                        continue;

                    } else if (spawn_type.startsWith("floating") == false && GameUtils.Misc.testCustomBlock(level_accessor.getBlockState(pos.below()), entry.getValue().get("ground_block")) == false) {

                        continue;

                    }

                }

                TXTFunction.run(level_accessor, level_server, pos, "presets/" + entry.getValue().get("path_settings"), true);
                return true;

            }

        }

        return false;

    }

}
