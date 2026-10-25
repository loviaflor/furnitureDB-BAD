package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application{
	Scene userScene;
	BorderPane bp;
	GridPane gp;
	TableView<Furniture> tableView;
	TableColumn<Furniture, String> idCol;
	TableColumn<Furniture, String> nameCol;
	TableColumn<Furniture, String> typeCol;
	TableColumn<Furniture, Integer> priceCol;
	ObservableList<Furniture> furnitureList;
	Label idLbl, nameLbl, typeLbl, priceLbl;
	TextField idField, nameField, priceField;
	ComboBox<String> typeCb;
	Button addBtn, updateBtn, deleteBtn;
	
	int idIncrement = 0;
	
	public void init() {
		bp = new BorderPane();
		gp = new GridPane();
		userScene = new Scene(bp, 1000, 700);
		
		idLbl = new Label("Furniture id:");
		nameLbl = new Label("Furniture name:");
		typeLbl = new Label("Furniture type:");
		priceLbl = new Label("Furniture price:");
		
		idField = new TextField();
		nameField = new TextField();
		priceField = new TextField();
		
		tableView = new TableView<Furniture>();
		
		idCol = new TableColumn<Furniture, String>("Furniture Id");
		idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
		
		nameCol = new TableColumn<Furniture, String>("Furniture Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		typeCol = new TableColumn<Furniture, String>("Furniture Type");
		typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
		
		priceCol = new TableColumn<Furniture, Integer>("Furniture Price");
		priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
		tableView.getColumns().addAll(idCol, nameCol, typeCol, priceCol);
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		typeCb = new ComboBox<String>();
		typeCb.getItems().addAll("Chair", "Bed", "Table", "Wardrobe");
		typeCb.getSelectionModel().selectFirst();
		
		addBtn = new Button("Add");
		updateBtn = new Button("Update");
		deleteBtn = new Button("Delete");
	}
	
	public void addComponent() {
		bp.setTop(tableView);
		bp.setCenter(gp);
		gp.add(idLbl, 0, 0);
		gp.add(nameLbl, 0, 1);
		gp.add(typeLbl, 0, 2);
		gp.add(priceLbl, 0, 3);
		gp.add(idField, 1, 0);
		gp.add(nameField, 1, 1);
		gp.add(typeCb, 1, 2);
		gp.add(priceField, 1, 3);
		gp.add(addBtn, 0, 4);
		gp.add(updateBtn, 1, 4);
		gp.add(deleteBtn, 2, 4);
		
	}
	
	public void setLayout() {
		gp.setPadding(new Insets(15));
		gp.setVgap(15);
		gp.setHgap(15);
	}
	
	public void loadData() {
		furnitureList = FXCollections.observableArrayList();
		
		try {
			Connection conn = Connect.getInstance().getConnection();
			
			String query = "SELECT * FROM furniture";
			
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			
			while(rs.next()) {
				String id = rs.getString("id");
				String name = rs.getString("name");
				String type = rs.getString("type");
				int price = rs.getInt("price");
				furnitureList.add(new Furniture(id, name, type, price));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		tableView.setItems(furnitureList);
	}
	
	public void eventHandler() {
		addBtn.setOnAction(e->addEvent());
		updateBtn.setOnAction(e->updateEvent());
		deleteBtn.setOnAction(e->deleteEvent());
	}
	
	public void addEvent() {
		String id = idField.getText();
		String nama = nameField.getText();
		String tipe = (String) typeCb.getValue();
		int price = Integer.parseInt(priceField.getText());
		try {
			Connection conn = Connect.getInstance().getConnection();
			String query = "INSERT INTO furniture(id, name, type, price) VALUES (?,?,?,?)";
			
			PreparedStatement prepareStatement = conn.prepareStatement(query);
			prepareStatement.setString(1, id);
			prepareStatement.setString(2, nama);
			prepareStatement.setString(3, tipe);
			prepareStatement.setInt(4, price);
			
			prepareStatement.executeUpdate();
			loadData();
			
			showAlert("Added", "Furniture added", AlertType.CONFIRMATION);
			
		} catch (Exception e) {
			showAlert("Failed", "Failed to add data", AlertType.ERROR);
			e.printStackTrace();
			// TODO: handle exception
		}
		
	}
	
	public void updateEvent() {
		Furniture selectedFurniture = tableView.getSelectionModel().getSelectedItem();
		if(selectedFurniture == null) {
			showAlert("Not Selected", "Choose one to update", AlertType.ERROR);
			return;
		}
		
		String id = selectedFurniture.getId();
		String nama = nameField.getText();
		String type = (String) typeCb.getValue();
		int price = Integer.parseInt(priceField.getText());
		
		try {
			Connection conn = Connect.getInstance().getConnection();
			String query = "UPDATE furniture SET id = ?, name = ?, type =?,  price = ? WHERE id = ?";
			PreparedStatement ps = conn.prepareStatement(query);
			
			ps.setString(1, id);
			ps.setString(2, nama);
			ps.setString(3, type);
			ps.setInt(4, price);
			ps.setString(5, id);
			
			ps.executeUpdate();
			
			loadData();
			
			showAlert("Updated", "Update Successful", AlertType.INFORMATION);
			
			
		} catch (Exception e) {
			showAlert("Error","Data not updated",AlertType.ERROR);
			e.printStackTrace();
			// TODO: handle exception
		}
		
	}
	
	public void deleteEvent() {
		Furniture selectedFr = tableView.getSelectionModel().getSelectedItem();
		
		String id = selectedFr.getId();
		
		try {
			Connection conn = Connect.getInstance().getConnection();
			String query = "DELETE FROM furniture WHERE id = ?";
			
			PreparedStatement ps = conn.prepareStatement(query);
			
			ps.setString(1, id);
			
			ps.executeUpdate();
			loadData();
			showAlert("Deleted", "List deleted", AlertType.CONFIRMATION);
			
		} catch (Exception e) {
			showAlert("Error","Data not deleted",AlertType.ERROR);
			e.printStackTrace();
			// TODO: handle exception
		}
	}
	
	public void showAlert(String title, String content, Alert.AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setContentText(content);
		alert.showAndWait();
	}

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage stage) throws Exception {
		init();
		addComponent();
		setLayout();
		loadData();
		eventHandler();
		stage.setScene(userScene);
		stage.show();
	}

}
