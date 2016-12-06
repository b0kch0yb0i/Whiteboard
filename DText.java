package WhiteBoard;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class DText extends DShape {

	public DText(DTextModel text)
	{
		super(text);
	}
	
	@Override
	public void draw()
	{
		Text drawn = new Text(super.getModel().getX(), super.getModel().getY(),"" );
		drawn.setFill(super.getModel().getColor());
	}
}
