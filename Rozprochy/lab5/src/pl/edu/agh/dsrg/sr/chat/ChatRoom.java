package pl.edu.agh.dsrg.sr.chat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jgroups.JChannel;

public class ChatRoom {

	String name;
	JChannel channel;
	
	/**
	 * <address, nick>
	 */
	Map<String, String> users = Collections
			.synchronizedMap(new HashMap<String, String>());

	boolean isOpen = false;
	
}
