package net.smileycorp.mounts.common.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.smileycorp.mounts.common.Constants;

public class PacketHandler {

	public static final SimpleNetworkWrapper NETWORK_INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.MODID);

	public static void initPackets() {
		int id = 0;
		NETWORK_INSTANCE.registerMessage(SpearAttackMessage::process, SpearAttackMessage.class, id++, Side.SERVER);
	}

}
