package flow.graph;

import flow.Action;
import flow.states.PassiveState;
import flow.states.State;
import flow.states.UnavailableState;
import flow.states.WaitState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class PassiveNetworkEndpoint<V> extends NetworkEndpoint<String, V> implements Runnable {

    protected ServerSocket serverSocket;

    public PassiveNetworkEndpoint(String id, ServerSocket serverSocket) {
        super(id, new PassiveState());
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        BufferedReader reader;
        StringBuilder builder;

        while (true) {
            try {
                if(getState().getIdentifier().equals(WaitState.ID)) {
                    Thread.sleep(5000);
                }

                Socket socket = serverSocket.accept();
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                builder = new StringBuilder();

                String s;
                while((s = reader.readLine()) != null) {
                    builder.append(s + "\n");
                }
                break;
            } catch (IOException e) {
                e.printStackTrace();
                setState(new UnavailableState());
            } catch (InterruptedException e) {
                e.printStackTrace();
                setState(new UnavailableState());
            }
        }
    }

    @Override
    public void initialize() {
        // not needed for PassiveNetworkEndpoint
    }

    @Override
    protected V send(String data, State state, Action action) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support sending messages!");
    }

}
