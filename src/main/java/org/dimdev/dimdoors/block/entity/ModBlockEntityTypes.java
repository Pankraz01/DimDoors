package org.dimdev.dimdoors.block.entity;

import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.client.DetachedRiftBlockEntityRenderer;
import org.dimdev.dimdoors.client.EntranceRiftBlockEntityRenderer;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

public class ModBlockEntityTypes {
	public static final BlockEntityType<DetachedRiftBlockEntity> DETACHED_RIFT = register(
			"dimdoors:detached_rift",
			DetachedRiftBlockEntity::new,
			ModBlocks.DETACHED_RIFT);

	public static final BlockEntityType<EntranceRiftBlockEntity> ENTRANCE_RIFT = register(
			"dimdoors:entrance_rift",
			EntranceRiftBlockEntity::new,
			ModBlocks.OAK_DIMENSIONAL_DOOR, ModBlocks.IRON_DIMENSIONAL_DOOR, ModBlocks.GOLD_DIMENSIONAL_DOOR, ModBlocks.QUARTZ_DIMENSIONAL_DOOR, ModBlocks.DIMENSIONAL_PORTAL);

	private static <E extends BlockEntity> BlockEntityType<E> register(String id, FabricBlockEntityTypeBuilder.Factory<E> factory, Block... blocks) {
		return Registry.register(Registry.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.create(factory, blocks).build());
	}

	public static void init() {
		//just loads the class
	}

	@Environment(EnvType.CLIENT)
	public static void initClient() {
		BlockEntityRendererRegistry.INSTANCE.register(ModBlockEntityTypes.ENTRANCE_RIFT, ctx -> new EntranceRiftBlockEntityRenderer());
		BlockEntityRendererRegistry.INSTANCE.register(ModBlockEntityTypes.DETACHED_RIFT, ctx -> new DetachedRiftBlockEntityRenderer());
	}
}
