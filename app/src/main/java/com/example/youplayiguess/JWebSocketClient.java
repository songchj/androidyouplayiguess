package com.example.youplayiguess;

import android.util.Log;

import com.example.youplayiguess.constants.NetWorkConstant;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class JWebSocketClient extends WebSocketClient {
    private static JWebSocketClient client ;
    private List<Observer> observers = new ArrayList<>();

    private JWebSocketClient(URI serverUri) {
        super(serverUri);
    }
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.i("JWebSocketClient", "onOpen()");
    }
    @Override
    public void onMessage(String message) {
        Log.i("JWebSocketClient", "onMessage()" + message);
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.i("JWebSocketClient", "onClose()");
        if (client != null) {
            client.close();
            client = null;
        }
    }
    @Override
    public void onError(Exception ex) {
        Log.e("JWebSocketClient", "onError()" +  ex.getMessage());
    }

    public static JWebSocketClient getClient(String roomNo, String username) {
        Log.i("JWebSocketClient",  "roomNo: " + roomNo +", username: " + username);
        if (client == null) {
            String url = NetWorkConstant.WEB_SOCKET_URL.replace("{roomNo}", roomNo).replace("{username}", username);
            client = new JWebSocketClient(URI.create(url));
        }
        if (!client.isOpen()) {
            client.connect();
        }
        return client;
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }
}