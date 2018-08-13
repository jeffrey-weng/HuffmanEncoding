import java.io.*;
import java.util.*;

class HuffmanEncoder extends HuffTree implements HuffmanCoding,Comparable{
	//test program//
   
	public static void main(String[]args){
		File f=new File(new String("/Users/Jeffrey Weng/Desktop/randTest.txt")); //change text file or path as needed
		HuffmanEncoder h=new HuffmanEncoder(); //create hufftree object to call methods with
		String s=h.getFrequencies(f);
		System.out.println(s);
		HuffTree result=h.buildTree(f);
		System.out.println(h.traverseHuffmanTree(result));
		System.out.println(h.encodeFile(f, result));
		System.out.println(h.decodeFile(h.encodeFile(f, result),result));
	}
	

}

class HuffTree implements HuffmanCoding,Comparable{
	private HuffBaseNode root; //a tree as an object is defined by its root node
	private HashMap<Character,String> charToCode;
	private HashMap<String,Character> codeToChar;
	/** Constructors */

	//Use no parameter constructor when instantiating a HuffTree object in main method!
	HuffTree(){
		root=null;
		charToCode=new HashMap<Character,String>();
		codeToChar=new HashMap<String,Character>();
	}
	//these constructors are used during the Huffman tree construction process
	HuffTree(char el, int wt)
	{ root = new HuffLeafNode(el, wt);}
	HuffTree(HuffBaseNode l, HuffBaseNode r, int wt)
	{ root = new HuffInternalNode(l, r, wt); }

	HuffBaseNode root() { return root; }

	int weight() // Weight of tree is weight of root
	{ return root.weight(); }

	public int compareTo(Object t) {
		HuffTree that = (HuffTree)t;
		if (root.weight() < that.weight()) return -1;
		else if (root.weight() == that.weight()) return 0;
		else return 1;
	}


	class MinHeap {
		private HuffTree[] Heap; // Pointer to the heap array
		private int size;          // Maximum size of the heap
		private int n;             // Number of things now in heap

		// Constructor supporting preloading of heap contents
		MinHeap(HuffTree[] h, int num, int max)
		{ Heap = h;  n = num;  size = max;  buildheap(); }

		// Return current size of the heap
		int heapsize() { return n; }

		// Return true if pos a leaf position, false otherwise
		boolean isLeaf(int pos)
		{ return (pos >= n/2) && (pos < n); }

		// Return position for left child of pos
		int leftchild(int pos) {
			if (pos >= n/2) return -1;
			return 2*pos + 1;
		}

		// Return position for right child of pos
		int rightchild(int pos) {
			if (pos >= (n-1)/2) return -1;
			return 2*pos + 2;
		}

		// Return position for parent
		int parent(int pos) {
			if (pos <= 0) return -1;
			return (pos-1)/2;
		}
		void swap(HuffTree[]heap,int currIndex,int parentIndex){
			HuffTree temp=heap[parentIndex];
			heap[parentIndex]=heap[currIndex];
			heap[currIndex]=temp;

		}

		// Insert val into heap
		void insert(HuffTree h) {
			if (n >= size) {
				System.out.println("Heap is full");
				return;
			}
			int curr = n++;
			Heap[curr] = h;  // Start at end of heap
			// Now sift up until curr's parent's key > curr's key
			while ((curr != 0) && (Heap[curr].compareTo(Heap[parent(curr)]) < 0)) {
				swap(Heap, curr, parent(curr));
				curr = parent(curr);
			}
		}

		// Heapify contents of Heap
		void buildheap()
		{ for (int i=n/2-1; i>=0; i--) siftdown(i); }

		// Put element in its correct place
		void siftdown(int pos) {
			if ((pos < 0) || (pos >= n)) return; // Illegal position
			while (!isLeaf(pos)) {
				int j = leftchild(pos);
				if ((j<(n-1)) && (Heap[j].compareTo(Heap[j+1]) > 0))
					j++; // j is now index of child with lesser value
				if (Heap[pos].compareTo(Heap[j]) <= 0) return;
				swap(Heap, pos, j);
				pos = j;  // Move down
			}
		}

		// Remove and return maximum value
		HuffTree removemin() {
			if (n == 0) return null;  // Removing from empty heap
			swap(Heap, 0, --n); // Swap maximum with last value
			if (n != 0)      // Not on last element
				siftdown(0);   // Put new heap root val in correct place
			return Heap[n];
		}

		// Remove and return element at specified position
		HuffTree remove(int pos) {
			if ((pos < 0) || (pos >= n)) return null; // Illegal heap position
			if (pos == (n-1)) n--; // Last element, no work to be done
			else {
				swap(Heap, pos, --n); // Swap with last value
				// If we just swapped in a big value, push it up
				while ((pos > 0) && (Heap[pos].compareTo(Heap[parent(pos)]) < 0)) {
					swap(Heap, pos, parent(pos));
					pos = parent(pos);
				}
				if (n != 0) siftdown(pos); // If it is little, push down
			}
			return Heap[n];
		}
	}





	/** Huffman tree node implementation: Base class */
	interface HuffBaseNode {
		boolean isLeaf(); 
		int weight();
		HuffBaseNode left();
		HuffBaseNode right();
	}


	/** Huffman tree node: Leaf class */
	class HuffLeafNode implements HuffBaseNode {
		private char element;      // Element for this node
		private int weight;        // Weight for this node

		/** Constructor */
		HuffLeafNode(char el, int wt)
		{ element = el; weight = wt; }

		/** @return The left child */
		public HuffBaseNode left() { return null; }

		/** @return The right child */
		public HuffBaseNode right() { return null; }

		/** @return The element value */
		char value() { return element; }

		/** @return The weight */
		public int weight() { return weight; }

		/** Return true */
		public boolean isLeaf() { return true; }
	}


	/** Huffman tree node: Internal class */
	class HuffInternalNode implements HuffBaseNode {
		private int weight;            
		private HuffBaseNode left;  
		private HuffBaseNode right; 

		/** Constructor */
		HuffInternalNode(HuffBaseNode l,
				HuffBaseNode r, int wt)
		{ left = l; right = r; weight = wt; }

		/** @return The left child */
		public HuffBaseNode left() { return left; }

		/** @return The right child */
		public HuffBaseNode right() { return right; }

		/** @return The weight */
		public int weight() { return weight; }

		/** Return false */
		public boolean isLeaf() { return false; }
	}

	//TO SORT CHARACTERS IN ASCII ORDER FOR FREQUENCIES AND CODES
	public static void insertionSort(Character chars [], int charsSize) {
		int i = 0;
		int j = 0;
		Character temp = null;  // Temporary variable for swap

		for (i = 1; i < charsSize; ++i) {
			j = i;
			// Insert chars[i] into sorted part 
			// stopping once chars[i] in correct position
			while (j > 0 && (int)chars[j] < (int)chars[j - 1]) {

				// Swap numbers[j] and numbers[j - 1]
				temp = chars[j];
				chars[j] = chars[j - 1];
				chars[j - 1] = temp;
				--j;
			}
		}
	}



	public String getFrequencies(File inputFile){
		String s="";
		String freq="";
		Character[]chars=new Character[95];
		int x=0;
		try (InputStream in = new FileInputStream(inputFile);
				Reader reader = new InputStreamReader(in)) {

			int c;
			while ((c = reader.read()) != -1) {
				s+=(char)c; 
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HashMap<Character,Integer>map=new HashMap<Character,Integer>();

		for(int i = 0; i < s.length(); i++){
			char c = s.charAt(i);
			Integer val = map.get(c);
			if(val != null){
				map.put(c, new Integer(val + 1));
			}else{
				map.put(c,1);
			}
		}
		for (Map.Entry<Character, Integer> entry : map.entrySet()) {
			chars[x] = entry.getKey();
			x++;

		}
		insertionSort(chars,x); //to sort in ascii order

		for(int i=0;i<x;i++){
			Character key = chars[i];

			if(key==' ')freq+="[space]" +" " + map.get(key)+"\n";
			else freq+=key + " " +map.get(key)+"\n";
		}
		return freq;


	}
	public HuffTree buildTree(File inputFile){
		String s="";
		HuffTree[] nodes=new HuffTree[95]; //there are 95 printable ascii characters
		MinHeap heap;
		try (InputStream in = new FileInputStream(inputFile);
				Reader reader = new InputStreamReader(in)) {

			int c;
			while ((c = reader.read()) != -1) {
				s+=(char)c; //reading file contents as a string
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HashMap<Character,Integer>map=new HashMap<Character,Integer>();

		//inserting characters and their frequencies into hash table
		for(int i = 0; i < s.length(); i++){
			char c = s.charAt(i);
			Integer val = map.get(c);
			if(val != null){
				map.put(c, new Integer(val + 1));
			}else{
				map.put(c,1);
			}
		}
		int i=0;
		for (Map.Entry<Character, Integer> entry : map.entrySet()) {
			Character key = entry.getKey();
			Integer value = entry.getValue();

			nodes[i]=new HuffTree(key,value);
			i++;
		}
		heap=new MinHeap(nodes,i,95);

		HuffTree tmp1, tmp2, tmp3 = null;

		while (heap.heapsize() > 1) { // While two items left
			tmp1 = heap.removemin();
			tmp2 = heap.removemin();
			tmp3 = new HuffTree(tmp1.root(), tmp2.root(),
					tmp1.weight() + tmp2.weight());
			heap.insert(tmp3);   // Return new tree to heap
		}
		return tmp3;            // Return the tree
	}	
	public String encodeFile(File inputFile, HuffTree huffTree){
		String s=""; //contains text file contents
		String encoding=""; //string with 0s and 1s

		traverseHuffmanTree(huffTree);

		try (InputStream in = new FileInputStream(inputFile);
				Reader reader = new InputStreamReader(in)) {

			int c;
			while ((c = reader.read()) != -1) {
				s+=(char)c; 
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0; i < s.length(); i++){
			char c = s.charAt(i);
			encoding+=charToCode.get(c);

		}
		return encoding;
	}
	public String traverseHuffmanTree(HuffTree huffTree){
		String freq="";
		Character chars[]=new Character[95];
		int x=0;

		printPaths(huffTree.root());

		for (Map.Entry<Character, String> entry : charToCode.entrySet()) {
			chars[x] = entry.getKey();
			x++;

		}
		insertionSort(chars,x); //to sort in ascii order

		for(int i=0;i<x;i++){
			Character key = chars[i];

			if(key==' ')freq+="[space]" +" " + charToCode.get(key)+"\n";
			else freq+=key + " " +charToCode.get(key)+"\n";
		}

		return freq;
	}
	public String decodeFile(String code, HuffTree huffTree){
		String s="";
		String decipher="";

		//create look up tables given huffTree
		traverseHuffmanTree(huffTree);

		for(int i = 0; i < code.length(); i++){
			s+= code.charAt(i);
			if(codeToChar.containsKey(s)){
				decipher+=codeToChar.get(s);
				s=""; //reset string for next encoded character
			}
		}
		return decipher;
	}

	String printPaths(HuffBaseNode node) 
	{
		int path[] = new int[1000];

		//convert system.out.print output of void method into a string for traverseHuffmanTree method
		// Create a stream to hold the output of void method
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(stream);

		PrintStream old = System.out;

		System.setOut(ps);
		// Print some output: goes to stream
		printPathsRecur(node, path,-1, 0);
		// Put things back
		System.out.flush();
		System.setOut(old);

		return stream.toString();
	}


	void printPathsRecur(HuffBaseNode node, int path[], int value, int pathLen) 
	{
		if (node == null)
			return;

		//to keep track of left or right branch taken
		if(value==-1);
		else{
			path[pathLen] = value;
			pathLen++;
		}
		// it's a leaf, so print the path that led to here  
		if (node.left() == null && node.right() == null){

			//put character and corresponding code into hash table for use in encoding method
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(stream);

			PrintStream old = System.out;

			System.setOut(ps);
			// Print output: goes to stream
			printArray(path, pathLen); //the code to print
			// Put things back
			System.out.flush();
			System.setOut(old);


			charToCode.put(((HuffLeafNode) node).value(),stream.toString());
			codeToChar.put(stream.toString(),((HuffLeafNode) node).value());
		}
		else
		{
			// otherwise try both subtrees
			printPathsRecur(node.left(), path,0, pathLen);
			printPathsRecur(node.right(), path,1, pathLen);
		}
	}

	/* Utility function that prints out codes on a line. */
	void printArray(int ints[], int len) 
	{
		int i;
		for (i = 0; i < len; i++) 
		{
			System.out.print(ints[i]);
		}
	}
}





