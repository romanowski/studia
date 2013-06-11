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
import java.util.Scanner;
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

public class ChatClient extends AbstractChat {

	String nick = "ja" + System.nanoTime() % 1000;

	public ChatClient(JChannel channel) throws Exception {

		mainChanel = channel;
		mainChanel.setReceiver(new ReceiverAdapter() {
			@Override
			public void viewAccepted(View view) {
				handleNewView(view);
			}

			@Override
			public void receive(Message msg) {

				try {
					handleState(msg);
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

	void connect(String addres) throws Exception {
		System.out.println("connecting");

		ChatRoom r = new ChatRoom();
		r.name = addres;
		r.isOpen = true;
		chatRooms.put(addres, r);
		r.channel = ChanelFactory.getChanel(addres);
		r.channel.setReceiver(new MessageRecivier(addres));
		r.channel.connect(addres);

		String nNick = nick + "@" + r.channel.getAddressAsString();

		ChatAction action = ChatAction.newBuilder().setAction(ActionType.JOIN)
				.setChannel(addres).setNickname(nNick).build();

		mainChanel.send(null, ChatState.newBuilder().addState(action).build()
				.toByteArray());

	}

	void list() {
		System.out.println("Chat list:");
		for (ChatRoom room : chatRooms.values()) {
			System.out.println("Chatroom: " + room.name);
			for (String add : room.users.keySet()) {
				System.out.printf("%s@%s\n", room.users.get(add), add);
			}
		}
	}

	void sendMessage(String addess, String message) throws Exception {
		ChatRoom room = chatRooms.get(addess);
		if (room != null) {
			ChatMessage msg = ChatMessage.newBuilder().setMessage(message)
					.build();
			room.channel.send(null, msg.toByteArray());

			System.out.println("message send");

		} else {
			System.out.println("connect to " + addess + " first");
		}
	}

	public void commandLine() {
		Scanner s = new Scanner(System.in);
		String line;
		while ((line = s.nextLine()) != "\\q") {
			try {
				System.out.println("Parsing " + line);
				String[] tmp = line.split("\\s+");

				if (line.startsWith("list")) {
					list();
				}
				if (line.startsWith("connect")) {
					connect(tmp[1]);
				}
				if (line.startsWith("send")) {
					sendMessage(tmp[1], tmp[2]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new ChatClient(ChanelFactory.mainChanel()).commandLine();
	}

}
