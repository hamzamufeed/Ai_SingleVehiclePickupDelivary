package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.scene.Scene;

public class MainController {

	@FXML
	private AnchorPane anchorPane;

	@FXML
	private Button browse;

	@FXML
	private TextField fileName;

	@FXML
	private Button start;

	@FXML
	private Button chart;
	
    @FXML
    private Button data;

	@FXML
	private Button restart;

	@FXML
	private Button exit;

	@FXML
	private CheckBox checkBox;

	@FXML
	private TableView<Vertex> table;

	@FXML
	private TableColumn<Vertex, Integer> id;

	@FXML
	private TableColumn<Vertex, String> type;

	@FXML
	private TableColumn<Vertex, Integer> demand;

	@FXML
	private TableColumn<Vertex, Integer> early_time;

	@FXML
	private TableColumn<Vertex, Integer> late_time;

	@FXML
	private TableColumn<Vertex, Integer> current_timer;

	@FXML
	private TableColumn<Vertex, Integer> current_capacity;

	@FXML
	private TableColumn<Vertex, Integer> wait_time;

	@FXML
	private TableColumn<Vertex, Integer> distance_time;

	@FXML
	private TextField total_wait;

	@FXML
	private TextField total_time;
	
    @FXML
    private TextField total_travel;

	static int Locations;
	static int Capacity;
	static int Tasks;
	static boolean forward;
	public static String file_path = "";
	public static ArrayList<Vertex> Path = new ArrayList<Vertex>();
	Scanner in = null;

	@FXML
	void start(ActionEvent event) {
		if(Path!=null)
			Path.clear();
		flag = true;
		forward = (checkBox.isSelected()) ? true : false;
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		if(file_path.contains("Dataset2"))
			vertices = readDataSet2();
		else if(file_path.contains("Dataset"))
			vertices = readDataSet1();
		else {
			Alert("Please Choose a Dataset File");
			return;
		}

		Graph graph = new Graph(2*Tasks+1,vertices);
		for(int i=1;i<=Tasks;i++) {
			graph.addEdge(i, vertices.get(i).Request_number, vertices.get(Tasks+i).Request_number, 
					getCost(vertices.get(i).X_coordinate, vertices.get(i).Y_coordinate, 
							vertices.get(Tasks+i).X_coordinate, vertices.get(Tasks+i).Y_coordinate),
					(vertices.get(Tasks+i).Late_Time_Window-vertices.get(i).Early_Time_Window));
		}
		for(int i=1;i<vertices.size();i++) {
			graph.AllRequests.add(vertices.get(i));
		}
		graph.Depot = vertices.get(0);
		ArrayList<Vertex> path = new ArrayList<>();
		path.add(vertices.get(0));
		// Searching Algorithm starts here
		path = DepthFirstSearch(path,graph);
		Path = path;
		int total_wait_time = 0;
		int total_cost = 0;
		if (path != null) {
			System.out.print("Depot: "+path.get(0).Request_number);
			System.out.println();
			for (int i = 1; i < path.size(); i++) {
				System.out.print("--> Request: "+path.get(i).Request_number+", time: "+ path.get(i).currentTime
						+", current Capacity: "+path.get(i).currentCapacity+", waiting time: "+
						path.get(i).waitTime+", distance time: "+path.get(i).cost);
				total_wait_time+= path.get(i).waitTime;
				total_cost+=path.get(i).cost;
				System.out.println();
			}
			total_time.setText(""+path.get(path.size()-1).currentTime);
			total_wait.setText(""+total_wait_time);
			total_travel.setText(""+total_cost);
			System.out.println("Total Time = "+path.get(path.size()-1).currentTime);
			System.out.println("Total Wait Time = "+total_wait_time);
			System.out.println("Total Travel Time = "+total_cost);

			ObservableList<Vertex> dataList = FXCollections.observableArrayList();
			for(int i=0;i<path.size();i++) {
				dataList.add(path.get(i));
			}
			id.setCellValueFactory(new PropertyValueFactory<Vertex,Integer>("Request_number"));
			type.setCellValueFactory(new PropertyValueFactory<Vertex,String>("type"));
			demand.setCellValueFactory(new PropertyValueFactory<Vertex,Integer>("Demand"));
			early_time.setCellValueFactory(new PropertyValueFactory<Vertex,Integer>("Early_Time_Window"));
			late_time.setCellValueFactory(new PropertyValueFactory<Vertex,Integer>("Late_Time_Window"));
			current_timer.setCellValueFactory(new PropertyValueFactory<Vertex,Integer>("currentTime"));
			current_capacity.setCellValueFactory(new PropertyValueFactory<Vertex,Integer>("currentCapacity"));
			wait_time.setCellValueFactory(new PropertyValueFactory<Vertex,Integer>("waitTime"));
			distance_time.setCellValueFactory(new PropertyValueFactory<Vertex,Integer>("cost"));
			table.setItems(dataList);

			table.setRowFactory(tv -> new TableRow<Vertex>() {
				@Override
				protected void updateItem(Vertex item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null || item.getType() == null)
						setStyle("");
					else if (item.getType().equals("Depot"))
						setStyle("-fx-background-color: #ff4d4d;");
					else if (item.getType().equals("Pickup"))
						setStyle("-fx-background-color: #80ff80;");	
					else if (item.getType().equals("Delivary"))
						setStyle("-fx-background-color: #66c2ff;");
					else
						setStyle("");
				}
			});
		}
		else {
			System.out.println("No Solution Found!");
			table.getItems().clear();
			total_time.clear();
			total_wait.clear();
			total_travel.clear();
			Alert("No Solution Found!");
		}
	}
	
	ArrayList<Vertex> readDataSet1() {
		File file = new File(file_path);
		try {
			in = new Scanner(file);
		} catch (FileNotFoundException e) {
			Alert("File not found!");
			return null;
		}
		in.nextLine();
		in.nextLine();
		in.nextLine();
		int tasks = in.nextInt();
		Tasks = tasks;
		Capacity = in.nextInt();
		Locations = Tasks*2;
		in.nextLine();
		ArrayList<Vertex> vertices = new ArrayList<>();
		
		int locations = Integer.parseInt(in.nextLine().substring(13));
		int[] vertex = new int[locations];
		int[] x = new int[locations];
		int[] y = new int[locations];
		
		for(int i=0;i<locations;i++) {
			vertex[i] = in.nextInt();
			x[i] = Integer.parseInt(in.next().substring(1));
			y[i] = Integer.parseInt(in.next().replace(")",""));
		}

		in.nextLine();
		in.nextLine();
		int initial = in.nextInt();
		in.nextLine();
		in.nextLine();
		int[] Id = new int[tasks];
		int[] pickup = new int[tasks];
		int[] delivery = new int[tasks];
		int[][] twin_pick = new int[tasks][2];
		int[][] twin_delivery = new int[tasks][2];
		int[] Demand = new int[tasks];
		vertices.add(new Vertex(0, x[initial], y[initial], 0, 0, 100000));
		vertices.get(0).type = "Depot";
		for(int i=0;i<tasks;i++) {
			Id[i] = in.nextInt();
			pickup[i] = Integer.parseInt(in.next().substring(1));
			in.next();
			delivery[i] = Integer.parseInt(in.next().replace(")",""));
			twin_pick[i][0] = Integer.parseInt(in.next().substring(1));
			twin_pick[i][1] = Integer.parseInt(in.next().replace("]",""));
			twin_delivery[i][0] = Integer.parseInt(in.next().substring(1));
			twin_delivery[i][1] = Integer.parseInt(in.next().replace("]",""));
			Demand[i] = in.nextInt();
			vertices.add(i+1, new Vertex(i+1, x[pickup[i]], y[pickup[i]], Demand[i], twin_pick[i][0], twin_pick[i][1]));
			vertices.get(i+1).type = "Pickup";
		}
		in.close();
		for(int i=0;i<tasks;i++) {
			vertices.add(i+tasks+1, new Vertex(i+tasks+1, x[delivery[i]], y[delivery[i]], -Demand[i], twin_delivery[i][0], twin_delivery[i][1]));
			vertices.get(i+tasks+1).type = "Delivary";
		}
		return vertices;
	}

	ArrayList<Vertex> readDataSet2() {
		File file = new File(file_path);
		try {
			in = new Scanner(file);
		} catch (FileNotFoundException e) {
			Alert("File not found!");
			return null;
		}
		
		Tasks = Integer.parseInt(file.getName().subSequence(4, file.getName().indexOf(".")).toString());
		Capacity = in.nextInt();
		Locations = 2*Tasks;
		in.nextLine();
		ArrayList<Vertex> vertices = new ArrayList<>();
		for(int i=0;i<=Tasks*2;i++) {
			vertices.add(new Vertex(in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt()));
			vertices.get(i).type = vertices.get(i).Request_number == 0 ? "Depot" : vertices.get(i).Request_number <= Tasks ? "Pickup" : "Delivary";
			in.nextLine();
		}
		in.close();
		return vertices;
	}
	
	@FXML
	void browse(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select File");
		File defaultDirectory = new File(System.getProperty("user.dir"));
		fileChooser.setInitialDirectory(defaultDirectory);
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
		Stage stage = (Stage)anchorPane.getScene().getWindow();
		stage.getIcons().add(new Image("file:browse.png"));
		File file = fileChooser.showOpenDialog(stage);
		if(file != null && file.isFile()) {
			if(file.getAbsoluteFile().toString().contains("Dataset2") ||
					file.getAbsoluteFile().toString().contains("Dataset")) {
				file_path = file.getAbsoluteFile().toString();
				fileName.setText(file.getName());
			}
			else {
				Alert("Please Choose a Dataset File");
				return;
			}
			try {
				in = new Scanner(file);
			} catch (FileNotFoundException e) {
				Alert("File not found!");
			}
		}
	}
	
    @FXML
    void displayData(ActionEvent event) {
		try {
			File file = new File(file_path);
			FileInputStream fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();
			String str = new String(data, "UTF-8");
			System.out.println(str);
		} catch (IOException e) {
			Alert("File not found!");
		}
    }

	@FXML
	void displayChart(ActionEvent event) throws IOException {
		AnchorPane show = (AnchorPane)FXMLLoader.load(getClass().getResource("Chart.fxml"));
		FadeTransition ft = new FadeTransition(Duration.millis(200), show);
		ft.setFromValue(0.0);
		ft.setToValue(1.0);
		ft.play();
		Stage stage = new Stage();
		stage.getIcons().add(new Image("file:chart.png"));
		stage.setTitle("Line Chart");
		stage.setScene(new Scene(show, 1200, 700));
		stage.show();
	}

	public static int getCost(int x1, int y1, int x2, int y2) {
		return (int) Math.sqrt(Math.abs((y2-y1)^2) + Math.abs((x2-x1)^2));
	}

	// Recursive Backtracking Search using the Depth First Search Algorithm
	public static ArrayList<Vertex> DepthFirstSearch(ArrayList<Vertex> path, Graph graph) {
		//Check if the Search is done and all nodes has been visited (goal)
		if(path.size() == Locations+1) return path;
		//Check if there's no Solution
		if(path == null || path.size() == 0) return null;
		//Make the nodes available to visit 
		//(All pickup points + points that are ready to get the delivery)
		UpdatePath(path,graph);

		//Select the next node to be visited
		Vertex vertex = null;
		vertex = forward ? useForwardChecking(path,graph) : getNextVertex(path,graph);
		while(vertex == null) {
			for(int i = 0; i < graph.AvailableRequests.size(); i++)
				if(!path.contains(graph.AvailableRequests.get(i)))
					graph.AvailableRequests.get(i).isVisited = false; 
			//Back track and remove the last node from the solution path
			Vertex top = path.get(path.size()-1);
			for(int i=graph.AvailableRequests.indexOf(top); i >= 0; i--)
				graph.AvailableRequests.get(i).isVisited = true;
			path.remove(path.size()-1);
			UpdatePath(path,graph);
			if(path == null || path.size() == 0) return null;
			vertex = forward ? useForwardChecking(path,graph) : getNextVertex(path,graph);
		}
		path.add(vertex);
		UpdatePath(path,graph);
		for(int i = 0; i < graph.AvailableRequests.size(); i++){
			if(!path.contains(graph.AvailableRequests.get(i)))
				graph.AvailableRequests.get(i).isVisited = false;
		}
		try {
			ArrayList<Vertex> result = DepthFirstSearch(path,graph);
			return (result != null) ? result : null;
		} catch (StackOverflowError ex) {
			Alert("Couldn't Find a Solution");
			System.out.println("Couldn't Find a Solution");
			return null;
		}
	}

	public static Vertex getNextVertex(ArrayList<Vertex> path, Graph graph) {
		if(path == null || path.size() == 0) return null;
		//Loop on all Available vertices to get the next location to be visited
		for(int i = 0; i < graph.AvailableRequests.size(); i++) {
			if(!graph.AvailableRequests.get(i).isVisited && !path.contains(graph.AvailableRequests.get(i))) {
				if(checkConstraints(graph.AvailableRequests.get(i),path,graph)) {
					graph.AvailableRequests.get(i).isVisited = true;
					return graph.AvailableRequests.get(i);
				}
			}
		} return null;
	}

	public static boolean flag = true;
	// A successor function to assign a value to an unassigned variable 
	public static Vertex useForwardChecking(ArrayList<Vertex> path, Graph graph) {
		if(path == null || path.size() == 0) return null;
		//initial Values
		int[] TimeRange = {graph.time,graph.time};
		Vertex[] ConstraintRange = {null,null}; 

		//Loop on all Available vertices 
		for(int i = 0; i < graph.AvailableRequests.size(); i++) {
			if((!graph.AvailableRequests.get(i).isVisited) && (!path.contains(graph.AvailableRequests.get(i)))) {
				if(flag == true) {
					if(checkConstraints(graph.AvailableRequests.get(i),path,graph)) {
						graph.AvailableRequests.get(i).isVisited = true;
						flag = !flag;
						return graph.AvailableRequests.get(i);
					}
				}
				//Get the request with the least early time window
				if(graph.AvailableRequests.get(i).Early_Time_Window < TimeRange[0]) {
					ConstraintRange[0] = graph.AvailableRequests.get(i);
					TimeRange[0] = graph.AvailableRequests.get(i).Early_Time_Window;
				}
				//Get the request with the least late time window
				if(graph.AvailableRequests.get(i).Late_Time_Window < TimeRange[1]) {
					ConstraintRange[1] = graph.AvailableRequests.get(i);
					TimeRange[1] = graph.AvailableRequests.get(i).Late_Time_Window;
				}
			}
		}
		if ((ConstraintRange[0] != null && !checkConstraints(ConstraintRange[0],path,graph)) || 
				(ConstraintRange[1] != null && !checkConstraints(ConstraintRange[1],path,graph))) {
			flag = true;
			return null;
		}

		//Return the Request with the least time window
		if(flag == false) {
			if (isValid(ConstraintRange[1],path,graph) && isValid(ConstraintRange[0],path,graph)) {
				if ((ConstraintRange[1].Late_Time_Window - ConstraintRange[1].Early_Time_Window) < (ConstraintRange[0].Late_Time_Window - ConstraintRange[0].Early_Time_Window)) {
					ConstraintRange[1].isVisited = true;
					flag = !flag;
					return ConstraintRange[1];
				} else {
					ConstraintRange[0].isVisited = true;
					flag = !flag;
					return ConstraintRange[0];
				}
			}

			if (isValid(ConstraintRange[1],path,graph)) {
				ConstraintRange[1].isVisited = true;
				flag = true;
				return ConstraintRange[1];
			}
			if (isValid(ConstraintRange[0],path,graph)) {
				ConstraintRange[0].isVisited = true;
				flag = true;
				return ConstraintRange[0];
			}
		}
		return null;
	}
	
	public static boolean isValid(Vertex vertex, ArrayList<Vertex> path, Graph graph) {
		return (vertex != null && checkConstraints(vertex,path,graph));
	}

	// This function checks if the constraints are satisfied
	public static boolean checkConstraints(Vertex vertex, ArrayList<Vertex> path, Graph graph) {
		if(path.size() == 0 || path == null || vertex == null) return false;
		int current_time = 0, current_wait = 0, travel_time = 0, current_capacity = 0;
		
		//The data for each request is stored in order to keep track of the solution 
		for (int i=0; i<path.size()-1; i++) {
			travel_time = getCost(path.get(i).X_coordinate, path.get(i).Y_coordinate, path.get(i + 1).X_coordinate, path.get(i + 1).Y_coordinate);
			current_time += travel_time;
			if (path.get(i+1).Early_Time_Window > current_time) {
				current_wait = path.get(i+1).Early_Time_Window-current_time;
				vertex.waitTime = current_wait;
				vertex.currentTime = current_time;
				current_time += current_wait;
			}
			else {
				vertex.currentTime = current_time;
				vertex.waitTime = 0;
			}
			current_capacity += path.get(i + 1).Demand;
		}
		current_capacity += vertex.Demand;
		if (current_capacity > Capacity)
			return false;

		travel_time = getCost(path.get(path.size() - 1).X_coordinate, path.get(path.size() - 1).Y_coordinate, vertex.X_coordinate, vertex.Y_coordinate);
		current_time += travel_time;
		if (vertex.Early_Time_Window > current_time) {
			current_wait = vertex.Early_Time_Window - current_time;
			vertex.waitTime = current_wait;
			vertex.currentTime = current_time;
			current_time += current_wait;
		}
		else {
			vertex.waitTime = 0;
			vertex.currentTime = current_time;
		}
		vertex.currentCapacity = current_capacity;
		vertex.cost = travel_time;
		
		//Check if all the request are within the constraints
		for(int i = 0; i < graph.AllRequests.size(); i++) {
			if (!path.contains(graph.AllRequests.get(i)))
				if (graph.AllRequests.get(i).Late_Time_Window <= current_time) 
					return false;             
		}
		return true;
	}

	public static void UpdatePath(ArrayList<Vertex> path, Graph graph) {
		graph.AvailableRequests = new ArrayList<Vertex>();
		for( int i = 0; i < graph.AllRequests.size()/2; i++ ) 
			graph.AvailableRequests.add(graph.AllRequests.get(i));
		for (int i = graph.AllRequests.size()/2; i < graph.AllRequests.size(); i++) 
			if(path.contains(graph.AllRequests.get(i - graph.AllRequests.size()/2)))
				graph.AvailableRequests.add(graph.AllRequests.get(i));
	}

	public static void Alert(String message) {
		javafx.scene.control.Alert alert = new Alert(AlertType.ERROR);
		alert.setContentText(message);
		alert.setTitle("Error!");
		alert.setHeaderText(null);
		alert.setResizable(false);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.show();
	}

	@FXML
	void exit(ActionEvent event) {
		System.exit(0);
	}

	@FXML
	void restart(ActionEvent event) throws IOException {
		AnchorPane show = (AnchorPane)FXMLLoader.load(getClass().getResource("Main.fxml"));
		FadeTransition ft = new FadeTransition(Duration.millis(500), show);
		ft.setFromValue(0.0);
		ft.setToValue(1.0);
		ft.play();
		Scene scene = new Scene(show);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		window.setScene(scene);
		window.show();
	}
}
