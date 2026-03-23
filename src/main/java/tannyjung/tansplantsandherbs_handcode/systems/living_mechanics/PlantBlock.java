package tannyjung.tansplantsandherbs_handcode.systems.living_mechanics;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import tannyjung.tansplantsandherbs_core.Core;
import tannyjung.tansplantsandherbs_core.game.GameUtils;
import tannyjung.tansplantsandherbs_core.outside.CacheManager;
import tannyjung.tansplantsandherbs_core.outside.ConfigDynamic;
import tannyjung.tansplantsandherbs_core.outside.FileManager;
import tannyjung.tansplantsandherbs_core.outside.TXTFunction;

import java.io.File;
import java.util.*;

public class PlantBlock {

    public static void whenClick (LevelAccessor level_accessor, Entity entity, BlockPos pos) {

        if (level_accessor.isClientSide() == true) {

            return;

        }

        ServerLevel level_server = (ServerLevel) level_accessor;
        ItemStack item = GameUtils.Item.getSlot(entity, EquipmentSlot.MAINHAND);
        String id = GameUtils.Tile.toText(level_accessor.getBlockState(pos))[0].replace(":", "-");
        boolean pass = false;

        if (item.getItem() == Items.SHEARS) {

            {

                if (getLootData(id).isEmpty() == true) {

                    GameUtils.Misc.summonText(level_server, pos.above().getCenter(), 0.5, "There is no loot for this / red", true);

                } else {

                    String[] split = new String[0];

                    for (String read_all : CacheManager.SaveMap.getTextList("loot", id)) {

                        split = read_all.split(" \\| ");

                        if (Math.random() < Double.parseDouble(split[0])) {

                            GameUtils.Item.spawn(level_server, pos.getCenter(), GameUtils.Item.fromID(split[1]));

                        }

                    }

                    GameUtils.Misc.playSound(level_server, pos, 2.0, 1.0, "minecraft:entity.sheep.shear");
                    pass = true;

                }

            }

        } else {

            if (GameUtils.Item.isTaggedAs(item, "minecraft:shovels") == true) {

                {

                    if (getLootData(id).isEmpty() == true) {

                        GameUtils.Misc.summonText(level_server, pos.above().getCenter(), 0.5, "You can not pick up this / red", true);

                    } else {

                        if (level_accessor.getBlockState(pos.below()).getBlock() == Blocks.GRASS_BLOCK) {

                            GameUtils.Tile.set(level_accessor, pos.below(), Blocks.DIRT.defaultBlockState(), false);

                        }

                        GameUtils.Item.spawn(level_server, pos.getCenter(), level_accessor.getBlockState(pos).getBlock().asItem().getDefaultInstance());
                        GameUtils.Misc.playSound(level_server, pos, 2.0, 0.0, "minecraft:item.shovel.flatten");
                        pass = true;

                    }

                }

            } else if (GameUtils.Item.isTaggedAs(item, "minecraft:swords") == true) {

                {

                    GameUtils.Misc.playSound(level_server, pos, 2.0, 2.0, "minecraft:entity.player.attack.sweep");
                    GameUtils.Misc.playSound(level_server, pos, 2.0, 0.75, "minecraft:block.grass.break");
                    pass = true;

                }

            } else if (GameUtils.Item.isTaggedAs(item, "minecraft:hoes") == true) {

                {

                    if (level_accessor.getBlockState(pos.below()).getBlock() == Blocks.GRASS_BLOCK) {

                        GameUtils.Tile.set(level_accessor, pos.below(), Blocks.DIRT.defaultBlockState(), false);

                    }

                    GameUtils.Misc.playSound(level_server, pos, 2.0, 0.0, "minecraft:item.hoe.till");
                    GameUtils.Misc.playSound(level_server, pos, 2.0, 0.75, "minecraft:block.grass.break");
                    pass = true;

                }

            }

        }

        if (pass == true) {

            GameUtils.Item.setCooldown(entity, item, 100);

            if (GameUtils.Mob.isCreativeMode(entity) == false) {

                GameUtils.Item.addDamage(item, 1);

            }

            GameUtils.Misc.spawnParticle(level_server, pos.getCenter().add(0.0, -0.25, 0.0), 0.25, 0.25, 0.25, 0.01, 10, "minecraft:campfire_cosy_smoke");
            GameUtils.Tile.remove(level_accessor, level_server, pos, false);
        }

    }

    public static void whenPlace (LevelAccessor level_accessor, Entity entity, BlockPos pos) {

        if (level_accessor.isClientSide() == true) {

            return;

        }

        String id = GameUtils.Tile.toText(level_accessor.getBlockState(pos))[0].replace(":", "-");
        Map<String, String> data = ConfigDynamic.getData("settings", "").get("").get(id);

        if (testPlace(level_accessor, pos, id, true, GameUtils.Mob.isCreativeMode(entity) == false) == true) {

            ServerLevel level_server = (ServerLevel) level_accessor;
            place(level_accessor, level_server, pos, data, id, false);

        }

    }

    public static void whenNeighbourUpdate (LevelAccessor level_accessor, BlockPos pos) {

        ServerLevel level_server = (ServerLevel) level_accessor;

        if (GameUtils.Mob.canTickingAt(level_server, pos) == false) {

            return;

        }

        Core.DelayedWorks.create(false, 5, () -> {

            String id = GameUtils.Tile.toText(level_accessor.getBlockState(pos))[0].replace(":", "-");

            if (testPlace(level_accessor, pos, id, false, false) == false) {

                GameUtils.Tile.remove(level_accessor, level_server, pos, false);

            }

        });

    }

    public static void whenPassThrough (LevelAccessor level_accessor, Entity entity, BlockPos pos) {

        if (level_accessor.isClientSide() == true) {

            return;

        }

        ServerLevel level_server = (ServerLevel) level_accessor;

        if (level_accessor.getBlockState(pos.below()).canBeReplaced() == true && level_accessor.isWaterAt(pos) == false && level_accessor.isWaterAt(pos.below()) == true) {

            // Move
            {

                double distanceX = Math.round(pos.getCenter().x - entity.position().x);
                double distanceZ = Math.round(pos.getCenter().z - entity.position().z);
                BlockPos pos_move = BlockPos.containing(pos.getCenter().add(distanceX, 0.0, distanceZ));

                if (level_accessor.getBlockState(pos_move).getCollisionShape(level_accessor, pos_move).isEmpty() == true) {

                    GameUtils.Tile.set(level_accessor, pos_move, level_accessor.getBlockState(pos), false);

                }

                GameUtils.Tile.remove(level_accessor, level_server, pos, false);
                GameUtils.Misc.playSound(level_server, pos_move, 0.5, 0.0, "minecraft:block.lily_pad.place");
                GameUtils.Misc.spawnParticle(level_server, pos_move.getCenter().add(0.0, -0.5, 0.0), 0.25, 0.25, 0.25, 0.0, 10, "minecraft:bubble");


            }

        } else {

            // Remove
            {

                boolean pass = false;

                if (entity.getDeltaMovement().y < -0.75) {

                    pass = true;

                } else {

                    if (Math.random() < 0.1) {

                        if (Math.abs(entity.getX() - entity.xo) > 0.25 || Math.abs(entity.getZ() - entity.zo) > 0.25) {

                            pass = true;

                        }

                    }

                }

                if (pass == true) {

                    GameUtils.Tile.remove(level_accessor, level_server, pos, false);

                    if (level_accessor.getBlockState(pos.below()).getBlock() == Blocks.GRASS_BLOCK) {

                        GameUtils.Tile.set(level_accessor, pos.below(), Blocks.DIRT.defaultBlockState(), false);

                    }

                    if (level_accessor.isWaterAt(pos.below()) == true) {

                        GameUtils.Misc.playSound(level_server, pos, 0.5, 0.0, "minecraft:block.lily_pad.place");
                        GameUtils.Misc.spawnParticle(level_server, pos.getCenter().add(0.0, -0.5, 0.0), 0.25, 0.25, 0.25, 0.0, 10, "minecraft:bubble");

                    } else {

                        GameUtils.Misc.playSound(level_server, pos, 1.0, 0.0, "minecraft:block.grass.break");
                        GameUtils.Misc.spawnParticle(level_server, pos.getCenter().add(0.0, -0.25, 0.0), 0.25, 0.25, 0.25, 0.01, 10, "minecraft:campfire_cosy_smoke");

                    }

                }

            }

        }

    }

    private static boolean testPlace (LevelAccessor level_accessor, BlockPos pos, String id, boolean message, boolean drop) {

        ServerLevel level_server = (ServerLevel) level_accessor;

        // Unloaded Area
        {

            if (level_server.isLoaded(pos.offset(16, 0, 16)) == false || level_server.isLoaded(pos.offset(16, 0, -16)) == false || level_server.isLoaded(pos.offset(-16, 0, 16)) == false || level_server.isLoaded(pos.offset(-16, 0, -16)) == false) {

                return false;

            }

        }

        String error = "";

        // Test
        {

            Map<String, Map<String, String>> data = ConfigDynamic.getData("settings", "").get("");
            Object[] surrounding_area_data = getSurroundingAreaData(level_accessor, level_server, (pos.getX() >> 4) * 16, (pos.getZ() >> 4) * 16);
            Map<String, Integer> height = (Map<String, Integer>) surrounding_area_data[0];
            List<BlockPos> water_locations = (List<BlockPos>) surrounding_area_data[1];
            Map<BlockPos, Holder<Biome>> biomes = (Map<BlockPos, Holder<Biome>>) surrounding_area_data[2];

            if (data.containsKey(id) == true) {

                {

                    String type = data.get(id).get("type");
                    String type_area = getAreaType(level_accessor, pos, height.get(pos.getX() + "/" + pos.getZ()), water_locations.isEmpty() == false, biomes.isEmpty() == false);

                    if (type_area.contains("|" + type + "|") == false) {

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

                        error = test(level_accessor, data, height, water_locations, biomes, pos, ceil_block, id, false);

                    }

                }

            } else {

                error = test(level_accessor, data, height, water_locations, biomes, pos, null, id, false);

            }

        }

        if (error.isEmpty() == false) {

            // Error Message
            {

                if (message == true) {

                    GameUtils.Misc.summonText(level_server, pos.getCenter(), 0.5, "Can not place here because " + error + " / red", true);

                }

                if (drop == true) {

                    GameUtils.Tile.removeDrop(level_accessor, level_server, pos);

                } else {

                    GameUtils.Tile.remove(level_accessor, level_server, pos, false);

                }

                return false;

            }

        }

        return true;

    }

    public static void place (LevelAccessor level_accessor, ServerLevel level_server, BlockPos pos, Map<String, String> data, String id, boolean is_world_gen) {

        if (CacheManager.SaveMap.existLogic("custom_placement", id) == false) {

            boolean exist = new File(Core.path_config + "/#dev/#temporary/custom_placement/" + id + ".txt").exists() == true;
            CacheManager.SaveMap.setLogic("custom_placement", id, exist);

        }

        if (CacheManager.SaveMap.getLogic("custom_placement", id) == true) {

            TXTFunction.run(level_accessor, level_server, pos, "custom_placement/" + id, true);

        } else {

            String block_id = GameUtils.Misc.testVariant(data.get("variants_center"));

            if (block_id.isEmpty() == true) {

                block_id = id.replace("-", ":");

            }

            if (data.get("type").equals("free_floating") == true) {

                {

                    BlockState block = GameUtils.Tile.fromText(block_id);
                    block = GameUtils.Tile.randomRotation(block);
                    GameUtils.Tile.set(level_accessor, pos, block, is_world_gen);

                }

            } else if (data.get("type").equals("floating_leaved") == true) {

                {

                    BlockState block = GameUtils.Tile.fromText(block_id);
                    GameUtils.Tile.set(level_accessor, pos, block, is_world_gen);
                    String variant = "";

                    for (int scanY = 1; scanY <= Integer.parseInt(data.get("deep")); scanY++) {

                        pos = pos.above();

                        if (level_accessor.isWaterAt(pos) == true) {

                            variant = GameUtils.Misc.testVariant(data.get("variants_middle"));

                            if (variant.isEmpty() == false) {

                                block = GameUtils.Tile.fromText(variant);

                            } else {

                                block = GameUtils.Tile.fromText(block_id + "_middle");

                            }

                            GameUtils.Tile.set(level_accessor, pos, block, is_world_gen);

                        } else {

                            variant = GameUtils.Misc.testVariant(data.get("variants_top"));

                            if (variant.isEmpty() == false) {

                                block = GameUtils.Tile.fromText(variant);

                            } else {

                                block = GameUtils.Tile.fromText(block_id + "_top");

                            }

                            block = GameUtils.Tile.randomRotation(block);
                            GameUtils.Tile.set(level_accessor, pos, block, is_world_gen);
                            break;

                        }

                    }

                }

            } else {

                BlockState block = GameUtils.Tile.fromText(block_id);
                GameUtils.Tile.set(level_accessor, pos, block, is_world_gen);

            }

        }

    }

    private static List<String> getLootData (String id) {

        if (CacheManager.SaveMap.existTextList("loot", id) == false) {

            CacheManager.SaveMap.setTextList("loot", id, FileManager.readTXT(Core.path_config + "/#dev/#temporary/loots/" + id + ".txt"));

        }

        return CacheManager.SaveMap.getTextList("loot", id);

    }

    public static Object[] getSurroundingAreaData (LevelAccessor level_accessor, ServerLevel level_server, int start_posX, int start_posZ) {

        Map<String, Integer> height = new HashMap<>();
        List<BlockPos> water_locations = new ArrayList<>();
        Map<BlockPos, Holder<Biome>> biomes = new HashMap<>();
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

                }

                biomes.put(pos, GameUtils.Space.getBiomeAt(level_accessor, level_server, pos));

            }

        }

        return new Object[]{height, water_locations, biomes};

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

    public static String test (LevelAccessor level_accessor, Map<String, Map<String, String>> data, Map<String, Integer> height, List<BlockPos> water_locations, Map<BlockPos, Holder<Biome>> biomes, BlockPos pos, BlockState ceil_block, String id, boolean test_chance) {

        if (data.containsKey(id) == true) {

            String type = data.get(id).get("type");

            // Non-Special
            {

                if (testPrioritization(level_accessor, data, pos, type) == false) {

                    return "prioritization";

                }

                boolean test_area_waterside = false;
                boolean test_area_landside = false;
                boolean test_area_cave = false;
                boolean test_center_biome = false;
                boolean test_ground_block = true;
                boolean test_deep = false;

                // Get What To Test
                {

                    if (type.equals("emergent") == true) {

                        if (level_accessor.isWaterAt(pos) == true) {

                            test_area_landside = true;

                        } else {

                            if (water_locations.isEmpty() == false) {

                                test_area_waterside = true;
                                test_center_biome = true;

                            }

                        }

                    } else if (type.equals("cave") == true) {

                        test_area_cave = true;
                        test_center_biome = true;

                    } else if (type.equals("riparian") == true) {

                        if (water_locations.isEmpty() == false) {

                            test_area_waterside = true;
                            test_center_biome = true;

                        }

                    } else if (type.equals("submergent") == true) {

                        test_area_landside = true;

                    } else if (type.equals("floating_leaved") == true) {

                        test_area_landside = true;
                        test_deep = true;

                    } else if (type.equals("free_floating") == true) {

                        test_area_landside = true;
                        test_ground_block = false;

                    }

                }

                if (test_center_biome == true) {

                    {

                        int originalY = height.get(pos.getX() + "/" + pos.getZ());

                        if (biomes.containsKey(pos.atY(originalY)) == false || GameUtils.Misc.testBiome(biomes.get(pos.atY(originalY)), data.get(id).get("biome")) == false) {

                            return "unsupported biome";

                        }

                    }

                }

                if (test_ground_block == true) {

                    {

                        if (GameUtils.Misc.testBlock(level_accessor.getBlockState(pos.below()), data.get(id).get("ground_block")) == false) {

                            return "unsupported ground block";

                        }

                    }

                }

                if (test_area_cave == true) {

                    {

                        if (ceil_block != null && GameUtils.Misc.testBlock(ceil_block, data.get(id).get("ground_block")) == false) {

                            return "unsupported cave ceiling block";

                        }

                    }

                }

                if (test_deep == true) {

                    {

                        if (height.get(pos.getX() + "/" + pos.getZ()) - pos.getY() > Integer.parseInt(data.get(id).get("deep"))) {

                            return "too deep";

                        }

                    }

                }

                return testAreaDistance(data.get(id), height, water_locations, biomes, pos, test_area_waterside, test_area_landside, test_area_cave, test_chance);

            }

        } else {

            // Special
            {

                if (level_accessor.getBlockState(pos.below()).canBeReplaced() == true) {

                    return "unsupported ground block";

                }

            }

        }

        return "";

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

    private static String testAreaDistance (Map<String, String> data, Map<String, Integer> height, List<BlockPos> water_locations, Map<BlockPos, Holder<Biome>> biomes, BlockPos pos, boolean test_area_waterside, boolean test_area_landside, boolean test_area_cave, boolean test_chance) {

        if (test_area_waterside == true || test_area_landside == true || test_area_cave == true) {

            double distance_test = 0;
            double distance = 0.0;

            if (test_area_waterside == true) {

                {

                    if (data.containsKey("distance_water") == true) {

                        distance_test = Double.parseDouble(data.get("distance_water"));
                        distance = water_locations.stream().min(Comparator.comparingDouble(sort -> sort.getCenter().distanceTo(pos.getCenter()))).get().getCenter().distanceTo(pos.getCenter());

                    }

                }

            } else if (test_area_landside == true) {

                {

                    if (data.containsKey("distance_land") == true) {

                        distance_test = Double.parseDouble(data.get("distance_land"));
                        Map<Holder<Biome>, Double> nearest_land = new HashMap<>();

                        for (Map.Entry<BlockPos, Holder<Biome>> entry : biomes.entrySet()) {

                            distance = pos.getCenter().distanceTo(entry.getKey().getCenter());

                            if (nearest_land.getOrDefault(entry.getValue(), 64.0) > distance) {

                                nearest_land.put(entry.getValue(), distance);

                            }

                        }

                        distance = 64.0;

                        for (Holder<Biome> biome : nearest_land.keySet()) {

                            if (GameUtils.Misc.testBiome(biome, data.get("biome")) == true) {

                                distance = Math.min(distance, nearest_land.get(biome));

                            }

                        }

                        if (distance == 64.0) {

                            return "not found supported biome nearby";

                        }

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
