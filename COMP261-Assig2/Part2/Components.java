import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Stack;
//=============================================================================
//   Finding Components
//   Finds all the strongly connected subgraphs in the graph
//   Constructs a Map recording the number of the subgraph for each Stop
//=============================================================================

public class Components {

    // Based on Kosaraju's_algorithm
    // https://en.wikipedia.org/wiki/Kosaraju%27s_algorithm
    // Use a visited set to record which stops have been visited
    public static Map<Stop, Integer> findComponents(Graph graph) {
        // Map to store the component number for each stop
        Map<Stop, Integer> componentMap = new HashMap<>();
        // Set to store visited stops
        Set<Stop> visited = new HashSet<>();
        // Stack to store stops in order of finishing times
        Stack<Stop> stack = new Stack<>();

        // First DFS to fill the stack based on finishing times
        for (Stop stop : graph.getStops()) {
            // If the stop has not been visited, perform DFS
            if (!visited.contains(stop)) {
                depthFirstSearch1(stop, visited, stack);
            }
        }
        // Reset visited set
        visited.clear();
        // Component number
        int componentNumber = 1;

        // Second DFS based on the finishing times from the stack
        while (!stack.isEmpty()) {
            // Pop the stop from the stack
            Stop currentStop = stack.pop();
            // If the stop has not been visited, perform DFS
            if (!visited.contains(currentStop)) {
                // Perform DFS and increment component number
                depthFirstSearch2(currentStop, visited, componentMap, componentNumber);
                componentNumber++;
            }
        }
        // Return the component map
        return componentMap;
    }

    // DFS to fill the stack based on finishing times
    private static void depthFirstSearch1(Stop stop, Set<Stop> visited, Stack<Stop> stack) {
        // Add the stop to the visited set
        visited.add(stop);
        // Iterate through the outgoing edges of the stop
        for (Edge edge : stop.getEdgesOut()) {
            // Get the next stop
            Stop nextStop = edge.toStop();
            // If the next stop has not been visited, perform DFS
            if (!visited.contains(nextStop)) {
                // Perform DFS
                depthFirstSearch1(nextStop, visited, stack);
            }
        }
        // Push the stop to the stack
        stack.push(stop);
    }

    // DFS to assign component numbers to stops
    private static void depthFirstSearch2(Stop stop, Set<Stop> visited, Map<Stop, Integer> componentMap,
            int componentNumber) {
        // Add the stop to the visited set
        visited.add(stop);
        // Assign the component number to the stop
        componentMap.put(stop, componentNumber);
        // Iterate through the incoming edges of the stop
        for (Edge edge : stop.getEdgesIn()) {
            // Get the next stop
            Stop nextStop = edge.fromStop();
            // If the next stop has not been visited, perform DFS
            if (!visited.contains(nextStop)) {
                depthFirstSearch2(nextStop, visited, componentMap, componentNumber);
            }
        }
    }
}
