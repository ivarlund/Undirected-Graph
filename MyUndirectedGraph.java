// Ivar Lund (ivlu1468). Jag har samarbetat med Jesper Byström (jeby1474) och Elin Lundstedt (ellu3451)

import java.util.*;

public class MyUndirectedGraph<T> implements UndirectedGraph<T> {

	private ArrayList<Node> graph = new ArrayList<>();
	private Stack<T> stack = new Stack<T>();
	private ArrayList<T> list = new ArrayList<T>();

	private class Node {
		private T data;
		private ArrayList<Edge> edges;
		private boolean visited;
		private T parent;

		public Node(T data) {
			this.data = data;
			this.edges = new ArrayList<Edge>();
			this.visited = false;
			graph.add(this);
		}
		
		public void setParent (T parent) {
			this.parent = parent;
		}
		
		public T getParent() {
			return parent;
		}

		public T getData() {
			return data;
		}

		public boolean getVisited() {
			return visited;
		}

		public void setVisited(Boolean visited) {
			this.visited = visited;
		}

	}

	private class Edge implements Comparable<Edge> {
		private Node nodeOne;
		private Node nodeTwo;
		private int cost;

		public Edge(Node nodeOne, Node nodeTwo, int cost) {
			this.nodeOne = nodeOne;
			this.nodeTwo = nodeTwo;
			this.cost = cost;

			if (!nodeOne.edges.contains(this)) {
				nodeOne.edges.add(this);
			}
			if (!nodeTwo.edges.contains(this)) {
				nodeTwo.edges.add(this);
			}
		}

		public int getCost() {
			return cost;

		}

		public void setCost(int newCost) {
			this.cost = newCost;
		}

		public T getNodeOne() {
			return nodeOne.getData();
		}

		public T getNodeTwo() {
			return nodeTwo.getData();
		}

		@Override
		public int compareTo(Edge o) {
			if (this.cost > o.getCost()) {
				return 1;
			} else if (this.cost < o.getCost()) {
				return -1;
			} else {
				return 0;
			}
		}
		
	}

	@Override
	public int getNumberOfNodes() {
		return graph.size();
	}

	@Override
	public int getNumberOfEdges() {
		int count = 0;
		for (Node n : graph) {
			for (Edge e : n.edges) {
				if (!e.nodeOne.getVisited() && e.nodeTwo.getVisited()) {
					count++;
					e.nodeOne.setVisited(true);
					e.nodeTwo.setVisited(true);
				}
			}
		}
		for (Node n : graph) {
			n.setVisited(false);
		}
		return count;
	}

	@Override
	public boolean add(T newNode) {
		for (Node n : graph) {
			if (n.getData().equals(newNode)) {
				return false;
			}
		}
		new Node(newNode);
		return true;
	}
	
	@Override
	public boolean connect(T node1, T node2, int cost) {
		if (cost < 1) {
			return false;
		}
		Node first = dataToNode(node1);
		Node second = dataToNode(node2);
		if (first == null || second == null) {
			return false;
		}
		for (Edge e : first.edges) {
			if (e.getNodeOne().equals(node2) && e.getNodeTwo().equals(node1) 
					|| e.getNodeTwo().equals(node2) && e.getNodeOne().equals(node1)) {
				e.setCost(cost);
				return true;
			}
		}
		new Edge(first, second, cost);
		return true;
	}

	private Node dataToNode(T data) {
		Node node = null;
		for (Node n : graph) {
			T tmp = n.getData();
			if (tmp.equals(data)) {
				node = n;
				return node;
			}
		}
		return node;
	}

	@Override
	public boolean isConnected(T node1, T node2) {
		Node first = dataToNode(node1);
		Node second = dataToNode(node2);
		if (first == null || second == null) {
			return false;
		}
		for (Edge e : first.edges) {
			if (e.getNodeOne().equals(node2) && e.getNodeTwo().equals(node1) 
						|| e.getNodeTwo().equals(node2) && e.getNodeOne().equals(node1)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getCost(T node1, T node2) {
		if (!isConnected(node1, node2)) {
			return -1;
		}
		Node tmp = dataToNode(node1);
		for (Edge e : tmp.edges) {
			if (e.getNodeOne().equals(node2) && e.getNodeTwo().equals(node1)
					|| e.getNodeTwo().equals(node2) && e.getNodeOne().equals(node1)) {
				return e.getCost();
			}
		}
		return -1;
	}

	@Override
	public List<T> depthFirstSearch(T start, T end) {
		if (stack.size() == 0) {
			stack.push(start);
		}
		if (!stack.peek().equals(start)) {
			stack.push(start);
		}
		while (stack.size() != 0 && stack.peek() != end) {
			Node tmp = dataToNode(stack.peek());
			tmp.setVisited(true);

			for (Edge e : tmp.edges) {
				if (!e.nodeOne.getVisited()) {
					return depthFirstSearch(e.getNodeOne(), end);
				} else if (!e.nodeTwo.getVisited()) {
					return depthFirstSearch(e.getNodeTwo(), end);
				}
			}
			stack.pop();
			return depthFirstSearch(stack.peek(), end);
		}

		return stack;
	}

	@Override
	public List<T> breadthFirstSearch(T start, T end) {
		Queue<T> queue = new LinkedList<T>();
		
		queue.add(start);
		while (!queue.peek().equals(end)) {
			T o = queue.peek();
			Node tmp = dataToNode(o);
			tmp.setVisited(true);
			for (int i = 0; i < tmp.edges.size(); i++) {
				Node nodeOne = tmp.edges.get(i).nodeOne;
				Node nodeTwo = tmp.edges.get(i).nodeTwo;
				if (nodeOne.equals(tmp)) {
					if (!nodeTwo.getVisited()) {
						queue.add(nodeTwo.getData());
						nodeTwo.setVisited(true);
						nodeTwo.setParent(nodeOne.getData());
					}
				}
				if (nodeTwo.equals(tmp)) {
					if (!nodeOne.getVisited()) {
						queue.add(nodeOne.getData());
						nodeOne.setVisited(true);
						nodeOne.setParent(nodeTwo.getData());
					}
				}
			}
			queue.remove();
		}
		if (start.equals(end)) {
			list.add(start);
		} else {
			toArray(start, end);
		}
		return list;
	}
	
	private void toArray (T start, T data) {
		Node n = dataToNode(data);
		T o = n.getParent();
		list.add(0, data);
		if (!o.equals(start)) {
			toArray(start, o);
		} else {
			list.add(0, start);
		}
	}

	@Override
	public UndirectedGraph<T> minimumSpanningTree() {
		PriorityQueue<Edge> heap = new PriorityQueue<Edge>();
		ArrayList<Edge> remove = new ArrayList<Edge>();
		if (graph.isEmpty()) {
			return null;
		}
		Node n = graph.get(0);
		n.setVisited(true);
		for (Edge e : n.edges) {
			if (!e.nodeOne.equals(e.nodeTwo)) {
				heap.add(e);
			}
		}
		while (!heap.isEmpty()) {
			Edge edge = heap.peek();
			if (remove.contains(edge)) {
				heap.remove();
				continue;
			}
			Node nodeOne = edge.nodeOne;
			Node nodeTwo = edge.nodeTwo;
			if (nodeOne.equals(nodeTwo)) {
				heap.remove();
				continue;
			}
			
			if (!nodeTwo.getVisited()) {
				for (Edge e : nodeTwo.edges) {
					heap.add(e);
				}
				remove.add(edge);
				heap.remove(edge);
				nodeTwo.setVisited(true);
			} else if (!nodeOne.getVisited()) {
				for (Edge e : nodeOne.edges) {
					heap.add(e);
				}
				remove.add(edge);
				heap.remove(edge);
				nodeOne.setVisited(true);
			} else {
				heap.remove();
			}
		}
		UndirectedGraph<T> minSTree = new MyUndirectedGraph<T>();
		
		for (Node node : graph) {
			minSTree.add(node.getData());
		}
		for (Edge e : remove) {
			T first = e.getNodeOne();
			T second = e.getNodeTwo();
			int cost = e.getCost();
			minSTree.connect(first, second, cost);
		}
		return minSTree;
	}
}
