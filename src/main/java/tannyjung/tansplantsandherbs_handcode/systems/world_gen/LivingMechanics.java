package tannyjung.tansplantsandherbs_handcode.systems.world_gen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import tannyjung.tansplantsandherbs_core.Core;
import tannyjung.tansplantsandherbs_core.game.GameUtils;
import tannyjung.tansplantsandherbs_core.outside.CacheManager;
import tannyjung.tansplantsandherbs_core.outside.ConfigDynamic;
import tannyjung.tansplantsandherbs_core.outside.TXTFunction;

import java.io.File;
import java.util.*;

public class LivingMechanics {

    private static int tick = 0;

    public static void start (LevelAccessor level_accessor, ServerLevel level_server) {

        tick = tick + 1;

        if (tick > 20) {

            tick = 0;

            for (int loop = 1; loop > 0; loop--) {

                runRandomChunk(level_accessor, level_server);

            }

        }

    }

    private static void runRandomChunk (LevelAccessor level_accessor, ServerLevel level_server) {

        ServerPlayer player = level_server.getRandomPlayer();

        if (player != null) {

            int distance = 0;

            if (distance == 0) {

                distance = level_server.getServer().getPlayerList().getSimulationDistance();

            }

            int start_posX = (player.chunkPosition().x + Mth.nextInt(RandomSource.create(), -distance, distance)) * 16;
            int start_posZ = (player.chunkPosition().z + Mth.nextInt(RandomSource.create(), -distance, distance)) * 16;
            BlockPos pos = new BlockPos(start_posX, 0, start_posZ);

            if (level_server.isPositionEntityTicking(pos) == true) {

                Map<String, Map<String, String>> data = ConfigDynamic.getData("settings", "enable_living_mechanics").getOrDefault("true", new HashMap<>());
                Object[] surrounding_area_data = getSurroundingAreaData(level_accessor, level_server, start_posX, start_posZ);
                Map<String, Integer> height = (Map<String, Integer>) surrounding_area_data[0];
                List<BlockPos> water_locations = (List<BlockPos>) surrounding_area_data[1];
                Map<BlockPos, Holder<Biome>> land_biomes = (Map<BlockPos, Holder<Biome>>) surrounding_area_data[2];

                BlockPos pos_move = null;
                int posX = 0;
                int posZ = 0;
                String id = "";
                String type_test = "";
                int originalY = 0;
                int original_movedY = 0;

                for (int loop = 16; loop > 0; loop--) {

                    posX = start_posX + Mth.nextInt(RandomSource.create(), 0, 16);
                    posZ = start_posZ + Mth.nextInt(RandomSource.create(), 0, 16);
                    originalY = height.get(posX + "/" + posZ);

                    for (int scanY = 0; scanY > -32; scanY--) {

                        pos = new BlockPos(posX, originalY + scanY, posZ);
                        id = GameUtils.Tile.toText(level_accessor.getBlockState(pos))[0].replace(":", "-");

                        if (data.containsKey(id) == true) {

                            for (int count = Integer.parseInt(data.get(id).get("spread_count")); count > 0; count--) {

                                if (Math.random() < Double.parseDouble(data.get(id).get("spread_chance"))) {

                                    // Move Pos
                                    {

                                        pos_move = pos.offset(Mth.nextInt(RandomSource.create(), -2, 2), 0, Mth.nextInt(RandomSource.create(), -2, 2));

                                        if (data.get(id).get("type").startsWith("floating") == false) {

                                            while (level_accessor.getBlockState(pos_move.below()).getCollisionShape(level_accessor, pos_move.below()).isEmpty() == true) {

                                                pos_move = pos_move.below();

                                            }

                                            while (level_accessor.getBlockState(pos_move).getCollisionShape(level_accessor, pos_move).isEmpty() == false) {

                                                pos_move = pos_move.above();

                                            }

                                        }

                                        if (Math.abs(pos.getY() - pos_move.getY()) > 3) {

                                            continue;

                                        }

                                        original_movedY = height.get(pos_move.getX() + "/" + pos_move.getZ());

                                    }

                                    if (level_server.isPositionEntityTicking(pos_move) == true) {

                                        // Place
                                        {

                                            type_test = LivingMechanics.getAreaType(level_accessor, pos_move, original_movedY, water_locations.isEmpty() == false, land_biomes.isEmpty() == false);

                                            if (type_test.isEmpty() == false && type_test.contains("|" + data.get(id).get("type") + "|") == true) {

                                                if (test(level_accessor, data.get(id), data.get(id).get("type"), height, water_locations, land_biomes, pos_move, original_movedY, false) == true) {

                                                    place(level_accessor, level_server, pos_move, id, false);

                                                }

                                            }

                                        }

                                    }

                                }

                            }

                            // Dead
                            {

                                if (Math.random() < Double.parseDouble(data.get(id).get("dead_chance"))) {

                                    level_accessor.removeBlock(pos, false);

                                }

                            }

                        }

                        // GameUtils.Misc.spawnParticle(level_server, pos.getCenter(), 0, 0, 0, 0, 1, "minecraft:flash");

                    }

                }

            }

        }

    }

    public static Object[] getSurroundingAreaData (LevelAccessor level_accessor, ServerLevel level_server, int start_posX, int start_posZ) {

        Map<String, Integer> height = new HashMap<>();
        List<BlockPos> water_locations = new ArrayList<>();
        Map<BlockPos, Holder<Biome>> land_biomes = new HashMap<>();
        BlockPos pos = null;
        int posX = 0;
        int posY = 0;
        int posZ = 0;

        for (int scanX = -16; scanX < 32; scanX++) {

            for (int scanZ = -16; scanZ < 32; scanZ++) {

                posX = start_posX + scanX;
                posZ = start_posZ + scanZ;
                posY = level_accessor.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, posX, posZ);
                pos = new BlockPos(posX, posY, posZ);
                height.put(posX + "/" + posZ, posY);

                if (level_accessor.isWaterAt(pos.below()) == true) {

                    water_locations.add(pos);

                } else {

                    land_biomes.put(pos, GameUtils.Space.getBiomeAt(level_server, pos));

                }

            }

        }

        return new Object[]{height, water_locations, land_biomes};

    }

    public static String getAreaType (LevelAccessor level_accessor, BlockPos pos, int originalY, boolean have_water_nearby, boolean have_land_nearby) {

        if (level_accessor.getBlockState(pos.below()).isAir() == false && level_accessor.getBlockState(pos).getCollisionShape(level_accessor, pos).isEmpty() == true) {

            if (pos.getY() < originalY) {

                if (level_accessor.isWaterAt(pos.below()) == false) {

                    if (level_accessor.getBlockState(pos.below()).getCollisionShape(level_accessor, pos.below()).isEmpty() == false) {

                        if (level_accessor.getBlockState(pos).canBeReplaced() == true) {

                            if (level_accessor.isWaterAt(pos) == true) {

                                // In Water
                                {

                                    if (level_accessor.isWaterAt(pos.above()) == true) {

                                        if (have_land_nearby == true) {

                                            return "|submerged_sharrow|";

                                        } else {

                                            return "|submerged_deep|";

                                        }

                                    } else {

                                        if (have_land_nearby == true) {

                                            return "|emergent|";

                                        }

                                    }

                                }


                            } else {

                                // Underground
                                {

                                    return "|cave|";

                                }

                            }

                        }

                    }

                }

            } else {

                if (level_accessor.isWaterAt(pos.below()) == true) {

                    // On Water
                    {

                        if (level_accessor.getBlockState(pos).canBeReplaced() == true) {

                            if (have_land_nearby == true) {

                                return "|floating_landside|";

                            } else {

                                return "|floating|";

                            }

                        }

                    }

                } else if (level_accessor.getBlockState(pos.below()).getCollisionShape(level_accessor, pos.below()).isEmpty() == false) {

                    // On Land
                    {

                        if (level_accessor.getBlockState(pos.above()).isAir() == true) {

                            if (have_water_nearby == true) {

                                if (level_accessor.getBlockState(pos).isAir() == true) {

                                    return "|normal|waterside|emergent|";

                                } else {

                                    if (level_accessor.getBlockState(pos).canBeReplaced() == true) {

                                        return "|waterside|emergent|";

                                    } else {

                                        return "|emergent|";

                                    }

                                }

                            } else {

                                if (level_accessor.getBlockState(pos).canBeReplaced() == true) {

                                    return "|normal|";

                                }

                            }

                        }

                    }

                }

            }

        }

        return "";

    }

    static boolean test (LevelAccessor level_accessor, Map<String, String> data, String type, Map<String, Integer> height, List<BlockPos> water_locations, Map<BlockPos, Holder<Biome>> land_biomes, BlockPos pos, int originalY, boolean is_world_gen) {

        boolean test_waterside = false;
        boolean test_landside = false;
        boolean test_cave = false;

        // Get What To Test
        {

            if (pos.getY() < originalY) {

                if (level_accessor.isWaterAt(pos) == true) {

                    if (land_biomes.isEmpty() == false) {

                        if (type.equals("emergent") == true) {

                            test_landside = true;

                        }

                    }

                } else {

                    if (type.equals("cave") == true) {

                        test_cave = true;

                    }

                }

            } else {

                if (water_locations.isEmpty() == false) {

                    if (land_biomes.isEmpty() == false) {

                        if (type.equals("emergent") == true) {

                            test_waterside = true;

                        } else if (type.equals("waterside") == true) {

                            test_waterside = true;

                        } else if (type.equals("floating_landside") == true) {

                            test_landside = true;

                        }

                    }

                }

            }

        }

        // Basic Test
        {

            if (test_landside == false) {

                if (land_biomes.containsKey(pos.atY(originalY)) == false || GameUtils.Misc.testCustomBiome(land_biomes.get(pos.atY(originalY)), data.get("biome")) == false) {

                    return false;

                }

            }

            if (GameUtils.Misc.testCustomBlock(level_accessor.getBlockState(pos.below()), data.get("ground_block")) == false) {

                return false;

            }

        }

        // Surrounding Area Test
        {

            if (test_waterside == true || test_landside == true || test_cave == true) {

                double distance = 0.0;

                if (test_waterside == true) {

                    {

                        distance = water_locations.stream().min(Comparator.comparingDouble(sort -> sort.getCenter().distanceTo(pos.getCenter()))).get().getCenter().distanceTo(pos.getCenter());

                    }

                } else if (test_landside == true) {

                    {

                        Map<Holder<Biome>, Double> nearest_land = new HashMap<>();

                        for (Map.Entry<BlockPos, Holder<Biome>> entry : land_biomes.entrySet()) {

                            distance = pos.getCenter().distanceTo(entry.getKey().getCenter());

                            if (nearest_land.getOrDefault(entry.getValue(), 64.0) > distance) {

                                nearest_land.put(entry.getValue(), distance);

                            }

                        }

                        distance = 64.0;

                        for (Holder<Biome> biome : nearest_land.keySet()) {

                            if (GameUtils.Misc.testCustomBiome(biome, data.get("biome")) == true) {

                                distance = Math.min(distance, nearest_land.get(biome));

                            }

                        }

                        if (distance == 64.0) {

                            return false;

                        }

                    }

                } else {

                    {

                        String[] split = new String[0];
                        int posX = 0;
                        int posY = 0;
                        int posZ = 0;
                        double test = 0;
                        distance = 64.0;

                        for (Map.Entry<String, Integer> entry : height.entrySet()) {

                            split = entry.getKey().split("/");
                            posX = Integer.parseInt(split[0]);
                            posZ = Integer.parseInt(split[1]);
                            posY = entry.getValue();
                            test = pos.getCenter().distanceTo(new Vec3(posX, posY, posZ));

                            if (distance > test) {

                                distance = test;

                            }

                        }

                        if (distance == 64.0) {

                            return false;

                        }

                    }

                }

                // Range Test
                {

                    double distance_test = Double.parseDouble(data.get("surrounding_area_distance")) + 1;

                    if (distance > distance_test) {

                        return false;

                    } else {

                        return Math.random() < (1.0 - (distance / distance_test));

                    }

                }

            }

        }

        return true;

    }

    public static void place (LevelAccessor level_accessor, ServerLevel level_server, BlockPos pos, String id, boolean is_world_gen) {

        if (CacheManager.SaveMap.existLogic("custom_placement", id) == false) {

            boolean custom = new File(Core.path_config + "/#dev/#temporary/custom_placement/" + id + ".txt").exists() == true;
            CacheManager.SaveMap.setLogic("custom_placement", id, custom);

        }

        if (CacheManager.SaveMap.getLogic("custom_placement", id) == true) {

            TXTFunction.run(level_accessor, level_server, pos, "custom_placement/" + id, true);

        } else {

            int type = 0;

            if (is_world_gen == false) {

                type = 3;

            }

            BlockState block = GameUtils.Tile.fromText(id.replace("-", ":"));

            if (level_accessor.isWaterAt(pos) == true) {

                block = GameUtils.Tile.setPropertyLogic(block, "waterlogged", true);

            }

            level_accessor.setBlock(pos, block, type);

        }

    }

}
