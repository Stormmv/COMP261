import javafx.util.Pair;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * Write a description of class PageRank here.
 *
 * @author Michael Visser
 * @version V2.1.1
 */
public class PageRank {
    // class members
    private static double dampingFactor = .85;
    private static int iter = 10;

    /**
     * build the fromLinks and toLinks
     */
    // TODO: Build the data structure to support Page rank. Compute the fromLinks
    // and toLinks for each node
    public static void computeLinks(Graph graph) {
        // TODO
        for (Edge edge : graph.getOriginalEdges()) {
            Gnode fromNode = edge.fromNode();
            Gnode toNode = edge.toNode();
            fromNode.addToLinks(toNode);
            toNode.addFromLinks(fromNode);
        }
        printPageRankGraphData(graph); //// may help in debugging
        // END TODO
    }

    public static void printPageRankGraphData(Graph graph) {
        System.out.println("\nPage Rank Graph");

        for (Gnode node : graph.getNodes().values()) {
            System.out.print("\nNode: " + node.toString());
            // for each node display the in edges
            System.out.print("\nIn links to nodes:");
            for (Gnode c : node.getFromLinks()) {

                System.out.print("[" + c.getId() + "] ");
            }

            System.out.print("\nOut links to nodes:");
            // for each node display the out edges
            for (Gnode c : node.getToLinks()) {
                System.out.print("[" + c.getId() + "] ");
            }
            System.out.println();
            ;

        }
        System.out.println("=================");
    }

    // TODO: Compute rank of all nodes in the network and display them at the
    // console
    public static void computePageRank(Graph graph) {
        // TODO
        Map<String, Gnode> nodes = graph.getNodes();
        int nodeCount = nodes.size();
        double initialRank = 1.0 / nodeCount;

        // Initialize the page rank for each node
        for (Gnode node : nodes.values()) {
            node.setPageRank(initialRank);
        }

        // Iteratively calculate the PageRank
        for (int i = 0; i < iter; i++) {
            Map<Gnode, Double> newPageRanks = new HashMap<>();
            double noOutLinkShare = 0.0;

            // Calculate the share for nodes with no outgoing links
            for (Gnode node : nodes.values()) {
                if (node.getToLinks().isEmpty()) {
                    noOutLinkShare += dampingFactor * (node.getPageRank() / nodeCount);
                }
            }

            // Calculate new PageRank values
            for (Gnode node : nodes.values()) {
                double newRank = noOutLinkShare + (1 - dampingFactor) / nodeCount;
                double rankSum = 0.0;

                for (Gnode inLinkNode : node.getFromLinks()) {
                    rankSum += inLinkNode.getPageRank() / inLinkNode.getToLinks().size();
                }

                newRank += dampingFactor * rankSum;
                newPageRanks.put(node, newRank);
            }

            // Update PageRank values
            for (Map.Entry<Gnode, Double> entry : newPageRanks.entrySet()) {
                entry.getKey().setPageRank(entry.getValue());
            }
        }

        // Display the PageRank after the final iteration
        System.out.println("Iteration " + iter + ":");
        for (Gnode node : nodes.values()) {
            System.out.println(node.getName() + "[" + node.getId() + "]: " + node.getPageRank());
        }
        // END TODO

    }

}
