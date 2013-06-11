package pl.edu.agh.dsrg.sr.chat;

import java.net.Inet4Address;

import org.jgroups.JChannel;
import org.jgroups.protocols.BARRIER;
import org.jgroups.protocols.FD_ALL;
import org.jgroups.protocols.FD_SOCK;
import org.jgroups.protocols.FRAG2;
import org.jgroups.protocols.MERGE2;
import org.jgroups.protocols.MFC;
import org.jgroups.protocols.PING;
import org.jgroups.protocols.UDP;
import org.jgroups.protocols.UFC;
import org.jgroups.protocols.UNICAST2;
import org.jgroups.protocols.VERIFY_SUSPECT;
import org.jgroups.protocols.pbcast.FLUSH;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK;
import org.jgroups.protocols.pbcast.STABLE;
import org.jgroups.protocols.pbcast.STATE_TRANSFER;
import org.jgroups.stack.ProtocolStack;

public class ChanelFactory {

	public static JChannel mainChanel(String nick) throws Exception {
		JChannel ch = new JChannel();

		ProtocolStack stack = new ProtocolStack();
		ch.setProtocolStack(stack);

		stack.addProtocol(new UDP())
				.addProtocol(new PING())
				.addProtocol(new MERGE2())
				.addProtocol(new FD_SOCK())
				.addProtocol(
						new FD_ALL().setValue("timeout", 12000).setValue(
								"interval", 3000))
				.addProtocol(new VERIFY_SUSPECT()).addProtocol(new BARRIER())
				.addProtocol(new NAKACK()).addProtocol(new UNICAST2())
				.addProtocol(new STABLE()).addProtocol(new GMS())
				.addProtocol(new UFC()).addProtocol(new MFC())
				.addProtocol(new FRAG2()).addProtocol(new STATE_TRANSFER())
				.addProtocol(new FLUSH());
		stack.init();

		ch.setName(nick);

		return ch;
	}

	public static JChannel getChanel(String address, String nick)
			throws Exception {
		JChannel ch = new JChannel();

		ProtocolStack stack = new ProtocolStack();
		ch.setProtocolStack(stack);

		stack.addProtocol(
				new UDP().setValue("mcast_group_addr",
						Inet4Address.getByName(address)))
				.addProtocol(new PING())
				.addProtocol(new MERGE2())
				.addProtocol(new FD_SOCK())
				.addProtocol(
						new FD_ALL().setValue("timeout", 12000).setValue(
								"interval", 3000))
				.addProtocol(new VERIFY_SUSPECT()).addProtocol(new BARRIER())
				.addProtocol(new NAKACK()).addProtocol(new UNICAST2())
				.addProtocol(new STABLE()).addProtocol(new GMS())
				.addProtocol(new UFC()).addProtocol(new MFC())
				.addProtocol(new FRAG2()).addProtocol(new STATE_TRANSFER())
				.addProtocol(new FLUSH());
		stack.init();

		ch.setName(nick);

		return ch;
	}

}
