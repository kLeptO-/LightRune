package org.lightrune.net;

import java.io.IOException;

import org.lightrune.net.protocol.RSChannelContext;

/**
 * Channel input decoder.
 * 
 * @author kLeptO <http://www.rune-server.org/members/klepto/>
 */
public interface NIODecoder {

	/**
	 * Decodes the incoming data.
	 * 
	 * @param channelContext
	 *            the associated channel context
	 */
	public void decode(RSChannelContext channelContext) throws IOException;

}