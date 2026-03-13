package tannyjung.tansplantsandherbs_handcode.data;

import tannyjung.tansplantsandherbs_core.Core;
import tannyjung.tansplantsandherbs_core.outside.FileManager;

import java.io.*;

public class DataMigration {

    public static void run (String type) {

        if (type.contains("config") == true) {

            String path = Core.path_config + "/#dev/version.txt";
            File test_exist = new File(Core.path_config).getParentFile();
            String version = "";

            // Get Version
            {

                if (test_exist.exists() == false) {

                    version = "missing";

                } else {

                    for (String read_all : FileManager.readTXT(path)) {

                        version = read_all;

                    }

                }

            }

            if (version.equals("missing") == false && Core.data_structure_version_mod.equals(version) == false) {

                config.test(version);

            }

            FileManager.writeTXT(path, Core.data_structure_version_mod, false);

        }

        if (type.contains("world") == true) {

            String path = Core.path_world_mod + "/version.txt";
            File test_exist = new File(Core.path_world_mod).getParentFile();
            String version = "";

            // Get Version
            {

                if (test_exist.exists() == false) {

                    version = "missing";

                } else {

                    for (String read_all : FileManager.readTXT(path)) {

                        version = read_all;

                    }

                }

            }

            if (version.equals("missing") == false && Core.data_structure_version_mod.equals(version) == false) {

                world.test(version);

            }

            FileManager.writeTXT(path, Core.data_structure_version_mod, false);

        }

    }

    private static class config {

        private static void test (String version) {



        }

        private static class run {



        }

    }

    private static class world {

        private static void test (String version) {



        }

        private static class run {



        }

    }

}
