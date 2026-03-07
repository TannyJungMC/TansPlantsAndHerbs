package tannyjung.tansplantsandherbs_handcode.systems.world_gen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import tannyjung.tansplantsandherbs_core.game.GameUtils;
import tannyjung.tansplantsandherbs_core.game.TXTFunction;
import tannyjung.tansplantsandherbs_core.outside.ConfigWorldGen;

import java.util.*;

public class PlantPlacer {

    public static void start (LevelAccessor level_accessor, ServerLevel level_server, ChunkPos chunk_pos) {

        scan(level_accessor, level_server, chunk_pos, ConfigWorldGen.getData());

    }

    private static void scan (LevelAccessor level_accessor, ServerLevel level_server, ChunkPos chunk_pos, Map<String, Map<String, Map<String, String>>> data) {

        int start_posX = chunk_pos.x * 16;
        int start_posZ = chunk_pos.z * 16;
        Map<String, Integer> height = new HashMap<>();
        int posX = 0;
        int posZ = 0;
        BlockPos pos = null;

        Map<BlockPos, Holder<Biome>> land_biomes = new HashMap<>();
        List<BlockPos> water_locations = new ArrayList<>();

        // Get Some Data
        {

            int originalY = 0;

            for (int scanX = -16; scanX < 32; scanX++) {

                for (int scanZ = -16; scanZ < 32; scanZ++) {

                    posX = start_posX + scanX;
                    posZ = start_posZ + scanZ;
                    originalY = level_accessor.getHeight(Heightmap.Types.WORLD_SURFACE_WG, posX, posZ);

                    height.put(posX + "/" + posZ, originalY);
                    pos = new BlockPos(posX, originalY, posZ);

                    if (level_accessor.isWaterAt(pos.below()) == false) {

                        land_biomes.put(pos, GameUtils.Space.getBiomeAt(level_server, pos));

                    } else {

                        water_locations.add(pos);

                    }

                }

            }

        }

        // Select and Place
        {

            boolean can_normal = data.containsKey("normal") == true && land_biomes.isEmpty() == false;
            boolean can_cave = data.containsKey("cave") == true && land_biomes.isEmpty() == false;
            boolean can_waterside = data.containsKey("waterside") == true && water_locations.isEmpty() == false;
            boolean can_landside = data.containsKey("landside") == true && land_biomes.isEmpty() == false;
            boolean can_aquatic_landside = data.containsKey("aquatic_landside") == true && land_biomes.isEmpty() == false;
            int originalY = 0;

            for (int scanX = 0; scanX < 16; scanX++) {

                for (int scanZ = 0; scanZ < 16; scanZ++) {

                    posX = start_posX + scanX;
                    posZ = start_posZ + scanZ;
                    originalY = height.get(posX + "/" + posZ);

                    for (int scanY = 32; scanY > -32; scanY--) {

                        // Up-Down Fading
                        {

                            if (Math.random() < (double) Math.abs(scanY) / 32.0) {

                                continue;

                            }

                        }

                        pos = new BlockPos(posX, height.get(posX + "/" + posZ) + scanY, posZ);

                        if (level_accessor.getBlockState(pos).canBeReplaced() == true) {

                            {

                                if (level_accessor.getBlockState(pos.below()).canBeReplaced() == false) {

                                    if (level_accessor.getBlockState(pos.above()).isAir() == true) {

                                        if (originalY <= pos.getY()) {

                                            // Waterside
                                            {

                                                if (can_waterside == true) {

                                                    place(level_accessor, level_server, data, land_biomes, originalY, pos, "waterside", "water", water_locations);

                                                }

                                            }

                                            // Normal
                                            {

                                                if (can_normal == true) {

                                                    place(level_accessor, level_server, data, land_biomes, originalY, pos, "normal", "", null);

                                                }

                                            }

                                        } else {

                                            if (level_accessor.isWaterAt(pos) == true) {

                                                // Landside
                                                {

                                                    if (can_landside == true) {

                                                        place(level_accessor, level_server, data, land_biomes, originalY, pos, "landside", "land", null);

                                                    }

                                                }

                                            } else {

                                                // Cave
                                                {

                                                    if (can_cave == true) {

                                                        place(level_accessor, level_server, data, land_biomes, originalY, pos, "cave", "", null);

                                                    }

                                                }

                                            }

                                        }

                                    }

                                } else {

                                    if (level_accessor.isWaterAt(pos.below()) == true) {

                                        if (level_accessor.getBlockState(pos).isAir() == true) {

                                            // Aquatic Landside
                                            {

                                                if (can_aquatic_landside == true) {

                                                    place(level_accessor, level_server, data, land_biomes, originalY, pos, "aquatic_landside", "land", null);

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

    }

    private static void place (LevelAccessor level_accessor, ServerLevel level_server, Map<String, Map<String, Map<String, String>>> data, Map<BlockPos, Holder<Biome>> land_biomes, int originalY, BlockPos pos, String spawn_type, String test_surrounding_area, List<BlockPos> water_locations) {

        if (data.containsKey(spawn_type) == true) {

            double nearest_water = 0.0;
            Map<Holder<Biome>, Double> nearest_land = new HashMap<>();

            // Get Nearest Water and Land
            {

                Vec3 vec3_originalY = pos.atY(originalY).getCenter();

                if (test_surrounding_area.equals("water") == true) {

                    nearest_water = water_locations.stream().min(Comparator.comparingDouble(sort -> sort.getCenter().distanceTo(vec3_originalY))).get().getCenter().distanceTo(vec3_originalY);

                } else if (test_surrounding_area.equals("land") == true) {

                    {

                        double distance = 0.0;

                        for (Map.Entry<BlockPos, Holder<Biome>> entry : land_biomes.entrySet()) {

                            distance = vec3_originalY.distanceTo(entry.getKey().getCenter());

                            if (nearest_land.getOrDefault(entry.getValue(), 64.0) > distance) {

                                nearest_land.put(entry.getValue(), distance);

                            }

                        }

                    }

                }

            }

            double distance = 0.0;
            double distance_test = 0.0;
            BlockPos biome_pos = pos.atY(originalY);

            for (Map.Entry<String, Map<String, String>> entry : data.get(spawn_type).entrySet()) {

                // Test
                {

                    if (entry.getValue().get("enable").equals("true") == false) {

                        continue;

                    }

                    if (Math.random() >= Double.parseDouble(entry.getValue().get("rarity")) * 0.01) {

                        continue;

                    }

                    if (test_surrounding_area.isEmpty() == true) {

                        if (land_biomes.containsKey(biome_pos) == false || GameUtils.Misc.testCustomBiome(land_biomes.get(biome_pos), entry.getValue().get("biome")) == false) {

                            continue;

                        }

                    } else {

                        // Surrounding Area Testing
                        {

                            if (test_surrounding_area.equals("land") == true) {

                                {

                                    distance = 64.0;

                                    for (Holder<Biome> biome : nearest_land.keySet()) {

                                        if (GameUtils.Misc.testCustomBiome(biome, entry.getValue().get("biome")) == true) {

                                            distance = Math.min(distance, nearest_land.get(biome));

                                        }

                                    }

                                    if (distance == 64.0) {

                                        continue;

                                    }

                                }

                            } else if (test_surrounding_area.equals("water") == true) {

                                {

                                    if (land_biomes.containsKey(biome_pos) == false || GameUtils.Misc.testCustomBiome(land_biomes.get(biome_pos), entry.getValue().get("biome")) == false) {

                                        continue;

                                    }

                                    distance = nearest_water;

                                }

                            } else if (test_surrounding_area.equals("cave") == true) {

                                {

                                    distance = originalY - pos.getY();

                                }

                            }

                            distance_test = Double.parseDouble(entry.getValue().get("surrounding_test_distance")) + 1.0;

                            if (distance > distance_test) {

                                continue;

                            }

                            if (distance > 1.0 && Math.random() < distance / distance_test) {

                                continue;

                            }

                        }

                    }

                    if (GameUtils.Misc.testCustomBlock(level_accessor.getBlockState(pos.below()), entry.getValue().get("ground_block")) == false) {

                        continue;

                    }

                }

                TXTFunction.run(level_accessor, level_server, pos, "presets/" + entry.getValue().get("path_settings"), true);

            }

        }

    }

}
