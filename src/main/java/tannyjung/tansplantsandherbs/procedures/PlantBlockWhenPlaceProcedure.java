package tannyjung.tansplantsandherbs.procedures;

import tannyjung.tansplantsandherbs_handcode.systems.living_mechanics.PlantBlock;

import tannyjung.tansplantsandherbs.TansplantsandherbsMod;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;

public class PlantBlockWhenPlaceProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (false) {
			if (world instanceof ServerLevel _level)
				_level.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, new Vec3(x, y, z), Vec2.ZERO, _level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(), "");
			TansplantsandherbsMod.LOGGER.info(entity);
		}
		PlantBlock.whenPlace(world, entity, BlockPos.containing(x, y, z));
	}
}