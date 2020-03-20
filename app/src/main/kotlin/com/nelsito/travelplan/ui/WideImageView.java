package com.nelsito.travelplan.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class WideImageView extends AppCompatImageView
{
    public WideImageView(Context context)
    {
        super(context);
    }

    public WideImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public WideImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth() * 9 / 32); //Snap to width
    }
}