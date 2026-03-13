/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package tannyjung.tansplantsandherbs.init;

import tannyjung.tansplantsandherbs.block.PlantSmallBlock;
import tannyjung.tansplantsandherbs.block.PlantLargeReedBlock;
import tannyjung.tansplantsandherbs.block.PlantLargeGrassBlock;
import tannyjung.tansplantsandherbs.block.PlantLargeCattailBlock;
import tannyjung.tansplantsandherbs.block.PlantFloatingDuckweedBlock;
import tannyjung.tansplantsandherbs.TansplantsandherbsMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

public class TansplantsandherbsModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, TansplantsandherbsMod.MODID);
	public static final RegistryObject<Block> PLANT_LARGE_CATTAIL;
	public static final RegistryObject<Block> PLANT_LARGE_REED;
	public static final RegistryObject<Block> PLANT_LARGE_GRASS;
	public static final RegistryObject<Block> PLANT_FLOATING_DUCKWEED;
	public static final RegistryObject<Block> PLANT_SMALL;
	static {
		PLANT_LARGE_CATTAIL = REGISTRY.register("plant_large_cattail", PlantLargeCattailBlock::new);
		PLANT_LARGE_REED = REGISTRY.register("plant_large_reed", PlantLargeReedBlock::new);
		PLANT_LARGE_GRASS = REGISTRY.register("plant_large_grass", PlantLargeGrassBlock::new);
		PLANT_FLOATING_DUCKWEED = REGISTRY.register("plant_floating_duckweed", PlantFloatingDuckweedBlock::new);
		PLANT_SMALL = REGISTRY.register("plant_small", PlantSmallBlock::new);
	}
	// Start of user code block custom blocks
	// End of user code block custom blocks
}