
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class JavaFXTemplate extends Application{
	private Stage primaryStage;
	private String prevScene, currScene;  
	private HashMap<String, Scene> sceneMap; 
	private EventHandler<ActionEvent> returnButtons, numberButtons;
	private GridPane grid;
	private Button drawButton, randSelectButton;
	private Button spot1, spot2, spot3, spot4;
	private Button draw1, draw2, draw3, draw4;
	private TextField winningText, matchText;
	private Player player;
	//use this for pausing between actions
	private PauseTransition returnPause = new PauseTransition(Duration.seconds(0.25));
	int numberOfDraws = 0;
	
	private Button btn;
	
	//constant values java style
	static final int picHeight = 275;
	static final int picWidth = 250;
	static final int sceneWidth = 900;
	static final int sceneHeight = 650;

	public static void main(String[] args) {
	
		launch(args);
	}
 
	public void start(Stage pStage) throws Exception {
		primaryStage = pStage;
		primaryStage.setTitle("Keno");
		
		player = new Player();
		sceneMap = new HashMap<String,Scene>();
		
		// pause for 0.25 seconds then switch back to previous scene
		returnPause.setOnFinished(e->primaryStage.setScene(sceneMap.get(prevScene)));
		
		// this handler is used by return buttons 
		returnButtons = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				returnPause.play(); //calls setOnFinished
				currScene = prevScene;
			}
		};
		// this handler is used for number select buttons
		numberButtons = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Button b = (Button)event.getSource();
				int num = Integer.parseInt(b.getText());
				if (player.getBetCard().add(num)) {
					if(player.getBetCard().full()) {
						drawButton.setDisable(false);
					}
					b.setStyle("-fx-background-color: yellow;");
				} else if(player.getBetCard().remove(num)) {
					drawButton.setDisable(true);
					b.setStyle("");
				}
			}
		};
		drawButton = new Button("Draw");
		drawButton.setDisable(true);
		drawButton.setMinWidth(100);
		drawButton.setOnAction(e -> {
			if(numberOfDraws == 0) {
				setupDrawings();
			}
			if(numberOfDraws < player.getBetCard().getDrawNumber()) {
				setSelectionGrid();
				drawButton.setDisable(true);
				HashSet<Integer> selection = player.getBetCard().getNumbers();
				HashSet<Integer> drawingResults = getDrawingResults();
				HashSet<Integer> matches = getIntersection(selection, drawingResults);
				matchText.setText("You match the numbers: ");
				winningText.setText("");
				animateDrawing(drawingResults, matches);
				numberOfDraws++;
				if(numberOfDraws == player.getBetCard().getDrawNumber()) {
					drawButton.setText("Play Again");
				}
			} else {
				setNewBetCard();
				numberOfDraws = 0;
			}
		});
		randSelectButton = new Button("Random Select");
		randSelectButton.setMinWidth(100);
		randSelectButton.setOnAction(e -> randomSelect());
		randSelectButton.setDisable(true);
		//All the scenes returned from their respective methods; put in hashmap
		sceneMap.put("WelcomeScene", createWelcomeScene());
		sceneMap.put("RulesScene", createRulesScene());
		sceneMap.put("OddsScene", createOddsScene());
		sceneMap.put("ExitScene", createExitScene());
		sceneMap.put("SelectionScene", createSelectionScene());
		
		primaryStage.setScene(sceneMap.get("WelcomeScene"));
		currScene = "WelcomeScene";
		primaryStage.show();
	}
	
	public void makeInitialMenuBar(MenuBar menu) {
		Menu rulesMenu = new Menu("Rules");
		Menu oddsMenu = new Menu("Odds");
		Menu exitMenu = new Menu("Exit");
		
		MenuItem rules = new MenuItem("Rules");
		MenuItem odds = new MenuItem("Odds");
		MenuItem exit = new MenuItem("Exit");
		
		PauseTransition p = new PauseTransition(Duration.seconds(0.25));
		p.setOnFinished(e->primaryStage.setScene(sceneMap.get(currScene)));
		
		rules.setOnAction(e-> {
			prevScene = currScene;
			currScene = "RulesScene";
			p.play();
		});
		odds.setOnAction(e-> {
			prevScene = currScene;
			currScene = "OddsScene";
			p.play();
		});
		exit.setOnAction(e-> {
			prevScene = currScene;
			currScene = "ExitScene";
			p.play();
		});
		
		rulesMenu.getItems().add(rules);
		oddsMenu.getItems().add(odds);
		exitMenu.getItems().add(exit);
		
		menu.getMenus().addAll(rulesMenu, oddsMenu, exitMenu);
		menu.setStyle("-fx-font-size: 18;"+"-fx-border-size: 20;"+ 
				"-fx-border-color: black;");
	}
	
	public void makeUpgradedMenuBar(MenuBar menu) {
		makeInitialMenuBar(menu);
		Menu newLookMenu = new Menu("New Look");
		MenuItem newLook = new MenuItem("New Look");
		newLook.setOnAction(e -> {
			for(Scene s : sceneMap.values()) {
				Parent n = s.getRoot();
				n.setStyle("-fx-background-color: linear-gradient(to left, #61D8DE, #E839F6);");
			}
		});
		newLookMenu.getItems().add(newLook);
		menu.getMenus().add(2, newLookMenu);
	}
	
	public void populateGrid() {
		grid = new GridPane();
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 10; col++) {
				Button b = new Button(Integer.toString(row*10 + col + 1));
				b.setOnAction(numberButtons);
				b.setDisable(true);
				b.setMinWidth(30);
				grid.add(b, col, row);
			}
		}
	}
	
	public void resetGrid() {
		for(Node n : grid.getChildren()) {
			Button b = (Button) n;
			b.setStyle("");
		}
	}
	public void enableGrid(boolean b) {
		for(Node n : grid.getChildren()) {
			Button btn = (Button) n;
			btn.setDisable(!b);
		}
	}
	
	public Button getGridButton(int num) {
	    ObservableList<Node> children = grid.getChildren();
	    return (Button) children.get(num - 1);
	}
	
	public void setSelectionGrid() {
		for(Node n : grid.getChildren()) {
			Button b = (Button) n;
			if(player.getBetCard().contains(Integer.parseInt(b.getText()))) {
				b.setStyle("-fx-background-color: yellow;");
			} else {
				b.setStyle("");
			}
		}
	}
	
	public void setupDrawings() {
		draw1.setDisable(true);
		draw2.setDisable(true);
		draw3.setDisable(true);
		draw4.setDisable(true);
		draw1.setStyle("");
		draw2.setStyle("");
		draw3.setStyle("");
		draw4.setStyle("");
		spot1.setDisable(true);
		spot2.setDisable(true);
		spot3.setDisable(true);
		spot4.setDisable(true);
		spot1.setStyle("");
		spot2.setStyle("");
		spot3.setStyle("");
		spot4.setStyle("");
		grid.setMouseTransparent(true);
		grid.setFocusTraversable(false);
		randSelectButton.setDisable(true);
		winningText.setVisible(true);
		matchText.setVisible(true);
		drawButton.setText("Draw Again");
	}
	
	public void setNewBetCard() {
		draw1.setDisable(false);
		draw2.setDisable(false);
		draw3.setDisable(false);
		draw4.setDisable(false);
		draw1.setStyle("-fx-background-color: yellow;");
		spot1.setDisable(false);
		spot2.setDisable(false);
		spot3.setDisable(false);
		spot4.setDisable(false);
		grid.setMouseTransparent(false);
		grid.setFocusTraversable(true);
		resetGrid();
		enableGrid(false);
		winningText.setVisible(false);
		matchText.setVisible(false);
		player.getBetCard().reset();
		drawButton.setText("Draw");
		drawButton.setDisable(true);
	}
	
	public void animateDrawing(HashSet<Integer> drawingResults, HashSet<Integer> matches) {
		Iterator<Integer> it = drawingResults.iterator();
		PauseTransition p = new PauseTransition(Duration.seconds(0.25));
		p.setOnFinished(e -> {
			if(it.hasNext()) {
				int num = it.next();
				btn = getGridButton(num);
				if(matches.contains(num)) {
					btn.setStyle("-fx-background-color: gold;");
					matchText.appendText(num + ", ");
				} else {
					btn.setStyle("-fx-background-color: snow;");
				}
				p.play();
			} else {
				int winnings = getWinnings(matches);
				player.addEarnings(winnings);
				String wintxt = "Your Winnings: " + winnings + "   Total Winnings: " + player.getEarnings();
				winningText.setText(wintxt);
				drawButton.setDisable(false);
			}
		});
		p.play();
	}
	
	public void randomSelect() {
		int num = 0;
		player.getBetCard().clearNumbers();
		resetGrid();
		while(!player.getBetCard().full()) {
			num = (int) (Math.random()*(80)) + 1;
			getGridButton(num).setStyle("-fx-background-color: yellow;");
			player.getBetCard().add(num);
		}
		drawButton.setDisable(false);
	}
	
	public HashSet<Integer> getDrawingResults() {
		HashSet<Integer> drawings = new HashSet<>();
		int maxDraw = 20;
		int i = 0;
		int num = 0;
		while(i < maxDraw) {
			num = (int) (Math.random()*(80)) + 1;
			if(drawings.add(num)) i++;
		}
		return drawings;
	}
	
	public HashSet<Integer> getIntersection(HashSet<Integer> s1, HashSet<Integer> s2) {
		HashSet<Integer> intersection = new HashSet<>(s1);
		intersection.retainAll(s2);
		return intersection;
	}
	
	public int getWinnings(HashSet<Integer> matches) {
		int spotNumber = player.getBetCard().getSpots();
		int winnings = 0;
		int matchCount = matches.size();
		switch(spotNumber) {
			case 1: if(matchCount == 1) { winnings = 2;}
					break;
			case 4: if(matchCount == 2) { winnings = 1; }
					else if(matchCount == 3) { winnings = 5; }
					else if(matchCount == 4) { winnings = 75; }
					break;
			case 8: if(matchCount == 4) { winnings = 2; }
					else if(matchCount == 5) { winnings = 12; }
					else if(matchCount == 6) { winnings = 50; }
					else if(matchCount == 7) { winnings = 750; }
					else if(matchCount == 8) { winnings = 10000; } 
					break;
			case 10: if(matchCount == 0) { winnings = 5; }
					else if(matchCount == 5) { winnings = 2; } 
					else if(matchCount == 6) { winnings = 15; }
					else if(matchCount == 7) { winnings = 40; }
					else if(matchCount == 8) { winnings = 450; }
					else if(matchCount == 9) { winnings = 4250; }
					else if(matchCount == 10) { winnings = 100000; }
					break;
		}
		return winnings;
	}
	
	public HBox createDrawButtons() {
		draw1 = new Button("1");
		draw2 = new Button("2");
		draw3 = new Button("3");
		draw4 = new Button("4");
		EventHandler<ActionEvent> drawButtons = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Button b = (Button)event.getSource();
				int num = Integer.parseInt(b.getText());
				int prevDraw = player.getBetCard().getDrawNumber();
				if(prevDraw != num) {
					switch(prevDraw) {
						case 1: draw1.setStyle("");
								break;
						case 2: draw2.setStyle("");
								break;
						case 3: draw3.setStyle("");
								break;
						case 4: draw4.setStyle("");
								break;
						default: break;
					}
					player.getBetCard().setDrawNumber(num);
					b.setStyle("-fx-background-color: yellow;");
				} 
			}
		};
		draw1.setOnAction(drawButtons);
		draw2.setOnAction(drawButtons);
		draw3.setOnAction(drawButtons);
		draw4.setOnAction(drawButtons);
		draw1.setMinWidth(50);
		draw2.setMinWidth(50);
		draw3.setMinWidth(50);
		draw4.setMinWidth(50);
		draw1.setStyle("-fx-background-color: yellow;");
		return new HBox(20, draw1, draw2, draw3, draw4);
	}
	
	public HBox createSpotsButtons() {
		spot1 = new Button("1");
		spot2 = new Button("4");
		spot3 = new Button("8");
		spot4 = new Button("10");
		EventHandler<ActionEvent> spotButtons = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Button b = (Button)event.getSource();
				int num = Integer.parseInt(b.getText());
				int prevSpot = player.getBetCard().getSpots();
				if(prevSpot != num) {
					switch(prevSpot) {
						case 1: spot1.setStyle("");
								break;
						case 4: spot2.setStyle("");
								break;
						case 8: spot3.setStyle("");
								break;
						case 10: spot4.setStyle("");
								break;
						default: enableGrid(true);
								randSelectButton.setDisable(false);
								break;
					}
					drawButton.setDisable(true);
					if(num < player.getBetCard().getNumberCount()) {
						resetGrid();
						player.getBetCard().clearNumbers();
					} else if(num == player.getBetCard().getNumberCount()) {
						drawButton.setDisable(false);
					}
					player.getBetCard().setSpots(num);
					b.setStyle("-fx-background-color: yellow;");
				} else {
					player.getBetCard().setSpots(0);
					b.setStyle("");
					randSelectButton.setDisable(true);
					drawButton.setDisable(true);
					enableGrid(false);
				}
			}
		};
		spot1.setOnAction(spotButtons);
		spot2.setOnAction(spotButtons);
		spot3.setOnAction(spotButtons);
		spot4.setOnAction(spotButtons);
		spot1.setMinWidth(50);
		spot2.setMinWidth(50);
		spot3.setMinWidth(50);
		spot4.setMinWidth(50);
		return new HBox(20, spot1, spot2, spot3, spot4);
	}
	
	//method to creates welcome scene with controls
	public Scene createWelcomeScene() {
		BorderPane pane = new BorderPane();
		MenuBar menu = new MenuBar();
		makeInitialMenuBar(menu);
		
		Image pic = new Image("Keno-Logo.png");
		ImageView v = new ImageView(pic);
		v.setFitHeight(picHeight);
		v.setFitWidth(picWidth);
		v.setPreserveRatio(true);
		
		Button startButton = new Button("Start");
		startButton.setStyle("-fx-font-size: 30;"+"-fx-border-size: 20;"+ 
				"-fx-border-color: red;");
		startButton.setOnAction(e -> {
			currScene = "SelectionScene";
			PauseTransition p = new PauseTransition(Duration.seconds(0.25));
			p.setOnFinished(f->primaryStage.setScene(sceneMap.get(currScene)));
			p.play();
		});
		
		VBox paneCenter = new VBox(50,v,startButton);
		paneCenter.setAlignment(Pos.CENTER);
		
		pane.setTop(menu);
		pane.setCenter(paneCenter);
		pane.setStyle("-fx-background-color: deepskyblue;");
		
		return new Scene(pane, sceneWidth, sceneHeight);
	}
	
	public Scene createSelectionScene() {
		BorderPane pane = new BorderPane();
		MenuBar menu = new MenuBar();
		makeUpgradedMenuBar(menu);
		
		populateGrid();
		
		TextField drawText = new TextField("How many consecutive games do you want to play?");
		drawText.setStyle("-fx-font-size: 12;-fx-background-color: inherit;");
		drawText.setEditable(false);
		drawText.setMouseTransparent(true);
		drawText.setFocusTraversable(false);
		drawText.setAlignment(Pos.CENTER);
		
		HBox draws = createDrawButtons();
		draws.setAlignment(Pos.CENTER);
		
		TextField spotText = new TextField("Pick the number of spots you wish to play.");
		spotText.setStyle("-fx-font-size: 12;-fx-background-color: inherit;");
		spotText.setEditable(false);
		spotText.setMouseTransparent(true);
		spotText.setFocusTraversable(false);
		spotText.setAlignment(Pos.CENTER);
		
		HBox spots = createSpotsButtons();
		spots.setAlignment(Pos.CENTER);
		
		matchText = new TextField();
		matchText.setStyle("-fx-font-size: 16;-fx-background-color: inherit;");
		matchText.setEditable(false);
		matchText.setMouseTransparent(true);
		matchText.setFocusTraversable(false);
		matchText.setVisible(false);
		matchText.setAlignment(Pos.CENTER);
		
		winningText = new TextField();
		winningText.setStyle("-fx-font-size: 16;-fx-background-color: inherit;");
		winningText.setEditable(false);
		winningText.setMouseTransparent(true);
		winningText.setFocusTraversable(false);
		winningText.setVisible(false);
		winningText.setAlignment(Pos.CENTER);
		
		Button space = new Button();
		space.setMinWidth(100);
		space.setVisible(false);
		HBox gridBox =  new HBox(10, randSelectButton, grid, space);
		gridBox.setAlignment(Pos.TOP_CENTER);
		
		VBox paneCenter = new VBox(20, drawText, draws, spotText, spots, gridBox,
								   drawButton, matchText, winningText);
		paneCenter.setAlignment(Pos.CENTER);
		
		pane.setTop(menu);
		pane.setCenter(paneCenter);
		pane.setStyle("-fx-background-color: white");
		return new Scene(pane, sceneWidth, sceneHeight);
	}
	
	public Scene createRulesScene() {
		BorderPane pane = new BorderPane();
		Button returnButton = new Button("Return");
		returnButton.setOnAction(returnButtons);
		returnButton.setStyle("-fx-font-size: 16;"+"-fx-border-size: 20;"+ 
				"-fx-border-color: black;");
		
		TextArea rulesText = new TextArea();
		rulesText.setText("1. Select how many consecutive draws to play. Pick between 1 and 4.\n"
						+ "2. Select how many spots to match from (1, 4, 8, or 10). The number of Spots\n    you choose will determine the amount you could win. See the odds chart to\n    determine the amount you could win with a $1 play.\n"
						+ "3. Pick as many numbers as you did Spots. You can select numbers from 1 to 80\n    or choose random select and let the computer randomly the numbers for you.\n"
						+ "4. Begin the draw and watch the 20 random numbers be picked. See what you\n    matched and be rewarded your prize.");
		rulesText.setStyle("-fx-font-size: 18;-fx-background-color: inherit;");
		
		pane.setLeft(returnButton);
		pane.setCenter(rulesText);
		pane.setStyle("-fx-background-color: white;");
		
		return new Scene(pane, sceneWidth, sceneHeight);
	}
	
	public Scene createOddsScene() {
		double factor = 0.85;
		BorderPane pane = new BorderPane();
		Button returnButton = new Button("Return");
		returnButton.setOnAction(returnButtons);
		returnButton.setStyle("-fx-font-size: 16;"+"-fx-border-size: 20;"+ 
				"-fx-border-color: black;");
		Pane leftPane = new Pane();
		leftPane.getChildren().add(returnButton);
		leftPane.setPrefWidth(100);
		
		Image pic1 = new Image("10-Spot.png");
		ImageView v1 = new ImageView(pic1);
		v1.setFitHeight(picHeight*factor);
		v1.setFitWidth(picWidth*factor);
		v1.setPreserveRatio(true);
		
		Image pic2 = new Image("8-Spot.png");
		ImageView v2 = new ImageView(pic2);
		v2.setFitHeight(picHeight*factor);
		v2.setFitWidth(picWidth*factor);
		v2.setPreserveRatio(true);

		Image pic3 = new Image("4-Spot.png");
		ImageView v3 = new ImageView(pic3);
		v3.setFitHeight(picHeight*factor);
		v3.setFitWidth(picWidth*factor);
		v3.setPreserveRatio(true);
		
		Image pic4 = new Image("1-Spot.png");
		ImageView v4 = new ImageView(pic4);
		v4.setFitHeight(picHeight*factor);
		v4.setFitWidth(picWidth*factor);
		v4.setPreserveRatio(true);
		VBox paneCenter = new VBox(v1, v2, v3, v4);
		paneCenter.setPadding(new Insets(0,0,0,sceneWidth/2-leftPane.getPrefWidth()-picWidth*factor/2));
		
		pane.setCenter(paneCenter);
		pane.setLeft(leftPane);
		pane.setStyle("-fx-background-color: white;");
		
		return new Scene(pane, sceneWidth, sceneHeight);
	}
	
	public Scene createExitScene() {
		BorderPane pane = new BorderPane();
		
		TextField text = new TextField("Would you like to leave?");
		text.setStyle("-fx-font-size: 30;-fx-background-color: inherit;");
		text.setEditable(false);
		text.setMouseTransparent(true);
		text.setFocusTraversable(false);
		text.setAlignment(Pos.CENTER);
		
		Button yesButton = new Button("Yes");
		Button noButton = new Button("No");
		yesButton.setOnAction(e->System.exit(0));
		noButton.setOnAction(returnButtons);
		yesButton.setStyle("-fx-font-size: 30;"+"-fx-border-size: 20;"+ 
				"-fx-border-color: black;");
		noButton.setStyle("-fx-font-size: 30;"+"-fx-border-size: 20;"+ 
				"-fx-border-color: black;");
		
		VBox paneCenter = new VBox(30, text, yesButton, noButton);
		paneCenter.setAlignment(Pos.CENTER);
		pane.setCenter(paneCenter);
		pane.setStyle("-fx-background-color: white;");
		
		return new Scene(pane, sceneWidth, sceneHeight);
	}
}

