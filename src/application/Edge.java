package application;


class Edge implements Comparable<Edge>{
		int id;
		int source;
		int destination;
		int weight;
		int time_diff;

		public Edge(int id, int source, int destination, int weight, int time_diff) {
			this.id = id;
			this.source = source;
			this.destination = destination;
			this.weight = weight;
			this.time_diff = time_diff;
		}
		
		@Override
		public int compareTo(Edge edge) {
	        if((this.time_diff+this.weight) > (edge.time_diff+edge.weight))
	            return 1;
	        else if ((this.time_diff+this.weight) < (edge.time_diff+edge.weight))
	            return -1;
	        else
	            return 0;
		}

		@Override
		public String toString() {
			return "Edge [id=" + id + ", source=" + source + ", destination=" + destination + ", weight=" + weight
					+ ", time_diff=" + time_diff + "]";
		}
	}
