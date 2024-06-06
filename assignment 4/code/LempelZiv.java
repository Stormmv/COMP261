
import java.util.*;

public class LempelZiv {
    private static final int WINDOW_SIZE = 100;
    private static final int LOOKAHEAD_BUFFER_SIZE = 8;
    /**
     * Take uncompressed input as a text string, compress it, and return it as a
     * text string.
     */
    public static String compress(String input) {
        StringBuilder compressed = new StringBuilder();
        int inputLength = input.length();
        int currentPosition = 0;

        while (currentPosition < inputLength) {
            int searchWindowStart = Math.max(0, currentPosition - WINDOW_SIZE);
            String searchWindow = input.substring(searchWindowStart, currentPosition);
            String lookaheadBuffer = input.substring(currentPosition, Math.min(currentPosition + LOOKAHEAD_BUFFER_SIZE, inputLength));

            int matchLength = 0;
            int matchOffset = 0;

            // Find the longest match in the search window
            for (int i = 0; i < lookaheadBuffer.length(); i++) {
                String sub = lookaheadBuffer.substring(0, i + 1);
                int index = searchWindow.indexOf(sub);
                if (index != -1) {
                    matchLength = i + 1;
                    matchOffset = searchWindow.length() - index;
                } else {
                    break;
                }
            }

            if (matchLength > 0) {
                char nextChar = (currentPosition + matchLength < inputLength) ? input.charAt(currentPosition + matchLength) : '\0';
                compressed.append("[").append(matchOffset).append("|").append(matchLength).append("|").append(nextChar).append("]");
                currentPosition += matchLength + 1;
            } else {
                compressed.append("[0|0|").append(input.charAt(currentPosition)).append("]");
                currentPosition++;
            }
        }

        return compressed.toString();
    }

    /**
     * Take compressed input as a text string, decompress it, and return it as a
     * text string.
     */
    public static String decompress(String compressed) {
        StringBuilder decompressed = new StringBuilder();
        int currentPosition = 0;
        int length = compressed.length();

        while (currentPosition < length) {
            int startIndex = compressed.indexOf('[', currentPosition);
            int endIndex = compressed.indexOf(']', startIndex);

            String tuple = compressed.substring(startIndex + 1, endIndex);
            String[] parts = tuple.split("\\|");

            int offset = Integer.parseInt(parts[0]);
            int matchLength = Integer.parseInt(parts[1]);
            char nextChar = parts[2].charAt(0);

            int decompressedLength = decompressed.length();
            if (offset > 0) {
                int matchStart = decompressedLength - offset;
                for (int i = 0; i < matchLength; i++) {
                    decompressed.append(decompressed.charAt(matchStart + i));
                }
            }
            if (nextChar != '\0') {
                decompressed.append(nextChar);
            }

            currentPosition = endIndex + 1;
        }

        return decompressed.toString();
    }

    /**
     * The getInformation method is here for your convenience, you don't need to
     * fill it in if you don't want to. It is called on every run and its return
     * value is displayed on-screen. You can use this to print out any relevant
     * information from your compression.
     */
    public String getInformation() {
        return "";
    }
}
