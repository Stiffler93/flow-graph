package flow.graph;

import flow.Action;
import flow.states.ActiveState;
import flow.states.State;
import flow.states.UnavailableState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class ActiveNetworkEndpoint<T, V> extends NetworkEndpoint<T, V> {

    protected Socket socket;
    private PrintWriter sender;
    private BufferedReader receiver;

    public ActiveNetworkEndpoint(String id, Socket socket) {
        super(id, new ActiveState());
        this.socket = socket;
    }

    @Override
    public void initialize() {
        try {
            sender = new PrintWriter(socket.getOutputStream());
            receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            setState(new UnavailableState());
        }
    }

    @Override
    protected V receive(T data, State state, Action action) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support receiving messages!");
    }
}
