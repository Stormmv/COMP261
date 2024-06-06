
/**
 * Implements the A* search algorithm to find the shortest path
 *  in a graph between a start node and a goal node.
 * If start or goal are null, it returns null
 * If start and goal are the same, it returns an empty path
 * Otherwise, it returns a Path consisting of a list of Edges that will
 * connect the start node to the goal node.
 */

import java.util.Collections;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class AStar {

    public static List<Edge> findShortestPath(Stop start, Stop goal) {
        if (start == null || goal == null) {
            return null;
        }

        if (start == goal) {
            return new ArrayList<Edge>(); // return an empty path as start and goal are the same
        }

        // The set of visited nodes that have already been evaluated
        Set<Stop> closedSet = new HashSet<>();

        // The set of currently discovered nodes that are not evaluated yet.
        // Initially, only the start node is known.
        PriorityQueue<SearchQueueItem> openSet = new PriorityQueue<>();

        // For each node, which node it can most efficiently be reached from.
        // If a node can be reached from many nodes, cameFrom will eventually
        // contain the most efficient previous step.
        Map<Stop, Edge> cameFrom = new HashMap<>();

        // For each node, the cost of getting from the start node to that node.
        Map<Stop, Double> costSoFar = new HashMap<>();

        // For the first node, that value is completely heuristic.
        openSet.add(new SearchQueueItem(start, null, 0, start.distanceTo(goal)));

        // The cost of going from start to start is zero.
        costSoFar.put(start, 0.0);

        // The main loop
        while (!openSet.isEmpty()) {
            // Get the node in openSet having the lowest fScore value
            SearchQueueItem current = openSet.poll();
            // Get the stop of the current node
            Stop currentStop = current.getStop();

            // If the current node is the goal, reconstruct the path and return it
            if (currentStop == goal) {
                return reconstructPath(cameFrom, currentStop);
            }

            // Mark the current node as evaluated
            closedSet.add(currentStop);

            // For each neighbor of the current node
            for (Edge edge : currentStop.getEdges()) {
                // Get the stop of the neighbor
                Stop nextStop = edge.toStop();
                // If the neighbor has already been evaluated, skip it
                if (closedSet.contains(nextStop)) {
                    continue; // Ignore the neighbor which is already evaluated.
                }

                // The distance from start to a neighbor
                double newCost = costSoFar.get(currentStop) + edge.distance();

                // If the neighbor is not in the openSet, add it
                if (!costSoFar.containsKey(nextStop) || newCost < costSoFar.get(nextStop)) {
                    // Update the cost of the neighbor
                    costSoFar.put(nextStop, newCost);
                    double priority = newCost + nextStop.distanceTo(goal);
                    // Add the neighbor to the openSet
                    openSet.add(new SearchQueueItem(nextStop, edge, newCost, priority));
                    // Record the path
                    cameFrom.put(nextStop, edge);
                }
            }
        }

        // No path found
        return null;
    }

    // Reconstruct the path from the cameFrom map
    private static List<Edge> reconstructPath(Map<Stop, Edge> cameFrom, Stop current) {
        List<Edge> totalPath = new ArrayList<>();
        while (cameFrom.containsKey(current)) {
            // Add the edge to the total path
            Edge edge = cameFrom.get(current);
            // Add the edge to the beginning of the list
            totalPath.add(edge);
            // Move to the next stop
            current = edge.fromStop();
        }
        // Reverse the list to get the correct order
        Collections.reverse(totalPath);
        // Return the total path
        return totalPath;
    }
}
