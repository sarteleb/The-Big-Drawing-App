package edu.msu.sarteleb.bigdrawing;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Brandon on 5/4/2015.
 */
public class DrawingView extends View{

    //region Private Members

    Drawing drawing = new Drawing();

    //endregion

    //region Public Methods

    public DrawingView(Context context){
        super(context);
        init(null, 0);
    }

    public DrawingView(Context context, AttributeSet attributes) {
        super(context, attributes);
        init(attributes, 0);
    }

    public DrawingView(Context context, AttributeSet attributes, int style) {
        super(context, attributes, style);
        init(attributes, style);
    }

    private void init(AttributeSet attributes, int style) {
        drawing.setView(this);
    }

    public void update(double latitude, double longitude) {
        drawing.update(latitude, longitude);
    }

    public void setLineWidth(float width) {
        drawing.setLineWidth(width);
    }

    public void showLocation() {
        drawing.showLocation();
    }

    public void setLineColor(int color) {
        drawing.setLineColor(color);
    }

    public void clearCanvas() {
        drawing.clearCanvas();
    }

    //endregion

    //region Overrides

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawing.draw(canvas);

    }

    //endregion
}
