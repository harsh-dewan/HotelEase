package hpFeatures;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

public class InvertedIndexTrie {
	
	public static class TrieNodes {
	    Map<Character, TrieNodes> children;
	    HashSet<Integer> documentsSet;
	    TrieNodes() {
	        children = new HashMap<>();
	        documentsSet = new HashSet<>();  
	    }
	}

	public static class TrieIndexing {
	    TrieNodes root;
	    TrieIndexing() {
	        root = new TrieNodes();
	    }
	    // add document name to document Id associated with characters
	    public void insertingToTree(String wordRecent, int documentNow_Id) {
	        TrieNodes currentNode = root;
	        for (char chr : wordRecent.toCharArray()) {
	        	//add new node if doesn't exists
	            currentNode.children.putIfAbsent(chr, new TrieNodes());
	            currentNode = currentNode.children.get(chr);
	        }
	        currentNode.documentsSet.add(documentNow_Id);
	    }
	    //Return document ids associated with the last character of the word
	    public HashSet<Integer> searchingInTree(String wordRecent) {
	        TrieNodes currentNode = root;
	        for (char chr : wordRecent.toCharArray()) {
	            if (!currentNode.children.containsKey(chr)) {
	                return new HashSet<>();
	            }
	            currentNode = currentNode.children.get(chr);
	        }
	        return currentNode.documentsSet;
	    }
	}

	
	public static void main() throws IOException {
        TrieIndexing trieIndex_obj = new TrieIndexing();
        Map<Integer, String> document_names = new HashMap<>();
        String folderPath = "json_output/"; // Replace this with your folder path
        File folder = new File(folderPath);
        File[] nameOfFiles = null;
        if (folder.exists() && folder.isDirectory()) {
        	nameOfFiles = folder.listFiles();
        }    
        // fetching document names along with Id and names
        fetchDocumentNames(nameOfFiles, document_names);
        // creating the Trie index
        creatingIndexTrie(nameOfFiles, trieIndex_obj);
        File freq_op = new File("Text_Files/freq_output.txt");
        try (Scanner sc_obj = new Scanner(freq_op)) {
        	while(sc_obj.hasNextLine()) {
        		String input_user_word = sc_obj.nextLine();
				// find documents containing the words 
				findDocument_containsWord(input_user_word, trieIndex_obj, document_names);
        	}
		}
    }
    // func to fetch document names 
    private static void fetchDocumentNames(File[] allFiles, Map<Integer, String> document_names) {
        for (int iCount = 0; iCount < allFiles.length; iCount++) {
            document_names.put(iCount, allFiles[iCount].getAbsoluteFile().toString());
        }
    }
    // function to build the Trie Index for files
    private static void creatingIndexTrie(File[] allFiles, TrieIndexing trieIndex_obj) {
        int id_of_file = 0;
        for (File getCurrent_file : allFiles) {
            try (BufferedReader br_file = new BufferedReader(new FileReader(getCurrent_file.getAbsoluteFile()))) {
                String line;
                while ((line = br_file.readLine()) != null) {
                    String[] collectionOfWords = line.split("\\W+");
                   
                    for (String wordRecent : collectionOfWords) {
                        wordRecent = wordRecent.toLowerCase();
                        trieIndex_obj.insertingToTree(wordRecent, id_of_file);   
                    }
                }
            } catch (IOException e) {
                System.out.println(getCurrent_file + " File is not found.");
            }
            id_of_file++;
        }
    }
    // func to find documents containing the words 
    private static void findDocument_containsWord(String input_user_word, TrieIndexing trieIndex_obj, Map<Integer, String> document_names) {
        String[] collectionOfWords = input_user_word.split("\\W+");
        for(int countIndexer=0;countIndexer<collectionOfWords.length; countIndexer++) {
        	HashSet<Integer> result_ofSearching = trieIndex_obj.searchingInTree(collectionOfWords[countIndexer].toLowerCase());
        	if (result_ofSearching.isEmpty()) {
                //System.out.println(collectionOfWords[countIndexer] + " - not found.");
                return;
            }
            System.out.println(collectionOfWords[countIndexer] + " found in: ");
            for (int id_of_file : result_ofSearching) {
            	// displaying documentId and documment name contains words
                System.out.println("\tDocument_Id: " + id_of_file + ", Document_Name: " + document_names.get(id_of_file) );
            }    	
        }
    } 
}