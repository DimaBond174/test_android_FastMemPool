package com.bond.testfastmempool.ui.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class WidImageView extends FrameLayout  {
  AppCompatImageView image;
  //ImageView image;
  int widgetHeight = 0;
  int widgetWidht = 0;
  private volatile float mouseDownX = 0.0f;
  private volatile float mouseDownY = 0.0f;
  private final Matrix mainMatrix = new Matrix();
  private final Matrix moveMatrix = new Matrix();
  private final Matrix zoomMatrix = new Matrix();
  private float[] mPreviousX = new float[10]; //На момент ПЕРВОГО нажатия
  private float[] mPreviousY = new float[10]; //На текущий момент
  private float[] mNewX = new float[10];
  private float[] mNewY = new float[10];

  private volatile boolean isMulty = false;//, inTouch=false;
  private volatile int touch1 = -1;
  private volatile int touch2 = -1;
  private volatile int mouseStage = 0;
  private volatile int centerX = 0;
  private volatile int centerY = 0;

  public WidImageView(Context context) {
    super(context);
    image = new AppCompatImageView(context);
    image.setDrawingCacheEnabled(true);
    image.setScaleType(ImageView.ScaleType.MATRIX);
    //image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    addView(image);
    return;
  }


  public void setImgBitmap(Bitmap bitmap) {
    image.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
    return;
  }

  private void mouseDown(float x, float  y) {
    mouseDownX = x;
    mouseDownY = y;
    mouseStage = 1;
    return;
  }

  private void zoom_end() {
    mouseStage = 0;
    mainMatrix.postConcat(moveMatrix);
    image.setImageMatrix(mainMatrix);
    mouseUp();
    return;
  }

  private void mouseUp() {
    mouseStage = 0;
    mouseDownX = 0.0f;
    mouseDownY = 0.0f;
    for (int  i = 0;  i < 10;  ++i)
    {
      mPreviousX[i] = mPreviousY[i] = mNewX[i] = mNewY[i] = -1.0f;
    }
    touch1 = -1;
    touch2 = -1;
    return;
  }

  private void zoom() {
    if (touch1 < 0  ||  touch2 < 0)  {  return;  }
    float vec1X  =  mPreviousX[touch2]  -  mPreviousX[touch1];
    float vec1Y  =  mPreviousY[touch2]  -  mPreviousY[touch1];
    float vec2X  =  mNewX[touch2]  -  mNewX[touch1];
    float vec2Y  =  mNewY[touch2]  -  mNewY[touch1];

    double vec1Len  =  Math.sqrt(vec1X * vec1X  +  vec1Y * vec1Y);
    double vec2Len  =  Math.sqrt(vec2X * vec2X  +  vec2Y * vec2Y);
    if  (vec1Len  <  1.0f   ||   vec1Len  <  1.0f)  return;
    //float dot=vec1X*vec2X + vec1Y*vec2Y; //Вращение решил не делать

    float zoom  = (float)(vec2Len / vec1Len);

    moveMatrix.reset();
    zoomMatrix.set(mainMatrix);
    //moveMatrix.setScale(zoom, zoom, mouseDownX, mouseDownY);
    moveMatrix.setScale(zoom, zoom, centerX, centerY);
    zoomMatrix.postConcat(moveMatrix);
    image.setImageMatrix(zoomMatrix);
    return;
  }

  private void move(float x, float  y) {
    //mainMatrix.setTranslate(x-mouseDownX, y-mouseDownY);
    moveMatrix.reset();
    moveMatrix.setTranslate(x-mouseDownX, y-mouseDownY);
    mainMatrix.postConcat(moveMatrix);
    mouseDownX=x;
    mouseDownY=y;
    //mouseStage=1;
    image.setImageMatrix(mainMatrix);
    return;
  }

  private void mouseMove(float x, float  y) {
    switch (mouseStage)
    {
      case 0:
        mouseDown(x,y);
      case 1:
        move(x,y);
        break;
      case 2:
        //Уже несколько касаний == zoom или/и rotate
        break;
      default:
        break;
    }
  }



  @Override
  public boolean onTouchEvent(MotionEvent e) {
    //return super.onTouchEvent(event);
    // MotionEvent reports input details from the touch screen
    // and other input controls.
    //Отсюда должны быть конкретные команды уходить:
    //Если мультитач:
    //1. Zoom-in, Zoom-out
    //Если одно касание:
    //1. Если один клик то выделить кубик и обределить какую плоскость вращать
    //2. Вращение в плоскости экрана если по краю пальцем водить
    //3. Вращение в Z плоскости если через центр проходит

    // индекс касания
    int pointerIndex = e.getActionIndex();
    if (pointerIndex>9) return true;

    int actionMask = e.getActionMasked();

    // число касаний
    int pointerCount = e.getPointerCount();

    float x = e.getX(pointerIndex);
    float y = e.getY(pointerIndex);
    mNewX[pointerIndex]=x;
    mNewY[pointerIndex]=y;
    switch (actionMask) {
      case MotionEvent.ACTION_DOWN: // первое касание
        //inTouch = true;
        touch1=pointerIndex;
        mouseDown( x,  y);
      case MotionEvent.ACTION_POINTER_DOWN: // последующие касания
        if (pointerIndex!=touch1) touch2 = pointerIndex;
        mPreviousX[pointerIndex]=x;
        mPreviousY[pointerIndex]=y;
        mNewX[pointerIndex]=x;
        mNewY[pointerIndex]=y;
        //isMulty=pointerCount>1;
        if (pointerCount>1) {
          mouseStage=2;
          zoomMatrix.set(mainMatrix);
        }
        break;

      case MotionEvent.ACTION_UP: // прерывание последнего касания
        //inTouch = false;
      case MotionEvent.ACTION_POINTER_UP: // прерывания касаний
        //upPI = pointerIndex;
        //if (isMulty)
        if (2==mouseStage) {
          zoom_end();
          //isMulty=false;
        } else mouseUp();

        break;

      case MotionEvent.ACTION_MOVE: // движение

        //if (isMulty)
        if (2==mouseStage) {
//                    double oldlen = Math.sqrt(Math.pow((mPreviousX[touch2]-mPreviousX[touch1]), 2) +Math.pow((mPreviousY[touch2]-mPreviousY[touch1]), 2));
//                    double newlen = Math.sqrt(Math.pow((mNewX[touch2]-mNewX[touch1]), 2) +Math.pow((mNewY[touch2]-mNewY[touch1]), 2));
//                    if (oldlen<10.0f || newlen<10.0f) return true;
          //GL2JNILib.zoom((float)(newlen/oldlen));
          // zoom((float)(oldlen/newlen));
          zoom();
        } else if (touch1==pointerIndex) mouseMove( x,  y);
//                sb.setLength(0);
//                for (int i = 0; i < 10; i++) {
//                    sb.append("Index = " + i);
//                    if (i < pointerCount) {
//                        sb.append(", ID = " + e.getPointerId(i));
//                        sb.append(", X = " + e.getX(i));
//                        sb.append(", Y = " + e.getY(i));
//                    } else {
//                        sb.append(", ID = ");
//                        sb.append(", X = ");
//                        sb.append(", Y = ");
//                    }
//                    sb.append("\r\n");
//                }
        break;
    }


    return true;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    widgetHeight = MeasureSpec.getSize(heightMeasureSpec);
    widgetWidht = MeasureSpec.getSize(widthMeasureSpec);

    centerX  =  widgetWidht  >>  1;
    centerY  =  widgetHeight  >>  1;
    int sz_height = MeasureSpec.makeMeasureSpec(widgetHeight, MeasureSpec.EXACTLY);
    int sz_width = MeasureSpec.makeMeasureSpec(widgetWidht, MeasureSpec.EXACTLY);

    image.measure(sz_width, sz_height);
    /* Скажем наверх насколько мы большие */
    setMeasuredDimension(widgetWidht,  widgetHeight);
    return;
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    //super.onLayout(changed, left, top, right, bottom);
    int width = right-left;
    int height = bottom - top;
    image.layout(0, 0,  width, height);
    return;
  }

} //WidImageView
