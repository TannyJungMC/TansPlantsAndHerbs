package tannyjung.tansplantsandherbs_handcode.systems.world_gen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import tannyjung.tansplantsandherbs_core.Core;
import tannyjung.tansplantsandherbs_core.outside.CacheManager;
import tannyjung.tansplantsandherbs_core.outside.ConfigDynamic;
import tannyjung.tansplantsandherbs_handcode.systems.living_mechanics.PlantBlock;

import java.util.*;

public class PlantPlacer {

    public static void start (LevelAccessor level_accessor, ServerLevel level_server, ChunkPos chunk_pos) {

        Core.GlobalLocking.test();

        {

            place(level_accessor, level_server, chunk_pos);

        }

    }

    public static void place (LevelAccessor level_accessor, ServerLevel level_server, ChunkPos chunk_pos) {

        int start_posX = chunk_pos.x * 16;
        int start_posZ = chunk_pos.z * 16;

        Map<String, Map<String, String>> data = ConfigDynamic.getData("settings");
        Object[] surrounding_area_data = PlantBlock.getSurroundingAreaData(level_accessor, start_posX, start_posZ);
        Map<BlockPos, Holder<Biome>> biomes = (Map<BlockPos, Holder<Biome>>) surrounding_area_data[0];
        Map<String, Integer> height = (Map<String, Integer>) surrounding_area_data[1];
        Set<BlockPos> water_locations = (Set<BlockPos>) surrounding_area_data[2];

        BlockPos pos = null;
        int posX = 0;
        int posZ = 0;
        int originalY = 0;
        String type_area = "";
        BlockState ceil_block = null;
        Set<String> set_plant = null;

        for (int scanX = 0; scanX < 16; scanX++) {

            for (int scanZ = 0; scanZ < 16; scanZ++) {

                posX = start_posX + scanX;
                posZ = start_posZ + scanZ;
                originalY = height.get(posX + "/" + posZ);

                for (int scanY = 0; scanY > -32; scanY--) {

                    pos = new BlockPos(posX, originalY + scanY, posZ);
                    type_area = PlantBlock.getAreaType(level_accessor, pos, originalY, water_locations);

                    if (level_accessor.getBlockState(pos.above()).canBeReplaced() == false) {

                        ceil_block = level_accessor.getBlockState(pos.above());

                    }

                    if (type_area.isEmpty() == false) {

                        for (String type_test : type_area.substring(1, type_area.length() - 1).split("\\|")) {

                            // Get Set
                            {

                                set_plant = CacheManager.DataText.getSet("set_plant").get(type_test);

                                if (set_plant == null) {

                                    set_plant = new HashSet<>();

                                    for (Map.Entry<String, Map<String, String>> entry : data.entrySet()) {

                                        if (entry.getValue().get("enable_world_gen").equals("true") == true && entry.getValue().get("type").equals(type_test) == true) {

                                            set_plant.add(entry.getKey());

                                        }

                                    }

                                    CacheManager.DataText.setSet("set_plant", type_test, set_plant);

                                }

                            }

                            for (String scan : set_plant) {

                                if (Math.random() < Double.parseDouble(data.get(scan).get("rarity"))) {

                                    if (PlantBlock.test(level_accessor, data, height, water_locations, biomes, pos, ceil_block, scan, true).isEmpty() == true) {

                                        PlantBlock.place(level_accessor, level_server, pos, data.get(scan), scan, true);
                                        break;

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
