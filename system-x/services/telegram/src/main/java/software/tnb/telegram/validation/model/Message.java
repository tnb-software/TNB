package software.tnb.telegram.validation.model;

public class Message {
    private String sender_id;
    private String text;
    private String chat_id;

    public String senderId() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String text() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String chatId() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }
}
