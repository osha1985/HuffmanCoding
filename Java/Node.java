public class Node {
	//Indicates which configuration of bits does this node represent
			private byte bitConfiguration = 0;
			
			//A reference to the left child of the current node
			private Node leftChild = null;
			
			//A reference to the right child of the current node 
			private Node rightChild = null;
			
			// Frequency with which the bit configuration that the node represents appears within the input file
			private int frequency = 0;
			
			private boolean isLeafNode;
			
			// Constructor used for creating leaf nodes
			public Node(byte bitConfiguration, int frequency) {
				this.setBitConfiguration(bitConfiguration);
				this.frequency = frequency;
				this.setLeafNode(true);
			}
			
			//Constructor used for creating parent nodes
			public Node(Node leftChild, Node rightChild, int frequency) {
				this.setLeftChild(leftChild);
				this.setRightChild(rightChild);
				this.frequency = frequency;
				this.setLeafNode(false);
			}
			public int getFrequency() {
				return frequency;
			}

			public byte getBitConfiguration() {
				return bitConfiguration;
			}

			public void setBitConfiguration(byte bitConfiguration) {
				this.bitConfiguration = bitConfiguration;
			}

			public Node getLeftChild() {
				return leftChild;
			}

			public void setLeftChild(Node leftChild) {
				this.leftChild = leftChild;
			}

			public Node getRightChild() {
				return rightChild;
			}

			public void setRightChild(Node rightChild) {
				this.rightChild = rightChild;
			}

			public boolean isLeafNode() {
				return isLeafNode;
			}

			public void setLeafNode(boolean isLeafNode) {
				this.isLeafNode = isLeafNode;
			}
}
