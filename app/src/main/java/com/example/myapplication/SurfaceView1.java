package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 *desc：可在单独线程中绘制大量耗时内容的View
 *time：2020/04/29
 *author：ezreal.mei
 */
public class SurfaceView1 extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private Paint pointPaint;
    public boolean isRunning=false;
    private DrawMapThread drawThread;          //绘制线程
    // 绘制的图片
    private Bitmap sourceBitmap;
    private TouchEvenHandler touchEvenHandler;
    public SurfaceView1(Context context) {
        super(context);
        init();

    }

    public SurfaceView1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        holder=getHolder();
        holder.addCallback(this);
        pointPaint=new Paint();
        pointPaint.setColor(Color.RED);
        pointPaint.setStrokeWidth(3);
        sourceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pi_ka_qiu);
        Logger.e("------图片的大小："+sourceBitmap.getWidth()+";"+sourceBitmap.getHeight());

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    /**
     * 开始绘制
     */
    public void startThread(){
        drawThread=new DrawMapThread();
        drawThread.start();
    }

    /**
     * 停止绘制
     */
    public void onStop(){
        if (drawThread!=null)
            drawThread.stopThread();
    }

    /**
     * 绘制图像的线程
     */
    public class DrawMapThread extends Thread{
        public DrawMapThread(){
            isRunning=true;
        }

        public void stopThread(){
            if (isRunning){
                isRunning=false;
                boolean workIsNotFinish=true;
                while (workIsNotFinish){
                    try {
                        drawThread.join();   //保证run方法执行完毕
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    workIsNotFinish=false;
                }
                Logger.e("终止线程");
            }

        }

        @Override
        public void run() {
            super.run();
            while (isRunning){
                long startTime=System.currentTimeMillis();
                Canvas canvas=null;
                try {
                    canvas=holder.lockCanvas();
                    if (canvas!=null){
                        drawMap(canvas);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if (canvas!=null){
                        holder.unlockCanvasAndPost(canvas);
                    }
                }
                long endTime=System.currentTimeMillis();
                Logger.e("------地图绘制耗时："+(endTime-startTime));
                long time=endTime-startTime;
                if (time<100){
                    try {
                        Thread.sleep(100-time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 绘制图片
     * @param canvas
     */
    private void drawMap(Canvas canvas) {
        pointPaint.setColor(Color.BLUE);
        pointPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(pointPaint);
        pointPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        canvas.drawColor(Color.RED);

        Matrix matrix = new Matrix();

//        float[] values = {0.5f,0f, 0.0f,
//                0f, 0.5f, 0.0f,
//                0.0f, 0.0f, 1.0f };

        float[] values = {0f,-1f, 0,
                          1f, 0f, 0.0f,
                          0.0f, 0.0f, 1.0f };
        matrix.setValues(values);

        Matrix matrix2 = new Matrix();

        float[] values2 = {1f,0f, sourceBitmap.getHeight(),
                          0f, 1f, 0.0f,
                0.0f, 0.0f, 1.0f };
        matrix2.setValues(values2);

        matrix.set(matrix2);
//        matrix.setRotate(90, 0, 0);  // 旋转 30 度，中心点为 (100, 100)
//        matrix.postTranslate(sourceBitmap.getHeight(), 0);    // 平移 100 像素到右边

        if (touchEvenHandler.currentStatus == touchEvenHandler.DEFAULT_BITMAP) {
            canvas.drawBitmap(sourceBitmap, matrix, pointPaint);
        } else {
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvas.drawBitmap(sourceBitmap, matrix, pointPaint);
            PointF point = touchEvenHandler.coordinatesToCanvas(100, 200);
            canvas.drawCircle((float) point.x, (float) point.y, 100, pointPaint);
        }
//    }

        Log.d("arvin", "matrix " + matrix.toString());
        Log.d("arvin", "touch——matrix " + touchEvenHandler.getMatrix().toString());
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed){
            // 分别获取到ImageView的宽度和高度
            touchEvenHandler=new TouchEvenHandler(this,sourceBitmap,true);
            touchEvenHandler.setCanRotate(false);

        }
    }



    public boolean onTouchEvent(MotionEvent event) {
        touchEvenHandler.touchEvent(event);
        return true;
    }





}
