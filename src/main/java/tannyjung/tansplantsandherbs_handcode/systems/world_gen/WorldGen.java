package tannyjung.tansplantsandherbs_handcode.systems.world_gen;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class WorldGen {

    public static void stepBeforePlants (LevelAccessor level_accessor, ServerLevel level_server, ChunkGenerator chunk_generator, String dimension, ChunkPos chunk_pos) {



    }

    public static void stepLast (LevelAccessor level_accessor, ServerLevel level_server, ChunkGenerator chunk_generator, String dimension, ChunkPos chunk_pos) {

        PlantPlacer.start(level_accessor, level_server, chunk_pos);

    }

    public static void stepEnd (String dimension, ChunkPos chunk_pos) {



    }

}
