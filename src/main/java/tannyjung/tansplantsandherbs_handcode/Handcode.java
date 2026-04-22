package tannyjung.tansplantsandherbs_handcode;

import tannyjung.tansplantsandherbs_core.Core;
import tannyjung.tansplantsandherbs_core.outside.ConfigClassic;
import tannyjung.tansplantsandherbs_core.outside.ConfigDynamic;
import tannyjung.tansplantsandherbs_core.outside.CustomPackOrganizing;
import tannyjung.tansplantsandherbs_core.outside.OutsideUtils;

import java.util.Map;

public class Handcode {

    public static void start () {

        Core.data_structure_version_core = 1;
        Core.data_structure_version_mod = "1.0.0";
        Core.data_structure_version_pack = "1.0.0";
        Core.tanny_pack_type = "Alpha";

        Core.mod_name = "Tan's Plants and Herbs";
        Core.mod_id = "tansplantsandherbs";
        Core.mod_id_short = "P";
        Core.github_pack = "TansPlantsAndHerbs-Main-Pack";
        Core.wiki = "https://sites.google.com/view/tannyjung/minecraft-mods/tans-plants-and-herbs";

        Core.have_world_data_cleaner = false;

    }

    public static void repairData () {

        CustomPackOrganizing.start("", "settings", "");

        ConfigDynamic.reorganize("settings", "settings", """
				enable_world_gen = false
				# X
				enable_living_mechanics = false
				# X
				biome = none
				# X
				ground_block = none
				# X
				rarity = 0
				# X
				spread_count = 0
				# X
				spread_chance = 0.0
				# X
				dead_chance = 0.0
				# X
				type = none
				# X
				distance_water = 0
				# X
				distance_biome = 0
				# X
				deep = 0
				# X
				variants_center = none
				# X
				variants_middle = none
				# X
				variants_top = none
				# X
				""");

    }

    public static class Config {

        public static boolean auto_check_update = false;
        public static boolean wip_version = false;

        public static boolean developer_mode = false;
        public static boolean world_gen_icon = false;

        public static void repair () {

            String write = """
                ----------------------------------------------------------------------------------------------------
                TannyJung's Main Pack
                ----------------------------------------------------------------------------------------------------
                
                auto_check_update = true
                | Check for new update from GitHub every time the world starts
                
                wip_version = false
                | Use development version of the pack, instead of release version. Not recommended for game play, as it's still in development, it might unstable. Sometimes it needed development version of the mod.
                
                ----------------------------------------------------------------------------------------------------
                Miscellaneous
                ----------------------------------------------------------------------------------------------------
                
                developer_mode = false
                | Enable some features for debugging such as detailed error messages, info overlay in-game, etc.
                
                ----------------------------------------------------------------------------------------------------
                """;

            ConfigClassic.repair(Core.path_config + "/config.txt", write);

        }

        public static void apply () {

            Map<String, String> data = ConfigClassic.getValues(Core.path_config + "/config.txt");

            auto_check_update = Boolean.parseBoolean(data.get("auto_check_update"));
            wip_version = Boolean.parseBoolean(data.get("wip_version"));

            developer_mode = Boolean.parseBoolean(data.get("developer_mode"));
            world_gen_icon = Boolean.parseBoolean(data.get("world_gen_icon"));

        }

    }

    public static class DataMigration {

        public static void runConfig (String version) {

            if (version.isEmpty() == true) {

                {

                    Core.logger.info("Running config data migration for failed condition");


                }

            }

            if (OutsideUtils.testVersion("1.0.0", version).equals("outdated") == true) {

                {

                    Core.logger.info("Running config data migration for 1.0.0");


                }

            }

        }

        public static void runWorld (String version) {

            if (version.isEmpty() == true) {

                {

                    Core.logger.info("Running world data migration for failed condition");


                }

            }

            if (OutsideUtils.testVersion("1.0.0", version).equals("outdated") == true) {

                {

                    Core.logger.info("Running world data migration for 1.0.0");


                }

            }

        }

    }

}