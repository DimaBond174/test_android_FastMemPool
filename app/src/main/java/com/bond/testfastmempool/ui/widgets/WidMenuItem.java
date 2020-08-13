package com.bond.testfastmempool.ui.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.content.res.AppCompatResources;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bond.testfastmempool.db.SpecTheme;


/**
 * Иконка + текст для менюхи
 */

public class WidMenuItem extends FrameLayout implements View.OnTouchListener {

    ImageView imageView;
    Drawable defDrawable;
    public TextView textView;
    //int rIcon;
    int margins;



  void  createContent (Context  context,  int  margins,  int  rString,
      float  rStringSize,  Drawable  drawable,  boolean  ellipsize)  {

        this.margins=margins;
        defDrawable = drawable;
        Resources res = context.getResources();
        imageView = new ImageView(context);
        //imageView.setImageDrawable(res.getDrawable(rIcon));
        imageView.setImageDrawable(defDrawable);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        //imageView.setOnTouchListener(this);
        addView(imageView, new LayoutParams(SpecTheme.dpButtonImgSize, SpecTheme.dpButtonImgSize));

        textView = new TextView(context);
        if  (ellipsize)  {
          textView.setSingleLine(true);
          textView.setMaxLines(1);
          textView.setEllipsize(TextUtils.TruncateAt.END);
        }
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, rStringSize);

        textView.setText(res.getText(rString));
        textView.setTextColor(SpecTheme.PTextColor);
        //textView.setOnTouchListener(this);
        addView(textView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));



        //setWillNotDraw(false);
        //setClickable(true);
        setOnTouchListener(this);
        //setOnClickListener(this);
    }


    public WidMenuItem(Context context, int margins, int rString, float rStringSize, int rIcon, int rColor) {
        super(context);
        //setParentsColor();
        Drawable drawable = AppCompatResources.getDrawable(context, rIcon);
        if (-1!=rColor) {
            //imageView.clearColorFilter();
            drawable.setColorFilter(new LightingColorFilter( 0, rColor));
        }
        createContent(context,  margins,  rString,  rStringSize, drawable, false);
    }

    public WidMenuItem(Context context, int margins, int rString, float rStringSize, Drawable drawable) {
        super(context);
        createContent(context,  margins,  rString,  rStringSize, drawable, false);
    }

  public WidMenuItem(Context context, int margins, int rString, float rStringSize, Drawable drawable, boolean ellipsize) {
    super(context);
    createContent(context,  margins,  rString,  rStringSize, drawable, ellipsize);
  }

    public void changeIcon(Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }

//    public void changeIcon(int rIcon, int rColor) {
//        imageView.setImageDrawable(defDrawable);
//        if (-1!=rColor) {
//            //imageView.clearColorFilter();
//            imageView.setColorFilter(new LightingColorFilter( 0, rColor));
//        } else {
//            imageView.clearColorFilter();
//        }
//    }

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
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//            int buttonWidht  = MeasureSpec.getSize(widthMeasureSpec);
//            int buttonHeight  = MeasureSpec.getSize(heightMeasureSpec);
        int widht = MeasureSpec.getSize(widthMeasureSpec);
        int text_width = widht - SpecTheme.dpButton2Padding - SpecTheme.dpButtonSmImgSize;
        int text_width_widthSpec = MeasureSpec.makeMeasureSpec(text_width, MeasureSpec.AT_MOST);
        textView.measure(text_width_widthSpec, heightMeasureSpec);
//        measureChildWithMargins(textView, text_width_widthSpec, 0,
//                heightMeasureSpec, 0);
        //int height = textView.getMeasuredHeight() + SpecTheme.dpButton2Padding;
        int height = textView.getMeasuredHeight() + margins;
        height = height < SpecTheme.dpButtonSmImgSize?
                SpecTheme.dpButtonSmImgSize : height;
        widht = SpecTheme.dpButtonSmImgSize + textView.getMeasuredWidth() + SpecTheme.dpButton2Padding;
        /* Скажем наверх насколько мы большие */
        setMeasuredDimension(widht, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //super.onLayout(changed, left, top, right, bottom);

        int halfHeight = (bottom-top)>>1;
        int half = SpecTheme.dpButtonSmImgSize >>1;
        imageView.layout(SpecTheme.dpButtonPadding,
                halfHeight - half,
                SpecTheme.dpButtonPadding + SpecTheme.dpButtonSmImgSize,
                halfHeight + half);
        half = (textView.getMeasuredHeight())>>1;
        textView.layout(SpecTheme.dpButton2Padding + SpecTheme.dpButtonSmImgSize,
                halfHeight - half,
                SpecTheme.dpButton2Padding + SpecTheme.dpButtonSmImgSize+textView.getMeasuredWidth(),
                halfHeight + half);
    }

//        @Override
//        protected void onDraw(Canvas canvas) {
//            canvas.drawLine(SpecTheme.dpButton2Padding + SpecTheme.dpButtonSmImgSize,
//                    curHeight,
//                    getWidth(),
//                    curHeight, SpecTheme.paintLine );
//        }

}
