
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Map;
import javafx.util.Pair;

/**
 * Edmond karp algorithm to find augmentation paths and network flow.
 * 
 * This would include building the supporting data structures:
 * 
 * a) Building the residual graph(that includes original and backward (reverse)
 * edges.)
 * - maintain a map of Edges where for every edge in the original graph we add a
 * reverse edge in the residual graph.
 * - The map of edges are set to include original edges at even indices and
 * reverse edges at odd indices (this helps accessing the corresponding backward
 * edge easily)
 * 
 * 
 * b) Using this residual graph, for each city maintain a list of edges out of
 * the city (this helps accessing the neighbours of a node (both original and
 * reverse))
 * 
 * The class finds : augmentation paths, their corresponing flows and the total
 * flow
 * 
 * 
 */

public class EdmondKarp {
    // class members

    // data structure to maintain a list of forward and reverse edges - forward
    // edges stored at even indices and reverse edges stored at odd indices
    private static Map<String, Edge> edges;

    // Augmentation path and the corresponding flow
    private static ArrayList<Pair<ArrayList<String>, Integer>> augmentationPaths = null;

    // TODO:Build the residual graph that includes original and reverse edges
    public static void computeResidualGraph(Graph graph) {
        // TODO
        edges = new HashMap<>(); // Initialize the map of edges

        // Iterate through original edges to create forward and reverse edges
        for (Edge originalEdge : graph.getOriginalEdges()) {
            if (originalEdge != null) {

                System.out.print("Original Edge: " + originalEdge.toString());
                // Add forward edge
                Edge forwardEdge = new Edge(originalEdge.fromCity(), originalEdge.toCity(), originalEdge.capacity(),
                        originalEdge.flow(), originalEdge.getId());
                edges.put(originalEdge.getId(), forwardEdge);
                System.out.print("Forward Edge: " + forwardEdge.toString());
                // Add reverse edge
                Edge reverseEdge = new Edge(originalEdge.toCity(), originalEdge.fromCity(), 0, 0,
                        originalEdge.getId() + "_reverse");
                edges.put(originalEdge.getId() + "_reverse", reverseEdge);
                System.out.print("Reverse Edge: " + reverseEdge.toString());

                // Update edgeIds for fromCity to include both forward and reverse edge IDs
                originalEdge.fromCity().addEdgeId(originalEdge.getId());
                System.out.print(
                        "Adding edge ID: " + originalEdge.getId() + " to city: " + originalEdge.fromCity().getName());
                originalEdge.fromCity().addEdgeId(originalEdge.getId() + "_reverse");
                System.out.print("Adding edge ID: " + originalEdge.getId() + "_reverse to city: "
                        + originalEdge.toCity().getName());
            } else {
                System.out.println("Original Edge is null");
            }
        }

        // Debug print to verify the edges map
        // System.out.println("Edges Map: " + edges);
        // Debug print to verify edgeIds for each city
        // for (City city : graph.getCities().values()) {
        // System.out.println("City: " + city.getName() + ", Edge IDs: " +
        // city.getEdgeIds());
        // }
        // END TODO
    }

    // Method to print Residual Graph
    public static void printResidualGraphData(Graph graph) {
        System.out.println("\nResidual Graph");
        System.out.println("\n=============================\nCities:");
        for (City city : graph.getCities().values()) {
            System.out.print(city.toString());

            // for each city display the out edges
            for (String eId : city.getEdgeIds()) {
                System.out.print("[" + eId + "] ");
            }
            System.out.println();
        }
        System.out.println("\n=============================\nEdges(Original(with even Id) and Reverse(with odd Id):");
        edges.forEach((eId, edge) -> System.out.println("[" + eId + "] " + edge.toString()));

        System.out.println("===============");
    }

    // =============================================================================
    // Methods to access data from the graph.
    // =============================================================================
    /**
     * Return the corresonding edge for a given key
     */

    public static Edge getEdge(String id) {
        return edges.get(id);
    }

    /**
     * find maximum flow
     * 
     */
    // TODO: Find augmentation paths and their corresponding flows
    public static ArrayList<Pair<ArrayList<String>, Integer>> calcMaxflows(Graph graph, City from, City to) {
        // // Call computeResidualGraph at the beginning of calcMaxflows
        // computeResidualGraph(graph);

        // augmentationPaths = new ArrayList<>();
        // // Debug print to confirm that computeResidualGraph was called
        // System.out.println("Residual graph computed");
        // // Iterate until no more augmentation paths can be found
        // bfs(graph, from, to);

        // return augmentationPaths;
        computeResidualGraph(graph);
        augmentationPaths = new ArrayList<>();

        while (true) {
            Pair<ArrayList<String>, Integer> pathFlowPair = bfs(graph, from, to);
            if (pathFlowPair == null)
                break;

            ArrayList<String> path = pathFlowPair.getKey();
            int flow = pathFlowPair.getValue();

            for (String edgeId : path) {
                Edge edge = getEdge(edgeId);
                System.out.println("Updating flow for edge: " + edgeId + " from " + edge.flow() + " by " + flow);
                edge.setFlow(edge.flow() + flow);
                System.out.println("Flow updated for edge: " + edgeId + " to " + edge.flow());
            }

            augmentationPaths.add(pathFlowPair);
        }

        return augmentationPaths;
    }

    // TODO:Use BFS to find a path from s to t along with the correponding
    // bottleneck flow
    public static Pair<ArrayList<String>, Integer> bfs(Graph graph, City s, City t) {

        // Pair<ArrayList<String>, Integer> augmentationPath;
        HashMap<String, String> backPointer = new HashMap<String, String>();
        // TODO
        HashSet<String> visited = new HashSet<>();
        Queue<City> queue = new LinkedList<>();

        queue.add(s);
        visited.add(s.getId());
        System.out.println("Starting BFS from city: " + s.getName());

        while (!queue.isEmpty()) {
            City currentCity = queue.poll();
            System.out.println("Visiting city: " + currentCity.getName());

            // Check all edges out of current city
            for (String edgeId : currentCity.getEdgeIds()) {
                Edge edge = getEdge(edgeId);

                // Check if edge leads to an unvisited city with available capacity
                City nextCity = edge.toCity();
                System.out.println("Considering edge: " + edgeId + " to city: " + nextCity.getName());
                System.out.println("Visited" + visited);
                if (!visited.contains(nextCity.getId()) && edge.capacity() > edge.flow()) {
                    queue.add(nextCity);
                    visited.add(nextCity.getId());
                    backPointer.put(nextCity.getId(), edgeId);
                    System.out.println("Added city to queue: " + nextCity.getName());

                    // If we reach the destination, backtrack to find augmentation path
                    if (nextCity.equals(t)) {
                        // visited.clear();
                        System.out.println("Reached destination city: " + t.getName());
                        ArrayList<String> path = new ArrayList<>();
                        int bottleneck = Integer.MAX_VALUE;
                        String cityId = nextCity.getId();

                        // Backtrack from destination to source to find augmentation path
                        while (!cityId.equals(s.getId())) {
                            String prevEdgeId = backPointer.get(cityId);
                            path.add(prevEdgeId);
                            Edge prevEdge = getEdge(prevEdgeId);
                            bottleneck = Math.min(bottleneck, prevEdge.capacity() - prevEdge.flow());
                            // prevEdge.setFlow(bottleneck + prevEdge.flow());
                            // System.out.println("Flow updated for edge: " + prevEdgeId + " to " +
                            // prevEdge.flow());
                            System.out.println("Bottleneck: " + bottleneck);
                            cityId = prevEdge.fromCity().getId();
                        }

                        // Reverse path to get it from source to destination
                        Collections.reverse(path);
                        System.out.println("Augmentation path found: " + path);
                        // if (bottleneck != 0) {
                        // augmentationPaths.add(new Pair<ArrayList<String>, Integer>(path,
                        // bottleneck));
                        // }
                        return new Pair<>(path, bottleneck);
                    }
                }
            }
        }

        // If no augmentation path found
        System.out.println("No augmentation path found");
        // END TODO
        return null;
    }

}
