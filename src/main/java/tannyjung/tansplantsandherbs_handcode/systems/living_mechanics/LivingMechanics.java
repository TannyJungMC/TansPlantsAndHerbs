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
import tannyjung.tansplantsandherbs_core.game.GameUtils;
import tannyjung.tansplantsandherbs_core.outside.ConfigDynamic;

import java.util.*;

public class LivingMechanics {

    private static int tick = 0;

    public static void start (LevelAccessor level_accessor, ServerLevel level_server) {

        tick = tick + 1;

        if (tick > 20) {

            tick = 0;
            runRandomChunk(level_accessor, level_server);

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

                Map<String, Map<String, String>> data = ConfigDynamic.getData("settings");
                Object[] surrounding_area_data = PlantBlock.getSurroundingAreaData(level_accessor, start_posX, start_posZ);
                Map<BlockPos, Holder<Biome>> biomes = (Map<BlockPos, Holder<Biome>>) surrounding_area_data[0];
                Map<String, Integer> height = (Map<String, Integer>) surrounding_area_data[1];
                Set<BlockPos> water_locations = (Set<BlockPos>) surrounding_area_data[2];

                BlockPos pos_move = null;
                int posX = 0;
                int posZ = 0;
                String id = "";
                String type = "";
                String type_area = "";
                int originalY = 0;
                BlockState ceil_block = null;

                for (int loop = 4; loop > 0; loop--) {

                    posX = start_posX + Mth.nextInt(RandomSource.create(), 0, 16);
                    posZ = start_posZ + Mth.nextInt(RandomSource.create(), 0, 16);

                    for (int scanY = 0; scanY > -64; scanY--) {

                        pos = new BlockPos(posX, height.get(posX + "/" + posZ) + scanY, posZ);
                        id = GameUtils.Tile.toText(level_accessor.getBlockState(pos))[0].replace(":", "-");

                        if (level_accessor.getBlockState(pos.above()).canBeReplaced() == false) {

                            ceil_block = level_accessor.getBlockState(pos.above());

                        }

                        if (data.containsKey(id) == true) {

                            if (data.get(id).get("enable_living_mechanics").equals("true") == true) {

                                // Spread
                                {

                                    for (int count = Integer.parseInt(data.get(id).get("spread_count")); count > 0; count--) {

                                        if (Math.random() < Double.parseDouble(data.get(id).get("spread_chance"))) {

                                            // Move Pos
                                            {

                                                pos_move = pos.offset(Mth.nextInt(RandomSource.create(), -2, 2), 0, Mth.nextInt(RandomSource.create(), -2, 2));

                                                // Up-Down
                                                {

                                                    if (data.get(id).get("type").equals("free_floating") == false) {

                                                        for (int scan = 5; scan > 0; scan--) {

                                                            if (level_accessor.getBlockState(pos_move.below()).canBeReplaced() == true) {

                                                                pos_move = pos_move.below();

                                                            } else {

                                                                break;

                                                            }

                                                        }

                                                        for (int scan = 5; scan > 0; scan--) {

                                                            if (level_accessor.getBlockState(pos_move).canBeReplaced() == false) {

                                                                pos_move = pos_move.above();

                                                            } else {

                                                                break;

                                                            }

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
                                                    type_area = PlantBlock.getAreaType(level_accessor, pos_move, originalY, water_locations);

                                                    if (type.isEmpty() == false && type_area.contains("|" + type + "|") == true) {

                                                        if (PlantBlock.test(level_accessor, data, height, water_locations, biomes, pos_move, ceil_block, id, true).isEmpty() == true) {

                                                            PlantBlock.place(level_accessor, level_server, pos_move, data.get(id), id, false);

                                                        }

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

                        } else {

                            // Remove Lone Special Parts
                            {

                                if (id.startsWith("tansplantsandherbs-plant_") == true) {

                                    if (level_accessor.getBlockState(pos.below()).canBeReplaced() == true) {

                                        GameUtils.Tile.remove(level_accessor, level_server, pos, false);

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
