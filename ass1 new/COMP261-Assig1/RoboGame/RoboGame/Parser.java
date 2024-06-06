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
    static final Pattern ACT_PATTERN = Pattern.compile("move|turnL|turnR|takeFuel|wait");

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

    ProgramNode parseStatementNode(Scanner s) {
        switch (s.next()) {
            case "act":
                ProgramNode act = parseActNode(s);
                require(";", "You missed the ';'", s);
                return act;
            case "loop":
                ProgramNode loop = parseLoopNode(s);
                return loop;
            case "if":
                ProgramNode if= parseIfNode(s);
                return ifNode;
            case "while":
                ProgramNode while = parseWhileNode(s);
                return while;
                throw new ParserFailureException("Act, loop. if, while needed");
        }
    }
    ProgramNode parseActNode(Scanner s) {
        if (s.hasNext("move")) {
            s.next();
            return new moveNode();
        } else if (s.hasNext("turnL")) {
            s.next();
            return new turnLNode();
        } else if (s.hasNext("turnR")) {
            s.next();
            return new turnRNode();
        } else if (s.hasNext("takeFuel")) {
            s.next();
            return new takeFuelNode();
        } else if (s.hasNext("wait")) {
            s.next();
            return new waitNode();
        } else if (s.hasNext("turnAround")) {
            s.next();
            return new turnAroundNode();
        } else if (s.hasNext("shieldOn")) {
            s.next();
            return new shieldOnNode();
        } else if (s.hasNext("shieldOff")) {
            s.next();
            return new shieldOffNode();
        }
        return null;
    }

    ProgramNode parseLoopNode(Scanner s) {
        return new loopNode(parseLoopBlockNode(s));
    }

    ProgramNode parseLoopBlockNode(Scanner s) {
        require(OPENBRACE, "Expecting {", s);
        List<ProgramNode> nodes = new ArrayList<ProgramNode>();
        // check to see if there is anything in the node, if not then throw error
        if (!s.hasNext(ACT_PATTERN) && !s.hasNext("loop")) {
            fail("Expecting an action or loop", s);
        }
        while (s.hasNext(ACT_PATTERN) || s.hasNext("loop")) {
            if (s.hasNext(ACT_PATTERN)) {
                nodes.add(parseActNode(s));
                require(";", "No ;", s);
            } else if (s.hasNext("loop")) {
                s.next();
                nodes.add(parseLoopNode(s));
            }
        }
        require(CLOSEBRACE, "Expecting }", s);
        return new listNode(nodes);
    }

    ProgramNode createIf(Scanner s){
        require(OPENPAREN, "Missing '('", s);
        Bool condition = createBoolNode(s);
        require(CLOSEPAREN, "expecting ')'", s);
        ProgramNode run= createRun(s);
        return new if(condition, run);
}


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
        default:
            throw new IllegalArgumentException("Unexpected sensor type.");
    }
}

    Bool createBool(Scanner s){
        String gtlteq= symbolType(s);
        require(OPENPAREN, "Missing '('", s);
        SensorNode sensor = sensor(s);
        require(COMMA_PATTERN, "Expecting ,", s);
        ValueNode value= value(s);
        require(CLOSEPAREN, "expecting ')'", s);
        return new bool(gtlteq, sensor , value);
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

class ifNode implements ProgramNode {
    Bool bool;
    ProgramNode run;

    public ifNode(Bool bool, ProgramNode run){
        this.bool = bool;
        this.run = run;
    }

public void execute(Robot robot){
    if(bool.condition(robot)){
        run.execute(robot);
    }
}
}

// You could add the node classes here or as separate java files.
// (if added here, they must not be declared public or private)
// For example:
// class BlockNode implements ProgramNode {.....
// with fields, a toString() method and an execute() method
//
