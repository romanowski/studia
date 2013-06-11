package pl.edu.agh.dsrg.sr.chat;

import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import com.google.protobuf.InvalidProtocolBufferException;

import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos.ChatMessage;

public class MessageRecivier extends ReceiverAdapter {

	private String room;

	public MessageRecivier(String room) {
		this.room = room;
	}


	@Override
	public void receive(Message message) {

		ChatMessage msg;
		try {
			msg = ChatMessage.parseFrom(message.getBuffer());

			System.out.println("Chanal: " + room + " :" + msg.getMessage());
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
