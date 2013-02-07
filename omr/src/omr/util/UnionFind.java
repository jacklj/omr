package omr.util;


public class UnionFind {
	
    /** The trees of Nodes that represent the disjoint sets. */
    Node[] nodes;
    Node[] stack;

    static class Node {
        Node parent;  // The root of the tree in which this Node resides
        Node child;
        int value;
        int rank;     // This Node's height in the tree

        public Node(int v) {
            value = v;
            rank = 0;
        }
    }
    
    // constructor /////////////////////////////////////////////////////////////////////////////////////////////////////
    public UnionFind(int size) {
        nodes = new Node[size];
        stack = new Node[size];
    }

    // public methods //////////////////////////////////////////////////////////////////////////////////////////////////
    public int find(int a) {
    	// Returns the integer value associated with the first Node in a set
        return findNode(a).value;
    }
    
    
    public boolean isEquiv(int a, int b) {
    	// Returns true if a and b are in the same set.
        return findNode(a) == findNode(b);
    }


    public void union(int a, int b) {
        // Combines the set that contains a with the set that contains b.

        Node na = findNode(a);
        Node nb = findNode(b);

        if (na == nb) {
            return;
        }

        // Link the smaller tree under the larger.
        if (na.rank > nb.rank) {
            // Delete nb.
            nb.child.parent = na.child;
            na.value = b;
        }
        else {
            // Delete na.
            na.child.parent = nb.child;
            nb.value = b;

            if (na.rank == nb.rank) {
                nb.rank++;
            }
        }
    }
    
    
    // private methods /////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Searches the disjoint sets for a given integer.  Returns the set
     * containing the integer a.  Sets are represented by a local class
     * <tt>Node</tt>.
     */
    private Node findNode(int a) {
          Node na = nodes[a];

          if (na == null) {
              // Start a new set with a root node
              Node root = new Node(a);

              root.child = new Node(a);
              root.child.parent = root;

              nodes[a] = root.child; // root nodes have an equally valued parent node with itself as a child (no other nodes have this)

              return root;
          }

          return findNode(na);
    }


    /**
     * Finds the set containing a given Node.
     */
    private Node findNode(Node node) {
        int top = 0;

        // Find the child of the root element.
        while (node.parent.child == null) {
            stack[top++] = node;
            node = node.parent;
        }

        // Do path compression on the way back down.
        Node rootChild = node;

        while (top > 0) {
            node = stack[--top];
            node.parent = rootChild;
        }

        return rootChild.parent;
    }


}