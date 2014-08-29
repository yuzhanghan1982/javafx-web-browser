import javafx.scene.image.Image;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class WebBrowser extends Application {

	private Scene scene;
	private BorderPane root;
	private Button reloadButton, goButton, backButton, forwardButton,
			homeButton, sethomeButton, addbookmarkButton;
	private ComboBox<String> bookmarksComboBox;
	private TextField addressField;
	private WebView webView;
	private WebEngine webEngine;

	private String homeAddress = "www.cs.duke.edu/rcd";
	private ArrayList<String> addresses = new ArrayList<String>();
	private int addressPointer = -1;

	@Override
	public void start(Stage stage) throws Exception {
		System.out.println(Thread.currentThread().getName());

		// The vertical box that will hold two horizontal boxes.
		VBox vBox = new VBox(5);
		vBox.setAlignment(Pos.CENTER);

		// Horizontal boxes that will host buttons and address field.
		HBox hBox0 = new HBox(5);
		hBox0.setAlignment(Pos.CENTER);
		HBox hBox1 = new HBox(5);
		hBox1.setAlignment(Pos.CENTER);

		// Buttons for navigation.
		reloadButton = new Button();
		reloadButton.setGraphic(new ImageView(new Image(getClass()
				.getResourceAsStream("resources/Refresh32.gif"))));
		goButton = new Button("Go");
		backButton = new Button();
		backButton.setGraphic(new ImageView(new Image(getClass()
				.getResourceAsStream("resources/Back32.gif"))));
		forwardButton = new Button();
		forwardButton.setGraphic(new ImageView(new Image(getClass()
				.getResourceAsStream("resources/Forward32.gif"))));
		homeButton = new Button();
		homeButton.setGraphic(new ImageView(new Image(getClass()
				.getResourceAsStream("resources/Home32.gif"))));
		sethomeButton = new Button("Set Home");
		addbookmarkButton = new Button();
		addbookmarkButton.setGraphic(new ImageView(new Image(getClass()
				.getResourceAsStream("resources/Favorite32.gif"))));

		// ComboBox for bookmarks
		bookmarksComboBox = new ComboBox<String>();
		bookmarksComboBox.setMaxWidth(200);
		bookmarksComboBox.setMinWidth(200);
		bookmarksComboBox.setPromptText("Go to bookmark ...");
		bookmarksComboBox.valueProperty().addListener(bookmarks);

		// Add listeners to the buttons.
		reloadButton.setOnAction(reload);
		goButton.setOnAction(go);
		backButton.setOnAction(back);
		forwardButton.setOnAction(forward);
		homeButton.setOnAction(home);
		sethomeButton.setOnAction(sethome);
		addbookmarkButton.setOnAction(addbookmark);

		// The TextField for entering URLs.
		addressField = new TextField("Enter URLs here...");
		addressField.setPrefColumnCount(50);
		addressField.setOnAction(go);

		// Add all out navigation nodes to the hbox.
		hBox0.getChildren().addAll(backButton, forwardButton, homeButton,
				reloadButton, addressField, goButton);
		hBox1.getChildren().addAll(addbookmarkButton, bookmarksComboBox,
				sethomeButton);
		vBox.getChildren().addAll(hBox0, hBox1);

		// WebView that displays the page.
		webView = new WebView();

		// The engine that manages the pages.
		webEngine = webView.getEngine();
		webEngine.setJavaScriptEnabled(true);
		homeAddress = extractAddress(homeAddress);
		webEngine.load("http://" + homeAddress);

		BorderPane root = new BorderPane();
		root.setPrefSize(1024, 768);

		// Add every node into the BorderPane.
		root.setTop(vBox);
		root.setCenter(webView);

		// The scene is where all the actions in JavaFX take place. A scene
		// holds
		// all Nodes, whose root node is the BorderPane.
		scene = new Scene(root);

		// the stage hosts the scene.
		stage.setTitle("Web Browser");
		stage.setScene(scene);

		addressPointer++;
		addresses.add(homeAddress);
		addressField.setText(webEngine.getLocation());
		resetButtons();

		stage.show();
	}

	private EventHandler<ActionEvent> reload = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			webEngine.reload();
			resetButtons();
		}
	};

	private EventHandler<ActionEvent> go = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			String address = addressField.getText();
			address = extractAddress(address);
			System.out.println(address);
			webEngine.load("http://" + address);
			addressField.setText(webEngine.getLocation());

			addressPointer++;
			while (addresses.size() - 1 >= addressPointer) {
				addresses.remove(addresses.size() - 1);
			}
			addresses.add(address);
			resetButtons();

		}
	};

	private EventHandler<ActionEvent> back = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {

			addressPointer--;
			if (addressPointer >= 0) {
				System.out.println(addresses.get(addressPointer));
				webEngine.load("http://" + addresses.get(addressPointer));
				addressField.setText(webEngine.getLocation());
			} else {
				addressPointer = 0;
			}
			resetButtons();
		}
	};

	private EventHandler<ActionEvent> forward = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {

			addressPointer++;
			if (addressPointer <= addresses.size() - 1) {
				System.out.println(addresses.get(addressPointer));
				webEngine.load("http://" + addresses.get(addressPointer));
				addressField.setText(webEngine.getLocation());
			} else {
				addressPointer = addresses.size() - 1;
			}
			resetButtons();
		}
	};

	private EventHandler<ActionEvent> home = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			homeAddress = extractAddress(homeAddress);
			System.out.println(homeAddress);
			webEngine.load("http://" + homeAddress);
			addressField.setText(webEngine.getLocation());

			addressPointer++;
			while (addresses.size() - 1 >= addressPointer) {
				addresses.remove(addresses.size() - 1);
			}
			addresses.add(homeAddress);
			resetButtons();
		}
	};

	private EventHandler<ActionEvent> sethome = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {

			homeAddress = addressField.getText();
			homeAddress = extractAddress(homeAddress);
			System.out.println(homeAddress);
			resetButtons();

		}
	};

	private EventHandler<ActionEvent> addbookmark = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {

			String address = webEngine.getLocation();
			address = extractAddress(address);
			System.out.println(address);
			bookmarksComboBox.getItems().add(address);
		}
	};

	private ChangeListener<String> bookmarks = new ChangeListener<String>() {
		@Override
		public void changed(ObservableValue ov, String t, String t1) {
			webEngine.load("http://" + t1);
			addressField.setText(webEngine.getLocation());

			addressPointer++;
			while (addresses.size() - 1 >= addressPointer) {
				addresses.remove(addresses.size() - 1);
			}
			addresses.add(t1);
			resetButtons();
		}
	};

	private void resetButtons() {
		System.out.println(addresses);
		System.out.println(addressPointer);

		if (addressPointer <= 0)
			backButton.setDisable(true);
		else
			backButton.setDisable(false);

		if (addressPointer >= addresses.size() - 1)
			forwardButton.setDisable(true);
		else
			forwardButton.setDisable(false);
	}

	private String extractAddress(String fullAddress) {
		String result = fullAddress;
		if (fullAddress.startsWith("http://")) {
			result = fullAddress.substring(7);
		}
		return result;
	}

	public static void main(String[] args) {
		launch(args);
	}
}