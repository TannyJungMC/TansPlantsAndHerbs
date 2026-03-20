/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package tannyjung.tansplantsandherbs.init;

import tannyjung.tansplantsandherbs.block.*;
import tannyjung.tansplantsandherbs.TansplantsandherbsMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

public class TansplantsandherbsModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, TansplantsandherbsMod.MODID);
	public static final RegistryObject<Block> GARDEN_SOIL;
	public static final RegistryObject<Block> PLANT_EMERGENT_CATTAIL;
	public static final RegistryObject<Block> PLANT_WATERSIDE_REED;
	public static final RegistryObject<Block> PLANT_WATERSIDE_GRASS;
	public static final RegistryObject<Block> PLANT_FLOATING_DUCKWEED;
	public static final RegistryObject<Block> PLANT_SUBMERGED_NYMPHAEA_MIDDLE;
	public static final RegistryObject<Block> PLANT_SUBMERGED_NYMPHAEA_TOP;
	public static final RegistryObject<Block> PLANT_SUBMERGED_NYMPHAEA_TOP_FLOWERING;
	public static final RegistryObject<Block> PLANT_SUBMERGED_NYMPHAEA;
	static {
		GARDEN_SOIL = REGISTRY.register("garden_soil", GardenSoilBlock::new);
		PLANT_EMERGENT_CATTAIL = REGISTRY.register("plant_emergent_cattail", PlantEmergentCattailBlock::new);
		PLANT_WATERSIDE_REED = REGISTRY.register("plant_waterside_reed", PlantWatersideReedBlock::new);
		PLANT_WATERSIDE_GRASS = REGISTRY.register("plant_waterside_grass", PlantWatersideGrassBlock::new);
		PLANT_FLOATING_DUCKWEED = REGISTRY.register("plant_floating_duckweed", PlantFloatingDuckweedBlock::new);
		PLANT_SUBMERGED_NYMPHAEA_MIDDLE = REGISTRY.register("plant_submerged_nymphaea_middle", PlantSubmergedNymphaeaMiddleBlock::new);
		PLANT_SUBMERGED_NYMPHAEA_TOP = REGISTRY.register("plant_submerged_nymphaea_top", PlantSubmergedNymphaeaTopBlock::new);
		PLANT_SUBMERGED_NYMPHAEA_TOP_FLOWERING = REGISTRY.register("plant_submerged_nymphaea_top_flowering", PlantSubmergedNymphaeaTopFloweringBlock::new);
		PLANT_SUBMERGED_NYMPHAEA = REGISTRY.register("plant_submerged_nymphaea", PlantSubmergedNymphaeaBlock::new);
	}
	// Start of user code block custom blocks
	// End of user code block custom blocks
}