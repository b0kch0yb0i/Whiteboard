package WhiteBoard;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import javafx.scene.shape.Line;
import java.util.List;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Whiteboard extends Application 
{
	Menu file;
	MenuBar menu;
	MenuItem open;
	MenuItem save;
	MenuItem savePNG;
	double orgSceneX, orgSceneY;
	double orgTranslateX, orgTranslateY;
	BorderPane pane;
	boolean drag;
	boolean resize;	
	boolean clicked;
	boolean dragLine;
	Rectangle selectedKnob;
	boolean opened = false;
	ObservableList<TableInfo> data = FXCollections.observableArrayList();
	TableColumn<TableInfo, Double> xColumn;
	TableColumn<TableInfo, Double> yColumn;
	TableColumn<TableInfo, Double> widthColumn;
	TableColumn<TableInfo, Double> heightColumn;
	TableColumn<TableInfo, SimpleStringProperty> nameColumn;
	DShape focusedObject = null;
	Canvas canvas = null;
	Rectangle[] knobs = null;
	public int port;
	boolean isServer;
	VBox vbox = null; //Saaj's change
	ConnectionStuff server;
	
	public Whiteboard(int port, boolean isServer)
	{
		this.port = port;
		this.isServer = isServer;
		drag = false;
		resize = false;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void start(Stage stage)
	{
		Scene scene = new Scene(new Group());
		stage.setTitle("Whiteboard");
		stage.setHeight(610);
		stage.setWidth(1100);
		
		Stage opener = new Stage();
		opener.setTitle("File Explorer");
		
		BorderPane pane = new BorderPane();
		TableView<TableInfo> table = new TableView();
		canvas = new Canvas(table);
		file = new Menu("FILE");
		menu = new MenuBar();
		open = new MenuItem("OPEN");
		save  = new MenuItem("SAVE"); 
		savePNG = new MenuItem("Save as PNG");
		menu.getMenus().add(file);
		file.getItems().addAll(open,save, savePNG);
		
		
		//	CREATING BUTTONS AND OTHER OBJECTS
		
		HBox buttonBox = new HBox();
		vbox = new VBox();
		HBox colorBox = new HBox();
		HBox textInfo = new HBox();
		
		HBox objectInfo = new HBox();  //saaj's code
		
		buttonBox.setSpacing(5);
		buttonBox.setPadding(new Insets(10, 0, 0, 10));
		
		Text add = new Text("Add:");
		Button addRect = new Button("Rect");
		Button addOval = new Button("Oval");
		Button addLine = new Button("Line");
		Button addText = new Button("Text");
		Button removeShape = new Button("Remove this object"); //Saaj's code		
		Button moveToFront = new Button("Move to Front"); //Saaj's code		
		Button moveToBack = new Button("Move to Back"); //Saaj's code	
		Button changeText = new Button("Change Text");
		Button colorPicker = new Button("Color");
		Button doneColorPicker = new Button("Done choosing color");
		//Button save = new Button("Save");
		//Button open = new Button("Open");
		ColorPicker cp = new ColorPicker();	//create a new ColorPicker
	
		TextField textBox = new TextField();
		textBox.setDisable(true);
		textBox.setVisible(false);
		table.setPrefWidth(stage.getWidth());
		table.setPrefHeight(365);  //saaj's code
		FileChooser fileChooser = new FileChooser();
		//fileChooser.

		/*
		nameColumn = new TableColumn<>("Name");
		nameColumn.setPrefWidth(stage.getWidth()/10);
		nameColumn.setCellValueFactory(new PropertyValueFactory<TableInfo, SimpleStringProperty>("name"));
		 */
		
		xColumn = new TableColumn<>("X");
		xColumn.setPrefWidth(stage.getWidth()/8.05); //saaj's code
		xColumn.setCellValueFactory(new PropertyValueFactory<TableInfo, Double>("x"));
		
		yColumn = new TableColumn<>("Y");
		yColumn.setPrefWidth(stage.getWidth()/8.05);
		yColumn.setCellValueFactory(new PropertyValueFactory<TableInfo, Double>("y"));
		
		widthColumn = new TableColumn<>("Width");
		widthColumn.setPrefWidth(stage.getWidth()/8.05);
		widthColumn.setCellValueFactory(new PropertyValueFactory<TableInfo, Double>("width"));
		
		heightColumn = new TableColumn<>("Height");
		heightColumn.setPrefWidth(stage.getWidth()/8.05);
		heightColumn.setCellValueFactory(new PropertyValueFactory<TableInfo, Double>("height"));
		
		table.getColumns().addAll(xColumn, yColumn, widthColumn, heightColumn);
		table.setMaxWidth(stage.getWidth()/2);
		//table.setPadding(new Insets(10, 10, 10, 10));
		
		
		//Server Set Up
		//###########################################################################################
		if(isServer)
		{
			server = new ConnectionStuff("server", port + "", canvas);
			//server.start();
			//ServerClass server = new ServerClass(canvas, table, controller);
		}		
		//#############################################################################################
		
		/*if(server != null)
		{
			server.setController(controller);
		}
		*/
			
		
		//INITIALIZING OBJECTS
		
		changeText.setVisible(false);
		changeText.setDisable(true);
		objectInfo.setSpacing(5);		
		objectInfo.setPadding(new Insets(10, 10, 10, 10)); //saaj's code		
		objectInfo.getChildren().addAll(removeShape, moveToFront, moveToBack); //saaj's code		
		hideButtons(removeShape, moveToFront, moveToBack);  //Saaj's code
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
		List<String> fontList = javafx.scene.text.Font.getFamilies();
		for(String f: fontList)
		{
			list.add(f);
		}
		
        ChoiceBox dropDown = new ChoiceBox();
        dropDown.setVisible(false);
        dropDown.setDisable(true);
		dropDown.getItems().addAll(list);
		textBox.setPromptText("Enter text here");
		
		//open Button
		open.setOnAction(new EventHandler(){
			public void handle(Event e)
			{
				File input = fileChooser.showOpenDialog(opener);
				
				try
				{
					InputStream input1 = new FileInputStream(input);
					XMLDecoder xml = new XMLDecoder(input1);
					DShapeModel[] shapes = (DShapeModel[]) xml.readObject();
					canvas.getList().clear();
					for(DShapeModel d: shapes)
					{
						canvas.addShape(d);
						/*if(d.getType().equals("rectangle"))
						{
							controller.getObjects().add(0, new DRect((DRectModel) d));
						}
						else if(d.getType().equals("oval"))
						{
							controller.getObjects().add(0, new DOval((DOvalModel) d));
						}
						else if(d.getType().equals("line"))
						{
							controller.getObjects().add(0, new DLine((DLineModel) d));
						}
						else if(d.getType().equals("text"))
						{
							controller.getObjects().add(0, new DText((DTextModel) d));
						}*/
					}
					input1.close();
					//canvas.draw(controller.getObjects(), table, server);
				}
				catch(Exception exception)
				{
					System.out.println("Can not find file " + exception);
				}
			}
		});
	
		//save button
		save.setOnAction(new EventHandler(){
			public void handle(Event e)
			{ 
				
				File input = fileChooser.showSaveDialog(opener);
				
				try 
				{
					XMLEncoder xml = new XMLEncoder( new BufferedOutputStream(new FileOutputStream(input)));
					ArrayList<DShape> x = canvas.getList();
					DShapeModel[] shapes = new DShapeModel[x.size()];
					int index = 0;
					for(int i = x.size() - 1; i >= 0; i--)
					{
						shapes[index] = x.get(i).getModel();
						index++;
					}
					xml.writeObject(shapes);
					xml.close();
					System.out.println("File has been created");
				} 
				catch (Exception e1) 
				{
					System.out.println("File System did not work");
				}
			}
		});
		
		savePNG.setOnAction(new EventHandler(){

			@Override
			public void handle(Event event) {

				FileChooser fileChooser = new FileChooser();
				fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png files (*.png)", "*.png"));
				
				File file = fileChooser.showSaveDialog(null);
				if(file != null){
					try{
						WritableImage png = canvas.snapshot(new SnapshotParameters(), null);
						//snapshot(null, png);
						RenderedImage rendered = SwingFXUtils.fromFXImage(png, null);
						ImageIO.write(rendered, "png", file);
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			
		});
		
		//rectangle button		
		addRect.setOnAction(new EventHandler()
				{
					public void handle(Event event) 
					{
						DRectModel model = new DRectModel();
						canvas.addShape(model);
						canvas.updateServer(server, canvas.getListAsArray(), "add", 0);//, model.getColor());
						//DRect rect = new DRect(model);
						//controller.addRectangle( rect);	
						
						/*if(isServer)
						{
							 doSend(model);
						}*/
					}
				});
		
		//oval button
		addOval.setOnAction(new EventHandler()
				{
					public void handle(Event e)
					{
						DOvalModel model = new DOvalModel();
						canvas.addShape(model);
						canvas.updateServer(server, canvas.getListAsArray(), "add", 0);
						//DOval oval = new DOval(model);
						//controller.addEllipse( oval);
						
						/*if (isServer)
						{
							 doSend(model);
						}*/
					}
				});
		
		//line button
		addLine.setOnAction(new EventHandler(){
			public void handle(Event e)
			{
				DLineModel model = new DLineModel(canvas);
				canvas.addShape(model);
				canvas.updateServer(server, canvas.getListAsArray(), "add", 0);
				//DLine line = new DLine(model);
				//controller.addLine(line);
			}
		});
		
		//text button
		addText.setOnAction(new EventHandler(){
			public void handle(Event e)
			{
				//try	{
				DTextModel model = new DTextModel();
				canvas.addShape(model);
				canvas.updateServer(server, canvas.getListAsArray(), "add", 0);
				//DText text = new DText(model);
				//controller.addText( text); //Saaj's code
				
				/*if(isServer)
				{
					 doSend(model);
				}	*/
			}
				//catch(Exception exception){
					//System.out.println("Something is wrong. Please check to make sure that you have written something in the text box and have chosen a font");
				//}
				//vbox.getChildren().add(t);
			
		});

		//ColorPicker button
		colorPicker.setOnAction(new EventHandler()
				{
					
					public void handle(Event arg0) {
						
							if (!colorBox.getChildren().contains(cp))
							{
								if(focusedObject!= null)
								{
									colorBox.getChildren().add(cp);  //Saaj's code		
									cp.setValue(focusedObject.getModel().getColor()); //Saaj's Code		
									doneColorPicker.setDisable(false); //Saaj's Code		
									doneColorPicker.setVisible(true); //Saaj's Code	
								}
						}		
						else //Saaj's Code		
						{		
							System.out.println("Please choose an object to set the the color to"); //Saaj's Code		
						}		
						
						}
						
					}
					
				);
		
		
		changeText.setOnAction(new EventHandler(){
			@Override
			public void handle(Event arg0) {
				try{
					if(isServer){
						canvas.updateServer(server, canvas.getListAsArray(), "change text", 0);
					}
					String f = list.get(dropDown.getSelectionModel().getSelectedIndex());
					Font font = new Font(f, 12);
					((DText) focusedObject).getModel().setText(textBox.getText());
					((DText) focusedObject).getModel().setFont(font);
					canvas.changeText((DText)focusedObject, font, textBox);
					
					
					
				}
				catch(Exception e){
					System.out.println("Error, fix this");
				}
				
			}
			
		});
		
		
		//done picking color button
		
		doneColorPicker.setOnAction(new EventHandler()
				{
					public void handle(Event event)
					{
						//canvas.draw();//server);

						canvas.setColor(cp.getValue(), focusedObject);
						if(isServer)
						{
							int index = canvas.getIndexOfObject(focusedObject);
							canvas.updateServer(server, canvas.getListAsArray(), "color", index);//, cp.getValue());
						}
//						System.out.println("Color of model in whiteboard: " + focusedObject.getModel().getColor());
						colorBox.getChildren().removeAll(cp);
						doneColorPicker.setVisible(false);
						doneColorPicker.setDisable(true);
					}
				});
		
		//Adding in the clicking component
		
		
		removeShape.setOnAction(new EventHandler(){
			
			@Override
			public void handle(Event event) 
			{
				//System.out.println(focusedObject);
				//canvas.getChildren().remove(focusedObject);
				//canvas.draw();//server);
				if(isServer)
				{
					int index = canvas.getIndexOfObject(focusedObject);
					canvas.updateServer(server, canvas.getListAsArray(), "remove", index);
				}
				canvas.removeObject(focusedObject);
				hideButtons(removeShape, moveToFront, moveToBack);
				//focusedObject = null;
				//knobs = null;
			}
		});
		
		moveToBack.setOnAction(new EventHandler(){
			public void handle(Event e)
			{
				canvas.move2Back(focusedObject);
				//canvas.draw();//server);
				if(isServer)
				{
					int index = canvas.getIndexOfObject(focusedObject);
					canvas.updateServer(server, canvas.getListAsArray(), "move to back", index);
				}
				makeUnfocused();
				hideButtons(removeShape, moveToFront, moveToBack);
				
			}
		});
				
		moveToFront.setOnAction(new EventHandler(){
			public void handle(Event e)
			{
				canvas.move2Front(focusedObject);
				//canvas.draw();//server);
				if(isServer)
				{
					int index = canvas.getIndexOfObject(focusedObject);
					canvas.updateServer(server, canvas.getListAsArray(), "move to front", index);
				}
				makeUnfocused();
				hideButtons(removeShape, moveToFront, moveToBack);
			}
		});
		
		
		/*  Another Way to drag a object
		canvas.setOnDragDetected(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent e)
			{
				 Dragboard db = canvas.startDragAndDrop(TransferMode.ANY);
				 ClipboardContent clip = new ClipboardContent();
				// clip.put
				// db.setContent(content)
				 System.out.println("Drag board");
			}
		});
		*/
		
		canvas.setOnMousePressed(new EventHandler<MouseEvent>()
		{

			public void handle(MouseEvent event)
			{
				
				if(!clicked)
				{
					if(focusedObject!= null)
					{
						for(int i = 0; i < knobs.length; i++)
						{
						Rectangle knob = knobs[i];
							if(event.getX()>knob.getX() && event.getX()< knob.getX() + knob.getWidth() && event.getY()>knob.getY() && event.getY()< knob.getY() + knob.getHeight())
							{
								System.out.println("knob detected");
								resize = true;
								clicked = true;
								selectedKnob = null;
							}
							else if(event.getX()> focusedObject.getModel().getX() && event.getX() < focusedObject.getModel().getX() + focusedObject.getModel().getWidth() && event.getY()> focusedObject.getModel().getY() && event.getY() < focusedObject.getModel().getY() + focusedObject.getModel().getHeight())
							{
								drag = true;
								clicked = true;	
							}
						}
					}
				}
			}
		});
		
		canvas.setOnMouseReleased(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				clicked = false;
				drag = false;
				resize = false;
				selectedKnob = null;
			}
		
		});
		
		//Selects a shape, assigns it to focusedObject
		canvas.setOnMouseClicked(new EventHandler<MouseEvent>()
		{
			public void handle(MouseEvent event)
			{
			System.out.println("clicking");	
				double middleX =  event.getX();
				double middleY = event.getY();
				boolean obj = false;
				for (DShape d: canvas.getList())
				{
					DShapeModel shape = d.getModel();
					if(shape instanceof DLineModel)
					{	
						int lowerX = Math.min(shape.getX(), shape.getWidth());
						int lowerY = Math.min(shape.getY(), shape.getHeight());
						int lineWidth = Math.abs(((shape.getX() - shape.getWidth()))); 
						int lineHeight = Math.abs(((shape.getY() - shape.getHeight())));

						if(middleX >= lowerX && middleX <= lowerX + lineWidth)
						{
							if (middleY >= lowerY && middleY <= lowerY + lineHeight)
							{
								if(d != focusedObject)
								{
									focusedObject = d;
									obj = true;
									
									//System.out.println(focusedObject.toString());
									break;
								}
								else
								{
									obj = false;
									break;
								}	
							}
						}
					}
					if(middleX >= shape.getX() && middleX <= shape.getX() + shape.getWidth())
					{
						if (middleY >= shape.getY() && middleY <= shape.getY() + shape.getHeight())
						{
							if(d != focusedObject)
							{
								focusedObject = d;
								obj = true;
								
								//System.out.println(focusedObject.toString());
								break;
							}
							else
							{
								obj = false;
								break;
							}	
						}
					}
				}
				if(focusedObject instanceof DLine)
				{
					DLine line = (DLine)focusedObject;
					Line drawn = line.drawn;
					
					drawn.setOnMousePressed(new EventHandler<MouseEvent>()
						{
						public void handle(MouseEvent event)
							{
								System.out.println("clicked");
							}
						});	
				}
				if (!obj)
				{
					if (knobs != null)
					{
						canvas.getChildren().removeAll(knobs);
						makeUnfocused();
						removeTextInfo(textBox, dropDown, changeText);
						hideButtons(removeShape, moveToBack, moveToFront);
						knobs = null;
					}
					focusedObject = null;
				}
				
				else if (focusedObject != null)
				{
					makeFocused(focusedObject);
					if(focusedObject instanceof DText)
						setUpTextInfo(textBox, dropDown, changeText);
					setUpButtons(removeShape, moveToBack, moveToFront);
	
				}
				
			}
			
		});
		
		canvas.setOnMouseDragged(new EventHandler<MouseEvent>()
		{

			public void handle(MouseEvent event)
			{
				canvas.draw();
	
				if(resize)
				{
					if(focusedObject instanceof DLine)
					{
						for(int i = 0; i < 2; i++)
						{
							Rectangle knob = knobs[i];
							if(selectedKnob == null && event.getX()>knob.getX() && event.getX()< knob.getX() + knob.getWidth() && event.getY()>knob.getY() && event.getY()< knob.getY() + knob.getHeight())
							{
								selectedKnob = knob;
							}
						}
						if(selectedKnob == knobs[0])
						{
							selectedKnob.setX(event.getX() - (knobs[0].getWidth()/2));
							selectedKnob.setY(event.getY() - (knobs[0].getHeight()/2));
							focusedObject.getModel().setX((int)selectedKnob.getX());
							focusedObject.getModel().setY((int)selectedKnob.getY());
						}
						if(selectedKnob == knobs[1])
						{
							selectedKnob.setX(event.getX() - (knobs[0].getWidth()/2));
							selectedKnob.setY(event.getY() - (knobs[0].getHeight()/2));
							focusedObject.getModel().setWidth((int)selectedKnob.getX());
							focusedObject.getModel().setHeight((int)selectedKnob.getY());	
						}	
						System.out.println(knobs);						
					}
					else{
					for(int i = 0; i < knobs.length; i++)
					{
						Rectangle knob = knobs[i];
						if(selectedKnob == null && event.getX()>knob.getX() && event.getX()< knob.getX() + knob.getWidth() && event.getY()>knob.getY() && event.getY()< knob.getY() + knob.getHeight())
						{
							selectedKnob = knob;
							System.out.println("success");
						}		

					}		
					if(selectedKnob == knobs[0])
					{
						selectedKnob.setX(event.getX() - (knobs[0].getWidth()/2));
						knobs[1].setX(event.getX() - (knobs[0].getWidth()/2));
						selectedKnob.setY(event.getY() - (knobs[0].getHeight()/2));
						knobs[2].setY(event.getY() - (knobs[0].getHeight()/2));
						focusedObject.getModel().setWidth((int)Math.abs(selectedKnob.getX() - knobs[2].getX()));
						focusedObject.getModel().setHeight((int)Math.abs(selectedKnob.getY() - knobs[1].getY()));
						System.out.println(0);
					}
					else if(selectedKnob == knobs[1])
					{
						selectedKnob.setX(event.getX() - (knobs[1].getWidth()/2));
						knobs[0].setX(event.getX() - (knobs[1].getWidth()/2));
						selectedKnob.setY(event.getY() - (knobs[1].getHeight()/2));
						knobs[3].setY(event.getY() - (knobs[1].getHeight()/2));
						focusedObject.getModel().setWidth((int)Math.abs(selectedKnob.getX() - knobs[3].getX()));
						focusedObject.getModel().setHeight((int)Math.abs(selectedKnob.getY() - knobs[0].getY()));
						System.out.println(1);
					}
					else if(selectedKnob == knobs[2])
					{
						selectedKnob.setX(event.getX() - (knobs[2].getWidth()/2));
						knobs[3].setX(event.getX() - (knobs[2].getWidth()/2));
						selectedKnob.setY(event.getY() - (knobs[2].getHeight()/2));
						knobs[0].setY(event.getY() - (knobs[2].getHeight()/2));
						focusedObject.getModel().setX(focusedObject.getModel().getX());
						focusedObject.getModel().setWidth((int)Math.abs(selectedKnob.getX() - knobs[0].getX()));
						focusedObject.getModel().setHeight((int)Math.abs(selectedKnob.getY() - knobs[3].getY()));
						System.out.println(2);
					}
					else if(selectedKnob == knobs[3])
					{
						selectedKnob.setX(event.getX() - (knobs[3].getWidth()/2));
						knobs[2].setX(event.getX() - (knobs[3].getWidth()/2));
						selectedKnob.setY(event.getY() - (knobs[3].getHeight()/2));
						knobs[1].setY(event.getY() - (knobs[3].getHeight()/2));
						focusedObject.getModel().setWidth((int)Math.abs(selectedKnob.getX() - knobs[1].getX()));
						focusedObject.getModel().setHeight((int)Math.abs(selectedKnob.getY() - knobs[2].getY()));
						System.out.println("o pisition : "+ knobs[0].getX() + "     3 position : " + knobs[3].getX());
					}
					
					// code to move shape into the right area
					if(knobs[0].getX()> knobs[2].getX())
					{
						focusedObject.getModel().setX((int)(knobs[2].getX()));
					}
					else
					{
						focusedObject.getModel().setX((int)(knobs[0].getX()));
					}
					
					if(knobs[0].getY()> knobs[1].getY())
					{
						focusedObject.getModel().setY((int)(knobs[1].getY()));
					}
					else
					{
						focusedObject.getModel().setY((int)(knobs[0].getY()));
					}
					canvas.drawKnobs(knobs);
					}
				}
				else if(drag)
				{
					if(focusedObject instanceof DLine)
					{
						
						int lineWidth = (focusedObject.getModel().getX() - focusedObject.getModel().getWidth());
						int lineHeight = (focusedObject.getModel().getY() - focusedObject.getModel().getHeight());
						focusedObject.getModel().setX((int)(event.getX() + (lineWidth/2)));
						focusedObject.getModel().setY((int)(event.getY() + (lineHeight/2)));						
						
						focusedObject.getModel().setWidth((int)(event.getX() - (lineWidth/2)));
						focusedObject.getModel().setHeight((int)(event.getY() - (lineHeight/2)));
					}
					else
					{
						focusedObject.getModel().setX((int)(event.getX() - (focusedObject.getModel().getWidth()/2)));
						focusedObject.getModel().setY((int)(event.getY() - (focusedObject.getModel().getHeight()/2)));						
						System.out.println("fail");
					}
				}
								
			}
		});
		
		
		// FINISHING OFF THE OBJECTS
		buttonBox.getChildren().addAll(add, addRect, addOval, addLine, addText);
		colorBox.getChildren().addAll(colorPicker, doneColorPicker);
		textInfo.getChildren().addAll(textBox, dropDown, changeText);
		
		vbox.setPadding(new Insets(10, 10, 40, 10)); //saaj's code				
		vbox.getChildren().addAll(menu, buttonBox, colorBox, textInfo, objectInfo, table);
		
		pane.setCenter(canvas);
		pane.setLeft(vbox);
		
		VBox container = new VBox();
		container.getChildren().add(pane);
		container.setStyle("-fx-background-color: teal");
		
		Text welcome = new Text("You are Offline");
		Font f = new Font("Times New Roman", 50);
		welcome.setFont(f);
		pane.setTop(welcome);
		
		if (server != null)
		{
			container.setStyle("-fx-background-color: red");
			welcome.setFill(Color.GOLD);
			welcome.setText("You are in the Server View");
			stage.setOnCloseRequest(new EventHandler(){
				public void handle(Event e)
				{
					server.setPage(false);
				}
			});
		}
		((Group) scene.getRoot()).getChildren().add(container);
		stage.setScene(scene);
		stage.show();
	}
	
	public void makeFocused(DShape object)
	{
		DShapeModel model = object.getModel();
		knobs = model.drawKnobs();
		canvas.getChildren().addAll(knobs);
	}
	
	//Saaj's Method------------------------------------------------------------------------------------------------------------------------------------------------		
		public void setUpButtons(Button remove, Button back, Button front)		
		{		
			remove.setVisible(true);		
			back.setVisible(true);		
			front.setVisible(true);		
			remove.setDisable(false);		
			back.setDisable(false);		
			front.setDisable(false);		
		}		
				
		public void makeUnfocused()		
		{			
			focusedObject = null;	
			knobs = null;//////////////
		}		
				
		public void hideButtons(Button remove, Button back, Button front)		
		{		
			remove.setDisable(true);		
			back.setDisable(true);		
			front.setDisable(true);		
			remove.setVisible(false);		
			back.setVisible(false);		
			front.setVisible(false);		
		}		
		//Saaj's Method End--------------------------------------------------------------------------------------------------------------------------------------------
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

        TableInfo(SimpleStringProperty v, double d, double e, double f, double g) {
            this.value = v;
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
    /*
	public static void main(String[] args){
		launch(args);
	}
     */
	public void modelChanged(DShapeModel model) {
		// TODO Auto-generated method stub
		
	}
	
	public void setUpTextInfo(TextField textBox, ChoiceBox dropDown, Button changeText)
	{
		textBox.setVisible(true);
		textBox.setDisable(false);
		dropDown.setVisible(true);
		dropDown.setDisable(false);
		changeText.setVisible(true);
		changeText.setDisable(false);
	}
	public void removeTextInfo(TextField textBox, ChoiceBox dropDown, Button changeText)
	{
		textBox.setVisible(false);
		textBox.setDisable(true);
		dropDown.setVisible(false);
		dropDown.setDisable(true);
		changeText.setVisible(false);
		changeText.setDisable(true);
	}
	public void  doSend(DShapeModel shape)
	{
		//canvas.updateServer(server, shape);
	}
	/*
	public Whiteboard getWhiteboard()
	{
		return this;
	}
	*/
}