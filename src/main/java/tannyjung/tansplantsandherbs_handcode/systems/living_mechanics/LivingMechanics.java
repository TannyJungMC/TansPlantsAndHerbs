package tannyjung.tansplantsandherbs_handcode.systems.living_mechanics;

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
import tannyjung.tansplantsandherbs_core.game.GameUtils;
import tannyjung.tansplantsandherbs_core.outside.ConfigDynamic;

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
                String type = "";
                String type_area = "";
                int originalY = 0;
                BlockState ceil_block = null;

                for (int loop = 16; loop > 0; loop--) {

                    posX = start_posX + Mth.nextInt(RandomSource.create(), 0, 16);
                    posZ = start_posZ + Mth.nextInt(RandomSource.create(), 0, 16);

                    for (int scanY = 0; scanY > -64; scanY--) {

                        pos = new BlockPos(posX, height.get(posX + "/" + posZ) + scanY, posZ);
                        id = GameUtils.Tile.toText(level_accessor.getBlockState(pos))[0].replace(":", "-");

                        if (level_accessor.getBlockState(pos.above()).getCollisionShape(level_accessor, pos.above()).isEmpty() == false) {

                            ceil_block = level_accessor.getBlockState(pos.above());

                        }

                        if (data.containsKey(id) == true) {

                            for (int count = Integer.parseInt(data.get(id).get("spread_count")); count > 0; count--) {

                                if (Math.random() < Double.parseDouble(data.get(id).get("spread_chance"))) {

                                    // Move Pos
                                    {

                                        pos_move = pos.offset(Mth.nextInt(RandomSource.create(), -2, 2), 0, Mth.nextInt(RandomSource.create(), -2, 2));

                                        // Up-Down
                                        {

                                            if (data.get(id).get("type").equals("free_floating") == false) {

                                                while (level_accessor.getBlockState(pos_move.below()).getCollisionShape(level_accessor, pos_move.below()).isEmpty() == true) {

                                                    pos_move = pos_move.below();

                                                }

                                                while (level_accessor.getBlockState(pos_move).getCollisionShape(level_accessor, pos_move).isEmpty() == false) {

                                                    pos_move = pos_move.above();

                                                }

                                                if (Math.abs(pos.getY() - pos_move.getY()) > 3) {

                                                    continue;

                                                }

                                            }

                                        }

                                        originalY = height.get(pos_move.getX() + "/" + pos_move.getZ());

                                    }

                                    if (level_server.isPositionEntityTicking(pos_move) == true) {

                                        // Place
                                        {

                                            type = data.get(id).get("type");
                                            type_area = getAreaType(level_accessor, pos_move, originalY, water_locations.isEmpty() == false, land_biomes.isEmpty() == false);

                                            if (type.equals("special") == false && type_area.contains("|" + type + "|") == true) {

                                                if (test(level_accessor, data, height, water_locations, land_biomes, pos_move, ceil_block, id, true).isEmpty() == true) {

                                                    PlantBlock.place(level_accessor, level_server, pos_move, id, false);

                                                }

                                            }

                                        }

                                    }

                                }

                            }

                            // Dead
                            {

                                if (Math.random() < Double.parseDouble(data.get(id).get("dead_chance"))) {

                                    GameUtils.Tile.remove(level_accessor, level_server, pos, false);

                                }

                            }

                        }

                    }

                }

            }

        }

    }

    public static Object[] getSurroundingAreaData (LevelAccessor level_accessor, ServerLevel level_server, int start_posX, int start_posZ) {

        Map<String, Integer> height = new HashMap<>();
        List<BlockPos> water_locations = new ArrayList<>();
        Map<BlockPos, Holder<Biome>> land_biomes = new HashMap<>();
        Map<String, BlockState> ceil_block = new HashMap<>();
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

                    land_biomes.put(pos, GameUtils.Space.getBiomeAt(level_accessor, level_server, pos));

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

                        if (level_accessor.isWaterAt(pos) == true) {

                            // In Water
                            {

                                if (level_accessor.isWaterAt(pos.above()) == true) {

                                    if (have_land_nearby == true) {

                                        return "|submergent|floating_leaved|";

                                    } else {

                                        return "|marine|";

                                    }

                                } else if (level_accessor.getBlockState(pos.above()).isAir() == true) {

                                    if (have_land_nearby == true) {

                                        return "|emergent|";

                                    }

                                }

                            }


                        } else {

                            // Underground
                            {

                                if (level_accessor.getBlockState(pos.above()).isAir() == true) {

                                    return "|cave|";

                                }

                            }

                        }

                    }

                }

            } else {

                if (level_accessor.getBlockState(pos.above()).isAir() == true) {

                    if (level_accessor.isWaterAt(pos.below()) == true) {

                        // On Water
                        {

                            if (have_land_nearby == true) {

                                return "|free_floating|";

                            }

                        }

                    } else if (level_accessor.getBlockState(pos.below()).getCollisionShape(level_accessor, pos.below()).isEmpty() == false) {

                        // On Land
                        {

                            if (level_accessor.getBlockState(pos.above()).isAir() == true) {

                                if (have_water_nearby == true) {

                                    return "|terrestrial|riparian|emergent|";

                                } else {

                                    return "|terrestrial|";

                                }

                            }

                        }

                    }

                }

            }

        }

        return "";

    }

    public static String test (LevelAccessor level_accessor, Map<String, Map<String, String>> data, Map<String, Integer> height, List<BlockPos> water_locations, Map<BlockPos, Holder<Biome>> land_biomes, BlockPos pos, BlockState ceil_block, String id, boolean test_chance) {

        String type = data.get(id).get("type");
        int originalY = height.get(pos.getX() + "/" + pos.getZ());

        if (testPrioritization(level_accessor, data, pos, type) == false) {

            return "prioritization";

        }

        boolean test_area_waterside = false;
        boolean test_area_landside = false;
        boolean test_area_cave = false;

        // Get What To Test
        {

            if (type.equals("emergent") == true) {

                if (level_accessor.isWaterAt(pos) == true) {

                    test_area_landside = true;

                } else {

                    test_area_waterside = true;

                }

            } else if (type.equals("cave") == true) {

                test_area_cave = true;

            } else if (type.equals("riparian") == true) {

                test_area_waterside = true;

            } else if (type.equals("submergent") == true || type.equals("floating_leaved") == true || type.equals("free_floating") == true) {

                test_area_landside = true;

            }

        }

        // Test Map
        {

            if (water_locations.isEmpty() == true) {

                test_area_waterside = false;

            } else if (land_biomes.isEmpty() == true) {

                test_area_waterside = false;
                test_area_landside = false;
                test_area_cave = false;

            }

        }

        // Basic Test
        {

            if (test_area_landside == false && type.equals("special") == false) {

                if (land_biomes.containsKey(pos.atY(originalY)) == false || GameUtils.Misc.testCustomBiome(land_biomes.get(pos.atY(originalY)), data.get(id).get("biome")) == false) {

                    return "unsupported biome";

                }

            }

            if (GameUtils.Misc.testCustomBlock(level_accessor.getBlockState(pos.below()), data.get(id).get("ground_block")) == false) {

                return "unsupported ground block";

            }

            if (test_area_cave == true) {

                if (ceil_block != null && GameUtils.Misc.testCustomBlock(ceil_block, data.get(id).get("ground_block")) == false) {

                    return "unsupported cave ceiling block";

                }

            }

        }

        return testSurroundingArea(data.get(id), height, water_locations, land_biomes, pos, test_area_waterside, test_area_landside, test_area_cave, test_chance);

    }

    private static boolean testPrioritization (LevelAccessor level_accessor, Map<String, Map<String, String>> data, BlockPos pos, String type) {

        String blacklist = "";

        if (type.equals("terrestrial") == true) {

            blacklist = "|riparian|emergent|";

        } else if (type.equals("riparian") == true) {

            blacklist = "|emergent|";

        }

        if (blacklist.isEmpty() == false) {

            String id = GameUtils.Tile.toText(level_accessor.getBlockState(pos))[0].replace(":", "-");

            if (data.containsKey(id) == true) {

                return blacklist.contains("|" + data.get(id).get("type") + "|") == false;

            }

        }

        return true;

    }

    private static String testSurroundingArea (Map<String, String> data, Map<String, Integer> height, List<BlockPos> water_locations, Map<BlockPos, Holder<Biome>> land_biomes, BlockPos pos, boolean test_area_waterside, boolean test_area_landside, boolean test_area_cave, boolean test_chance) {

        if (test_area_waterside == true || test_area_landside == true || test_area_cave == true) {

            double distance_test = 0;
            double distance = 0.0;

            if (test_area_waterside == true) {

                {

                    distance_test = Double.parseDouble(data.get("distance_water"));
                    distance = water_locations.stream().min(Comparator.comparingDouble(sort -> sort.getCenter().distanceTo(pos.getCenter()))).get().getCenter().distanceTo(pos.getCenter());

                }

            } else if (test_area_landside == true) {

                {

                    distance_test = Double.parseDouble(data.get("distance_land"));
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

                        return "not found supported biome nearby";

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

                        return "this area is too deep";

                    }

                }

            }

            distance_test = distance_test + 1;

            if (distance > distance_test) {

                return "this area is too far from supported area";

            } else {

                if (test_chance == true) {

                    if (Math.random() >= (1.0 - (distance / distance_test))) {

                        return "chance";

                    }

                }

            }

        }

        return "";

    }

}
