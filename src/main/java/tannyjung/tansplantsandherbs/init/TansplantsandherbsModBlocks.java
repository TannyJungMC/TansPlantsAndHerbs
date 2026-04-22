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
	public static final RegistryObject<Block> PLANT_FLOATING_LEAVED_NYMPHAEA;
	public static final RegistryObject<Block> PLANT_FREE_FLOATING_DUCKWEED;
	public static final RegistryObject<Block> PLANT_RIPARIAN_GRASS;
	public static final RegistryObject<Block> PLANT_RIPARIAN_REED;
	public static final RegistryObject<Block> PLANT_EMERGENT_TARO;
	public static final RegistryObject<Block> PLANT_FREE_FLOATING_WATER_HYACINTH;
	public static final RegistryObject<Block> PLANT_FREE_FLOATING_WATER_HYACINTH_FLOWERING;
	public static final RegistryObject<Block> PLANT_FLOATING_LEAVED_NYMPHAEA_PART_MIDDLE;
	public static final RegistryObject<Block> PLANT_FLOATING_LEAVED_NYMPHAEA_PART_TOP;
	public static final RegistryObject<Block> PLANT_FLOATING_LEAVED_NYMPHAEA_PART_TOP_FLOWERING;
	static {
		GARDEN_SOIL = REGISTRY.register("garden_soil", GardenSoilBlock::new);
		PLANT_EMERGENT_CATTAIL = REGISTRY.register("plant_emergent_cattail", PlantEmergentCattailBlock::new);
		PLANT_FLOATING_LEAVED_NYMPHAEA = REGISTRY.register("plant_floating_leaved_nymphaea", PlantFloatingLeavedNymphaeaBlock::new);
		PLANT_FREE_FLOATING_DUCKWEED = REGISTRY.register("plant_free_floating_duckweed", PlantFreeFloatingDuckweedBlock::new);
		PLANT_RIPARIAN_GRASS = REGISTRY.register("plant_riparian_grass", PlantRiparianGrassBlock::new);
		PLANT_RIPARIAN_REED = REGISTRY.register("plant_riparian_reed", PlantRiparianReedBlock::new);
		PLANT_EMERGENT_TARO = REGISTRY.register("plant_emergent_taro", PlantEmergentTaroBlock::new);
		PLANT_FREE_FLOATING_WATER_HYACINTH = REGISTRY.register("plant_free_floating_water_hyacinth", PlantFreeFloatingWaterHyacinthBlock::new);
		PLANT_FREE_FLOATING_WATER_HYACINTH_FLOWERING = REGISTRY.register("plant_free_floating_water_hyacinth_flowering", PlantFreeFloatingWaterHyacinthFloweringBlock::new);
		PLANT_FLOATING_LEAVED_NYMPHAEA_PART_MIDDLE = REGISTRY.register("plant_floating_leaved_nymphaea_part_middle", PlantFloatingLeavedNymphaeaPartMiddleBlock::new);
		PLANT_FLOATING_LEAVED_NYMPHAEA_PART_TOP = REGISTRY.register("plant_floating_leaved_nymphaea_part_top", PlantFloatingLeavedNymphaeaPartTopBlock::new);
		PLANT_FLOATING_LEAVED_NYMPHAEA_PART_TOP_FLOWERING = REGISTRY.register("plant_floating_leaved_nymphaea_part_top_flowering", PlantFloatingLeavedNymphaeaPartTopFloweringBlock::new);
	}
	// Start of user code block custom blocks
	// End of user code block custom blocks
}