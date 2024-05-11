package hpFeatures;

import java.io.*;
import java.util.*;

/**
 * The WordCompletion class serves as the entry point for a tool
 * that helps users find the destination of hotels through word completion.
 * It integrates a Trie data structure for efficient search and suggestions
 * based on user input in the "Search destination" field.
 *
 * The class contains nested TrieNode and Trie classes for building and
 * managing the Trie data structure. It also includes the main method to
 * execute the tool, read hotel destinations from a file, and perform
 * word completion based on user input.
 */
public class WordCompletion {

	public static class TrieNode {
		char value;
		boolean isEndOfWord;
		TrieNode[] children;

		TrieNode(char value) {
			this.value = value;
			this.isEndOfWord = false;
			// Assuming a total of 128 possible characters (ASCII range)
			this.children = new TrieNode[128];
		}
	}

	public static class Trie {
		TrieNode root;

		Trie() {
			this.root = new TrieNode(' ');
		}

	public void insert(String word) {
				TrieNode current = root;
				for (char ch : word.toLowerCase().toCharArray()) {
					int index = ch;
	
					if (index < 0 || index >= 128) {
						//throw new IllegalArgumentException("Invalid character: " + ch);
						continue;
					}
	
					if (current.children[index] == null) {
						current.children[index] = new TrieNode(ch);
					}
					current = current.children[index];
				}
				current.isEndOfWord = true;
		}

		List<String> autoComplete(String prefix) {
			TrieNode node = findNode(prefix.toLowerCase());
			List<String> suggestions = new ArrayList<>();
			autoCompleteHelper(node, prefix.toLowerCase(), suggestions);

			if (suggestions.isEmpty()) {}

			return suggestions;
		}

		private TrieNode findNode(String prefix) {
			TrieNode current = root;
			for (char ch : prefix.toLowerCase().toCharArray()) {
				if (ch == ' ') {
					// Handle space character
					continue;
				}
				int index = ch;

				if (index < 0 || index >= 128 || current.children[index] == null) {
					return null;
				}
				current = current.children[index];
			}
			return current;
		}

		// Recursive helper function for word completion
		private void autoCompleteHelper(TrieNode node, String currentPrefix, List<String> suggestions) {
			if (node == null) {
				return;
			}

			if (node.isEndOfWord) {
				suggestions.add(currentPrefix);
			}

			for (int i = 0; i < 128; i++) {
				if (node.children[i] != null) {
					char nextChar = (char) i;
					autoCompleteHelper(node.children[i], currentPrefix + nextChar, suggestions);
				}
			}
		}
	}

	public static List wordCompletion(String word) throws Exception {
		Trie trie = new Trie();
		try (BufferedReader reader = new BufferedReader(new FileReader("Text_Files/destinations.txt"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				trie.insert(line.trim());
			}
		} catch (IOException e) {
			//e.printStackTrace();
		}

		List<String> suggestions = trie.autoComplete(word);

		return suggestions;
	}

}