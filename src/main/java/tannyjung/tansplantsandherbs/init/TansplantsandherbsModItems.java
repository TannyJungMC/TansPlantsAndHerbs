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
	public static final RegistryObject<Item> PLANT_LARGE_CATTAIL;
	public static final RegistryObject<Item> PLANT_LARGE_REED;
	public static final RegistryObject<Item> PLANT_LARGE_GRASS;
	public static final RegistryObject<Item> PLANT_FLOATING_DUCKWEED;
	public static final RegistryObject<Item> TEST;
	static {
		PLANT_LARGE_CATTAIL = block(TansplantsandherbsModBlocks.PLANT_LARGE_CATTAIL);
		PLANT_LARGE_REED = block(TansplantsandherbsModBlocks.PLANT_LARGE_REED);
		PLANT_LARGE_GRASS = block(TansplantsandherbsModBlocks.PLANT_LARGE_GRASS);
		PLANT_FLOATING_DUCKWEED = block(TansplantsandherbsModBlocks.PLANT_FLOATING_DUCKWEED);
		TEST = block(TansplantsandherbsModBlocks.TEST);
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