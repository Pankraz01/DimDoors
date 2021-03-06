package org.dimdev.dimdoors.network;

import net.minecraft.network.PacketByteBuf;

import java.io.IOException;

public interface SimplePacket<T> {
	SimplePacket<T> read(PacketByteBuf buf) throws IOException;

	PacketByteBuf write(PacketByteBuf buf) throws IOException;

	void apply(T listener);
}
