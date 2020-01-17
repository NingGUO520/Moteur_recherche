package index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class RadixTree {

	class Edge {
		Node targetNode;
		String label;

		public Edge(Node targetNode, String label) {
			this.targetNode = targetNode;
			this.label = label;
		}

		public Edge(String label) {
			this.targetNode = new Node();
			this.label = label;
		}
	}

	class Node {
		ArrayList<Edge> edges;
		Map<Integer, ArrayList<Coord>> books;

		public Node(ArrayList<Edge> edges, Map<Integer, ArrayList<Coord>> books) {
			this.edges = edges;
			this.books = books;
		}

		public Node(Map<Integer, ArrayList<Coord>> books) {
			this.edges = new ArrayList<Edge>();
			this.books = books;
		}

		public Node() {
			edges = new ArrayList<Edge>();
			books = new TreeMap<Integer, ArrayList<Coord>>();
		}

		public boolean isLeaf() {
			return edges.isEmpty();
		}

	}

	protected Node root;

	public RadixTree() {
		root = new Node();
	}

	public void addIndexingFile(int id, IndexingFile f) {
		for (Entry<String, ArrayList<Coord>> entry : f.getIndex().entrySet()) {
			String key = entry.getKey();
			ArrayList<Coord> coords = entry.getValue();
			addElement(root, key,id, coords);
		}
	}

	private void addElement(Node n, String str,int id,ArrayList<Coord>coords) {
		// basis
		if (n.edges.isEmpty()) {
			Map<Integer, ArrayList<Coord>> books = new HashMap<Integer,ArrayList<Coord>>();
			books.put(id, coords);
			Node child = new Node(books);
			Edge e = new Edge(child, str);
			n.edges.add(e);
			return;
		} else {
			int ind = 0;
			Edge edit = null;
			// find the biggest common prefix among all edges
			for (Edge e : n.edges) {
				int ind_prefix = findBiggestPrefix(e.label, str);
				if (ind_prefix > ind) {
					ind = ind_prefix;
					edit = e;
				}
			}
			// the str doens't match with anybody
			if (ind == 0 && edit == null) {
				Map<Integer, ArrayList<Coord>> books = new HashMap<Integer,ArrayList<Coord>>();
				books.put(id, coords);
				Node child = new Node(books);
				Edge edge = new Edge(child, str);
				n.edges.add(edge);
				return;
			} else {
				// we decompose the str such as prefix and suffix
				String prefix = str.substring(0, ind);
				String suffix1 = str.substring(ind);
				String suffix2 = edit.label.substring(ind);
				// the prefix doesn't exist in the radix tree
				if (!edit.label.equals(prefix)) {
					edit.label = prefix;
					Node new_node = new Node();
					Edge e2 = new Edge(new Node(edit.targetNode.books), suffix2);
					new_node.edges.add(e2);
					edit.targetNode = new_node;
					addElement(edit.targetNode, suffix1,id,coords);
				}
				// it exists and we only have to go to the target
				else {
					if(!edit.targetNode.books.isEmpty()) {
						if(!edit.targetNode.books.containsKey(id)) edit.targetNode.books.put(id,coords);
					}
					else {
						addElement(edit.targetNode, suffix1,id,coords);
					}
					
				}
			}
			return;
		}
	}

	public Map<Integer, ArrayList<Coord>> search(String s) {
		Node n = root;
		boolean next = true;
		String str = s;
		while (!n.isLeaf() && next) {
			int ind = 0;
			Edge traverse = null;
			for (Edge e : n.edges) {
				// in case when the word search is a prefix
				if (e.label.isEmpty() && str.isEmpty())
					return e.targetNode.books;
				int ind_prefix = findBiggestPrefix(e.label, str);
				if (ind_prefix > ind) {
					ind = ind_prefix;
					traverse = e;
				}
			}
			if (traverse == null) {
				next = false;
			} else {
				str = str.substring(ind);
				n = traverse.targetNode;
			}
		}
		return n.books;
	}

	public void print(Node n) {
		Map<Integer, ArrayList<Coord>> c = n.books;
		ArrayList<Edge> e = n.edges;

		System.out.println("books");
		if (c != null) {
			for (Entry<Integer, ArrayList<Coord>> entry : c.entrySet()) {
				Integer k = entry.getKey();
				ArrayList<Coord> coords = entry.getValue();
				System.out.println("For the book : " + k);
				for (Coord coord : coords) {
					System.out.println(coord);
				}

			}
		}

		System.out.println("Edges");
		if (e != null || !e.isEmpty()) {
			for (Edge ed : e) {
				System.out.println(ed.label);
				print(ed.targetNode);
			}
		}

	}

	public int findBiggestPrefix(String s1, String s2) {

		char[] c1 = s1.toCharArray();
		char[] c2 = s2.toCharArray();
		int i = 0;

		if (c1.length < c2.length) {
			for (int j = 0; j < c1.length; j++) {
				if (c1[j] == c2[j]) {
					i++;
				} else
					break;
			}
		} else if (c2.length < c1.length) {
			for (int j = 0; j < c2.length; j++) {
				if (c1[j] == c2[j]) {
					i++;
				} else
					break;
			}
		} else {
			for (int j = 0; j < c1.length; j++) {
				if (c1[j] == c2[j]) {
					i++;
				} else
					break;
			}
		}
		return i;
	}

	public Node getRoot() {
		return root;
	}
}
