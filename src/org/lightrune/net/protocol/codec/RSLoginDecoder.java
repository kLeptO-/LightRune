package org.lightrune.net.protocol.codec;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.lightrune.net.NIODecoder;
import org.lightrune.net.protocol.RSChannelContext;
import org.lightrune.net.protocol.packet.PacketBuilder;
import org.lightrune.player.Player;
import org.lightrune.player.PlayerSaving;
import org.lightrune.util.ISAAC;
import org.lightrune.util.RS2Utils;
import org.lightrune.world.World;

/**
 * RuneScape login procedure decoder.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class RSLoginDecoder implements NIODecoder {

	private State state = State.READ_USERNAME_HASH;
	private ByteBuffer buffer = ByteBuffer.allocateDirect(126);

	@Override
	public void decode(RSChannelContext channelContext) throws IOException {
		/*
		 * Read the incoming data.
		 */
		channelContext.channel().read(buffer);
		buffer.flip();

		/*
		 * Handle login procedure.
		 */
		switch (state) {

		case READ_USERNAME_HASH:
			/*
			 * Check if buffer has enough readable data.
			 */
			if (buffer.remaining() < 2) {
				buffer.compact();
				break;
			}

			/*
			 * Login packet opcode.
			 */
			int loginType = buffer.get() & 0xFF;
			if (loginType != 14) {
				System.out.println("Invalid login type.");
				channelContext.channel().close();
				break;
			}

			/*
			 * Name hash which is probably used to select proper login
			 * server.
			 */
			@SuppressWarnings("unused")
			int nameHash = buffer.get() & 0xFF;

			/*
			 * Write the response after first state.
			 */
			PacketBuilder out = PacketBuilder.allocate(17);
			out.putBytes(0, 17);
			out.sendTo(channelContext.channel());

			/*
			 * Switch to the next login procedure state.
			 */
			state = State.READ_LOGIN_HEADER;
			buffer.compact();
			break;

		case READ_LOGIN_HEADER:
			/*
			 * Check if buffer has enough readable data.
			 */
			if (buffer.remaining() < 2) {
				buffer.compact();
				break;
			}

			/*
			 * Login request. 16 means that it's normal connection and 18
			 * means that it's reconnection.
			 */
			int loginRequest = buffer.get() & 0xFF;
			if (loginRequest != 16 && loginRequest != 18) {
				System.out.println("Invalid login request.");
				channelContext.channel().close();
				break;
			}

			/*
			 * Login payload size.
			 */
			loginPacketLength = buffer.get() & 0xFF;

			/*
			 * Switching to the last login state.
			 */
			state = State.READ_LOGIN_PAYLOAD;

		case READ_LOGIN_PAYLOAD:
			/*
			 * Check if buffer has enough readable data.
			 */
			if (buffer.remaining() < loginPacketLength) {
				buffer.compact();
				break;
			}

			/*
			 * Opcode of the last login state.
			 */
			int loginOpcode = buffer.get() & 0xFF;
			if (loginOpcode != 255) {
				System.out.println("Invalid magic id.");
				channelContext.channel().close();
				break;
			}

			/*
			 * Version of the client, in this case 317.
			 */
			int clientVersion = buffer.getShort();
			if (clientVersion != 317) {
				System.out.println("Invalid client version.");
				channelContext.channel().close();
				break;
			}

			/*
			 * Client memory version, indicates if client is on low or high
			 * detail mode.
			 */
			@SuppressWarnings("unused")
			int memoryVersion = buffer.get() & 0xFF;

			/*
			 * Skipping the RSA packet.
			 */
			for (int i = 0; i < 9; i++) {
				buffer.getInt();
			}

			/*
			 * The actual payload size, just another indicator to check if
			 * it's correct login packet.
			 */
			int expectedPayloadSize = buffer.get() & 0xFF;
			if (expectedPayloadSize != loginPacketLength - 41) {
				System.out.println("Invalid payload size.");
				channelContext.channel().close();
				break;
			}

			/*
			 * The RSA packet opcode.
			 */
			int rsaOpcode = buffer.get() & 0xFF;
			if (rsaOpcode != 10) {
				System.out.println("Invalid RSA operation code.");
				channelContext.channel().close();
				break;
			}

			/*
			 * Skipping the ISAAC seeds as we are not using it.
			 */
			long clientSeed = buffer.getLong();
			long serverSeed = buffer.getLong();

			int sessionSeed[] = new int[4];
			sessionSeed[0] = (int) (clientSeed >> 32);
			sessionSeed[1] = (int) clientSeed;
			sessionSeed[2] = (int) (serverSeed >> 32);
			sessionSeed[3] = (int) serverSeed;
			channelContext.decryption(new ISAAC(sessionSeed));

			for (int i = 0; i < 4; i++) {
				sessionSeed[i] += 50;
			}
			channelContext.encryption(new ISAAC(sessionSeed));

			/*
			 * The user id.
			 */
			int userId = buffer.getInt();

			/*
			 * The user identify.
			 */
			String username = RS2Utils.formatString(RS2Utils.getRSString(buffer));
			String password = RS2Utils.getRSString(buffer);
			System.out.println("Identify: " + userId + " " + username + " " + password);

			/*
			 * Create the player object for this channel.
			 */
			Player player = new Player(username, password, channelContext);
			boolean loaded = PlayerSaving.load(player);

			/*
			 * Generate response opcode.
			 */
			int response = 2;

			if (!loaded) {
				/*
				 * Invalid username or password.
				 */
				response = 3;
			} else {
				channelContext.player(player);
				World.register(player);
			}

			if (player.index() == -1) {
				/*
				 * World is full.
				 */
				response = 10;
			}

			/*
			 * Write the login procedure response.
			 */
			out = PacketBuilder.allocate(3);
			out.putByte(response);
			out.putByte(player.rights());
			out.putByte(0);
			out.sendTo(channelContext.channel());

			/*
			 * Since login procedure is finished, switching to packet decoder.
			 */
			channelContext.decoder(new RSPacketDecoder());

			/*
			 * And finally sending the initialization packet to the client.
			 */
			player.packetSender().sendInitPacket();
			break;
		}
	}

	/*
	 * We use this to store login packet length to check if all data has
	 * arrived.
	 */
	private int loginPacketLength;

	/**
	 * Login procedure states.
	 */
	private enum State {
		READ_USERNAME_HASH, READ_LOGIN_HEADER, READ_LOGIN_PAYLOAD
	}

}