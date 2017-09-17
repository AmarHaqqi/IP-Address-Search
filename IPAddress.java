package data;

public class IPAddress implements Comparable<IPAddress> {

	public String ip_address;
	public String country;
	public float latitude;
	public float longitude;

	public String toString() {
		return ip_address + " " + country + " " + latitude + " " + longitude;
	}

	public IPAddress() {
	}

	public IPAddress(String ip_address, String cntry, float latitude, float longitude) {
		this.ip_address = ip_address;
		this.country = cntry;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public int compareTo(IPAddress target) {
		int[] ip = new int[4];
		String[] parts = this.ip_address.split("\\.");
		for (int i = 0; i < 4; i++) {
			ip[i] = Integer.parseInt(parts[i]);
		}

		int[] ip2 = new int[4];
		String[] parts2 = target.ip_address.split("\\.");
		for (int i = 0; i < 4; i++) {
			ip2[i] = Integer.parseInt(parts2[i]);
		}
		if (ip[0] > ip2[0]) {
			return 1;
		} else if (ip[0] < ip2[0]) {
			return -1;
		} else {
			if (ip[1] > ip2[1]) {
				return 1;
			} else if (ip[1] < ip2[1]) {
				return -1;
			} else {
				if (ip[2] > ip2[2]) {
					return 1;
				} else if (ip[2] < ip2[2]) {
					return -1;
				} else {
					if (ip[3] > ip2[3]) {
						return 1;
					} else if (ip[3] < ip2[3]) {
						return -1;
					} else {
						return 0;
					}
				}
			}
		}
	}

}
package data_structures;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

public class BalancedSearchTree<E> implements Iterable<E> {
	public BalancedSearchTree() {
		root = null;
		currentSize = 0;
	}

	public enum BalanceState {
		Balanced, LeftUnbalanced, RightUnbalanced,
	}

	public enum Heaviness {
		Even, LeftHeavy, RightHeavy;
	}

	public class Node<E> {
		E data;
		Node<E> leftChild;
		Node<E> rightChild;
		Node<E> parent;

		// public Node(E obj) {
		// data = obj;
		// parent = leftChild = rightChild = null;
		// }

		public Node(E obj, Node<E> up, Node<E> left, Node<E> right) {
			data = obj;
			parent = up;
			leftChild = left;
			rightChild = right;
		}

		private int maxHeightOf(Node<E> node) {
			if (node == null)
				return 0;
			int leftHeight = maxHeightOf(node.leftChild) + 1;
			int rightHeight = maxHeightOf(node.rightChild) + 1;
			if (leftHeight > rightHeight)
				return leftHeight;
			else
				return rightHeight;
		}

		private int maxHeight() {
			int leftHeight = maxHeightOf(this.leftChild) + 1;
			int rightHeight = maxHeightOf(this.rightChild) + 1;
			if (leftHeight > rightHeight)
				return leftHeight;
			else
				return rightHeight;
		}

		private int leftMaxHeight() {
			return maxHeightOf(leftChild);
		}

		private int rightMaxHeight() {
			return maxHeightOf(rightChild);
		}

		private BalanceState balanceState() {
			if (leftMaxHeight() - rightMaxHeight() > 1) {
				return BalanceState.LeftUnbalanced;
			} else if (rightMaxHeight() - leftMaxHeight() > 1) {
				return BalanceState.RightUnbalanced;
			} else
				return BalanceState.Balanced;
		}

		private Heaviness heavinessBF() {
			if (leftMaxHeight() > rightMaxHeight()) {
				return Heaviness.LeftHeavy;
			} else if (rightMaxHeight() < leftMaxHeight()) {
				return Heaviness.RightHeavy;
			} else
				return Heaviness.Even;
		}

	}

	public void checkBalanceBottomUp(Node<E> node) {
		if (node == null)
			return;
		if (node.balanceState() != BalanceState.Balanced) {
			rebalance(node);
		}
		checkBalanceBottomUp(node.parent);
	}

	private void rebalance(Node<E> uNode) {
		if (uNode.balanceState() == BalanceState.RightUnbalanced) {
			if (uNode.rightChild.heavinessBF() == Heaviness.LeftHeavy) {
				rightLeftRotate(uNode);
			} else {
				leftRotate(uNode);
			}
		} else if (uNode.balanceState() == BalanceState.LeftUnbalanced) {
			if (uNode.leftChild.heavinessBF() == Heaviness.RightHeavy) {
				leftRightRotate(uNode);
			} else {
				rightRotate(uNode);
			}
		}
		// if (uNode.leftMaxHeight() != uNode.rightMaxHeight())
	//	System.out.println("After balancing, the heights are: " + uNode.leftMaxHeight() + uNode.rightMaxHeight());
	}

	private Node<E> root;
	private int currentSize;

	/**
	 * adds a node to the tree to the root if applicable, or calls addAfter to
	 * choose where to add.
	 * 
	 * @param obj
	 *            - the data that we want to add to the tree
	 * @return true if node gets added successfully.
	 */

	public boolean add(E obj) {
		Node<E> node = new Node<E>(obj, null, null, null);
		if (root == null) {
			root = node;
			currentSize++;
			return true;
		} else {// (root != null)
			Node<E> tmp = addAfter(root, obj);
			currentSize++;
			checkBalanceBottomUp(tmp);
			return true;
		}
	}

	/**
	 * adds a node to the tree in the correct location.
	 * 
	 * @param current
	 *            - node we're currently in to traverse the tree.
	 * @param toAdd
	 *            - the data we want to add to the tree.
	 */

	private Node<E> addAfter(Node current, E toAdd) {
		if ((((Comparable<E>) toAdd).compareTo((E) current.data)) >= 0) {
			if (current.rightChild == null) {
				current.rightChild = new Node(toAdd, current, null, null);
				return current.rightChild;
			} else {
				return addAfter(current.rightChild, toAdd);
			}
		} else {
			if (current.leftChild == null) {
				current.leftChild = new Node(toAdd, current, null, null);
				return current.leftChild;
			} else {
				return addAfter(current.leftChild, toAdd);
			}
		}
	}

	/**
	 * obtains the node we want to delete
	 * 
	 * @param current
	 *            - the node we're currently in to traverse
	 * @param toDelete
	 *            - the data we're looking for to find a match
	 * @return the node we want, or null if it doesn't exist.
	 */

	private Node<E> nodeDelete(Node<E> current, E toDelete) {
		if (current == null)
			return null;
		if (toDelete == current.data)
			return current;
		if (((Comparable<E>) toDelete).compareTo((E) current.data) >= 0)
			return nodeDelete(current.rightChild, toDelete);
		else
			return nodeDelete(current.leftChild, toDelete);
	}

	/**
	 * 
	 * @param arg1
	 *            - the data that we want to add to the tree
	 * @return true if node gets added successfully.
	 */

	private void linkToParent(Node<E> n, Node<E> replacer) {
		if (n.parent == null) {
			root = replacer;
		}
		if (replacer != null)
			replacer.parent = n.parent;
		if (n != null && n.parent != null) {
			if (((Comparable<E>) n.parent.data).compareTo((E) n.data) >= 0) {
				n.parent.leftChild = replacer;
			} else {
				n.parent.rightChild = replacer;
			}
		}
	}

	public boolean delete(E obj) {
		Node<E> n = nodeDelete(root, obj);// Find rNode
		if (n == null)
			return false;
		currentSize--;
		// check children of rNode
		// if rNode has no children
		// then remove rNode
		// if rNode has no children then make rNode null
		if (n.leftChild == null && n.rightChild == null) {
			linkToParent(n, null);
			checkBalanceBottomUp(n.parent);
		}

		// if rNode only has a right child
		// find out if rNode is a right or left child
		// make rNode.parent.(left or right child) = right child of rNode
		if (n.leftChild == null && n.rightChild != null) {
			linkToParent(n, n.rightChild);
			checkBalanceBottomUp(n.rightChild);
		}

		// if rNode only has a left child
		// find out if rNode is a right or left child
		// make rNode.parent.(left or right child) = left child of rNode
		if (n.leftChild != null && n.rightChild == null) {
			linkToParent(n, n.leftChild);
			checkBalanceBottomUp(n.leftChild);
		}
		// if rNode has 2 children
		// find in-order successor
		// while successor's left child is not null
		// iterate to the left until you reach null
		// if successor has a right child make it the successor's parent's left
		// child
		// determine whether rNode is left child or right child
		// replace deleted node (n) with successor (success) by setting
		// n.parent.child to success
		// n.parent.(left or right)Child = success
		// set success.parent to n.parent
		// set success.children to n.children
		if (n.leftChild != null && n.rightChild != null) {
			Node<E> success = n.rightChild.leftChild;
			if (n.rightChild.leftChild != null) {

				while (success.leftChild != null) {
					success = success.leftChild;
				}

				success.parent.leftChild = success.rightChild;
				if (success.rightChild != null)
					success.rightChild.parent = success.parent.leftChild;

				linkToParent(n, success);

				success.leftChild = n.leftChild;
				success.rightChild = n.rightChild;
				n.leftChild.parent = success;
				n.rightChild.parent = success;

			} else {
				success = n.rightChild;
				linkToParent(n, success);

				success.leftChild = n.leftChild;
				n.leftChild.parent = success;
			}
			checkBalanceBottomUp(success);
		}
		return true;
	}

	public boolean contains(E value) {
		return get(value) != null;
	}
	
	private E find(Node<E> current, E toFind) {
		if (current == null)
			return null;
		if (((Comparable<E>) toFind).compareTo((E) current.data) == 0)
			return toFind;
		if (((Comparable<E>) toFind).compareTo((E) current.data) >= 0)
			return find(current.rightChild, toFind);
		else
			return find(current.leftChild, toFind);
	}

	public E get(E obj) {
		if (root == null)
			return null;
		return find(root, obj);
		// find and retrieve an object in the tree
	}

	public int size() {
		return currentSize;// return the current size of the tree
	}

	public int height() {
		return root.maxHeight();// return the current height of the tree
	}

	public int heightBelow(Node<E> node) {
		return node.maxHeight();// return the height of the tree below the node
								// passed in. The
		// height is the longest path from this node to a leaf
	}

	public boolean isEmpty() {
		if (root == null && currentSize == 0)
			return true;
		return false;// is the tree empty?
	}

	public boolean isFull() {
		return false;// is the tree full?
	}

	/*
	 * Node<E> current = new Node<E> if(root == null) System.out.println(
	 * "The tree is empty");; if (root != null) { go left print go right }
	 */
	private class IteratorHelper implements Iterator<E> {
		protected Stack<BalancedSearchTree<E>.Node<E>> visitedNodes = new Stack<Node<E>>();

		public IteratorHelper(BalancedSearchTree<E>.Node<E> treeroot) {
			WalkLeftFrom(treeroot);
		}

		public void WalkLeftFrom(Node<E> node) {
			if (node != null) {
				visitedNodes.push(node);
				WalkLeftFrom(node.leftChild);
			}
		}

		public E next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			Node<E> node = visitedNodes.pop();
			E result = node.data;

			if (node.rightChild != null) {
				WalkLeftFrom(node.rightChild);
			}
			return result;
		}

		public boolean hasNext() {
			return !visitedNodes.empty();
		}
	}

	public Iterator<E> allElements() {
		Iterator<E> bstiterator = new IteratorHelper(root);
		return bstiterator;
	}

	public Iterator<E> iterator() {
		return allElements();
	}

	public E findNext(E obj) {
		Node<E> node = nodeDelete(root, obj);
		Node<E> success = null;
		
		if (node.rightChild == null)
			return null;
		if (node.rightChild != null) {
			if (node.rightChild.leftChild != null) {
				success = node.rightChild.leftChild;
				while (success.leftChild != null) {
					success = success.leftChild;
				}
			} else {
				success = node.rightChild;
			}
		}
		return success.data;
	}

	public E findPrevious(E obj) {
		Node<E> node = nodeDelete(root, obj);
		Node<E> success = null;
		
		if (node.leftChild == null)
			return null;
		if (node.leftChild.rightChild != null) {
			success = node.leftChild.rightChild;
			while (success.rightChild != null) {
				success = success.rightChild;
			}
		} else {
			success = node.leftChild;
		}
		return success.data;
	}

	public Node<E> rootNode() {
		return root;
	}

	private void linkWithParent(Node<E> pivot, Node<E> gp) {
		if (gp.parent != null) {
			if (gp.parent.leftChild == gp) {
				gp.parent.leftChild = pivot;
			} else if (gp.parent.rightChild == gp) {
				gp.parent.rightChild = pivot;
			}
		} else {
			root = pivot;
		}
		pivot.parent = gp.parent;
		gp.parent = pivot;
	}

	private Node<E> leftRotate(Node<E> grandparent) {
		Node<E> tmp = grandparent.rightChild;
		grandparent.rightChild = tmp.leftChild;
		if (tmp.leftChild != null)
			tmp.leftChild.parent = grandparent;
		tmp.leftChild = grandparent;
		linkWithParent(tmp, grandparent);
		return tmp;
	}

	private Node<E> rightRotate(Node<E> grandparent) {
		Node<E> tmp = grandparent.leftChild;
		grandparent.leftChild = tmp.rightChild;
		if (tmp.rightChild != null)
			tmp.rightChild.parent = grandparent;
		tmp.rightChild = grandparent;
		linkWithParent(tmp, grandparent);
		return tmp;
	}

	public Node<E> leftRightRotate(Node<E> node) {
		node.leftChild = leftRotate(node.leftChild);
		return rightRotate(node);
	}

	public Node<E> rightLeftRotate(Node<E> node) {
		node.rightChild = rightRotate(node.rightChild);
		return leftRotate(node);
	}

}
package data_structures;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

import data.WordyBloom;
import data.Wordy;
import data.IPAddress;

public class Main {
	private static final boolean PROCESS_PARTIALFILE_FORQUICKTEST = false;
	private static final String INPUT_FILE="src/data/ip2country.tsv";
	private static final int NUMBEROFLINES_TO_PROCESS_FORQUICKTEST = 1000;
	public static final int IPADDRESS_TOTAL_COUNT = 64000;
	private static final int IPMAXCOUNT_TO_LOOKUP = 10000;

	private static void loadIPInfoInHash(WordyBloom bst) {

		String[] fields;
		String lineRead = null;
		boolean skipFirstLine = true;

		IPAddress ip2 = new IPAddress();

		int linesRead = 0;

		IPAddress ip = null;
		Wordy wordyIP = null;

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(INPUT_FILE));
			while (true) {
				lineRead = reader.readLine();

				if (lineRead == null) {
					break;
				} else {
					fields = lineRead.split("\t");
					if (skipFirstLine) {
						skipFirstLine = false;// skip the first line which
												// contains the header
					} else {

						wordyIP = new Wordy(fields[0], (int) Float.parseFloat(fields[3]));
						bst.addWordy(wordyIP, 0);
					}
				}
				if (PROCESS_PARTIALFILE_FORQUICKTEST && linesRead++ > NUMBEROFLINES_TO_PROCESS_FORQUICKTEST)
					break;

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private static void LookupIPsInfoInHash(WordyBloom bst) {

		String[] fields;
		String lineRead = null;
		int linesRead = 0;
		int ipsSearched = 0;

		boolean skipFirstLine = true;

		float ipsloaded = 0;
		float ipsprocessed = 0;
		float ipsskipped = 0;
		float ipsfound = 0;
		float ipsnotfound = 0;

		IPAddress ip = null;
		Wordy wordyIP = null;
		Random randomnoGenerator = new Random();

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(INPUT_FILE));
			while (true) {
				lineRead = reader.readLine();

				if (lineRead == null) {
					break;
				} else {
					fields = lineRead.split("\t");
					if (skipFirstLine) {
						skipFirstLine = false;// skip the first line which
												// contains the header
					} else {

						ipsloaded++;
						// System.out.println("Processing ip address:" +
						// fields[0]);

						wordyIP = new Wordy(fields[0], (int) Float.parseFloat(fields[3]));
						int randomno1 = randomnoGenerator.nextInt(4);
						int randomno2 = randomnoGenerator.nextInt(4);

						// System.out.println("random1 " + randomno1 + " random2
						// " + randomno2 + (randomno1 == randomno2 ? " matched!"
						// : " did not match"));

						if (randomno1 == randomno2) {
							ipsprocessed++;
							if (bst.hasWordy(wordyIP)) {
								ipsfound++;
								// System.out.println(ip.ip_address + " found");
							} else {
								ipsnotfound++;
								System.out.println(ip.ip_address + " not found");
							}
						} else {
							ipsskipped++;
							// System.out.println(ip.ip_address +" skipped");
						}
					}
				}
				if (PROCESS_PARTIALFILE_FORQUICKTEST && linesRead++ > NUMBEROFLINES_TO_PROCESS_FORQUICKTEST)
					break;

				if (ipsSearched++ > IPMAXCOUNT_TO_LOOKUP)
					break;
			}
			System.out.println("Percentage of IPs processed =" + (ipsprocessed / ipsloaded) * 100 + "% (" + ipsprocessed
					+ "/" + ipsloaded + ")");
			System.out.println("Percentage of IPs skipped =" + (ipsskipped / ipsloaded) * 100 + "% (" + ipsskipped + "/"
					+ ipsloaded + ")");

			System.out.println("Percentage of IPs found =" + (ipsfound / ipsprocessed) * 100 + "% (" + ipsfound + "/"
					+ ipsprocessed + ")");
			System.out.println("Percentage of IPs not found =" + (ipsnotfound / ipsprocessed) * 100 + "% ("
					+ ipsnotfound + "/" + ipsprocessed + ")");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private static void loadIPInfoInAVL(BalancedSearchTree<IPAddress> bst) {

		String[] fields;
		String lineRead = null;
		boolean skipFirstLine = true;

		int linesRead = 0;

		IPAddress ip = null;

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(INPUT_FILE));
			while (true) {
				lineRead = reader.readLine();

				if (lineRead == null) {
					break;
				} else {
					fields = lineRead.split("\t");
					if (skipFirstLine) {
						skipFirstLine = false;// skip the first line which
												// contains the header
					} else {
						ip = new IPAddress(fields[0], fields[1], Float.parseFloat(fields[3]),
								Float.parseFloat(fields[3]));
						bst.add(ip);
					}
				}
				if (PROCESS_PARTIALFILE_FORQUICKTEST && linesRead++ > NUMBEROFLINES_TO_PROCESS_FORQUICKTEST)
					break;

			}

			for (IPAddress elementObject : bst) {
				// System.out.println("Loaded: info for IPaddress=" +
				// elementObject.ip_address.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private static void LookupIPsInfoInAVL(BalancedSearchTree<IPAddress> bst) {

		String[] fields;
		String lineRead = null;
		int linesRead = 0;
		int ipsSearched = 0;

		boolean skipFirstLine = true;

		float ipsloaded = 0;
		float ipsprocessed = 0;
		float ipsskipped = 0;
		float ipsfound = 0;
		float ipsnotfound = 0;

		IPAddress ip = null;
		Random randomnoGenerator = new Random();

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(INPUT_FILE));
			while (true) {
				lineRead = reader.readLine();

				if (lineRead == null) {
					break;
				} else {
					fields = lineRead.split("\t");
					if (skipFirstLine) {
						skipFirstLine = false;// skip the first line which
												// contains the header
					} else {

						ipsloaded++;
						// System.out.println("Processing ip address:" +
						// fields[0]);

						ip = new IPAddress(fields[0], fields[1], Float.parseFloat(fields[3]),
								Float.parseFloat(fields[3]));

						int randomno1 = randomnoGenerator.nextInt(4);
						int randomno2 = randomnoGenerator.nextInt(4);

						// System.out.println("random1 " + randomno1 + " random2
						// " + randomno2 + (randomno1 == randomno2 ? " matched!"
						// : " did not match"));

						if (randomno1 == randomno2) {
							ipsprocessed++;
							if (bst.contains(ip)) {
								ipsfound++;
								// System.out.println(ip.ip_address + " found");
							} else {
								ipsnotfound++;
								System.out.println(ip.ip_address + " not found");
							}
						} else {
							ipsskipped++;
							// System.out.println(ip.ip_address +" skipped");
						}
					}
				}
				if (PROCESS_PARTIALFILE_FORQUICKTEST && linesRead++ > NUMBEROFLINES_TO_PROCESS_FORQUICKTEST)
					break;

				if (ipsSearched++ > IPMAXCOUNT_TO_LOOKUP)
					break;
			}
			System.out.println("Percentage of IPs processed =" + (ipsprocessed / ipsloaded) * 100 + "% (" + ipsprocessed
					+ "/" + ipsloaded + ")");
			System.out.println("Percentage of IPs skipped =" + (ipsskipped / ipsloaded) * 100 + "% (" + ipsskipped + "/"
					+ ipsloaded + ")");

			System.out.println("Percentage of IPs found =" + (ipsfound / ipsprocessed) * 100 + "% (" + ipsfound + "/"
					+ ipsprocessed + ")");
			System.out.println("Percentage of IPs not found =" + (ipsnotfound / ipsprocessed) * 100 + "% ("
					+ ipsnotfound + "/" + ipsprocessed + ")");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {
		BalancedSearchTree<Integer> tree = new BalancedSearchTree<Integer>();
		System.out.println("Size of tree is: " + tree.size());
		tree.add(20);
		tree.add(15);
		tree.add(8);
		tree.add(12);
		tree.add(25);
		tree.add(23);
		tree.add(24);
		tree.add(14);
		tree.add(13);
		tree.add(11);
		tree.add(10);
		tree.add(18);
		tree.add(35);
		tree.add(50);
		tree.add(30);
		tree.add(51);
		System.out.println("Size of tree is: " + tree.size());
		System.out.println("added: " + tree.get(44));
		System.out.println("added: " + tree.get(55));
		System.out.println("added: " + tree.get(52));
		System.out.println("added: " + tree.get(4));
		System.out.println("added: " + tree.get(59));
		System.out.println("After 18 is: " + tree.findNext(18));
		for (Iterator i = tree.allElements(); i.hasNext();) {
			System.out.println("iteration: Current BST node is: " + i.next());
		}

		tree.delete(44);
		System.out.println("Is 44 there: " + tree.get(44));
		System.out.println("Is 55 there: " + tree.get(55));
		System.out.println("Is 52 there: " + tree.get(52));
		System.out.println("Is 59 there: " + tree.get(59));
		System.out.println("Size of tree is: " + tree.size());
		
		BalancedSearchTree<IPAddress> bstOfIPAddresses = new BalancedSearchTree<IPAddress>();
		long startTime, stopTime, elapsedTime;

		startTime = System.currentTimeMillis();
		loadIPInfoInAVL(bstOfIPAddresses);
		stopTime = System.currentTimeMillis();
		elapsedTime = stopTime - startTime;
		System.out.println("loadIPInfoInAVL elapsed time (millsecs): " + elapsedTime);

		startTime = System.currentTimeMillis();
		LookupIPsInfoInAVL(bstOfIPAddresses);
		stopTime = System.currentTimeMillis();
		elapsedTime = stopTime - startTime;
		System.out.println("LookupIPsInfoInAVL elapsed time (millsecs): " + elapsedTime);

		
		WordyBloom wbOfIPaddresses = new WordyBloom();
		


		startTime = System.currentTimeMillis();
		loadIPInfoInHash(wbOfIPaddresses);
		stopTime = System.currentTimeMillis();
		elapsedTime = stopTime - startTime;
		System.out.println("loadIPInfoInHash elapsed time (millsecs): " + elapsedTime);

		startTime = System.currentTimeMillis();
		LookupIPsInfoInHash(wbOfIPaddresses);
		stopTime = System.currentTimeMillis();
		elapsedTime = stopTime - startTime;
		System.out.println("LookupIPsInfoInHash elapsed time (millsecs): " + elapsedTime);
		
		System.out.println("Tests completed");

	}

}

