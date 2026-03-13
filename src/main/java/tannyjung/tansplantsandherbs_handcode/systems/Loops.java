package tannyjung.tansplantsandherbs_handcode.systems;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import tannyjung.tansplantsandherbs_handcode.systems.world_gen.LivingMechanics;

public class Loops {

    public static void tick (LevelAccessor level_accessor, ServerLevel level_server) {

        LivingMechanics.start(level_accessor, level_server);

    }

    public static void second (LevelAccessor level_accessor, ServerLevel level_server) {



    }

    public static void minute (LevelAccessor level_accessor, ServerLevel level_server) {



    }

}
