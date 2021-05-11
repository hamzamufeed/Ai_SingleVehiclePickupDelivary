package application;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Vertex {
		int Request_number;
		int X_coordinate;
		int Y_coordinate;
		int Demand;
		int Early_Time_Window;
		int Late_Time_Window;
		int currentTime;
		int currentCapacity;
		int waitTime;
		int cost;
		String type;
		Set<Edge> edges; //collection of edges to neighbors 
		boolean isVisited;
		
		public Vertex(int Request_number,int X_coordinate,int Y_coordinate,int Demand,int Early_Time_Window,int Late_Time_Window) {
			this.Request_number = Request_number;
			this.X_coordinate = X_coordinate;
			this.Y_coordinate = Y_coordinate;
			this.Demand = Demand;
			this.Early_Time_Window = Early_Time_Window;
			this.Late_Time_Window = Late_Time_Window;
			edges = new HashSet<>();
			this.isVisited = false;
		}
		
		boolean addEdge(Edge edge){
			return edges.add(edge);
		}

		List<Edge> getEdges() {
			return new ArrayList<>(edges);
		}

		public int getRequest_number() {
			return Request_number;
		}

		public int getDemand() {
			return Demand;
		}

		public int getEarly_Time_Window() {
			return Early_Time_Window;
		}

		public int getLate_Time_Window() {
			return Late_Time_Window;
		}

		public int getCurrentTime() {
			return currentTime;
		}

		public int getCurrentCapacity() {
			return currentCapacity;
		}

		public int getWaitTime() {
			return waitTime;
		}

		public int getCost() {
			return cost;
		}

		public String getType() {
			return type;
		}

		@Override
		public String toString() {
			return "Vertex [Request_number=" + Request_number + ", X_coordinate=" + X_coordinate + ", Y_coordinate="
					+ Y_coordinate + ", Demand=" + Demand + ", Early_Time_Window=" + Early_Time_Window
					+ ", Late_Time_Window=" + Late_Time_Window + ", edges=" + edges + "]\n";
		}
	}
