package sonntag.declarative.nodes;

import sonntag.declarative.AbstractNode;
import sonntag.declarative.states.State;

import java.util.ArrayList;
import java.util.List;

public final class SplitStringNode extends AbstractNode<String, List<String>> {

    private int chunkSize = 1;

    public SplitStringNode(String id) {
        super(id);
    }

    public SplitStringNode(String id, int chunkSize) {
        super(id);
    }

    public SplitStringNode(String id, State state) {
        super(id, state);
    }

    public SplitStringNode(String id, State state, int chunkSize) {
        super(id, state);

        this.chunkSize = chunkSize;
    }

    @Override
    public List<String> execute(String data, State state) {

        List<String> list = new ArrayList<>();

        int numChunks = 0;
        for(int charsLeft = data.length(); charsLeft > 0; charsLeft -= chunkSize, numChunks++) {
            int sizeOfNextChunk = Math.min(charsLeft, chunkSize);
            int startPosNextChunk = numChunks * chunkSize;
            String nextChunk = data.substring(startPosNextChunk, startPosNextChunk + sizeOfNextChunk);
            list.add(nextChunk);
        }

        return list;
    }
}
