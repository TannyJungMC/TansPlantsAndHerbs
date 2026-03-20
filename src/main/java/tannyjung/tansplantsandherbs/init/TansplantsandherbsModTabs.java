/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package tannyjung.tansplantsandherbs.init;

import tannyjung.tansplantsandherbs.TansplantsandherbsMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

public class TansplantsandherbsModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TansplantsandherbsMod.MODID);
	public static final RegistryObject<CreativeModeTab> TAB = REGISTRY.register("tab",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.tansplantsandherbs.tab")).icon(() -> new ItemStack(TansplantsandherbsModBlocks.PLANT_EMERGENT_CATTAIL.get())).displayItems((parameters, tabData) -> {
				tabData.accept(TansplantsandherbsModBlocks.GARDEN_SOIL.get().asItem());
				tabData.accept(TansplantsandherbsModBlocks.PLANT_EMERGENT_CATTAIL.get().asItem());
				tabData.accept(TansplantsandherbsModBlocks.PLANT_WATERSIDE_REED.get().asItem());
				tabData.accept(TansplantsandherbsModBlocks.PLANT_WATERSIDE_GRASS.get().asItem());
				tabData.accept(TansplantsandherbsModBlocks.PLANT_FLOATING_DUCKWEED.get().asItem());
				tabData.accept(TansplantsandherbsModBlocks.PLANT_SUBMERGED_NYMPHAEA_MIDDLE.get().asItem());
				tabData.accept(TansplantsandherbsModBlocks.PLANT_SUBMERGED_NYMPHAEA_TOP.get().asItem());
				tabData.accept(TansplantsandherbsModBlocks.PLANT_SUBMERGED_NYMPHAEA_TOP_FLOWERING.get().asItem());
				tabData.accept(TansplantsandherbsModBlocks.PLANT_SUBMERGED_NYMPHAEA.get().asItem());
			}).build());
}