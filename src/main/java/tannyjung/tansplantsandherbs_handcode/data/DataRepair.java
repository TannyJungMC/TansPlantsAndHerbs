package tannyjung.tansplantsandherbs_handcode.data;

import tannyjung.tansplantsandherbs_core.outside.CustomPackOrganizing;
import tannyjung.tansplantsandherbs_core.outside.ConfigDynamic;

public class DataRepair {

    public static void start () {

        CustomPackOrganizing.start("");
        FileConfig.repair();
        FileConfig.apply();
        ConfigDynamic.reorganize("settings", "settings");

    }

}
