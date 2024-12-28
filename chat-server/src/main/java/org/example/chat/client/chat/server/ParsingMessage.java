package org.example.chat.client.chat.server;

public class ParsingMessage {
    private String[] arrayWord;

    public ParsingMessage (String messeage) {
        arrayWord = messeage.split(" ");
    }

    public boolean isPrivateMessage() {
        return arrayWord[1].charAt(0) == '@';
    }

    public String getName() {
        return arrayWord[1].substring(1);
    }

    public String getMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < arrayWord.length ; i++) {
            if (i != 1) {
                stringBuilder.append(arrayWord[i]);
                if (i != arrayWord.length -1) {
                    stringBuilder.append(" ");
                }
            }
        }
        return stringBuilder.toString();
    }

}
