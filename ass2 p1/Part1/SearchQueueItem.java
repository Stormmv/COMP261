/**
 * AStar search uses a priority queue of partial paths
 * that the search is building.
 * Each partial path needs several pieces of information
 * to specify the path to that point, its cost so far, and
 * its estimated total cost
 */

public class SearchQueueItem implements Comparable<SearchQueueItem> {

    private Stop stop;
    private Edge edge;
    private double costSoFar;
    private double estimatedTotalCost;

    public SearchQueueItem(Stop stop, Edge edge, double costSoFar, double estimatedTotalCost) {
        this.stop = stop;
        this.edge = edge;
        this.costSoFar = costSoFar;
        this.estimatedTotalCost = estimatedTotalCost;
    }

    public Stop getStop() {
        return stop;
    }

    public Edge getEdge() {
        return edge;
    }

    public double getCostSoFar() {
        return costSoFar;
    }

    public double getEstimatedTotalCost() {
        return estimatedTotalCost;
    }

    public int compareTo(SearchQueueItem other) {
        return Double.compare(this.estimatedTotalCost, other.estimatedTotalCost);
    }
}
