import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Map.Entry;


public class HuffmanCoding {

    public static void main(String [] args) {
        String fileName[] = new String[2];
        Scanner scanner = new Scanner(System.in);
        char choice;
        System.out.println("Please choose an option");
        System.out.println("(a) Encode");
        System.out.println("(b) Decode");
        choice = scanner.nextLine().charAt(0);
        switch(choice) {
        case 'a':
            System.out.println("Please enter the name of the file you want to encode");
            fileName[0] = scanner.nextLine();
            System.out.println("Please enter the name you want to give the encoded file");
            fileName[1] = scanner.nextLine();
            encode(fileName[0], fileName[1]);
            break;
        case 'b':
            System.out.println("Please enter the file you want to decode");
            fileName[0] = scanner.nextLine();
            System.out.println("Please enter the name you want to give to the decoded file");
            fileName[1] = scanner.nextLine();
            decode(fileName[0], fileName[1]);
            break;
        default:
            System.out.println("This is an invalid option");
        }
        scanner.close();
    }


	/**
	 * Uses a recursive pre-order traversal to create the huffman codes from the huffman tree
	 * @param huffmanCodes
	 * @param node
	 * @param value
	 */
	public static void createHuffmanCodes(HashMap <Byte, HuffmanCode> huffmanCodes, Node node, int value, int length) {
	    if(node.isLeafNode() == true) {
	    	huffmanCodes.put(node.getBitConfiguration(), new HuffmanCode(value, length));
	        return;
	    }
	    createHuffmanCodes(huffmanCodes, node.getLeftChild(), (value<<1) | 0, length + 1);
	    createHuffmanCodes(huffmanCodes, node.getRightChild(), (value<<1) | 1, length + 1);
	}
	
	
	/**
	 * This will use the Huffman tree to encode the file.
	 *
	 * @param fileName The name of the file that will be encoded
	 */
	public static void encode(String inputFileName, String encodeFileName) {
		
		PriorityQueue <Node> priorityQueue;
		
		//Used for encoding the encoded file by providing the table with the word and returning its encoded counterpart
	    HashMap <Byte, HuffmanCode> huffmanCodes = new HashMap<Byte, HuffmanCode>();
		
		//Counts the occurrences of a specific configuration of bits within a byte within the input file
	    HashMap <Byte, Integer> byteConfigurationCount = new HashMap<Byte, Integer>();
	    
	    // Keeps track of the amount of bits that were written since length isn't reliable
	    int bitCount = 0;
	
	    //Open the file that will be encoded.
	    File file = new File(inputFileName);
	
	
	    //Stores the the entire coded document.
	    BitSet encoding = new BitSet();
	
	
	    //Stores the entire document that will be coded
	    byte [] document = new byte [(int) file.length()];
	
	    try {
	
	        //Read the file into the byte array
	        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
	        bufferedInputStream.read(document);
	        bufferedInputStream.close();
	
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	
	    //Counts the occurrences of the bit configurations within the file
	    for(int i = 0; i < document.length; i++) {
	        if(byteConfigurationCount.containsKey(document[i])) {
	            byteConfigurationCount.put(document[i], (byteConfigurationCount.get(document[i]) + 1) );
	        } else {
	            byteConfigurationCount.put(document[i], 1);
	        }
	    }
	
	    priorityQueue = new PriorityQueue<Node>(byteConfigurationCount.size(), new Comparator<Node>() {
	
	    	@Override
	        public int compare(Node first, Node second) {
	            if(first.getFrequency() < second.getFrequency()) {
	                return -1;
	            } else if (first.getFrequency() > second.getFrequency()) {
	                return 1;
	            } else {
	                return 0;
	            }
	        }
	    	
	    });
	
	    for(Entry<Byte, Integer> entry : byteConfigurationCount.entrySet()) {
	        priorityQueue.add(new Node(entry.getKey(), entry.getValue()));
	    }
	
	    while(priorityQueue.size() != 1) {
	        Node leftChild = priorityQueue.remove();
	        Node rightChild = priorityQueue.remove();
	        priorityQueue.add(new Node(leftChild, rightChild, leftChild.getFrequency() + rightChild.getFrequency()));
	    }
	    
	    // The root node of the huffman tree
	    Node root = priorityQueue.remove();
	    
	    createHuffmanCodes(huffmanCodes, root, 0, 0);
	
	
	   /* Store a reference to huffman codes in entryArrayList. 
	    * This is necessary because I need to be able to sort the codes which are stored in the hashmap 
	    * which stores the huffman codes. 
	    */
	    ArrayList<Entry<Byte, HuffmanCode>> entryArrayList = new ArrayList<Entry<Byte, HuffmanCode>>();
	    entryArrayList.addAll(huffmanCodes.entrySet());
	    
	    //Sort the huffman codes by string length, then within each string length group, sort them lexicographically
	    Collections.sort(entryArrayList, new Comparator<Entry<Byte, HuffmanCode>>(){
	
	    	@Override
	    	public int compare(Entry<Byte, HuffmanCode> first, Entry<Byte, HuffmanCode> second) {
	    		if(first.getValue().getLength() < second.getValue().getLength()) {
	    			return -1;
	    		} else if (first.getValue().getLength() > second.getValue().getLength()) {
	    			return 1;
	    		} else {
	    			if(first.getKey() < second.getKey()) {
	    				return -1;
	    			} else if(first.getKey() > second.getKey()) {
	    				return 1;
	    			} else {
	    				return 0;
	    			}
	    		}
	    	}
	    	
	    });
	    
	    /* 
	     * The algorithm for creating the canonical huffman code.
	     * Refer to https://en.wikipedia.org/wiki/Canonical_Huffman_code for a description of how this algorithm works.
	     * Since entryArrayList stores a reference to the huffman codes, by modifying entryArrayList, I am actually modifying
	     * the Huffman codes.
	     */
	    int code = 0;
	    for(int i = 0; i < entryArrayList.size(); i++) {
	        entryArrayList.get(i).getValue().setValue(code);
	        if(i + 1 < entryArrayList.size()) {
	        	code = (code + 1)<<(entryArrayList.get(i + 1).getValue().getLength() - entryArrayList.get(i).getValue().getLength());
	      }
	  }
	    
	    /* 
	     * This will pass over every single possible character and record their bit length in the huffman code.
	     * So if character 133 is 010, this will store 3 in position 133 of the array.
	     */
	    byte [] lengths = new byte[256];
	    for(int i = 0; i < 256; i++) {
	    	if(huffmanCodes.containsKey(Byte.valueOf((byte) i))) {
	    		lengths[i] = (byte) huffmanCodes.get(Byte.valueOf((byte) i)).getLength();
	    	} else {
	    		lengths[i] = 0;
	    	}
	    }
	    
	    //Put the huffman codes into a bitset
	    for(int i = 0; i < document.length; i++) {
	        HuffmanCode huffmanCode = huffmanCodes.get(document[i]);
	        for(int j = 0; j < huffmanCode.getLength(); j++) {
	            encoding.set(bitCount, huffmanCode.get(j) == 1);
	            bitCount++;
	        }
	    }
	    
	    /*
	     * Place a end of file set bit at the end to indicate where the file ends. 
	     * This bit will later be discarded when we are decoding.
	     */
	    encoding.set(bitCount, true);
	    
	    try {
	        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(encodeFileName));
	        bufferedOutputStream.write(lengths);
	        bufferedOutputStream.write(encoding.toByteArray());
	        bufferedOutputStream.close();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	}
	
	/**
	 * This will decode the file
	 * 
	 * @param fileName	Name of the decoded file
	 */
	public static void decode(String encodedFileName, String decodedFileName) {
	    try {
	    	File file = new File(encodedFileName);
			byte [] fileBuffer = new byte [(int) file.length()];
			BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
			bufferedInputStream.read(fileBuffer);
			bufferedInputStream.close();
			BitSet encoding = BitSet.valueOf(Arrays.copyOfRange(fileBuffer, 256, fileBuffer.length));
	        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(decodedFileName));
	        byte [] lengths = Arrays.copyOfRange(fileBuffer, 0, 256);
	        int accumulator = 0;
		    int length = 0;
	        
	        //Used for decoding the encoded file by providing the table with the encoded word and returning its decoded counterpart
	        HashMap <HuffmanCode, Byte> huffmanCodes = new HashMap<HuffmanCode, Byte>();
	        
	        HuffmanCode temp = new HuffmanCode();
	        
	        for(int i = 0; i < 256; i++) {
	        	if(lengths[i] > 0) {
	        		huffmanCodes.put(new HuffmanCode(i, lengths[i]), Byte.valueOf((byte) i));
	        	}
	        }
	        
	        
	        
	        ArrayList<Entry<HuffmanCode, Byte>> entryArrayList = new ArrayList<Entry<HuffmanCode, Byte>>();
	        
	        entryArrayList.addAll(huffmanCodes.entrySet());
	        
	        //Sort the huffman codes by string length, then within each string length group, sort them lexicographically
	        Collections.sort(entryArrayList, new Comparator<Entry<HuffmanCode, Byte >>() {
	        	
	        	@Override
	        	public int compare(Entry<HuffmanCode, Byte> first, Entry<HuffmanCode, Byte> second) {
	        		if(first.getKey().getLength() < second.getKey().getLength()) {
	        			return -1;
	        		} else if (first.getKey().getLength() > second.getKey().getLength()) {
	        			return 1;
	        		} else {
	        			if(first.getValue() < second.getValue()) {
	        				return -1;
	        			} else if(first.getValue() > second.getValue()) {
	        				return 1;
	        			} else {
	        				return 0;
	        			}
	        		}
	        	}
	        });
	        
	        huffmanCodes = new HashMap<HuffmanCode, Byte>();
	        
	       /* 
	        * The algorithm for creating the canonical huffman code
	        * Refer to https://en.wikipedia.org/wiki/Canonical_Huffman_code for a description of how this algorithm works.
	        */
	        int code = 0;
	        for(int i = 0; i < entryArrayList.size(); i++) {
	            entryArrayList.get(i).getKey().setValue(code);
	            huffmanCodes.put(entryArrayList.get(i).getKey(), entryArrayList.get(i).getValue());
	            if(i + 1 < entryArrayList.size()) {
	            	code = (code + 1)<<(entryArrayList.get(i + 1).getKey().getLength() - entryArrayList.get(i).getKey().getLength());
	            }
	        }
	       
	        
	       /*
	        * Loop through the encoded file. When you encounter a 1, bit shift right and add 1 to the accumulator and increment the length. 
	        * Otherwise just bit shift right and increment the length. 
	        * If the accumulator and length matches any of the codes in the canonical huffman codes, then output the character that matches
	        * that code. 
	        */
	       for(int i = 0; i < (encoding.length() - 1); i++) {
			   if(encoding.get(i)) {
				  accumulator = accumulator << 1;
				  accumulator++;
				  length++;
			   } else {
				   accumulator = accumulator << 1;
				   length++;
			   }
			   temp.setValue(accumulator);
			   temp.setLength(length);
			   if(huffmanCodes.containsKey(temp)) {
				   bufferedOutputStream.write(huffmanCodes.get(temp));
				   length = 0;
				   accumulator = 0;
			   }
	       }
	        bufferedOutputStream.close();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	
	}
}