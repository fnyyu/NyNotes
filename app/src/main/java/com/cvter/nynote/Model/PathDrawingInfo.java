package com.cvter.nynote.Model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.cvter.nynote.Utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cvter on 2017/6/6.
 */

public class PathDrawingInfo extends PathInfo{

    @Override
    public void draw(Canvas canvas, int type) {
        canvas.drawPath(path, paint);
    }
}
