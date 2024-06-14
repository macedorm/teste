package br.com.jjconsulting.mobile.dansales.model;

public class MessageSend {
    private Context context;
    private Input input;

    public MessageSend(String id, String text, String user, String email){
        context = new Context();
        context.setSender(user);
        context.setConversation_id(id);
        context.setEmail_user(email);
        context.setOrigin("DANSALES");

        input = new Input();
        input.setText(text);
    }


    public class Context{
        private String sender;
        private String email_user;
        private String conversation_id;
        private String origin;

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getEmail_user() {
            return email_user;
        }

        public void setEmail_user(String email_user) {
            this.email_user = email_user;
        }

        public String getConversation_id() {
            return conversation_id;
        }

        public void setConversation_id(String conversation_id) {
            this.conversation_id = conversation_id;
        }

        public String getOrigin() {
            return origin;
        }

        public void setOrigin(String origin) {
            this.origin = origin;
        }
    }

    public class Input {
        private String text;


        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}



