import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.File;
import java.io.PrintStream;
import java.io.FileReader;

public class PA2{

	//PA #2 TODO: finds the smallest tree in a given forest, allowing for a single skip
	//Finds the smallest tree (by weight) in the supplied forest.  
	//Note that this function accepts a second optional parameter of an index to skip.  
	//Use this index to allow your function to also find the 2nd smallest tree in the 
	//forest.
	//DO NOT change the first findSmallestTree function. Only work in the second one!
	public static int findSmallestTree(List<HuffmanTree<Character>> forest)
	{
		return findSmallestTree(forest, -1); //find the real smallest 
	}
	public static int findSmallestTree(List<HuffmanTree<Character>> forest, int index_to_ignore) 
	{
		int min = Integer.MAX_VALUE;
		int minIndex = 0;
		for(int i = 0; i < forest.size(); i++){
			if(forest.get(i).getWeight() < min && i != index_to_ignore){
				min = forest.get(i).getWeight();
				minIndex = i;
			}
		}
		return minIndex; //find the smallest except the index to ignore.
	}

	//PA #2 TODO: Generates a Huffman character tree from the supplied text
	//Builds a Huffman Tree from the supplied list of strings.
	//This function implement's Huffman's Algorithm as specified in page 
	//435 of the book.	
	public static HuffmanTree<Character> huffmanTreeFromText(List<String> data) {
		//In order for your tree to be the same as mine, you must take care 
		//to do the following:
		//1.	When merging the two smallest subtrees, make sure to place the 
		//      smallest tree on the left side!
		//2.	Have the newly created tree take the spot of the smallest 
		//		tree in the forest(e.g. list.set(smallest_index, merged_tree) ).
		//3.	Use list.remove(second_smallest_index) to remove 
		//      the other tree from the forest.	

		//Make a hashmap of all the characters and their frequencies/weight
		Map<Character, Integer> freq = new HashMap<Character, Integer>();
		for(int i = 0; i < data.size(); i++){
			Scanner scan = new Scanner(data.get(i));
			if(scan.hasNextLine()){
				String line = scan.nextLine();
				for(int j = 0; j < line.length(); j++){
					char currentChar = line.charAt(j);
					if(freq.containsKey(currentChar)){
						freq.replace(currentChar, freq.get(currentChar) + 1);
					} else {
						freq.put(currentChar, 1);
					}
				}
			}

		}

		//Create an array and fill it with HuffmanLeafNodes
		ArrayList<HuffmanTree<Character>> pot = new ArrayList();
		for (Map.Entry<Character, Integer> entry : freq.entrySet()){
			HuffmanTree<Character> newHufNode = new HuffmanTree<Character>(entry.getKey(), entry.getValue());
		    pot.add(newHufNode);
		}

		//While loop that goes through the arraylist, finds the two lowest weight nodes and combines them
		//until there is only one node left
		while(pot.size() != 1){
			//Figure out which node has the smallest value
			int minOne = findSmallestTree(pot);
			int minTwo = findSmallestTree(pot, minOne);
			//Make a new internal node and combine both smallest nodes with the smallest on the left
			HuffmanTree<Character> T = new HuffmanTree<Character>(pot.get(minOne), pot.get(minTwo));
			pot.set(minOne, T); //Sets the merged node into smallest index
			pot.remove(minTwo); //Removes second smallest node
		}
		
		//note that root is a HuffmanNode instance. This type cast would only work 
		//if you are sure that root is not a leaf node.
		//Vice versa, for this assignment, you might need to force type cast a HuffmanNode
		//to a HuffmanLeafNode when you are sure that what you are getting is a HuffmanLeafNode.
		//The line below is just an example on how to do forced casting. It is NOT part of the code.
		//HuffmanInternalNode<Character> i_root = (HuffmanInternalNode<Character>)root;  
		return pot.get(0);
	}

	//PA #2 TODO: Generates a Huffman character tree from the supplied encoding map
	//NOTE: I used a recursive helper function to solve this!
	public static HuffmanTree<Character> huffmanTreeFromMap(Map<Character, String> huffmanMap) {
		//Generates a Huffman Tree based on the supplied Huffman Map.Recall that a 
		//Huffman Map contains a series of codes(e.g. 'a' = > 001).Each digit(0, 1) 
		//in a given code corresponds to a left branch for 0 and right branch for 1.
		
		HuffmanNode sus = new HuffmanInternalNode<Character>(null, null);

		for(Map.Entry<Character, String> entry : huffmanMap.entrySet()){
			mapToTreeHelper(sus, entry.getKey(), entry.getValue(), 0);
		}
		
		HuffmanInternalNode<Character> f1 = (HuffmanInternalNode<Character>)sus;
		HuffmanTree<Character> f2 = new HuffmanTree<Character>(f1);
		return f2;
	}

	public static void mapToTreeHelper (HuffmanNode treee, Character c, String binary, int level){
		
		//if not final spot, cast node into internalnode
		HuffmanInternalNode<Character> tree = (HuffmanInternalNode<Character>)treee;

		//if we are at last level, create a leaf node
		if(level + 1== binary.length()){
			if(binary.charAt(level) == '0'){
				HuffmanLeafNode leaf = new HuffmanLeafNode<Character> (c, 1);
				tree.setLeftChild(leaf);
			} else {
				HuffmanLeafNode leaf = new HuffmanLeafNode<Character> (c, 1);
				tree.setRightChild(leaf);
			}
		
		} else {

				
			//No node exists on the left side, create new node and traverse down it
			if(binary.charAt(level) == '0' && tree.getLeftChild() == null){
				level++;
				HuffmanInternalNode connector = new HuffmanInternalNode<Character>(null, null);
				tree.setLeftChild(connector);
				mapToTreeHelper(connector, c, binary, level);

			//No node exists on the right side, create new node and traverse down it
			} else if(binary.charAt(level) == '1' && tree.getRightChild() == null){
				level++;
				HuffmanInternalNode connector = new HuffmanInternalNode<Character>(null, null);
				tree.setRightChild(connector);
				mapToTreeHelper(connector, c, binary, level);

			//Node on the left exists and needs to be traversed
			} else if(binary.charAt(level) == '0'){
				level++;
				mapToTreeHelper(tree.getLeftChild(), c, binary, level);

			//Node on the right exists and needs to be traversed
			} else {
				level++;
				mapToTreeHelper(tree.getRightChild(), c, binary, level);
			}
		}
		
	}

	//PA #2 TODO: Generates a Huffman encoding map from the supplied Huffman tree
	//NOTE: I used a recursive helper function to solve this!
	public static Map<Character, String> huffmanEncodingMapFromTree(HuffmanTree<Character> tree) {
		//Generates a Huffman Map based on the supplied Huffman Tree.  Again, recall 
		//that a Huffman Map contains a series of codes(e.g. 'a' = > 001).Each digit(0, 1) 
		//in a given code corresponds to a left branch for 0 and right branch for 1.  
		//As such, a given code represents a pre-order traversal of that bit of the 
		//tree.  I used recursion to solve this problem.
		
		Map<Character, String> result = new HashMap<>();
		String start = "";
		encodingMapFromTreeHelper(tree.getRoot(), result, start);

		return result;
	}

	//Recursive Helper
	public static void encodingMapFromTreeHelper(HuffmanNode<Character> tree, Map<Character, String> result, String binary){

		//Base case
		//Hit a leaf node and add it to the map by reading the key and looking at "binary" from call
		if(tree.isLeaf() == true){
			//Turn node into leaf node
			HuffmanLeafNode<Character> leaf = (HuffmanLeafNode<Character>)tree;
			result.put(leaf.getValue(), binary);
		} else { //We have internal node, aka recursive case
			HuffmanInternalNode<Character> subTree = (HuffmanInternalNode<Character>)tree;
			String left = binary + "0";
			String right = binary + "1";
			encodingMapFromTreeHelper(subTree.getLeftChild(), result, left);
			encodingMapFromTreeHelper(subTree.getRightChild(), result, right);
		}
	}

	//Decoder helper
	//PA #2 TODO: Writes an encoding map to file.  Needed for decompression.
	public static void writeEncodingMapToFile(Map<Character, String> huffmanMap, String file_name) {
		//Writes the supplied encoding map to a file.  My map file has one 
		//association per line (e.g. 'a' and 001).  Each association is separated by 
		//a sentinel value.  In my case, I went with a double pipe (||).

		try {
			PrintStream o = new PrintStream(new File(file_name));
            System.setOut(o);
			
			for (Map.Entry<Character, String> entry : huffmanMap.entrySet()){
				System.out.println(entry.getKey() + "||" + entry.getValue());
			}
			o.close();
		} catch (Exception e) {
			// TODO: handle exception
		}

		
	}

	//Decoder helper
	//PA #2 TODO: Reads an encoding map from a file.  Needed for decompression.
	public static Map<Character, String> readEncodingMapFromFile(String file_name) {
		//Creates a Huffman Map from the supplied file.Essentially, this is the 
		//inverse of writeEncodingMapToFile. Use String.split() function - note that
		//the split() function takes a Regular Expression as an input, not a "string" itself. 
		//To separate based on "||", the argument for the function should be: split("\\|\\|")
		
		Map<Character, String> result = new HashMap<>();

		File file = new File(file_name);
		try {
			Scanner scan = new Scanner(new FileReader(file));
			while(scan.hasNextLine() == true){
				String line = scan.nextLine();
				String [] bleh = (line.split("\\|\\|", 2));
				ArrayList<String> holder = new ArrayList(Arrays.asList(bleh));

				char c = holder.get(0).charAt(0);
				String binary = holder.get(1);
				result.put(c, binary);
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}

	//Decompressor
	//PA #2 TODO: Converts a list of bits (bool) back into readable text using the supplied Huffman map
	public static String decodeBits(List<Boolean> bits, Map<Character, String> huffmanMap) {
		//Uses the supplied Huffman Map to convert the list of bools (bits) back into text.
		//To solve this problem, I converted the Huffman Map into a Huffman Tree and used 
		//tree traversals to convert the bits back into text.
		
		//Use a StringBuilder to append results. 
		StringBuilder result = new StringBuilder();
		HuffmanTree<Character> og = huffmanTreeFromMap(huffmanMap);
		HuffmanNode<Character> tree = og.getRoot();

		for(int i = 0; i < bits.size(); i++){
			HuffmanInternalNode<Character> merged = (HuffmanInternalNode<Character>)tree; //cast and traverse
			if(bits.get(i) == false){ //Traverse left and check
				if(merged.getLeftChild().isLeaf() == true){ //If child is a leaf, cast it and get character
					HuffmanLeafNode<Character> leaf = (HuffmanLeafNode<Character>)merged.getLeftChild();
					result.append(leaf.getValue()); //append character
					tree = og.getRoot(); //reset tree
				} else { //Not leaf so remember spot
					tree = merged.getLeftChild();
				}
			} else { //Traverse right and check
				if(merged.getRightChild().isLeaf() == true){
					HuffmanLeafNode<Character> leaf = (HuffmanLeafNode<Character>)merged.getRightChild();
					result.append(leaf.getValue());
					tree = og.getRoot();
				} else {
					tree = merged.getRightChild();
				}
			}

		}

		return result.toString();
	}
	//Compressor
	//PA #2 TODO: Using the supplied Huffman map compression, converts the supplied text into a series of bits (boolean values)
	public static List<Boolean> toBinary(List<String> text, Map<Character, String> huffmanMap) {
		List<Boolean> result = new ArrayList<>();

		//For every String in the list, go through each strings character and add the correct boolean
		//sequence for each character
		for(int i = 0; i < text.size(); i++){
			Scanner scan = new Scanner(text.get(i));
			if(scan.hasNextLine()){
				String currentWord = scan.nextLine();
				for(int j = 0; j < currentWord.length(); j++){
					char currentChar = currentWord.charAt(j);
					String binary = huffmanMap.get(currentChar);
					for(int x = 0; x < binary.length(); x++){
						if(binary.charAt(x) == '0'){
							result.add(false);
						} else {
							result.add(true);
						}
					}
				}
			}
		}
		return result;
	}

}
