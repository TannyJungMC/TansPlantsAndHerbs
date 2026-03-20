package tannyjung.tansplantsandherbs_handcode.systems.world_gen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import tannyjung.tansplantsandherbs_core.outside.ConfigDynamic;
import tannyjung.tansplantsandherbs_handcode.systems.living_mechanics.LivingMechanics;

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
        int originalY = 0;
        String type_area = "";
        BlockState ceil_block = null;

        for (int scanX = 0; scanX < 16; scanX++) {

            for (int scanZ = 0; scanZ < 16; scanZ++) {

                posX = start_posX + scanX;
                posZ = start_posZ + scanZ;
                originalY = height.get(posX + "/" + posZ);

                for (int scanY = 0; scanY > -32; scanY--) {

                    pos = new BlockPos(posX, originalY + scanY, posZ);
                    type_area = LivingMechanics.getAreaType(level_accessor, pos, originalY, water_locations.isEmpty() == false, land_biomes.isEmpty() == false);

                    if (level_accessor.getBlockState(pos.above()).getCollisionShape(level_accessor, pos.above()).isEmpty() == false) {

                        ceil_block = level_accessor.getBlockState(pos.above());

                    }

                    if (type_area.isEmpty() == false) {

                        for (String type_test : type_area.substring(1, type_area.length() - 1).split("\\|")) {

                            if (data.containsKey(type_test) == true) {

                                for (Map.Entry<String, Map<String, String>> entry : data.get(type_test).entrySet()) {

                                    if (entry.getValue().get("enable_world_gen").equals("true") == true) {

                                        if (Math.random() < Double.parseDouble(entry.getValue().get("rarity"))) {

                                            if (LivingMechanics.test(level_accessor, data.get(type_test), height, water_locations, land_biomes, pos, ceil_block, entry.getKey(), true).isEmpty() == true) {

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

    }

}
