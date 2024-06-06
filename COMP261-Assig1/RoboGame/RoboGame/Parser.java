import java.util.*;
import java.util.regex.*;

/**
 * See assignment handout for the grammar.
 * You need to implement the parse(..) method and all the rest of the parser.
 * There are several methods provided for you:
 * - several utility methods to help with the parsing
 * See also the TestParser class for testing your code.
 */
public class Parser {

    // Useful Patterns

    static final Pattern NUMPAT = Pattern.compile("-?[1-9][0-9]*|0");
    static final Pattern OPENPAREN = Pattern.compile("\\(");
    static final Pattern CLOSEPAREN = Pattern.compile("\\)");
    static final Pattern OPENBRACE = Pattern.compile("\\{");
    static final Pattern CLOSEBRACE = Pattern.compile("\\}");
    static final Pattern ACT_PATTERN = Pattern.compile("move|turnL|turnR|takeFuel|wait|turnAround|shieldOn|shieldOff");

    // ----------------------------------------------------------------
    /**
     * The top of the parser, which is handed a scanner containing
     * the text of the program to parse.
     * Returns the parse tree.
     */
    ProgramNode parse(Scanner s) {
        // Set the delimiter for the scanner.
        s.useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");
        // THE PARSER GOES HERE
        // Call the parseProg method for the first grammar rule (PROG) and return the
        List<ProgramNode> nodes = new ArrayList<ProgramNode>();
        while (s.hasNext()) {
            nodes.add(parseStatementNode(s));
        }
        return new listNode(nodes);
    }
    
    /**
     * Parse a statement node.
     * This method is used to parse the statement node.
     * It checks the next token to see if it is an action or a block.
     * If it is an action, it will parse the action node.
     * If it is a block, it will parse the block node.
     */
    ProgramNode parseStatementNode(Scanner s) {
        if (s.hasNext(ACT_PATTERN)) {
            ProgramNode act = parseActNode(s);
            require(";", "You missed the ';'", s);
            return act;
        }
        switch (s.next()) {
            case "loop":
                ProgramNode loop = parseLoopBlockNode(s);
                return loop;
            case "if":
                ProgramNode ifNodeNew = parseIfBlockNode(s);
                return ifNodeNew;
            case "while":
                ProgramNode whileNodeNew = parseWhileBlockNode(s);
                return whileNodeNew;
            default:
                throw new ParserFailureException("Act, loop. if, while needed");
        }
    }

    /**
     * Parse an action node.
     * This method is used to parse the action node.
     * It checks the next token to see if it is an action.
     * If it is an action, it will parse the action node.
     * If it is not an action, it will return null.
     */
    ProgramNode parseActNode(Scanner s) {
        switch (s.next()) {
            case "move":
                return new moveNode();
            case "turnL":
                return new turnLNode();
            case "turnR":
                return new turnRNode();
            case "takeFuel":
                return new takeFuelNode();
            case "wait":
                return new waitNode();
            case "turnAround":
                return new turnAroundNode();
            case "shieldOn":
                return new shieldOnNode();
            case "shieldOff":
                return new shieldOffNode();
        }
        return null;
    }

    /**
     * Parse a loop block node.
     * This method is used to parse the loop block node.
     */

    ProgramNode parseLoopBlockNode(Scanner s) {
        require(OPENBRACE, "Expecting {", s);
        ProgramNode run = createRun(s);
        require(CLOSEBRACE, "Expecting }", s);
        return new loopNode(run);
    }

    /**
     * Parse an if block node.
     * This method is used to parse the if block node.
     */

    ProgramNode parseIfBlockNode(Scanner s) {
        require(OPENPAREN, "Missing '('", s);
        Bool condition = createBool(s);
        require(CLOSEPAREN, "expecting ')'", s);
        require(OPENBRACE, "Expecting {", s);
        ProgramNode run = createRun(s);
        require(CLOSEBRACE, "Expecting }", s);
        return new ifNode(condition, run);
    }

    /**
     * Parse a while block node.
     * This method is used to parse the while block node.
     */

    ProgramNode parseWhileBlockNode(Scanner s) {
        require(OPENPAREN, "missing '('", s);
        Bool condition = createBool(s);
        require(CLOSEPAREN, "expecting ')'", s);
        require(OPENBRACE, "Expecting {", s);
        ProgramNode run = createRun(s);
        require(CLOSEBRACE, "Expecting }", s);
        return new whileNode(condition, run);
    }

    /**
     * Parse a run node.
     * This method is used to parse the run node.
     * It checks the next token to see if it is an action or a block.
     * If it is an action, it will parse the action node.
     * If it is a block, it will parse the block node.
     */

    ProgramNode createRun(Scanner s) {
        List<ProgramNode> nodes = new ArrayList<ProgramNode>();
        // check to see if there is anything in the node, if not then throw error
        if (!s.hasNext(ACT_PATTERN) && !s.hasNext("loop") && !s.hasNext("if") && !s.hasNext("while")) {
            fail("Expecting an action or loop", s);
        }
        while (s.hasNext(ACT_PATTERN) || s.hasNext("loop") || s.hasNext("if") || s.hasNext("while")) {
            if (s.hasNext(ACT_PATTERN)) {
                nodes.add(parseActNode(s));
                require(";", "No ;", s);
            } else {
                switch (s.next()) {
                    case "loop":
                        nodes.add(parseLoopBlockNode(s));
                        break;
                    case "if":
                        nodes.add(parseIfBlockNode(s));
                        break;
                    case "while":
                        nodes.add(parseWhileBlockNode(s));
                        break;
                    default:
                        // Handle unexpected token
                        break;
                }
            }
        }
        return new run(nodes);
    }

    /**
     * Parse a sensor node.
     * This method is used to parse the sensor node.
     * It checks the next token to see if it is a sensor.
     * If it is a sensor, it will parse the sensor node.
     * If it is not a sensor, it will throw an exception.
     */

    SensorNode createSensor(Scanner s) {
        switch (s.next()) {
            case "fuelLeft":
                return new fuelLeftNode();
            case "oppLR":
                return new oppLRNode();
            case "oppFB":
                return new oppFBNode();
            case "numBarrels":
                return new numBarrelsNode();
            case "barrelLR":
                return new barrelLRNode();
            case "barrelFB":
                return new barrelFBNode();
            case "wallDist":
                return new wallDistNode();
            default:
                throw new ParserFailureException("Unexpected sensor type.");
        }
    }

    /**
     * Parse a symbol node.
     * This method is used to parse the symbol node.
     * It checks the next token to see if it is a symbol.
     * If it is a symbol, it will parse the symbol node.
     * If it is not a symbol, it will throw an exception.
     */

    public String symbolType(Scanner s) {
        switch (s.next()) {
            case "gt":
                return "gt";
            case "lt":
                return "lt";
            case "eq":
                return "eq";
            default:
                throw new ParserFailureException("Unexpected symbol type.");
        }
    }

    /**
     * Parse a value node.
     * This method is used to parse the value node.
     * It checks the next token to see if it is a value.
     * If it is a value, it will parse the value node.
     */

    public Value createValue(Scanner s) {
        int value = requireInt(NUMPAT, "Expecting a number", s);
        return new value2(value);
    }

    /**
     * Parse a bool node.
     * This method is used to parse the bool node.
     */

    public Bool createBool(Scanner s) {
        String gtlteq = symbolType(s);
        require(OPENPAREN, "Missing '('", s);
        SensorNode sensor = createSensor(s);
        require(",", "Expecting ,", s);
        Value val = createValue(s);
        require(CLOSEPAREN, "expecting ')'", s);
        return new bool2(gtlteq, sensor, val);
    }
    // ----------------------------------------------------------------
    // utility methods for the parser
    // - fail(..) reports a failure and throws exception
    // - require(..) consumes and returns the next token as long as it matches the
    // pattern
    // - requireInt(..) consumes and returns the next token as an int as long as it
    // matches the pattern
    // - checkFor(..) peeks at the next token and only consumes it if it matches the
    // pattern

    /**
     * Report a failure in the parser.
     */
    static void fail(String message, Scanner s) {
        String msg = message + "\n   @ ...";
        for (int i = 0; i < 5 && s.hasNext(); i++) {
            msg += " " + s.next();
        }
        throw new ParserFailureException(msg + "...");
    }

    /**
     * Requires that the next token matches a pattern if it matches, it consumes
     * and returns the token, if not, it throws an exception with an error
     * message
     */
    static String require(String p, String message, Scanner s) {
        if (s.hasNext(p)) {
            return s.next();
        }
        fail(message, s);
        return null;
    }

    static String require(Pattern p, String message, Scanner s) {
        if (s.hasNext(p)) {
            return s.next();
        }
        fail(message, s);
        return null;
    }

    /**
     * Requires that the next token matches a pattern (which should only match a
     * number) if it matches, it consumes and returns the token as an integer
     * if not, it throws an exception with an error message
     */
    static int requireInt(String p, String message, Scanner s) {
        if (s.hasNext(p) && s.hasNextInt()) {
            return s.nextInt();
        }
        fail(message, s);
        return -1;
    }

    static int requireInt(Pattern p, String message, Scanner s) {
        if (s.hasNext(p) && s.hasNextInt()) {
            return s.nextInt();
        }
        fail(message, s);
        return -1;
    }

    /**
     * Checks whether the next token in the scanner matches the specified
     * pattern, if so, consumes the token and return true. Otherwise returns
     * false without consuming anything.
     */
    static boolean checkFor(String p, Scanner s) {
        if (s.hasNext(p)) {
            s.next();
            return true;
        }
        return false;
    }

    static boolean checkFor(Pattern p, Scanner s) {
        if (s.hasNext(p)) {
            s.next();
            return true;
        }
        return false;
    }

}

// ----------------------------------------------------------------

/**
 * Move node class
 * This class is used to store a move node
 */

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

/**
 * Turn left node class
 * This class is used to store a turn left node
 */

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

/**
 * Turn right node class
 * This class is used to store a turn right node
 */

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

/**
 * Take fuel node class
 * This class is used to store a take fuel node
 */

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

/**
 * Wait node class
 * This class is used to store a wait node
 */

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

/**
 * Loop node class
 * This class is used to store a loop node
 */

class loopNode implements ProgramNode {
    private ProgramNode node;

    public loopNode(ProgramNode node) {
        this.node = node;
    }

    public String toString() {
        return "loop " + node;
    }

    public void execute(Robot robot) {
        while(true){
            node.execute(robot);
        }
    }
}

/**
 * Run node class
 * This class is used to store a run node
 */

class run implements ProgramNode {
    private List<ProgramNode> nodes;

    public run(List<ProgramNode> nodes) {
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

/**
 * List node class
 * This class is used to store a list node
 */

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

/**
 * Turn around node class
 * This class is used to store a turn around node
 */

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

/**
 * Shield on node class
 * This class is used to store a shield on node
 */

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

/**
 * Shield off node class
 * This class is used to store a shield off node
 */

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

/**
 * While node class
 * This class is used to store a while node
 */

class whileNode implements ProgramNode {
    private Bool bool;
    private ProgramNode run;

    public whileNode(Bool bool, ProgramNode run) {
        this.bool = bool;
        this.run = run;
    }

    public void execute(Robot robot) {
        while (bool.condition(robot)) {
            run.execute(robot);
        }
    }

    public String toString() {
        return "while (" + bool + ") " + run;
    }
}

/**
 * If node class
 * This class is used to store an if node
 */

class ifNode implements ProgramNode {
    private Bool bool;
    private ProgramNode run;

    public ifNode(Bool bool, ProgramNode run) {
        this.bool = bool;
        this.run = run;
    }

    public void execute(Robot robot) {
        if (bool.condition(robot)) {
            run.execute(robot);
        }
    }

    public String toString() {
        return "if (" + bool + ") " + run;
    }
}

/**
 * Bool interface
 * This interface is used to store a boolean condition
 */

interface Bool {
    public boolean condition(Robot robot);
}

/**
 * Sensor node interface
 * This interface is used to store a sensor node
 */

interface SensorNode {
    public int num(Robot robot);
}

/**
 * Value interface
 * This interface is used to store a value node
 */

interface Value {
    public int getValue();
}

/**
 * Bool2 class
 * This class is used to store a boolean condition
 */

class bool2 implements Bool {
    private String gtlteq;
    private SensorNode sensor;
    private Value value;

    public bool2(String gtlteq, SensorNode sensor, Value value) {
        this.gtlteq = gtlteq;
        this.sensor = sensor;
        this.value = value;
    }

    public boolean condition(Robot robot) {
        switch (gtlteq) {
            case "gt":
                return sensor.num(robot) > value.getValue();
            case "lt":
                return sensor.num(robot) < value.getValue();
            case "eq":
                return sensor.num(robot) == value.getValue();
            default:
                throw new IllegalArgumentException("Unexpected symbol type.");
        }
    }

    public String toString() {
        return sensor + " " + gtlteq + " " + value;
    }
}

/**
 * Value2 class
 * This class is used to store a value node
 */

class value2 implements Value {
    private int value;

    public value2(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        return "" + value;
    }
}

/**
 * oppLRNode class
 * This class is used to store the opponent's left/right position
 */

class oppLRNode implements SensorNode {
    public oppLRNode() {
    }

    public int num(Robot robot) {
        return robot.getOpponentLR();
    }

    public String toString() {
        return "oppLR";
    }
}

/**
 * oppFBNode class
 * This class is used to store the opponent's front/back position
 */

class oppFBNode implements SensorNode {
    public oppFBNode() {
    }

    public int num(Robot robot) {
        return robot.getOpponentFB();
    }

    public String toString() {
        return "oppFB";
    }
}

/**
 * numBarrelsNode class
 * This class is used to store the number of barrels
 */

class numBarrelsNode implements SensorNode {
    public numBarrelsNode() {
    }

    public int num(Robot robot) {
        return robot.numBarrels();
    }

    public String toString() {
        return "numBarrels";
    }
}

/**
 * fuelLeftNode class
 * This class is used to store the amount of fuel left
 */

class fuelLeftNode implements SensorNode {
    public fuelLeftNode() {
    }

    public int num(Robot robot) {
        return robot.getFuel();
    }

    public String toString() {
        return "fuelLeft";
    }
}

/**
 * barrelLRNode class
 * This class is used to store the closest barrel's left/right position
 */

class barrelLRNode implements SensorNode {
    public barrelLRNode() {
    }

    public int num(Robot robot) {
        return robot.getClosestBarrelLR();
    }

    public String toString() {
        return "barrelLR";
    }
}

/**
 * barrelFBNode class
 * This class is used to store the closest barrel's front/back position
 */

class barrelFBNode implements SensorNode {
    public barrelFBNode() {
    }

    public int num(Robot robot) {
        return robot.getClosestBarrelFB();
    }

    public String toString() {
        return "barrelFB";
    }
}

/**
 * wallDistNode class
 * This class is used to store the distance to the wall
 */

class wallDistNode implements SensorNode {
    public wallDistNode() {
    }

    public int num(Robot robot) {
        return robot.getDistanceToWall();
    }

    public String toString() {
        return "wallDist";
    }
}

// You could add the node classes here or as separate java files.
// (if added here, they must not be declared public or private)
// For example:
// class BlockNode implements ProgramNode {.....
// with fields, a toString() method and an execute() method
//
