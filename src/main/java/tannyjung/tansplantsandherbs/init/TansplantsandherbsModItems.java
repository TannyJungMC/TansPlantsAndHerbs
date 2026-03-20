/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package tannyjung.tansplantsandherbs.init;

import tannyjung.tansplantsandherbs.TansplantsandherbsMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

public class TansplantsandherbsModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, TansplantsandherbsMod.MODID);
	public static final RegistryObject<Item> GARDEN_SOIL;
	public static final RegistryObject<Item> PLANT_EMERGENT_CATTAIL;
	public static final RegistryObject<Item> PLANT_WATERSIDE_REED;
	public static final RegistryObject<Item> PLANT_WATERSIDE_GRASS;
	public static final RegistryObject<Item> PLANT_FLOATING_DUCKWEED;
	public static final RegistryObject<Item> PLANT_SUBMERGED_NYMPHAEA_MIDDLE;
	public static final RegistryObject<Item> PLANT_SUBMERGED_NYMPHAEA_TOP;
	public static final RegistryObject<Item> PLANT_SUBMERGED_NYMPHAEA_TOP_FLOWERING;
	public static final RegistryObject<Item> PLANT_SUBMERGED_NYMPHAEA;
	static {
		GARDEN_SOIL = block(TansplantsandherbsModBlocks.GARDEN_SOIL);
		PLANT_EMERGENT_CATTAIL = block(TansplantsandherbsModBlocks.PLANT_EMERGENT_CATTAIL);
		PLANT_WATERSIDE_REED = block(TansplantsandherbsModBlocks.PLANT_WATERSIDE_REED);
		PLANT_WATERSIDE_GRASS = block(TansplantsandherbsModBlocks.PLANT_WATERSIDE_GRASS);
		PLANT_FLOATING_DUCKWEED = block(TansplantsandherbsModBlocks.PLANT_FLOATING_DUCKWEED);
		PLANT_SUBMERGED_NYMPHAEA_MIDDLE = block(TansplantsandherbsModBlocks.PLANT_SUBMERGED_NYMPHAEA_MIDDLE);
		PLANT_SUBMERGED_NYMPHAEA_TOP = block(TansplantsandherbsModBlocks.PLANT_SUBMERGED_NYMPHAEA_TOP);
		PLANT_SUBMERGED_NYMPHAEA_TOP_FLOWERING = block(TansplantsandherbsModBlocks.PLANT_SUBMERGED_NYMPHAEA_TOP_FLOWERING);
		PLANT_SUBMERGED_NYMPHAEA = block(TansplantsandherbsModBlocks.PLANT_SUBMERGED_NYMPHAEA);
	}

	// Start of user code block custom items
	// End of user code block custom items
	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return block(block, new Item.Properties());
	}

	private static RegistryObject<Item> block(RegistryObject<Block> block, Item.Properties properties) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), properties));
	}
}