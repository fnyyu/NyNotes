package com.cvter.nynote.Utils;

import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by cvter on 2017/6/12.
 */

public class DrawPolygon {

    public DrawPolygon(){}

   //画圆
    public void drawCircle(Path path, float startX, float startY, float endX, float endY){
        path.rewind();
        RectF rectF = new RectF(startX, startY, endX, endY);
        path.addOval(rectF, Path.Direction.CW);
    }

    //画直线
    public void drawLine(Path path, float startX, float startY, float endX, float endY){
        path.rewind();
        path.moveTo(startX, startY);
        path.lineTo(endX, endY);
    }

    //画矩形
    public void drawSquare(Path path, float startX, float startY, float endX, float endY){
        path.rewind();
        path.addRect(Math.min(startX, endX), Math.min(startY, endY), Math.max(endX, startX), Math.max(endY, startY), Path.Direction.CW);
    }

    //画正三角形
    public void drawDelta(Path path, float radius, float x, float y){
        path.rewind();
        path.moveTo(x, y);
        float spaceX = (float) (radius*(Math.sin(60.0)));
        float spaceY = (float) ((radius*(Math.cos(60.0))) + radius/2);
        path.lineTo((x + spaceX), (y - spaceY));
        path.moveTo((x + spaceX), (y - spaceY));
        path.lineTo((x - spaceX), (y - spaceY));
        path.moveTo((x - spaceX), (y - spaceY));
        path.lineTo(x, y);
    }

    //画正五边形
    public void drawPentagon(Path path, float radius, float x, float y){
        path.rewind();
        path.moveTo(x, y + radius);
        float spaceX = (float) (radius*(Math.sin(72.0)));
        float spaceY = (float) (radius - radius*(Math.cos(72.0)));

        float spaceX2 = (float)(radius*(Math.sin(36.0)));
        float spaceY2 = (float)(radius*(Math.cos(36.0)));

        path.lineTo((x + spaceX), (y + spaceY));
        path.moveTo((x + spaceX), (y + spaceY));

        path.lineTo((x + spaceX2), (y + spaceY2));
        path.moveTo((x + spaceX2), (y + spaceY2));

        //path.lineTo((x - spaceX2), (y + spaceY2));
        //path.moveTo((x - spaceX2), (y + spaceY2));


        //path.lineTo((x - spaceX), (y - spaceY));
        //path.moveTo((x - spaceX), (y - spaceY));


        //path.lineTo(x, y - radius);

    }

    //画五角星
    public void drawStar(Path path, float startX, float startY, float endX, float endY){
        path.rewind();
    }

    //画球体
    public void drawSphere(Path path, float startX, float startY, float endX, float endY){
        path.rewind();
    }

    //画圆锥
    public void drawCone(Path path, float startX, float startY, float endX, float endY){
        path.rewind();
    }

    //画正方体
    public void drawCube(Path path, float startX, float startY, float endX, float endY){
        path.rewind();
    }

}