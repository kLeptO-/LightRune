package org.lightrune.net.protocol.packet;

import org.lightrune.net.protocol.RSChannelContext;
import org.lightrune.net.protocol.packet.impl.*;

/**
 * Packet handlers manager.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class PacketHandlers {

	private static PacketHandler[] handlers = new PacketHandler[256];

	/**
	 * Handles an incoming packet.
	 * 
	 * @param channelContext
	 *            the channel socket context
	 * 
	 * @param packet
	 *            the packet
	 */
	public static void handlePacket(RSChannelContext channelContext, Packet packet) {
		if (handlers[packet.opcode()] != null) {
			handlers[packet.opcode()].handle(channelContext.player(), packet);
		}
	}

	/**
	 * Static constructor for packet handlers initializing.
	 */
	static {
		handlers[4] = new ChatPacketHandler();
		handlers[103] = new CommandPacketHandler();
		handlers[41] = new EquipPacketHandler();
		handlers[214] = new SwitchItemPacketHandler();
		handlers[185] = new ButtonPacketHandler();
		handlers[87] = new DropPacketHandler();
		handlers[236] = new PickupPacketHandler();
		handlers[121] = new RegionPacketHandler();
		handlers[130] = new CloseInterfacePacketHandler();

		handlers[MovementPacketHandler.COMMAND_MOVEMENT_OPCODE] = new MovementPacketHandler();
		handlers[MovementPacketHandler.GAME_MOVEMENT_OPCODE] = new MovementPacketHandler();
		handlers[MovementPacketHandler.MINIMAP_MOVEMENT_OPCODE] = new MovementPacketHandler();

		handlers[ItemActionPacketHandler.THIRD_ITEM_ACTION_OPCODE] = new ItemActionPacketHandler();
		handlers[ItemActionPacketHandler.SECOND_ITEM_ACTION_OPCODE] = new ItemActionPacketHandler();
		handlers[ItemActionPacketHandler.FIRST_ITEM_ACTION_OPCODE] = new ItemActionPacketHandler();
		handlers[ItemActionPacketHandler.FOURTH_ITEM_ACTION_OPCODE] = new ItemActionPacketHandler();

		handlers[TradePacketHandler.TRADE_OPCODE] = new TradePacketHandler();
		handlers[TradePacketHandler.CHATBOX_TRADE_OPCODE] = new TradePacketHandler();
	}

}