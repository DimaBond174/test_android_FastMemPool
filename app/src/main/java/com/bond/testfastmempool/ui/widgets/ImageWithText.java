package com.bond.testfastmempool.ui.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.LightingColorFilter;
import android.support.v7.content.res.AppCompatResources;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bond.testfastmempool.db.SpecTheme;

/**
 * Created by dbond on 15.03.18.
 */

public class ImageWithText extends FrameLayout implements View.OnTouchListener {

    ImageView imageView;
    public TextView textView;
    int rIcon;
    int curHeight = 0;

    public ImageWithText(Context context, int rString, float rStringSize, int rIcon, int rColor) {
        super(context);
        this.rIcon=rIcon;
        Resources res = context.getResources();
        imageView = new ImageView(context);
        //imageView.setImageDrawable(res.getDrawable(rIcon));
        imageView.setImageDrawable(AppCompatResources.getDrawable(context, rIcon));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        //imageView.setOnTouchListener(this);
        addView(imageView, new LayoutParams(SpecTheme.dpButtonImgSize, SpecTheme.dpButtonImgSize));

        textView = new TextView(context);
        //textView.setSingleLine(true);
        //textView.setMaxLines(1);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, rStringSize);
        //textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setText(res.getText(rString));
        textView.setTextColor(SpecTheme.PTextColor);
        //textView.setOnTouchListener(this);
        addView(textView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        //setParentsColor();
        if (-1 != rColor) {
            //imageView.clearColorFilter();
            imageView.setColorFilter(new LightingColorFilter( 0, rColor));
        }

        setWillNotDraw(false);
        //setClickable(true);
        setOnTouchListener(this);
        //setOnClickListener(this);
    }

//    @Override
//    public void onClick(View v) {
//        //Toast.makeText(UiRoot.getInstance().getForDialogCtx(), "BottomButton Click ", Toast.LENGTH_SHORT).show();
//        callback.onBottomButtonClick(rIcon);
//    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //handler.postDelayed(runnable, STEP_DELAY);
                setHighlighted(true);
                //return true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                setHighlighted(false);
                //userImage.animate().cancel();
                //handler.removeCallbacks(mRunnable);
                //return false;
                break;
        }
        return false;
    }

//    public void setParentsColor() {
//        imageView.clearColorFilter();
//        imageView.setColorFilter(lcf);
//        textView.setTextColor(color);
//    }


    /* Выделенное сообщение */
    public void setHighlighted(boolean hiLight) {
        if (hiLight) {
            setBackgroundColor(SpecTheme.PHiBackColor);
        } else {
            // setBackgroundColor(backDefColor);
            setBackground(null);
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//            int buttonWidht  = MeasureSpec.getSize(widthMeasureSpec);
//            int buttonHeight  = MeasureSpec.getSize(heightMeasureSpec);
        int widht = MeasureSpec.getSize(widthMeasureSpec);
        int text_width = widht - SpecTheme.dpButton2Padding - SpecTheme.dpButtonImgSize;
        int text_width_widthSpec = MeasureSpec.makeMeasureSpec(text_width, MeasureSpec.AT_MOST);

        measureChildWithMargins(textView, text_width_widthSpec, 0,
                heightMeasureSpec, 0);
        int height = textView.getMeasuredHeight();
        curHeight = height < SpecTheme.dpButtonTouchSize?
                SpecTheme.dpButtonTouchSize : height;
        /* Скажем наверх насколько мы большие */
        setMeasuredDimension(widht, curHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int buttonWidht = right-left;
        imageView.layout(SpecTheme.dpButtonPadding,
                curHeight - SpecTheme.dpButtonPadding - SpecTheme.dpButtonImgSize,
                SpecTheme.dpButtonPadding + SpecTheme.dpButtonImgSize,
                curHeight - SpecTheme.dpButtonPadding);

        textView.layout(SpecTheme.dpButton2Padding + SpecTheme.dpButtonImgSize,
                curHeight - SpecTheme.dpButtonPadding - textView.getMeasuredHeight(),
                buttonWidht,
                curHeight - SpecTheme.dpButtonPadding);
    }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawLine(SpecTheme.dpButton2Padding + SpecTheme.dpButtonImgSize,
                    curHeight,
                    getWidth(),
                    curHeight, SpecTheme.paintLine );
        }

}
