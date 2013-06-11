package pl.edu.agh.dsrg.sr.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.View;

import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos.ChatAction;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos.ChatState;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos.ChatAction.ActionType;

public class AbstractChat {

	JChannel mainChanel;

	Map<String, ChatRoom> chatRooms = Collections
			.synchronizedMap(new HashMap<String, ChatRoom>());

	void handleNewView(View v) {
		System.out.println("new View! connected:" + v.getCreator());

		Set<String> addresses = new HashSet<String>();
		for (Address mem : mainChanel.getView().getMembers()) {
			addresses.add(mem.toString());
		}

		for (ChatRoom room : chatRooms.values()) {
			if (room.isOpen)
				for (Address mem : room.channel.getView().getMembers()) {
					addresses.add(mem.toString());
				}
		}

		String addess = v.getCreator().toString();
		Set<String> toRem = new HashSet<String>();
		Set<String> toRemAdd = new HashSet<String>();

		for (ChatRoom room : chatRooms.values()) {
			for (String string : room.users.keySet()) {
				if (!addresses.contains(string)) {
					toRemAdd.add(string);
				}
			}
		}

		for (ChatRoom room : chatRooms.values()) {
			for (String string : toRemAdd) {
				room.users.remove(string);
			}
		}
		for (ChatRoom room : chatRooms.values()) {
			if (room.users.isEmpty()) {
				toRem.add(room.name);
			}
		}

		for (String string : toRem) {
			ChatRoom room = chatRooms.get(string);
			chatRooms.remove(string);
		}
	}

	void handleState(Message m) throws Exception {

		System.out.println("handle state from: " + m.getSrc() + "to: "
				+ m.getDest());

		ChatState state = ChatState.parseFrom(m.getBuffer());

		for (ChatAction action : state.getStateList()) {

			String[] tmp = action.getNickname().split("@");

			String nick = tmp[0];
			String addess = tmp[1];
			String chName = action.getChannel();

			System.out.printf("adding info: nick: %s addess: %s room: %s\n",
					nick, addess, chName);

			ChatRoom room = chatRooms.get(chName);

			if (room == null) {
				room = new ChatRoom();
				chatRooms.put("ch", room);
				room.channel = ChanelFactory.getChanel(chName);
				room.channel.setReceiver(new MessageRecivier(chName) {
					@Override
					public void viewAccepted(View view1) {
						handleNewView(view1);
					}
				});
				room.name = chName;
			}

			room.users.put(addess, nick);
		}
	}

}
