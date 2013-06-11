package pl.edu.agh.dsrg.sr.chat;

import java.awt.Desktop.Action;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.ChannelListener;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import com.google.protobuf.InvalidProtocolBufferException;

import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos.ChatAction;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos.ChatMessage;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos.ChatState;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos.ChatAction.ActionType;

public class ChatServer extends AbstractChat {

	public ChatServer(JChannel channel) throws Exception {

		mainChanel = channel;
		mainChanel.setReceiver(new ReceiverAdapter() {
			@Override
			public void viewAccepted(View view) {
				handleNewView(view);
			}

			@Override
			public void receive(Message msg) {

				try {
					handleAction(msg);
				} catch (Exception e) {
					System.out.println("Bad chat message!");
					e.printStackTrace();
				}
			}
		});
		mainChanel.connect("ChatManagement768264");

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				mainChanel.close();
				for (ChatRoom room : chatRooms.values()) {
					if (room.isOpen) {
						room.channel.close();
					}
				}
			}
		});

	}

	void handleAction(Message m) throws Exception {

		System.out.println("mam chanels");
		for (String chName : chatRooms.keySet()) {
			System.out.println(chName + " " + chatRooms.get(chName).name);

		}

		ChatState state = ChatState.parseFrom(m.getBuffer());

		for (ChatAction action : state.getStateList()) {

			System.out.printf("connected: to %s room: %s nick: %s.\n",
					m.getSrc(), action.getChannel(), action.getNickname());

			String chName = action.getChannel();

			ChatRoom newRoom = chatRooms.get(chName);

			if (action.getAction() == ActionType.JOIN) {
				if (newRoom == null) {
					newRoom = new ChatRoom();
					chatRooms.put(chName, newRoom);
					newRoom.name = chName;
					System.out.println("Rooom created: " + chName);
				}

				String[] tmp = action.getNickname().split("@");

				String nick = tmp[0];
				String addess = tmp[1];

				ChatState.Builder stateB = ChatState.newBuilder();

				for (ChatRoom room : chatRooms.values()) {
					for (String u : room.users.keySet()) {

						System.out
								.printf("    adding info action: to %s room: %s nick: %s.\n",
										m.getSrc(), room.name,
										room.users.get(u) + "@" + u);

						ChatAction ns = ChatAction.newBuilder()
								.setAction(ActionType.JOIN)
								.setChannel(room.name)
								.setNickname(room.users.get(u) + "@" + u)
								.build();
						stateB.addState(ns);
					}
				}
				mainChanel.send(m.getSrc(), stateB.build().toByteArray());
				newRoom.users.put(addess, nick);
				System.out.println("adding address: " + addess + " nick "
						+ nick);
			} else {
				if (newRoom != null) {
					newRoom.users.remove(action.getNickname());
				} else {
					System.out.println("No such chanal!");
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new ChatServer(ChanelFactory.mainChanel());
	}

}
