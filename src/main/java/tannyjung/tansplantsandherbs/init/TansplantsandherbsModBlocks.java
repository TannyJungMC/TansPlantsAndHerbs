/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package tannyjung.tansplantsandherbs.init;

import tannyjung.tansplantsandherbs.block.PlantCattailBlock;
import tannyjung.tansplantsandherbs.block.PlantBushBigBlock;
import tannyjung.tansplantsandherbs.TansplantsandherbsMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

public class TansplantsandherbsModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, TansplantsandherbsMod.MODID);
	public static final RegistryObject<Block> PLANT_CATTAIL;
	public static final RegistryObject<Block> PLANT_BUSH_BIG;
	static {
		PLANT_CATTAIL = REGISTRY.register("plant_cattail", PlantCattailBlock::new);
		PLANT_BUSH_BIG = REGISTRY.register("plant_bush_big", PlantBushBigBlock::new);
	}
	// Start of user code block custom blocks
	// End of user code block custom blocks
}