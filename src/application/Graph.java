package application;

import java.util.ArrayList;
import java.util.LinkedList;

class Graph {
		int vertices;
		int time = 10000000;
		LinkedList<Edge> [] adjacencylist;
		ArrayList<Vertex> VertciesList;
	    Vertex Depot;
	    ArrayList<Vertex> AllRequests;
	    ArrayList<Vertex> AvailableRequests;

		@SuppressWarnings("unchecked")
		public Graph(int vertices, ArrayList<Vertex> VertciesList) {
			this.vertices = vertices;
			this.VertciesList = VertciesList;
	        this.AllRequests = new ArrayList<Vertex>();
	        this.AvailableRequests = null;
			adjacencylist = new LinkedList[vertices];
			//initialize adjacency lists for all the vertices
			for (int i = 0; i <vertices ; i++) {
				adjacencylist[i] = new LinkedList<>();
			}
		}
		
	    public Graph(ArrayList<Vertex> AllRequests, Vertex Depot) {
	        this.AllRequests = AllRequests;
	        this.Depot = Depot;
	        this.AvailableRequests = null;
	    }
	       
		public void addEdge(int id, int source, int destination, int weight, int time_diff) {
			Edge edge = new Edge(id,source, destination, weight,time_diff);
			VertciesList.get(source).getEdges().add(edge);
			Edge edge2 = new Edge(id, destination, source, weight,time_diff);
			VertciesList.get(destination).getEdges().add(edge2);
			adjacencylist[source].add(edge); //for directed graph
		}
		
		public void removeEdge(Edge edge) {
			adjacencylist[edge.source].remove(edge);
			VertciesList.get(edge.source).getEdges().remove(edge);
			Edge edge2 = new Edge(edge.id, edge.destination, edge.source, edge.weight,edge.time_diff);
			VertciesList.get(edge.destination).getEdges().remove(edge2);
		}

		public void printGraph(){
			for (int i = 0; i <vertices ; i++) {
				LinkedList<Edge> list = adjacencylist[i];
				//Vertex vertex = VertciesList.get(i);
				for (int j = 0; j <list.size() ; j++) {
					System.out.println("Edge ID: "+list.get(j).id+
							", Pickup Location ID " + list.get(j).source+" ("+
							VertciesList.get(list.get(j).source).X_coordinate+","+VertciesList.get(list.get(j).source).Y_coordinate+
							") ==> Delivary Location ID " +list.get(j).destination+" ("+
							VertciesList.get(list.get(j).destination).X_coordinate+","+VertciesList.get(list.get(j).destination).Y_coordinate+
							") with weight: " +  list.get(j).weight+" and time difference: "+list.get(j).time_diff);
				}
			}
		}
	}