
/**
 * Interface for all nodes that can be executed,
 * including the top level program node
 */

import java.util.*;

interface ProgramNode {
    public void execute(Robot robot);
}

class moveNode implements ProgramNode {

    public moveNode() {
    }

    public String toString() {
        return "move ";
    }

    public void execute(Robot robot) {
        robot.move();
    }
}

class turnLNode implements ProgramNode {
    public turnLNode() {
    }

    public String toString() {
        return "turnL";
    }

    public void execute(Robot robot) {
        robot.turnLeft();
    }
}

class turnRNode implements ProgramNode {
    public turnRNode() {
    }

    public String toString() {
        return "turnR";
    }

    public void execute(Robot robot) {
        robot.turnRight();
    }
}

class takeFuelNode implements ProgramNode {
    public takeFuelNode() {
    }

    public String toString() {
        return "takeFuel";
    }

    public void execute(Robot robot) {
        robot.takeFuel();
    }
}

class waitNode implements ProgramNode {
    public waitNode() {
    }

    public String toString() {
        return "wait";
    }

    public void execute(Robot robot) {
        robot.idleWait();
    }
}

class loopNode implements ProgramNode {
    private ProgramNode node;

    public loopNode(ProgramNode node) {
        this.node = node;
    }

    public String toString() {
        return "loop " + node;
    }

    public void execute(Robot robot) {
        node.execute(robot);
    }
}

class BlockNode implements ProgramNode {
    private List<ProgramNode> nodes;

    public BlockNode(List<ProgramNode> nodes) {
        this.nodes = nodes;
    }

    public String toString() {
        String result = "{";
        for (int i = 0; i < nodes.size(); i++) {
            result += nodes.get(i);
        }
        result += "}";
        return result;
    }

    public void execute(Robot robot) {
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).execute(robot);
        }
    }
}

class listNode implements ProgramNode {
    private List<ProgramNode> nodes;

    public listNode(List<ProgramNode> nodes) {
        this.nodes = nodes;
    }

    public String toString() {
        String result = "";
        for (ProgramNode node : nodes) {
            result += node;
        }
        return result;
    }

    public void execute(Robot robot) {
        for (ProgramNode node : nodes) {
            node.execute(robot);
        }
    }
}

class turnAroundNode implements ProgramNode {
    public turnAroundNode() {
    }

    public String toString() {
        return "turnAround";
    }

    public void execute(Robot robot) {
        robot.turnAround();
    }
}

class shieldOnNode implements ProgramNode {
    public shieldOnNode() {
    }

    public String toString() {
        return "shieldOn";
    }

    public void execute(Robot robot) {
        robot.setShield(true);
    }
}

class shieldOffNode implements ProgramNode {
    public shieldOffNode() {
    }

    public String toString() {
        return "shieldOff";
    }

    public void execute(Robot robot) {
        robot.setShield(false);
    }
}

class ifNode implements ProgramNode {
    private Bool bool;
    private ProgramNode run;

    public ifNode(Bool bool, ProgramNode run) {
        this.bool= bool;
        this.run= run;
    }

    public void execute(Robot robot){
        if(bool.condition(robot)){
            run.execute(robot);
        }
    }
}

interface Bool{
    public boolean condition(Robot robot);
}

class fuelLeftNode implements SensorNode{
    public fuelLeftNode(){
    }

    public int num(Robot robot) {
        return robot.getFuel();
    }
}

interface SensorNode{
    public int num(Robot robot);
}