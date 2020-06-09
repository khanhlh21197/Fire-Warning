package com.khanhlh.firewarning.viewcustom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.khanhlh.firewarning.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomCircleImageView extends CircleImageView {

    public CustomCircleImageView(Context context) {
        super(context);
    }

    public CustomCircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.gray));
        paint.setStyle(Paint.Style.FILL);

        float radius = getWidth() / 2f;
        RectF oval = new RectF();
        oval.set(getWidth() / 2 - radius,
                getHeight() / 2 - radius,
                getWidth() / 2 + radius,
                getHeight() / 2 + radius);

        canvas.drawArc(oval, 30f, 120f, false, paint);
    }
}
