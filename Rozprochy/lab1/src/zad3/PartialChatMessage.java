package zad3;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AllPermission;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;

public class PartialChatMessage {

	String author;
	Map<Integer, String> messageDatas = new TreeMap<Integer, String>();
	int id;
	boolean complete = false;
	boolean last = false;

	public PartialChatMessage() {
	}

	public void addPart(byte flag, int in_id, InputStream data)
			throws IOException {

		
		//dla wygody
		DataInputStream input = new DataInputStream(data);
		

		//calosc miesci sie w jednym "parcie"
		if(flag == Flags.NonParted){
			
			//testowanie
			if(messageDatas.size() != 0){
				throw new RuntimeException("bad flag!");
			}
			
			//pytamy gdzie pociąć
			int splitPos = input.readInt();
			String stringData = input.readUTF();
			
			author = stringData.substring(0, splitPos);
			messageDatas.put(0, stringData.substring(splitPos));
			
			complete = true;
			last = true;
			return;
		}
		
		//sprawdzamy id
		if(messageDatas.size() == 0){
			id = in_id;
		}
		else{
			if(in_id != id){
				throw new RuntimeException("id dont match!");
			}
		}

		if (flag == Flags.FirstPart) {
			int splitPos = input.readInt();
			String stringData = input.readUTF();
			author = stringData.substring(0, splitPos);
			messageDatas.put(0, stringData.substring(splitPos));
		} else {
			//która cześć wiadomości
			int pos = input.readInt();

			messageDatas.put(pos, input.readUTF());
		}

		//sprawdzamy czy mamy luki
		complete = ((SortedSet<Integer>)messageDatas.keySet()).last() >= messageDatas.size() -1;
		
		if (flag == Flags.LastPart) {
			last = true;
		}

	}

	public boolean isAll() {
		return complete && last;
	}
	

	public ChatMessage createMessage(){
		if(!isAll()){
			throw new RuntimeException("Message is not complete!");
		}
		StringBuilder builder = new StringBuilder();
		for (Integer pos : messageDatas.keySet()) {
			builder.append(messageDatas.get(pos));
		}
		return new ChatMessage(builder.toString(), author);
	}
}
