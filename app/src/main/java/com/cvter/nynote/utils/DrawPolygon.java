package com.cvter.nynote.utils;

import android.graphics.Path;
import android.graphics.RectF;

/**
 * Created by cvter on 2017/6/12.
 * 图形逻辑绘制类
 */

public class DrawPolygon {

    private float dashRadiusX;
    private float dashRadiusY;
    private float dashX;
    private float dashY;

    //画圆
    public void drawCircle(Path path, float startX, float startY, float endX, float endY) {
        path.rewind();
        RectF rectF = new RectF(startX, startY, endX, endY);
        path.addOval(rectF, Path.Direction.CW);
    }

    //画直线
    public void drawLine(Path path, float startX, float startY, float endX, float endY) {
        path.rewind();
        path.moveTo(startX, startY);
        path.lineTo(endX, endY);
    }

    //画矩形
    public void drawSquare(Path path, float startX, float startY, float endX, float endY) {
        path.rewind();
        path.addRect(Math.min(startX, endX), Math.min(startY, endY), Math.max(endX, startX), Math.max(endY, startY), Path.Direction.CW);
    }

    //画正三角形
    public void drawDelta(Path path, float radius, float x, float y) {
        path.rewind();
        path.moveTo(x, y);
        float spaceX = (float) (radius * (Math.sin(Math.PI * 60 / 180)));
        float spaceY = (float) (radius + radius * (Math.cos(Math.PI * 60 / 180)));
        path.lineTo((x + spaceX), (y + spaceY));
        path.moveTo((x + spaceX), (y + spaceY));
        path.lineTo((x - spaceX), (y + spaceY));
        path.moveTo((x - spaceX), (y + spaceY));
        path.lineTo(x, y);
    }

    //画正五边形
    public void drawPentagon(Path path, float radius, float x, float y) {
        path.rewind();
        path.moveTo(x, y - radius);
        float spaceX = (float) (radius * (Math.sin(Math.PI * 72 / 180)));
        float spaceY = (float) (radius * (Math.cos(Math.PI * 72 / 180)));

        float spaceX2 = (float) (radius * (Math.sin(Math.PI * 36 / 180)));
        float spaceY2 = (float) (radius * (Math.cos(Math.PI * 36 / 180)));

        path.lineTo((x + spaceX), (y - spaceY));
        path.moveTo((x + spaceX), (y - spaceY));

        path.lineTo((x + spaceX2), (y + spaceY2));
        path.moveTo((x + spaceX2), (y + spaceY2));

        path.lineTo((x - spaceX2), (y + spaceY2));
        path.moveTo((x - spaceX2), (y + spaceY2));

        path.lineTo((x - spaceX), (y - spaceY));
        path.moveTo((x - spaceX), (y - spaceY));

        path.lineTo(x, y - radius);

    }

    //画五角星
    public void drawStar(Path path, float radius, float x, float y) {
        path.rewind();
        path.moveTo(x, y - radius);
        float spaceX = (float) (radius * (Math.sin(Math.PI * 72 / 180)));
        float spaceY = (float) (radius * (Math.cos(Math.PI * 72 / 180)));

        float spaceX2 = (float) (radius * (Math.sin(Math.PI * 36 / 180)));
        float spaceY2 = (float) (radius * (Math.cos(Math.PI * 36 / 180)));

        path.lineTo((x + spaceX2), (y + spaceY2));
        path.moveTo((x + spaceX2), (y + spaceY2));

        path.lineTo((x - spaceX), (y - spaceY));
        path.moveTo((x - spaceX), (y - spaceY));

        path.lineTo((x + spaceX), (y - spaceY));
        path.moveTo((x + spaceX), (y - spaceY));

        path.lineTo((x - spaceX2), (y + spaceY2));
        path.moveTo((x - spaceX2), (y + spaceY2));


        path.lineTo(x, y - radius);
    }

    //画球体
    public void drawSphere(Path path) {
        path.rewind();
    }

    //画棱锥
    public void drawCone(Path path, float radius, float radiusY, float x, float y) {
        path.rewind();
        path.moveTo(x + radius / 6, y);

        float spaceX = radius / 2;
        float spaceY = radius * 2;
        float spaceX2 = radius * 3 / 2;
        float spaceY2 = radius * 3 / 2;

        if (radiusY * radius > 0) {
            path.lineTo(x - spaceX, y + spaceY);
            path.lineTo(x + radius, y + spaceY);
            path.lineTo(x + radius / 6, y);
            path.lineTo(x + spaceX2, y + spaceY2);
            path.lineTo(x + radius, y + spaceY);

        } else {
            path.lineTo(x - spaceX, y - spaceY);
            path.lineTo(x + radius, y - spaceY);
            path.lineTo(x + radius / 6, y);
            path.lineTo(x + spaceX2, y - spaceY2);
            path.lineTo(x + radius, y - spaceY);

        }
    }

    //画正方体
    public void drawCube(Path path, float radius, float x, float y) {
        path.rewind();
        path.moveTo(x, y);

        float spaceX = (radius * 1) / 3;
        float spaceY = (radius * 1) / 3;
        float spaceX2 = (radius * 4) / 3;
        float spaceY2 = (radius * 2) / 3;

        if (radius > 0) {
            path.lineTo(x + radius, y);
            path.lineTo(x + radius, y + radius);
            path.lineTo(x, y + radius);//
            path.lineTo(x, y);
            path.lineTo(x + spaceX, y - spaceY);
            path.lineTo(x + spaceX2, y - spaceY);//
            path.lineTo(x + spaceX2, y + spaceY2);
            path.moveTo(x + radius, y + radius);
            path.lineTo(x + spaceX2, y + spaceY2);//
            path.moveTo(x + radius, y);
            path.lineTo(x + spaceX2, y - spaceY);
        } else {
            path.lineTo(x + radius, y);
            path.lineTo(x + radius, y - radius);
            path.lineTo(x, y - radius);
            path.lineTo(x, y);
            path.lineTo(x + spaceX, y + spaceY);
            path.lineTo(x + spaceX2, y + spaceY);
            path.lineTo(x + spaceX2, y - spaceY2);
            path.moveTo(x + radius, y - radius);
            path.lineTo(x + spaceX2, y - spaceY2);
            path.moveTo(x + radius, y);
            path.lineTo(x + spaceX2, y + spaceY);
        }


    }

    //设置虚线相关属性
    public void setDash(float dashRadiusX, float dashRadiusY, float dashX, float dashY) {
        this.dashRadiusX = dashRadiusX;
        this.dashRadiusY = dashRadiusY;
        this.dashX = dashX;
        this.dashY = dashY;
    }

    //绘制棱锥虚线
    public void drawConeDash(Path path) {
        float spaceX = dashRadiusX / 2;
        float spaceY = dashRadiusX * 2;
        float spaceX2 = dashRadiusX * 3 / 2;
        float spaceY2 = dashRadiusX * 3 / 2;

        if (dashRadiusX * dashRadiusY > 0) {
            path.moveTo(dashX + spaceX2, dashY + spaceY2);
            path.lineTo(dashX, dashY + spaceY2);
            path.lineTo(dashX - spaceX, dashY + spaceY);
            path.moveTo(dashX + dashRadiusX / 6, dashY);
            path.lineTo(dashX, dashY + spaceY2);
        } else {
            path.moveTo(dashX + spaceX2, dashY - spaceY2);
            path.lineTo(dashX, dashY - spaceY2);
            path.lineTo(dashX - spaceX, dashY - spaceY);
            path.moveTo(dashX + dashRadiusX / 6, dashY);
            path.lineTo(dashX, dashY - spaceY2);
        }

    }

    //绘制正方体虚线
    public void drawCubeDash(Path path) {
        float spaceX = (dashRadiusX * 1) / 3;
        float spaceY = (dashRadiusX * 1) / 3;
        float spaceX2 = (dashRadiusX * 4) / 3;
        float spaceY2 = (dashRadiusX * 2) / 3;

        if (dashRadiusX > 0) {
            path.moveTo(dashX, dashY + dashRadiusX);
            path.lineTo(dashX + spaceX, dashY + spaceY2);
            path.lineTo(dashX + spaceX, dashY - spaceY);
            path.moveTo(dashX + spaceX, dashY + spaceY2);
            path.lineTo(dashX + spaceX2, dashY + spaceY2);
        } else {
            path.moveTo(dashX, dashY - dashRadiusX);
            path.lineTo(dashX + spaceX, dashY - spaceY2);
            path.lineTo(dashX + spaceX, dashY + spaceY);
            path.moveTo(dashX + spaceX, dashY - spaceY2);
            path.lineTo(dashX + spaceX2, dashY - spaceY2);
        }

    }

    public void drawSphereDash(Path path) {

    }

}
