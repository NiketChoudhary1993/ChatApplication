package com.example.niket.chatapplication.pojoClass;

/**
 * Created by Niket on 2/2/2018.
 */

public class MessagePojo {
    private String message;
    private String senderID;
    private String ReceiverID;
    private String MessageId;
    private String senderName;
    private String receiverName;
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getMessageId() {
        return MessageId;
    }

    public void setMessageId(String messageId) {
        MessageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return ReceiverID;
    }

    public void setReceiverID(String receiverID) {
        ReceiverID = receiverID;
    }


}
