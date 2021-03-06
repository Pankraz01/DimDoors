package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import org.dimdev.dimdoors.util.Location;

import net.minecraft.nbt.CompoundTag;

public class GlobalReference extends RiftReference {
	public static Codec<GlobalReference> CODEC = Location.CODEC.fieldOf("location").xmap(GlobalReference::new, GlobalReference::getReferencedLocation).codec();

	protected Location target;

	public GlobalReference(Location target) {
		this.target = target;
	}

	@Override
	public Location getReferencedLocation() {
		return this.target;
	}

	@Override
	public VirtualTargetType<? extends VirtualTarget> getType() {
		return VirtualTargetType.GLOBAL;
	}

	public static CompoundTag toTag(GlobalReference virtualTarget) {
		CompoundTag tag = new CompoundTag();
		tag.put("target", Location.toTag(virtualTarget.getReferencedLocation()));
		return tag;
	}

	public static GlobalReference fromTag(CompoundTag nbt) {
		return new GlobalReference(Location.fromTag(nbt.getCompound("target")));
	}
}
