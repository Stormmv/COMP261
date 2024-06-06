/**
 * A new KMP instance is created for every substring search performed. Both the
 * pattern and the text are passed to the constructor and the search method. You
 * could, for example, use the constructor to create the match table and the
 * search method to perform the search itself.
 */
public class KMP {

	/**
	 * Perform KMP substring search on the given text with the given pattern.
	 * 
	 * This should return the starting index of the first substring match if it
	 * exists, or -1 if it doesn't.
	 */
	public static int search(String pattern, String text) {
		int[] matchTable = buildMatchTable(pattern);
		int i = 0;
		int j = 0;
		while (i < text.length()) {
			if (pattern.charAt(j) == text.charAt(i)) {
				if (j == pattern.length() - 1) {
					return i - j;
				}
				i++;
				j++;
			} else {
				if (j > 0) {
					j = matchTable[j - 1];
				} else {
					i++;
				}
			}
		}
		return -1;
	}

	/**
	 * Build the KMP match table for the given pattern.
	 * 
	 * This should return an array of integers where matchTable[i] is the length of
	 * the longest proper suffix of pattern.substring(0, i + 1) that is also a
	 * proper prefix of pattern.substring(0, i + 1).
	 */
	public static int[] buildMatchTable(String pattern) {
		int[] matchTable = new int[pattern.length()];
		int i = 1;
		int j = 0;
		while (i < pattern.length()) {
			if (pattern.charAt(j) == pattern.charAt(i)) {
				matchTable[i] = j + 1;
				i++;
				j++;
			} else {
				if (j > 0) {
					j = matchTable[j - 1];
				} else {
					i++;
				}
			}
		}
		return matchTable;
	}
}
