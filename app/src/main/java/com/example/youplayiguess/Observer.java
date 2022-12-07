package com.example.youplayiguess;

public interface Observer {
    <T> void subscribe(T subject);
    void update(String message);
}
