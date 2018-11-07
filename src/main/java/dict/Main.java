package dict;

import example.ConcatStringsNode;
import example.DebugNode;
import example.SplitStringNode;
import flow.graph.Graph;
import flow.graph.Node;

import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        Node<List<String>, String> concatNode = new ConcatStringsNode("ConcatStringNode");
        Node<String, String> debugNode1 = new DebugNode<>();
        Node<String, List<String>> splitNode = new SplitStringNode("SplitStringNode");
        Node<List<String>, List<String>> debugNode2 = new DebugNode<>();

        concatNode.then(debugNode1);
        debugNode1.then(splitNode);
        splitNode.then(debugNode2);
        debugNode2.then(concatNode);

        List<String> input = Arrays.asList("A", "B", "C", "AB");
        Graph.start(concatNode, input);

        Graph.clean(concatNode);

        debugNode1.remove("SplitStringNode");

        List<String> input2 = Arrays.asList("Machen", "Buhu", "C", "Halleluja");
        Graph.start(concatNode, input2);

    }
}
