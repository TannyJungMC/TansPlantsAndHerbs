package tannyjung.tansplantsandherbs_handcode;

import tannyjung.tansplantsandherbs_core.Core;
import tannyjung.tansplantsandherbs_core.game.GameUtils;
import tannyjung.tansplantsandherbs_handcode.config.FileConfig;

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

    }

    public static void registry () {



    }

}
