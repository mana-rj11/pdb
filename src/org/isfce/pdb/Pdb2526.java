package org.isfce.pdb;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Pdb2526 extends Application {

	@Override
	public void start(Stage primaryStage) {
		BorderPane cp = new BorderPane();
		
		// TOP : la barre de titre existante 
		Label titre = new Label("PDB2526");
		titre.getStyleClass().add("titre-label");
		titre.setMaxWidth(Double.MAX_VALUE);
		HBox hb=new HBox(titre);
		hb.setAlignment(Pos.CENTER);
		hb.getStyleClass().add("top-bar");
		HBox.setHgrow(titre, Priority.ALWAYS);
		cp.setTop(hb);
		
		// LEFT : boutons de navigation
		VBox leftMenu = new VBox(10);
		leftMenu.setPadding(new Insets(10));
		leftMenu.setAlignment(Pos.TOP_CENTER);
		
		
		Button btnHBox = new Button("HBox");
		Button btnVBox = new Button("VBox");
		Button btnFlow = new Button("FlowPane");
		Button btnStack = new Button("StackPane");
		Button btnAnchor = new Button("AnchorPane");
		Button btnGrid = new Button("GridPane");
		Button btnTextField = new Button("TextField");
		Button btnAlert = new Button("Alert");
		Button btnExit = new Button("EXIT");
		
		// Dans le menu gauche : ajoute après btnGrid
		
		btnAlert.setPrefWidth(100);
		btnTextField.setPrefWidth(100);
		// leftMenu.getChildren().add(btnAlert);
				
		// action : ajoute avec les autres actions
		btnAlert.setOnAction(_ -> afficherAlert(primaryStage));
		
		
		// largeur uniforme
		for (Button b : new Button[] {btnHBox, btnVBox, btnFlow,
										btnStack, btnAnchor, btnGrid, 
										btnTextField, btnAlert, btnExit})
			b.setPrefWidth(100);
		
		leftMenu.getChildren().addAll(
			btnHBox, btnVBox, btnFlow,
			btnStack, btnAnchor, btnGrid, 
			btnTextField, btnAlert, btnExit
		);
		cp.setLeft(leftMenu);
		
		// CENTER : label par défaut 
		cp.setCenter(new Label("<- Choisissez un conteneur"));
		
		// ACTIONS
		btnHBox.setOnAction(_   -> cp.setCenter(creerHBox()));
		btnVBox.setOnAction(_   -> cp.setCenter(creerVBox()));
		btnFlow.setOnAction(_   -> cp.setCenter(creerFlowPane()));
		btnStack.setOnAction(_  -> cp.setCenter(creerStackPane()));
		btnAnchor.setOnAction(_ -> cp.setCenter(creerAnchorPane()));
		btnGrid.setOnAction(_   -> cp.setCenter(creerGridPane()));
		btnTextField.setOnAction(_ -> cp.setCenter(creerTextFieldNumerique()));
		btnExit.setOnAction(_   -> Platform.exit());
		
		// SCENE
		Scene scene = new Scene(cp);
		scene.getStylesheets().add(
			getClass().getResource("pdb2526.css").toExternalForm()); // getRessource() retourne une URL
		primaryStage.setScene(scene);
		primaryStage.setWidth(600);
		primaryStage.setHeight(450);
		primaryStage.setTitle("Découverte des conteneurs JavaFX");
		primaryStage.show();
		
	}
	
	// HBox
	private HBox creerHBox() {
		HBox hbox = new HBox(10);
		hbox.setPadding(new Insets(10));
		hbox.setAlignment(Pos.CENTER);
		for (int i = 1; i <= 5; i++)
			hbox.getChildren().add(new Button("Btn " + i));
		return hbox;
	}
	
	// VBox 
	private VBox creerVBox() {
		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10));
		vbox.setAlignment(Pos.CENTER);
		for (int i = 1; i <= 5; i++)
			vbox.getChildren().add(new Button("Btn " + i));
		return vbox;
	}
	
	// FlowPlane
	private FlowPane creerFlowPane() {
		FlowPane flow = new FlowPane(10, 10);
		flow.setPadding(new Insets(10));
		for (int i = 1; i <= 10; i++)
			flow.getChildren().add(new Button("Btn " + i));
		return flow;
	}
	
	// StackPane
	private StackPane creerStackPane() {
		StackPane stack = new StackPane();
		Label fond = new Label("FOND");
		fond.setStyle("-fx-background-color: lightblue; -fx-padding: 40;");
		Label dessus = new Label("DESSUS");
		dessus.setStyle(
			"-fx-background-color: rgba(255,100,100,0.7); -fx-padding: 15;");
		stack.getChildren().addAll(fond, dessus);
		return stack;
	}
	
	// AnchorPane
	private AnchorPane creerAnchorPane() {
		AnchorPane anchor = new AnchorPane();
		Button btn = new Button("Accroché aux 4 coins");
		AnchorPane.setTopAnchor(btn, 10.0);
		AnchorPane.setBottomAnchor(btn, 10.0);
		AnchorPane.setLeftAnchor(btn, 10.0);
		AnchorPane.setRightAnchor(btn, 10.0);
		anchor.getChildren().add(btn);
		return anchor;
	}
	
	// GridPane
	private GridPane creerGridPane() {
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10));
		grid.setHgap(5);
		grid.setVgap(5);
		grid.setAlignment(Pos.CENTER);
		String[][] touches = {
				{"7","8","9"},
				{"4","5","6"},
				{"1","2","3"},
				{"0",".","+"}
		};
		for (int row = 0; row < touches.length; row++)
			for (int col = 0; col < touches[row].length; col++) {
				Button btn = new Button(touches[row][col]);
				btn.setPrefSize(60, 40);
				grid.add(btn, col, row);
			}
		return grid;
	}
	
	private VBox creerTextFieldNumerique() {
		VBox vbox = new VBox(15);
		vbox.setPadding(new Insets(20));
		vbox.setAlignment(Pos.CENTER);
		
		Label instruction = new Label("Entrez un nombre :");
		
		// TextField avec filtre numérique 
		TextField tf = new TextField();
		tf.setMaxWidth(150);
		
		// TextFormatter : filtre les caractères non numérique
		tf.setTextFormatter(new TextFormatter<>(change -> {
			String newText = change.getControlNewText();
			if (newText.matches("[0-9]*")) // regew uniquement les chiffres
				return change;			   // accepte le changement 
			return null;				   // refuse le changement
		}));
		
		// label qui affiche la valeur en temps réel
		Label lblValeur = new Label("Valeur : ");
		
		// Listener sur le texte : se déclenche à chaque frappe
		tf.textProperty().addListener((_, _, newVal) ->
			lblValeur.setText("Valeur : " + (newVal.isEmpty() ? "vide" : newVal))
		);
		
		// bouton pour afficher la valeur
		Button btnAfficher = new Button("Afficher");
		btnAfficher.setOnAction(_ -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Valeur saisie");
			alert.setHeaderText(null);
			alert.setContentText("Vous avez saisi : " +
				(tf.getText().isEmpty() ? "rien !" : tf.getText()));
			alert.showAndWait();
		});
		
		vbox.getChildren().addAll(instruction, tf, lblValeur, btnAfficher);
		return vbox;
	}
	
	private void afficherAlert(Stage Owner) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText("Voulez-vous quitter ?");
		alert.setContentText("L'application va se fermer.");
		alert.initOwner(Owner); // lie l'alert à la fenêtre principale
		
		// showAndWait() attends la réponse avant de continuer 
		var result = alert.showAndWait();
		
		// result est un Optionnal<ButtonType>
		result.ifPresent(btn -> {
			if (btn == ButtonType.OK)
				Platform.exit();
		});
	}
	

	public static void main(String[] args) {
		launch(args);
	}
}
