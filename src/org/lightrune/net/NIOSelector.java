package org.lightrune.net;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import org.lightrune.net.protocol.RSChannelContext;
import org.lightrune.world.World;

/**
 * NIO server selector.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public class NIOSelector {

	private static Selector selector;

	/**
	 * Selects all active channels with read interest.
	 */
	public static void select() {
		try {
			selector().selectNow();
			for (SelectionKey key : selector().selectedKeys()) {
				if (key.attachment() == null) {
					continue;
				}

				/*
				 * Retrieve associated channel context.
				 */
				RSChannelContext channelContext = (RSChannelContext) key.attachment();

				try {
					/*
					 * Decode incoming data.
					 */
					channelContext.decoder().decode(channelContext);

				} catch (Exception e) {
					/*
					 * If exception occurred, unregister player and close
					 * connection.
					 */
					World.deregister(channelContext.player());
					key.channel().close();
					key.cancel();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the associated selector.
	 * 
	 * @param selector
	 *            the selector
	 */
	public static void selector(Selector selector) {
		NIOSelector.selector = selector;
	}

	/**
	 * Gets the associated selector.
	 * 
	 * @return the selector
	 */
	public static Selector selector() {
		return selector;
	}

}