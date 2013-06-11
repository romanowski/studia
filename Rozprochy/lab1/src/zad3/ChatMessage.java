package zad3;

public class ChatMessage {

	String message;
	String author;
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public ChatMessage(String message, String author) {
		setAuthor(author);
		setMessage(message);
	}
	
	
	
	
}
