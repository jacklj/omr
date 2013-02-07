package omr.util;

public class UnionFindSimple {

	private final int[] parent;
	
	public UnionFindSimple(int maxNumberOfNodes) {
		this.parent = new int[maxNumberOfNodes + 1]; // +1 because element 0 in parent[] isnt used
	}
	
	
	public int findRoot(int node) {
		
//		System.out.println("findRoot(int node = " + node);
		while(parent[node] != 0) {
			node = parent[node];
		}
		return(node);
	}
	
	public void union(int x, int y) {
		while(parent[x] != 0) {
			x = parent[x];
		}
		while(parent[y] != 0) {
			y = parent[y];
		}
		if(x != y) {
			parent[x] = y; // make x child of y
		}
		// could introduce a 'rank' value to nodes so we add the smaller tree.
		
		
	}
	
	
	public int[] getVector() {
		return parent;
	}
}
