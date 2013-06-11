package zad3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class UDPChatManager {

	private Map<Integer, PartialChatMessage> currentMessages = new HashMap<Integer, PartialChatMessage>();

	private static final int maxCharInPart = 24;// 1024;
	// 1 bajt na flage, 4 bajty na id, 4 bajty na koniec autora
	private static final int firstHeaderSize = 1 + 4 + 4;
	// 1 bajt na flagi + 4 bajty na id
	private static final int nextHeaderSize = 1 + 4;

	// w UFT-8 char to max 3 bajty
	public static final int bufLenght = maxCharInPart * 3 + firstHeaderSize;

	private static Random r = new Random(System.nanoTime());

	/**
	 * returns chat message when chat message is complete or null
	 * 
	 * @param packet
	 * @return
	 * @throws IOException
	 */
	public ChatMessage manageDatagram(DatagramPacket packet) throws IOException {
		DataInputStream input = null;
		try {
			input = new DataInputStream(new ByteArrayInputStream(
					packet.getData(), 0, packet.getLength()));
			byte flag = input.readByte();
			int id = input.readInt();

			PartialChatMessage tmp = currentMessages.get(id);
			if (tmp == null) {
				tmp = new PartialChatMessage();
				currentMessages.put(id, tmp);
			}
			tmp.addPart(flag, id, input);
			if (tmp.isAll()) {
				currentMessages.remove(id);
				return tmp.createMessage();
			}
			return null;

		} catch (Exception e) {
			throw new IOException("while sending", e);
		} finally {
			if (input != null)
				input.close();
		}
	}

	public static List<DatagramPacket> sendMessage(ChatMessage message)
			throws IOException {
		List<DatagramPacket> list = new ArrayList<DatagramPacket>();

		int stringLenght = message.getMessage().length()
				+ message.getAuthor().length();

		int dataCount = stringLenght / maxCharInPart + 1;
		int pos = 0;

		int id = r.nextInt();

		ByteArrayOutputStream out = new ByteArrayOutputStream(bufLenght);
		DataOutputStream dataOut = new DataOutputStream(out);

		pos = maxCharInPart - message.getAuthor().length();

		// zapisywanie 1 czesci
		if (dataCount == 1) {
			dataOut.writeByte(Flags.NonParted);

		} else {
			dataOut.writeByte(Flags.FirstPart);
		}

		dataOut.writeInt(id);
		dataOut.writeInt(message.getAuthor().length());
		if (message.getMessage().length() < pos) {
			pos = message.getMessage().length();
		}
		dataOut.writeUTF(message.getAuthor()
				+ message.getMessage().substring(0, pos));

		list.add(createPacket(out));
		// zapisujemy pozostałe
		for (int i = 1; i < dataCount; i++) {
			// ostatnia czesc
			if (i == dataCount - 1) {
				dataOut.writeByte(Flags.LastPart);
			} else {
				dataOut.writeByte(Flags.MiddlePart);
			}
			dataOut.writeInt(id);
			dataOut.writeInt(i);
			int end = pos + maxCharInPart;
			if (end > message.getMessage().length()) {
				end = message.getMessage().length();
			}
			dataOut.writeUTF(message.getMessage().substring(pos, end));
			pos += maxCharInPart;
			list.add(createPacket(out));
		}
		return list;
	}

	public static boolean checkAuthor(String author) {
		return author.length() < 100 && author.length() < maxCharInPart / 2;
	}

	private static DatagramPacket createPacket(ByteArrayOutputStream out) {
		DatagramPacket p = new DatagramPacket(out.toByteArray(), out.size());
		p.setAddress(ChatClient.hostAddress);
		p.setPort(ChatClient.port);
		out.reset();
		return p;
	}
}
