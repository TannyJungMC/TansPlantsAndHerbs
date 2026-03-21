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
import tannyjung.tansplantsandherbs_core.Core;
import tannyjung.tansplantsandherbs_core.game.GameUtils;
import tannyjung.tansplantsandherbs_core.outside.CacheManager;
import tannyjung.tansplantsandherbs_core.outside.ConfigDynamic;
import tannyjung.tansplantsandherbs_core.outside.FileManager;
import tannyjung.tansplantsandherbs_core.outside.TXTFunction;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

                List<String> loot = getLootData(id);

                if (loot.isEmpty() == false) {

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

                    List<String> loot = getLootData(id);

                    if (loot.isEmpty() == false) {

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

            GameUtils.Tile.remove(level_accessor, level_server, pos, false);
            GameUtils.Misc.spawnParticle(level_server, pos.getCenter().add(0.0, -0.25, 0.0), 0.25, 0.25, 0.25, 0.01, 10, "minecraft:campfire_cosy_smoke");

        }

    }

    public static void whenPlace (LevelAccessor level_accessor, Entity entity, BlockPos pos) {

        if (level_accessor.isClientSide() == true) {

            return;

        }

        String id = GameUtils.Tile.toText(level_accessor.getBlockState(pos))[0].replace(":", "-");

        if (testPlace(level_accessor, pos, id, true, GameUtils.Mob.isCreativeMode(entity) == false) == true) {

            ServerLevel level_server = (ServerLevel) level_accessor;
            place(level_accessor, level_server, pos, id, false);

        }

    }

    public static void whenNeighbourUpdate (LevelAccessor level_accessor, BlockPos pos) {

        ServerLevel level_server = (ServerLevel) level_accessor;

        if (GameUtils.Mob.canTickingAt(level_server, pos) == false) {

            return;

        }

        Core.DelayedWorks.create(false, 5, () -> {

            String id = GameUtils.Tile.toText(level_accessor.getBlockState(pos))[0].replace(":", "-");
            testPlace(level_accessor, pos, id, false, false);

        });

    }

    private static boolean testPlace (LevelAccessor level_accessor, BlockPos pos, String id, boolean message, boolean drop) {

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

                Map<String, Map<String, String>> data = ConfigDynamic.getData("settings", "").get("");

                if (data.containsKey(id) == false) {

                    error = "data not found";

                } else {

                    Object[] surrounding_area_data = LivingMechanics.getSurroundingAreaData(level_accessor, level_server, (pos.getX() >> 4) * 16, (pos.getZ() >> 4) * 16);
                    Map<String, Integer> height = (Map<String, Integer>) surrounding_area_data[0];
                    List<BlockPos> water_locations = (List<BlockPos>) surrounding_area_data[1];
                    Map<BlockPos, Holder<Biome>> land_biomes = (Map<BlockPos, Holder<Biome>>) surrounding_area_data[2];

                    String type = data.get(id).get("type");
                    String type_area = LivingMechanics.getAreaType(level_accessor, pos, height.get(pos.getX() + "/" + pos.getZ()), water_locations.isEmpty() == false, land_biomes.isEmpty() == false);

                    if (type.equals("special") == false && type_area.contains("|" + type + "|") == false) {

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

                GameUtils.Tile.remove(level_accessor, level_server, pos, false);

            }

            return false;

        }

        return true;

    }

    private static List<String> getLootData (String id) {

        if (CacheManager.SaveMap.existTextList("loot", id) == false) {

            CacheManager.SaveMap.setTextList("loot", id, FileManager.readTXT(Core.path_config + "/#dev/#temporary/loots/" + id + ".txt"));

        }

        return CacheManager.SaveMap.getTextList("loot", id);

    }

    private static boolean placeCustom (LevelAccessor level_accessor, ServerLevel level_server, BlockPos pos, String id) {

        if (CacheManager.SaveMap.existLogic("custom_placement", id) == false) {

            boolean custom = new File(Core.path_config + "/#dev/#temporary/custom_placement/" + id + ".txt").exists() == true;
            CacheManager.SaveMap.setLogic("custom_placement", id, custom);

        }

        if (CacheManager.SaveMap.getLogic("custom_placement", id) == true) {

            TXTFunction.run(level_accessor, level_server, pos, "custom_placement/" + id, true);
            return true;

        }

        return false;

    }

    public static void place (LevelAccessor level_accessor, ServerLevel level_server, BlockPos pos, String id, boolean is_world_gen) {

        if (PlantBlock.placeCustom(level_accessor, level_server, pos, id) == false) {

            BlockState block = GameUtils.Tile.fromText(id.replace("-", ":"));
            GameUtils.Tile.set(level_accessor, pos, block, is_world_gen);

        }

    }

}
