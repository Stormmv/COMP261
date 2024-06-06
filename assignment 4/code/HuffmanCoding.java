
/**
 * A new instance of HuffmanCoding is created for every run. The constructor is
 * passed the full text to be encoded or decoded, so this is a good place to
 * construct the tree. You should store this tree in a field and then use it in
 * the encode and decode methods.
 */

import java.util.*;

public class HuffmanCoding {
	private Node root;
	private Map<Character, String> huffmanCodeMap;

	/*
	 * This class is used to store the tree. Each node has a character, a frequency,
	 * and a left and right child. It also implements the Comparable interface so
	 * that the nodes can be compared by frequency.
	 */
	private static class Node implements Comparable<Node> {
		char ch;
		int frequency;
		Node left, right;

		Node(char ch, int frequency) {
			this.ch = ch;
			this.frequency = frequency;
		}

		Node(char ch, int frequency, Node left, Node right) {
			this.ch = ch;
			this.frequency = frequency;
			this.left = left;
			this.right = right;
		}

		@Override
		public int compareTo(Node other) {
			if (this.frequency != other.frequency) {
				return Integer.compare(this.frequency, other.frequency);
			}
			return Character.compare(this.ch, other.ch);
		}
	}

	/**
	 * This would be a good place to compute and store the tree.
	 */
	public HuffmanCoding(String text) {
		// Step 1: Construct the frequency table
		Map<Character, Integer> frequencyMap = new HashMap<>();
		for (char ch : text.toCharArray()) {
			frequencyMap.put(ch, frequencyMap.getOrDefault(ch, 0) + 1);
		}

		// Step 2: Build the Huffman Tree
		PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
		for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
			priorityQueue.add(new Node(entry.getKey(), entry.getValue()));
		}

		while (priorityQueue.size() > 1) {
			Node left = priorityQueue.poll();
			Node right = priorityQueue.poll();
			Node combined = new Node('\0', left.frequency + right.frequency, left, right);
			priorityQueue.add(combined);
		}

		root = priorityQueue.poll();

		// Step 3: Generate Huffman Codes
		huffmanCodeMap = new HashMap<>();
		generateCodes(root, "");
	}

	/*
	 * Generate the Huffman Codes for each character in the tree.
	 */
	private void generateCodes(Node node, String code) {
		if (node == null) {
			return;
		}

		if (node.left == null && node.right == null) {
			huffmanCodeMap.put(node.ch, code);
		}

		generateCodes(node.left, code + '0');
		generateCodes(node.right, code + '1');
	}

	/**
	 * Take an input string, text, and encode it with the stored tree. Should
	 * return the encoded text as a binary string, that is, a string containing
	 * only 1 and 0.
	 */
	public String encode(String text) {
		StringBuilder encodedText = new StringBuilder();
		for (char ch : text.toCharArray()) {
			encodedText.append(huffmanCodeMap.get(ch));
		}
		return encodedText.toString();
	}

	/**
	 * Take encoded input as a binary string, decode it using the stored tree,
	 * and return the decoded text as a text string.
	 */
	public String decode(String encoded) {
		StringBuilder decodedText = new StringBuilder();
		Node current = root;
		for (char bit : encoded.toCharArray()) {
			if (bit == '0') {
				current = current.left;
			} else {
				current = current.right;
			}

			if (current.left == null && current.right == null) {
				decodedText.append(current.ch);
				current = root;
			}
		}
		return decodedText.toString();
	}

	/**
	 * The getInformation method is here for your convenience, you don't need to
	 * fill it in if you don't wan to. It is called on every run and its return
	 * value is displayed on-screen. You could use this, for example, to print
	 * out the encoding tree.
	 */
	public String getInformation() {
		StringBuilder info = new StringBuilder();
		for (Map.Entry<Character, String> entry : huffmanCodeMap.entrySet()) {
			info.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
		}
		return info.toString();
	}
}
