package tannyjung.tansplantsandherbs_handcode;

import tannyjung.tansplantsandherbs_core.outside.CustomPackOrganizing;
import tannyjung.tansplantsandherbs_handcode.config.FileConfig;
import tannyjung.tansplantsandherbs_core.outside.ConfigWorldGen;

public class DataRepair {

    public static void start () {

        CustomPackOrganizing.start("", "presets/world_gen");

        FileConfig.repair();
        ConfigWorldGen.reorganize();
        FileConfig.apply();

    }

}
