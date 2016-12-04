package WhiteBoard;

import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Whiteboard extends Application
{
	boolean opened = false;
	ObservableList<TableInfo> data = FXCollections.observableArrayList();
	TableColumn<TableInfo, Double> xColumn;
	TableColumn<TableInfo, Double> yColumn;
	TableColumn<TableInfo, Double> widthColumn;
	TableColumn<TableInfo, Double> heightColumn;
	public void start(Stage stage)
	{
		Scene scene = new Scene(new Group());
		stage.setTitle("Whiteoard");
		stage.setHeight(500);
		stage.setWidth(600);
		
		BorderPane pane = new BorderPane();
		
		//	CREATING BUTTONS AND OTHER OBJECTS
		Controller controller = new Controller();
		HBox buttonBox = new HBox();
		VBox vbox = new VBox();
		HBox colorBox = new HBox();
		HBox textInfo = new HBox();
		buttonBox.setSpacing(5);
		buttonBox.setPadding(new Insets(10, 0, 0, 10));
		Text add = new Text("Add:");
		Button addRect = new Button("Rect");
		Button addOval = new Button("Oval");
		Button addLine = new Button("Line");
		Button addText = new Button("Text");
		Button colorPicker = new Button("Color");
		Button doneColorPicker = new Button("Done choosing color");
		ColorPicker cp = new ColorPicker();	//create a new ColorPicker
		TextField textBox = new TextField();
		TableView<TableInfo> table = new TableView();
		table.setPrefWidth(stage.getWidth());
		xColumn = new TableColumn<>("X");
		xColumn.setPrefWidth(stage.getWidth()/4);
		xColumn.setCellValueFactory(new PropertyValueFactory<TableInfo, Double>("x"));
		yColumn = new TableColumn<>("Y");
		yColumn.setPrefWidth(stage.getWidth()/4);
		yColumn.setCellValueFactory(new PropertyValueFactory<TableInfo, Double>("y"));
		widthColumn = new TableColumn<>("Width");
		widthColumn.setPrefWidth(stage.getWidth()/4);
		widthColumn.setCellValueFactory(new PropertyValueFactory<TableInfo, Double>("width"));
		heightColumn = new TableColumn<>("Height");
		heightColumn.setPrefWidth(stage.getWidth()/4);
		heightColumn.setCellValueFactory(new PropertyValueFactory<TableInfo, Double>("height"));
		table.getColumns().addAll(xColumn, yColumn, widthColumn, heightColumn);
		//table.setPadding(new Insets(10, 10, 10, 10));
		
		
		//INITIALIZING OBJECTS
		doneColorPicker.setDisable(true);
        doneColorPicker.setVisible(false);
        cp.setValue(Color.WHITE);				//initialize the color as white
        cp.setMinHeight(30);					//sets the minimum height to make the box bigger
        colorBox.setSpacing(5);
		colorBox.setPadding(new Insets(10, 0, 0, 10));
		textInfo.setSpacing(5);
		textInfo.setPadding(new Insets(10, 0, 0, 10));
       // ObservableList<String> listOfTexts = FXCollections.observableArrayList(,, );
		ArrayList<String> list = new ArrayList<>();
		list.add("Times New Roman");
		list.add("Calibri");
		list.add( "Arial");
        ChoiceBox dropDown = new ChoiceBox();
		dropDown.getItems().addAll(list);
		textBox.setPromptText("Enter text here");
		//rectangle button
		addRect.setOnAction(new EventHandler()
				{
					public void handle(Event event) 
					{
						controller.addRectangle(table);
						
					}
				});
		
		//oval button
		addOval.setOnAction(new EventHandler()
				{
					public void handle(Event e)
					{
						controller.addEllipse(table);
					}
				});
		
		//line button
		addLine.setOnAction(new EventHandler(){
			public void handle(Event e)
			{
				controller.addLine(table);
			}
		});
		
		//text button
		addText.setOnAction(new EventHandler(){
			public void handle(Event e)
			{
				
				//Text t = new Text(textBox.getText());
				//t.setFont(Font.font(font));
				try
				{
					String font = list.get(dropDown.getSelectionModel().getSelectedIndex());
					Font f = new Font(font, 12);
					controller.addText(textBox.getText(), font, table);
				}
				catch(Exception exception)
				{
					System.out.println("Something is wrong. Please check to make sure that you have written something in the text box and have chosen a font");
				}
				//vbox.getChildren().add(t);
			}
		});
        
		//ColorPicker button
		colorPicker.setOnAction(new EventHandler()
				{
					
					public void handle(Event arg0) {
						
							if (!colorBox.getChildren().contains(cp))
							{
					        colorBox.getChildren().addAll(cp);
					        doneColorPicker.setDisable(false);
					        doneColorPicker.setVisible(true);
							}
						
					}
					
				});
		
		
		//done picking color button
		doneColorPicker.setOnAction(new EventHandler()
				{
					public void handle(Event event)
					{
						
							controller.setColor(cp.getValue());
							colorBox.getChildren().removeAll(cp);
							doneColorPicker.setVisible(false);
							doneColorPicker.setDisable(true);
					}
				});
		
		
		
		// FINISHING OFF THE OBJECTS
		buttonBox.getChildren().add(add);
		buttonBox.getChildren().add(addRect);
		buttonBox.getChildren().add(addOval);
		buttonBox.getChildren().add(addLine);
		buttonBox.getChildren().add(addText);
		
		colorBox.getChildren().add(colorPicker);
		colorBox.getChildren().add(doneColorPicker);

		textInfo.getChildren().addAll(textBox, dropDown);
		
		vbox.getChildren().addAll(buttonBox, colorBox, textInfo, table);
		
		pane.setLeft(vbox);
		
		((Group) scene.getRoot()).getChildren().add(pane);
		
		stage.setScene(scene);
		stage.show();
	}
	
	
	
	/*public void updateTable(ArrayList<Object> list, TableView table)
	{
		//table.getItems().addAll(10, 10, 10, 10);
		table.getColumns().clear();
		table.setItems(data);
		table.getColumns().addAll(xColumn, yColumn, widthColumn, heightColumn);
		//table.refresh();
		/*int i = 0;
		for(Object e: list)
		{
			TableColumn x = (TableColumn) table.getColumns().get(0);
			x.getText();
		}
		
	}*/
	

    public static class TableInfo {
        StringProperty value;
        double x;
        double w;
        double y;
        double h;

        TableInfo(String v, double d, double e, double f, double g) {
            this.value = new SimpleStringProperty(v);
            this.x = d;
            this.y = e;
            this.w = f;
            this.h =g;
        }
        public String getValue() {
            return value.get();
        }

        public double getX() {
            return x;
        }
        public void setX(double x)
        {
        	this.x = x;
        }
        public double getY() {
            return y;
        }
        public void setY(double y)
        {
        	this.y = y;
        }
        public double getWidth() {
            return w;
        }
        public void setWidth(double w)
        {
        	this.w = w;
        }
        public double getHeight() {
            return h;
        }
        public void setHeight(double h)
        {
        	this.h = h;
        }
    }
    
	public static void main(String[] args){
		launch(args);
	}
	
}