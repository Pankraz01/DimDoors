package org.dimdev.dimdoors.world.pocket.type;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import com.mojang.serialization.Codec;

import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.world.level.DimensionalRegistry;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

public class Pocket extends AbstractPocket<Pocket> {
	public static String KEY = "pocket";

	public BlockBox box; // TODO: make protected
	public VirtualLocation virtualLocation;

	public Pocket(int id, RegistryKey<World> world, int x, int z) {
		super(id, world);
		int gridSize = DimensionalRegistry.getPocketDirectory(world).getGridSize() * 16;
		this.box = BlockBox.create(x * gridSize, 0, z * gridSize, (x + 1) * gridSize, 0, (z + 1) * gridSize);
		this.virtualLocation = new VirtualLocation(world, x, z, 0);
	}

	protected Pocket() {

	}

	public boolean isInBounds(BlockPos pos) {
		return this.box.contains(pos);
	}

	public BlockPos getOrigin() {
		return new BlockPos(this.box.minX, this.box.minY, this.box.minZ);
	}

	public void offsetOrigin(Vec3i vec) {
		offsetOrigin(vec.getX(), vec.getY(), vec.getZ());
	}

	public void offsetOrigin(int x, int y, int z) {
		this.box = box.offset(x, y, z);
	}

	public void setSize(Vec3i size) {
		setSize(size.getX(), size.getY(), size.getZ());
	}

	public void setSize(int x, int y, int z) {
		this.box = BlockBox.create(this.box.minX, this.box.minY, this.box.minZ, this.box.minX + x - 1, this.box.minY + y - 1, this.box.minZ + z - 1);
	}

	public Vec3i getSize() {
		return this.box.getDimensions();
	}

	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		tag.putIntArray("box", IntStream.of(this.box.minX, this.box.minY, this.box.minZ, this.box.maxX, this.box.maxY, this.box.maxZ).toArray());
		tag.put("virtualLocation", VirtualLocation.toTag(this.virtualLocation));
		return tag;
	}

	@Override
	public AbstractPocketType<Pocket> getType() {
		return AbstractPocketType.POCKET;
	}

	public Pocket fromTag(CompoundTag tag) {
		super.fromTag(tag);
		int[] box = tag.getIntArray("box");
		this.box = new BlockBox(box[0], box[1], box[2], box[3], box[4], box[5]);
		this.virtualLocation = VirtualLocation.fromTag(tag.getCompound("virtualLocation"));

		return this;
	}

	public enum PocketColor {
		WHITE(0, DyeColor.WHITE),
		ORANGE(1, DyeColor.ORANGE),
		MAGENTA(2, DyeColor.MAGENTA),
		LIGHT_BLUE(3, DyeColor.LIGHT_BLUE),
		YELLOW(4, DyeColor.YELLOW),
		LIME(5, DyeColor.LIME),
		PINK(6, DyeColor.PINK),
		GRAY(7, DyeColor.GRAY),
		LIGHT_GRAY(8, DyeColor.LIGHT_GRAY),
		CYAN(9, DyeColor.CYAN),
		PURPLE(10, DyeColor.PURPLE),
		BLUE(11, DyeColor.BLUE),
		BROWN(12, DyeColor.BROWN),
		GREEN(13, DyeColor.GREEN),
		RED(14, DyeColor.RED),
		BLACK(15, DyeColor.BLACK),
		NONE(16, null);

		private final int id;
		private final DyeColor color;

		public static Codec<PocketColor> CODEC = Codec.INT.xmap(PocketColor::from, PocketColor::getId);

		PocketColor(int id, DyeColor color) {
			this.id = id;
			this.color = color;
		}

		public DyeColor getColor() {
			return this.color;
		}

		public Integer getId() {
			return this.id;
		}

		public static PocketColor from(DyeColor color) {
			for (PocketColor a : PocketColor.values()) {
				if (color == a.color) {
					return a;
				}
			}

			return NONE;
		}

		public static PocketColor from(int id) {
			for (PocketColor a : PocketColor.values()) {
				if (id == a.id) {
					return a;
				}
			}

			return NONE;
		}
	}

	public Map<BlockPos, BlockEntity> getBlockEntities() {
		ServerWorld serverWorld = DimensionalDoorsInitializer.getWorld(this.getWorld());
		Map<BlockPos, BlockEntity> blockEntities = new HashMap<>();
		ChunkPos.stream(new ChunkPos(new BlockPos(box.minX, box.minY, box.minZ)), new ChunkPos(new BlockPos(box.maxX, box.maxY, box.maxZ))).forEach(chunkPos -> serverWorld.getChunk(chunkPos.x, chunkPos.z).getBlockEntities().forEach((blockPos, blockEntity) -> {
			if (this.box.contains(blockPos)) blockEntities.put(blockPos, blockEntity);
		}));
		return blockEntities;
	}

	public BlockBox getBox() {
		return box;
	}

	public Map<String, Double> toVariableMap(Map<String, Double> variableMap) {
		variableMap = super.toVariableMap(variableMap);
		variableMap.put("originX", (double) this.box.minX);
		variableMap.put("originY", (double) this.box.minY);
		variableMap.put("originZ", (double) this.box.minZ);
		variableMap.put("width", (double) this.box.getDimensions().getX());
		variableMap.put("height", (double) this.box.getDimensions().getY());
		variableMap.put("length", (double) this.box.getDimensions().getZ());
		variableMap.put("depth", (double) this.virtualLocation.getDepth());
		return variableMap;
	}

	@Override
	public Pocket getReferencedPocket() {
		return this;
	}

	public void expand(int amount) {
		if (amount == 0) return;
		this.box = BlockBox.create(box.minX - amount, box.minY - amount, box.minZ - amount, box.maxX + amount, box.maxY + amount, box.maxZ + amount);
	}

	public static PocketBuilder<?, Pocket> builder() {
		return new PocketBuilder<>(AbstractPocketType.POCKET);
	}

	// TODO: flesh this out a bit more, stuff like box() makes little sense in how it is implemented atm
	public static class PocketBuilder<P extends PocketBuilder<P, T>, T extends Pocket> extends AbstractPocketBuilder<P, T> {
		private final Map<Class<? extends PocketAddon.PocketBuilderAddon<?>>, PocketAddon.PocketBuilderAddon<?>> addons = new HashMap<>();

		private Vec3i origin = new Vec3i(0, 0, 0);
		private Vec3i size = new Vec3i(0, 0, 0);
		private Vec3i expected = new Vec3i(0, 0, 0);
		private VirtualLocation virtualLocation;
		private PocketColor dyeColor = PocketColor.NONE;

		protected PocketBuilder(AbstractPocketType<T> type) {
			super(type);
		}

		public <C extends PocketAddon.PocketBuilderAddon<X>, X extends PocketAddon<X>> boolean hasAddon(Class<C> addonClass) {
			return addons.containsKey(addonClass);
		}

		protected <C extends PocketAddon.PocketBuilderAddon<X>, X extends PocketAddon<X>> void addAddon(Class<C> addonClass, C addon) {
			addons.put(addonClass, addon);
		}

		public <C extends PocketAddon.PocketBuilderAddon<X>, X extends PocketAddon<X>> C getAddon(Class<C> addonClass) {
			return (C) addons.get(addonClass);
		}

		public P getSelf() {
			return (P) this;
		}

		@Override
		public Vec3i getExpectedSize() {
			return expected;
		}

		public T build() {
			T instance = super.build();

			instance.box = BlockBox.create(origin.getX(), origin.getY(), origin.getZ(), origin.getX() + size.getX(), origin.getY() + size.getY(), origin.getZ() + size.getZ());
			instance.virtualLocation = virtualLocation;

			return instance;
		}

		public P offsetOrigin(Vec3i offset) {
			this.origin = new Vec3i(origin.getX() + offset.getX(), origin.getY() + offset.getY(), origin.getZ() + offset.getZ());
			return (P) this;
		}

		public P expand(Vec3i expander) {
			this.size = new Vec3i(size.getX() + expander.getX(), size.getY() + expander.getY(), size.getZ() + expander.getZ());
			this.expected = new Vec3i(expected.getX() + expander.getX(), expected.getY() + expander.getY(), expected.getZ() + expander.getZ());
			return (P) this;
		}

		public P expandExpected(Vec3i expander) {
			this.expected = new Vec3i(expected.getX() + expander.getX(), expected.getY() + expander.getY(), expected.getZ() + expander.getZ());
			return (P) this;
		}

		public P virtualLocation(VirtualLocation virtualLocation) {
			this.virtualLocation = virtualLocation;
			return (P) this;
		}

		public P dyeColor(PocketColor dyeColor) {
			this.dyeColor = dyeColor;
			return (P) this;
		}
	}
}
