package edu.msu.sarteleb.bigdrawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;

import java.util.ArrayList;

/**
 * Created by Brandon on 5/4/2015.
 */
public class Drawing {


    //region Private Members

    private Paint line;
    private Paint brush;
    private static float metersToX;
    private static float metersToY;
    private static final float minimumDistance = 2.0f;
    private float ratio;
    private float width;
    private float height;
    private double prevLatitude;
    private double prevLongitude;
    private float x;
    private float y;
    private float stroke;
    private int color;
    private boolean brushFlag;
    private float brushLength;
    private ArrayList<Position> positions;
    private DrawingView drawingView;

    //endregion

    //region Nested Class

    private class Position {
        private float x;
        private float y;
        private float stroke;
        private int color;

        public Position(float x, float y, float stroke, int color) {
            this.x = x;
            this.y = y;
            this.stroke = stroke;
            this.color = color;
        }

        public float getX() {
            return this.x;
        }

        public float getY() {
            return this.y;
        }

        public float getStroke() {
            return this.stroke;
        }

        public int getColor() {
            return this.color;
        }
    }

    //endregion


    //region Constructor

    public Drawing() {
        this.metersToX = 0.01f;
        this.metersToY = 0.01f;
        this.width = -1.0f;
        this.height = -1.0f;
        this.prevLatitude = -1.0f;
        this.prevLongitude = -1.0f;
        this.x = 0.5f;
        this.y = 0.5f;
        this.stroke = 0;
        this.color = Color.BLACK;
        this.brushLength = 20.0f;
        this.brushFlag = false;
        positions = new ArrayList<>();

        brush = new Paint(Paint.ANTI_ALIAS_FLAG);
        brush.setColor(color);
        brush.setStrokeWidth(3.0f);

        line = new Paint(Paint.ANTI_ALIAS_FLAG);
        line.setStyle(Paint.Style.STROKE);
        line.setStrokeJoin(Paint.Join.ROUND);
        line.setStrokeCap(Paint.Cap.ROUND);
        line.setStrokeWidth(3.0f);
        line.setColor(color);

        positions.add(new Position(0.5f, 0.5f, stroke, color));
    }

    //endregion

    //region Public Methods

    public void setView(DrawingView view){
        this.drawingView = view;
    }

    public void setLineColor(int color) {
        this.color = color;
    }

    public void setLineWidth(float strokeWidth) {
        this.stroke = strokeWidth;
    }

    public void showLocation() {
        brushFlag = !brushFlag;
        drawingView.invalidate();
    }

    public void update(double latitude, double longitude) {

        if (prevLatitude == -1.0f) {
            prevLongitude = longitude;
            prevLatitude = latitude;
            return;
        }

        float[] result = new float[2];
        Location.distanceBetween(prevLatitude, prevLongitude, latitude, longitude, result);
        float distance = result[0];
        float angle = result[1];


        if (distance > minimumDistance) {

            float xDist = (float) (distance * Math.sin(Math.toRadians(angle)));
            float yDist = (float) -(distance * Math.cos(Math.toRadians(angle)));

            x = x + (xDist * metersToX);
            y = y + (yDist * metersToY * ratio);

            positions.add(new Position(x, y, stroke, color));

            prevLongitude = longitude;
            prevLatitude = latitude;

            drawingView.invalidate();
        }
    }

    public void clearCanvas() {
        positions.clear();
        x = 0.5f;
        y = 0.5f;
        positions.add(new Position(x, y, stroke, color));
        drawingView.invalidate();
    }


    public void draw(Canvas canvas) {
        if (width == -1.0) {
            width = canvas.getWidth();
            height = canvas.getHeight();
            ratio = width/height;
            drawingView.invalidate();
        }

        int check = positions.size()-1;

        for(int x = 0; x < check; x++) {
            Position point1 = positions.get(x);
            Position point2 = positions.get(x+1);
            line.setStrokeWidth(point2.getStroke());
            line.setColor(point2.getColor());

            if (line.getStrokeWidth() != 0.0f) {
                canvas.drawLine(point1.getX() * width, point1.getY() * height,
                        point2.getX() * width, point2.getY() * height, line);
            }
        }

        if (brushFlag) {
            canvas.drawLine(x*width - brushLength, y*height,
                    x*width + brushLength, y*height, brush);
            canvas.drawLine(x*width, y*height - brushLength,
                    x*width, y*height + brushLength, brush);
        }


    }

    //endregion


}
