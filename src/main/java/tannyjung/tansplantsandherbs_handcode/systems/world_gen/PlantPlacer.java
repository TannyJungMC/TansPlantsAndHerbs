package tannyjung.tansplantsandherbs_handcode.systems.world_gen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import tannyjung.tansplantsandherbs_core.outside.ConfigDynamic;

import java.util.*;

public class PlantPlacer {

    public static void start (LevelAccessor level_accessor, ServerLevel level_server, ChunkPos chunk_pos) {

        int start_posX = chunk_pos.x * 16;
        int start_posZ = chunk_pos.z * 16;

        Map<String, Map<String, Map<String, String>>> data = ConfigDynamic.getData("settings", "type");
        Object[] surrounding_area_data = LivingMechanics.getSurroundingAreaData(level_accessor, level_server, start_posX, start_posZ);
        Map<String, Integer> height = (Map<String, Integer>) surrounding_area_data[0];
        List<BlockPos> water_locations = (List<BlockPos>) surrounding_area_data[1];
        Map<BlockPos, Holder<Biome>> land_biomes = (Map<BlockPos, Holder<Biome>>) surrounding_area_data[2];

        BlockPos pos = null;
        int posX = 0;
        int posZ = 0;
        String type = "";
        int originalY = 0;

        for (int scanX = 0; scanX < 16; scanX++) {

            for (int scanZ = 0; scanZ < 16; scanZ++) {

                posX = start_posX + scanX;
                posZ = start_posZ + scanZ;
                originalY = height.get(posX + "/" + posZ);

                for (int scanY = 0; scanY > -64; scanY--) {

                    pos = new BlockPos(posX, originalY + scanY, posZ);
                    type = LivingMechanics.getAreaType(level_accessor, pos, originalY, water_locations.isEmpty() == false, land_biomes.isEmpty() == false);

                    if (type.isEmpty() == false) {

                        for (String type_test : type.substring(1, type.length() - 1).split("\\|")) {

                            if (data.containsKey(type_test) == true) {

                                for (Map.Entry<String, Map<String, String>> entry : data.get(type_test).entrySet()) {

                                    if (LivingMechanics.test(level_accessor, level_server, entry.getValue(), height, water_locations, land_biomes, pos, originalY, true) == true) {

                                        LivingMechanics.place(level_accessor, level_server, pos, entry.getKey(), true);

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
