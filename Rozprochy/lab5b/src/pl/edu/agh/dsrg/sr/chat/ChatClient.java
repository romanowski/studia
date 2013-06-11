package pl.edu.agh.dsrg.sr.chat;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos.ChatAction;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos.ChatAction.ActionType;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos.ChatMessage;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos.ChatState;

import com.google.protobuf.InvalidProtocolBufferException;

public class ChatClient {

	public static final short STATE = 1;
	public static final short ACTION = 2;

	private static Scanner sc = new Scanner(System.in);


	String nick;

	JChannel main;
	Map<String, Room> rooms = Collections
			.synchronizedMap(new HashMap<String, Room>());

	public ChatClient(String nick) throws Exception {
		this.nick = nick;

		main = ChanelFactory.mainChanel(nick);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {

				for (Room r : rooms.values()) {
					if (r.isOpen) {
						r.channel.close();
					}
				}
				main.close();
			}
		});

		main.setReceiver(new ReceiverAdapter() {

			@Override
			public void viewAccepted(View view) {
				handleView(view);
			}

			@Override
			public void receive(Message msg) {

				try {
					if (msg.getFlags() == STATE) {
						handleState(ChatState.parseFrom(msg.getBuffer()));
					}
					if (msg.getFlags() == ACTION) {
						handleAction(ChatAction.parseFrom(msg.getBuffer()));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		});

		main.connect("ChatManagement768264");
	}

	public void handleState(ChatState s) {

		for (ChatAction action : s.getStateList()) {
			handleAction(action);
		}
	}

	public void handleAction(ChatAction a) {

		Room r = rooms.get(a.getChannel());
		if (r == null) {
			r = new Room();
			rooms.put(a.getChannel(), r);
		}
		if (a.getAction() == ChatAction.ActionType.JOIN) {
			r.users.add(a.getNickname());
		} else {
			r.users.remove(a.getNickname());
		}
	}

	public void handleMessage(ChatMessage m, String nick) {
		System.out.printf("Message form %s: %s \n", nick, m.getMessage());
	}

	public Message createHello() {

		ChatState.Builder builder = ChatState.newBuilder();

		for (String rName : rooms.keySet()) {
			for (String user : rooms.get(rName).users) {
				builder.addState(ChatAction.newBuilder().setChannel(rName)
						.setNickname(user).setAction(ActionType.JOIN).build());
			}
		}

		Message m = new Message();
		m.setFlag(STATE);
		m.setBuffer(builder.build().toByteArray());

		return m;
	}

	public boolean isAdmin() {
		return main.getView().getMembers().get(0).equals(main.getAddress());
	}

	void handleView(View view) {

		Set<String> valid = new HashSet<String>();

		System.out.println("New View, admin: "
				+ main.getName(view.getMembers().get(0)));
		for (Address address : view) {
			valid.add(main.getName(address));
			System.out.println("mambers " + main.getName(address));
		}

		if (isAdmin())
			try {
				Message m = createHello();
				m.setDest(view.getCreator());
				m.setSrc(main.getAddress());
				main.send(m);
			} catch (Exception e) {
				e.printStackTrace();
			}

		for (Room room : rooms.values()) {
			for (String u : room.users) {
				if (!valid.contains(u)) {
					room.users.remove(u);
				}
			}
		}
	}

	public void connect(String name) throws Exception {

		System.out.println("Connecting " + name);

		Room newRoom = rooms.get(name);

		if (newRoom == null) {
			newRoom = new Room();
			rooms.put(name, newRoom);
		}

		if (!newRoom.isOpen) {
			newRoom.channel = ChanelFactory.getChanel(name, nick);
			newRoom.channel.connect(name);
			final JChannel channel = newRoom.channel;
			newRoom.channel.setReceiver(new ReceiverAdapter() {
				@Override
				public void receive(Message msg) {
					try {
						handleMessage(ChatMessage.parseFrom(msg.getBuffer()),
								channel.getName(msg.getSrc()));
					} catch (InvalidProtocolBufferException e) {
						e.printStackTrace();
					}
				}
			});
			newRoom.isOpen = true;
		}

		Message m = new Message();
		m.setDest(null);
		m.setFlag(ACTION);
		m.setBuffer(ChatAction.newBuilder().setChannel(name).setNickname(nick)
				.setAction(ActionType.JOIN).build().toByteArray());

		main.send(m);

		newRoom.users.add(nick);

		System.out.println("Connected");

	}

	void disconnet(String name) throws Exception {

		Room room = rooms.get(name);

		assert room != null;

		System.out.println("Disconnecting " + name);

		Message m = new Message();
		m.setDest(null);
		m.setFlag(ACTION);
		m.setBuffer(ChatAction.newBuilder().setChannel(name).setNickname(nick)
				.setAction(ActionType.LEAVE).build().toByteArray());

		main.send(m);

		room.isOpen = false;
		room.channel.close();

		System.out.println("Disconnected");

	}

	public void list() {

		System.out.println("All rooms");

		for (String rName : rooms.keySet()) {

			System.out.println("  Chatroom " + rName);
			for (String u : rooms.get(rName).users) {
				System.out.println("   " + u);
			}
		}
		System.out.println("-------------");

	}

	public void send(String channal, String msg) throws Exception {

		Room r = rooms.get(channal);
		if (r != null && r.isOpen) {

			r.channel.send(null, ChatMessage.newBuilder().setMessage(msg)
					.build().toByteArray());
		} else {
			System.out.println("No such channal or you have to connect:"
					+ channal + "!");
		}

	}

	void commandLine() {
		System.out.println("Listing:)");

		String line;

		while ((line = sc.nextLine()) != "\\q") {

			try {
				String[] tmp = line.split("\\s+");

				if (tmp[0].equals("list")) {
					list();
				}
				if (tmp[0].equals("connect")) {
					connect(tmp[1]);
				}
				if (tmp[0].equals("disconnect")) {
					disconnet(tmp[1]);
				}
				if (tmp[0].equals("send")) {
					send(tmp[1], tmp[2]);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) throws Exception {

		System.out.println("Hello, podaj nick:");
		ChatClient c = new ChatClient(sc.nextLine());
		c.commandLine();

	}

}
