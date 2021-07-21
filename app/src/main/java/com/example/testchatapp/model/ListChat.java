package com.example.testchatapp.model;

public class ListChat {
    private String id;

    public ListChat(String id) {
        this.id = id;
    }

    public ListChat() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ListChat{" +
                "id='" + id + '\'' +
                '}';
    }
}
