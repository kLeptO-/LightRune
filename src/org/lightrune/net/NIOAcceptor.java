package org.lightrune.net;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.lightrune.net.protocol.RSChannelContext;
import org.lightrune.net.protocol.codec.RSLoginDecoder;
import org.lightrune.world.World;

/**
 * NIO connections acceptor.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class NIOAcceptor extends Thread {

	@Override
	public void run() {
		while (NIOServer.channel().isOpen()) {
			try {
				/*
				 * Accepting connection in blocking manner.
				 */
				SocketChannel channel = NIOServer.channel().accept();
				channel.configureBlocking(false);
				RSChannelContext channelContext = new RSChannelContext(channel, new RSLoginDecoder());

				/*
				 * Giving 150 milliseconds for fast login procedure decoding, if
				 * connection doesn't make it in time, it will be registered
				 * with selector and login will be decoded every
				 * 600 milliseconds instead.
				 */
				for (int i = 0; i < 3; i++) {
					try {
						channelContext.decoder().decode(channelContext);
						Thread.sleep(50);
					} catch (Exception e) {
						e.printStackTrace();
						channel.close();
					}
				}

				/*
				 * If exception occurred while decoding login, let's skip
				 * dealing with this channel.
				 */
				if (!channel.isOpen()) {
					if (channelContext.player() != null) {
						World.deregister(channelContext.player());
					}
					continue;
				}

				/*
				 * Register channel with selector for incoming data decoding.
				 */
				channel.register(NIOSelector.selector(), SelectionKey.OP_READ, channelContext);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}