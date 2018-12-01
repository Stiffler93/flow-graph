package main;

import sonntag.declarative.nodes.ConcatStringsNode;
import sonntag.declarative.nodes.DebugNode;
import sonntag.declarative.nodes.SplitStringNode;
import sonntag.declarative.Graph;
import sonntag.declarative.Node;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.LogManager;

public class Main {

    public static void main(String[] args) {

        InputStream is = Main.class
                .getClassLoader()
                .getResourceAsStream("logging.properties");

        try {
            LogManager.getLogManager().readConfiguration(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Node<List<String>, String> concatNode = new ConcatStringsNode("ConcatStringNode");
        Node<String, String> debugNode1 = new DebugNode<>();
        Node<String, List<String>> splitNode = new SplitStringNode("SplitStringNode");
        Node<List<String>, List<String>> debugNode2 = new DebugNode<>();

        concatNode.then(debugNode1);
        debugNode1.then(splitNode);
        splitNode.then(debugNode2);

        List<String> input = Arrays.asList("A", "B", "C", "AB");
        Graph.start(concatNode).input(input).parallel(4).run();
    }
}
