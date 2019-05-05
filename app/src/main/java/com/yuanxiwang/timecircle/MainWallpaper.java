package com.yuanxiwang.timecircle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.util.Calendar;

public class MainWallpaper extends WallpaperService {
    private Paint mSecPaint, mMinPaint, mHourPaint, mDayPaint, mMonthPaint, mYearPaint, mBgPaint;
    private int screenWidth;
    private int screenHeight;
    private static int currentColor = Color.RED;
    private static int normalColor = Color.BLUE;
    private static int bgColor = Color.WHITE;
    private int bgRes = -1;
    private int textSize = 25;
    private int distance = 25;
    private static int offsetX = 0;
    private static int offsetY = 0;
    private static int offsetAngle = 0;
    private SurfaceHolder holder;
    private UpdateReceiver updateReceiver;

    @Override
    public Engine onCreateEngine() {
        updateReceiver = new UpdateReceiver();
        registerReceiver(updateReceiver, new IntentFilter("UpdateReceiver"));
        return new MyEngine();
    }

    class MyEngine extends Engine {
        private Runnable viewRunnable;
        private Handler handler;

        public MyEngine() {
            this.handler = new Handler();
            this.initRunnable();
            this.handler.post(this.viewRunnable);
        }

        private void initRunnable() {
            this.viewRunnable = new Runnable() {
                @Override
                public void run() {
                    onDraw();
                }

            };
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            holder = surfaceHolder;
            screenWidth = ScreenUtil.getScreenWidth(getApplicationContext());
            screenHeight = ScreenUtil.getScreenHeight(getApplicationContext());
            initPaint();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
        }

        @Override
        public SurfaceHolder getSurfaceHolder() {
            return super.getSurfaceHolder();
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            if (this.handler != null) {
                this.handler.removeCallbacks(this.viewRunnable);
            } else {
                //nothing
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (this.handler != null) {
                this.handler.removeCallbacks(this.viewRunnable);
            } else {
                //nothing
            }
        }

        private void onDraw() {
            initPaint();
            this.handler.postDelayed(this.viewRunnable, 1000);
            Canvas canvas = holder.lockCanvas();
            // 对画布加锁
            if (canvas != null) {
                // 绘制背景色
                Calendar calendar = Calendar.getInstance();
                int seconds = calendar.get(Calendar.SECOND);
                int min = calendar.get(Calendar.MINUTE);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                drawBackGround(canvas);
                drawSecondsCircle(canvas, seconds);
                drawMinsCircle(canvas, min);
                drawHourCircle(canvas, hour);
                drawDayCircle(canvas, day);
                drawMonthCircle(canvas, month);
//        drawYearCircle(canvas, year);
                holder.unlockCanvasAndPost(canvas);
            }
        }

        /**
         * 初始化画笔
         */
        private void initPaint() {
            mSecPaint = new Paint();
            mSecPaint.setAntiAlias(true);//抗锯齿
            mSecPaint.setDither(true);//防抖动
            mSecPaint.setStyle(Paint.Style.FILL);
            mSecPaint.setTextSize(textSize);
            mSecPaint.setColor(normalColor);

            mMinPaint = new Paint();
            mMinPaint.setAntiAlias(true);//抗锯齿
            mMinPaint.setDither(true);//防抖动
            mMinPaint.setStyle(Paint.Style.FILL);
            mMinPaint.setTextSize(textSize);
            mMinPaint.setColor(normalColor);

            mHourPaint = new Paint();
            mHourPaint.setAntiAlias(true);//抗锯齿
            mHourPaint.setDither(true);//防抖动
            mHourPaint.setStyle(Paint.Style.FILL);
            mHourPaint.setTextSize(textSize);
            mHourPaint.setColor(normalColor);

            mDayPaint = new Paint();
            mDayPaint.setAntiAlias(true);//抗锯齿
            mDayPaint.setDither(true);//防抖动
            mDayPaint.setStyle(Paint.Style.FILL);
            mDayPaint.setColor(normalColor);
            mDayPaint.setTextSize(textSize);

            mMonthPaint = new Paint();
            mMonthPaint.setAntiAlias(true);//抗锯齿
            mMonthPaint.setDither(true);//防抖动
            mMonthPaint.setStyle(Paint.Style.FILL);
            mMonthPaint.setColor(normalColor);
            mMonthPaint.setTextSize(textSize);

            mYearPaint = new Paint();
            mYearPaint.setAntiAlias(true);//抗锯齿
            mYearPaint.setDither(true);//防抖动
            mYearPaint.setStyle(Paint.Style.FILL);
            mYearPaint.setColor(normalColor);
            mYearPaint.setTextSize(textSize);

            mBgPaint = new Paint();
            mBgPaint.setAntiAlias(true);
            mBgPaint.setDither(true);
            mBgPaint.setColor(bgColor);
        }

        private void drawYearCircle(Canvas canvas, int year) {
            mYearPaint.setColor(currentColor);
            Path path = new Path();
            String yearString = switchIntToString(year / 1000, true) + switchIntToString(year % 1000 / 100, true) + switchIntToString(year % 100 / 10, true) + switchIntToString(year % 10, true) + "年";
            path.moveTo((screenWidth - mYearPaint.measureText(yearString)) / 2, (screenHeight - mSecPaint.getTextSize()) / 2);
            path.lineTo(screenWidth / 2 + mYearPaint.measureText(yearString) / 2, (screenHeight - mSecPaint.getTextSize()) / 2);
            canvas.drawTextOnPath(yearString, path, 0, 0, mYearPaint);
        }

        private void drawMonthCircle(Canvas canvas, int month) {
            String monthString = null;
            for (int i = 0; i < 12; i++) {
                month++;
                String secondsString = "伍拾玖";
                String minString = "伍拾玖";
                String hourString = "伍拾玖";
                String dayString = "伍拾玖";
                if (month > 12) {
                    month -= 12;
                }
                if (month / 10 >= 1) {
                    monthString = switchIntToString(month / 10, false) + "拾" + switchIntToString(month % 10, false);
                } else {
                    monthString = switchIntToString(month % 10, false);
                }
                int rightStartX = (int) (screenWidth - mSecPaint.measureText(secondsString) - mMinPaint.measureText(minString) - mHourPaint.measureText(hourString) - mDayPaint.measureText(dayString) - mMonthPaint.measureText(monthString));
                int rightStartY = (int) ((screenHeight - mSecPaint.getTextSize()) / 2);
                int rightEndX = (int) (screenWidth - mSecPaint.measureText(secondsString) - mMinPaint.measureText(minString) - mHourPaint.measureText(hourString) - mDayPaint.measureText(dayString));
                int rightEndY = (int) ((screenHeight - mSecPaint.getTextSize()) / 2);
                Point startPoint = new Point(rightStartX - 5 * distance, rightStartY);
                Point endPoint = new Point(rightEndX - 5 * distance, rightEndY);
                if (i == 0) {
                    mMonthPaint.setColor(currentColor);
                    Path path = new Path();
                    path.moveTo(endPoint.x, endPoint.y);
                    path.lineTo(rightEndX + mMonthPaint.measureText("月"), endPoint.y);
                    canvas.drawTextOnPath("月", path, 0, 0, mMonthPaint);
                } else {
                    mMonthPaint.setColor(normalColor);
                }
                double angle = 360f * i * Math.PI / (12 * 180);
                drawTextCircle(canvas, monthString, startPoint, endPoint, angle, mMonthPaint);
            }
        }

        private void drawDayCircle(Canvas canvas, int day) {
            int currentMonthDay = getCurrentMonthLastDay();
            String dayString = null;
            for (int i = 0; i < currentMonthDay; i++) {
                String secondsString = "伍拾玖";
                String minString = "伍拾玖";
                String hourString = "伍拾玖";
                if (day > currentMonthDay) {
                    day -= currentMonthDay;
                }
                if (day / 10 >= 1) {
                    dayString = switchIntToString(day / 10, false) + "拾" + switchIntToString(day % 10, false);
                } else {
                    dayString = switchIntToString(day % 10, false);
                }
                double angle = 360f * i * Math.PI / (currentMonthDay * 180);
                int rightStartX = (int) (screenWidth - mSecPaint.measureText(secondsString) - mMinPaint.measureText(minString) - mHourPaint.measureText(hourString) - mDayPaint.measureText(dayString));
                int rightStartY = (int) ((screenHeight - mSecPaint.getTextSize()) / 2);
                int rightEndX = (int) (screenWidth - mSecPaint.measureText(secondsString) - mMinPaint.measureText(minString) - mHourPaint.measureText(hourString));
                int rightEndY = (int) ((screenHeight - mSecPaint.getTextSize()) / 2);
                Point startPoint = new Point(rightStartX - 4 * distance, rightStartY);
                Point endPoint = new Point(rightEndX - 4 * distance, rightEndY);
                if (i == 0) {
                    mDayPaint.setColor(currentColor);
                    Path path = new Path();
                    path.moveTo(endPoint.x, endPoint.y);
                    path.lineTo(endPoint.x + mDayPaint.measureText("号"), endPoint.y);
                    canvas.drawTextOnPath("号", path, 0, 0, mDayPaint);
                } else {
                    mDayPaint.setColor(normalColor);
                }
                drawTextCircle(canvas, dayString, startPoint, endPoint, angle, mDayPaint);
                day++;
            }
        }

        private void drawHourCircle(Canvas canvas, int hour) {
            String hourString = null;
            for (int i = 0; i < 24; i++) {
                String secondsString = "伍拾玖";
                String minString = "伍拾玖";
                if (hour >= 24) {
                    hour -= 24;
                }
                if (hour == 0) {
                    hourString = "零";
                } else if (hour / 10 >= 1) {
                    hourString = switchIntToString(hour / 10, false) + "拾" + switchIntToString(hour % 10, false);
                } else {
                    hourString = switchIntToString(hour % 10, false);
                }
                double angle = 360f * i * Math.PI / (24 * 180);
                int rightStartX = (int) (screenWidth - mSecPaint.measureText(secondsString) - mMinPaint.measureText(minString) - mHourPaint.measureText(hourString));
                int rightStartY = (int) ((screenHeight - mSecPaint.getTextSize()) / 2);
                int rightEndX = (int) (screenWidth - mSecPaint.measureText(secondsString) - mMinPaint.measureText(minString));
                int rightEndY = (int) ((screenHeight - mSecPaint.getTextSize()) / 2);
                Point startPoint = new Point(rightStartX - 3 * distance, rightStartY);
                Point endPoint = new Point(rightEndX - 3 * distance, rightEndY);
                if (i == 0) {
                    mHourPaint.setColor(currentColor);
                    Path path = new Path();
                    path.moveTo(endPoint.x, endPoint.y);
                    path.lineTo(endPoint.x + mHourPaint.measureText("时"), endPoint.y);
                    canvas.drawTextOnPath("时", path, 0, 0, mHourPaint);
                } else {
                    mHourPaint.setColor(normalColor);
                }
                drawTextCircle(canvas, hourString, startPoint, endPoint, angle, mHourPaint);
                hour++;
            }
        }

        /**
         * 画分圈
         *
         * @param canvas
         */
        private void drawMinsCircle(Canvas canvas, int min) {
            String minString = null;
            for (int i = 0; i < 60; i++) {
                String secondsString = "伍拾玖";
                if (min >= 60) {
                    min -= 60;
                }
                if (min == 0) {
                    minString = "零";
                } else if (min / 10 >= 1) {
                    minString = switchIntToString(min / 10, false) + "拾" + switchIntToString(min % 10, false);
                } else {
                    minString = switchIntToString(min % 10, false);
                }
                double angle = 360f * i * Math.PI / (60 * 180);
                int rightStartX = (int) (screenWidth - mSecPaint.measureText(secondsString) - mMinPaint.measureText(minString));
                int rightStartY = (int) ((screenHeight - mSecPaint.getTextSize()) / 2);
                int rightEndX = (int) (screenWidth - mSecPaint.measureText(secondsString));
                int rightEndY = (int) ((screenHeight - mSecPaint.getTextSize()) / 2);
                Point startPoint = new Point(rightStartX - 2 * distance, rightStartY);
                Point endPoint = new Point(rightEndX - 2 * distance, rightEndY);
                if (i == 0) {
                    mMinPaint.setColor(currentColor);
                    Path path = new Path();
                    path.moveTo(endPoint.x, endPoint.y);
                    path.lineTo(endPoint.x + mMinPaint.measureText("分"), endPoint.y);
                    canvas.drawTextOnPath("分", path, 0, 0, mMinPaint);
                } else {
                    mMinPaint.setColor(normalColor);
                }
                drawTextCircle(canvas, minString, startPoint, endPoint, angle, mMinPaint);
                min++;
            }
        }

        /**
         * 画秒圈
         *
         * @param canvas
         */
        private void drawSecondsCircle(Canvas canvas, int seconds) {
            String secondsString = null;
            for (int i = 0; i < 60; i++) {
                if (seconds >= 60) {
                    seconds -= 60;
                }
                if (seconds == 0) {
                    secondsString = "零";
                } else if (seconds / 10 >= 1) {
                    secondsString = switchIntToString(seconds / 10, false) + "拾" + switchIntToString(seconds % 10, false);
                } else {
                    secondsString = switchIntToString(seconds % 10, false);
                }
                double angle = 360f * i * Math.PI / (60 * 180);
                int rightStartX = (int) (screenWidth - mSecPaint.measureText(secondsString));
                int rightStartY = (int) ((screenHeight - mSecPaint.getTextSize()) / 2);
                int rightEndX = screenWidth;
                int rightEndY = (int) ((screenHeight - mSecPaint.getTextSize()) / 2);
                Point startPoint = new Point(rightStartX - distance, rightStartY);
                Point endPoint = new Point(rightEndX - distance, rightEndY);
                if (i == 0) {
                    mSecPaint.setColor(currentColor);
                    Path path = new Path();
                    path.moveTo(endPoint.x, endPoint.y);
                    path.lineTo(endPoint.x + mSecPaint.measureText("秒"), endPoint.y);
                    canvas.drawTextOnPath("秒", path, 0, 0, mSecPaint);
                } else {
                    mSecPaint.setColor(normalColor);
                }
                drawTextCircle(canvas, secondsString, startPoint, endPoint, angle, mSecPaint);
                seconds++;
            }
        }

        /**
         * 沿圆弧画text
         *
         * @param canvas        画布
         * @param secondsString 需要绘画的string
         * @param angle         两个text之间的圆心角
         */
        private void drawTextCircle(Canvas canvas, String secondsString, Point startPoint, Point endPoint, double angle, Paint paint) {
            Path path = new Path();
            int centerX = screenWidth / 2;
            int offsetStartX = startPoint.x - (int) (centerX + (startPoint.x - centerX) * Math.cos(angle));
            int offsetStartY = (int) ((startPoint.x - centerX) * Math.sin(angle));
            int offsetEndX = endPoint.x - (int) (centerX + (endPoint.x - centerX) * Math.cos(angle));
            int offSetEndY = (int) ((endPoint.x - centerX) * Math.sin(angle));
            path.moveTo(startPoint.x - offsetStartX, startPoint.y + offsetStartY);
            path.lineTo(endPoint.x - offsetEndX, endPoint.y + offSetEndY);
            canvas.drawTextOnPath(secondsString, path, 0, 0, paint);
        }

        private String switchIntToString(int seconds, boolean needZore) {
            switch (seconds) {
                case 0:
                    if (needZore) {
                        return "零";
                    } else {
                        return "";
                    }
                case 1:
                    return "壹";
                case 2:
                    return "贰";
                case 3:
                    return "弎";
                case 4:
                    return "肆";
                case 5:
                    return "伍";
                case 6:
                    return "陆";
                case 7:
                    return "柒";
                case 8:
                    return "捌";
                case 9:
                    return "玖";
            }
            return "";
        }

        private void drawBackGround(Canvas canvas) {
            RectF bgRectF = new RectF(0, 0, screenWidth, screenHeight);
            if (bgRes != -1) {
                canvas.drawBitmap(getBitmap(getApplicationContext(), bgRes), 0, 0, new Paint());
            } else {
                canvas.drawRect(bgRectF, mBgPaint);
            }
        }

        private Bitmap getBitmap(Context context, int vectorDrawableId) {
            Bitmap bitmap = null;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                Drawable vectorDrawable = context.getDrawable(vectorDrawableId);
                bitmap = Bitmap.createBitmap(screenWidth,
                        screenHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                vectorDrawable.draw(canvas);
            } else {
                bitmap = BitmapFactory.decodeResource(context.getResources(), vectorDrawableId);
            }
            return bitmap;
        }

        /**
         * 取得当月天数
         */
        public int getCurrentMonthLastDay() {
            Calendar a = Calendar.getInstance();
            a.set(Calendar.DATE, 1);//把日期设置为当月第一天
            a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
            int maxDate = a.get(Calendar.DATE);
            return maxDate;
        }
    }

    public static class UpdateReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.hasExtra("currentColor")) {
                currentColor = intent.getIntExtra("currentColor", currentColor);
            }
            if (intent != null && intent.hasExtra("normalColor")) {
                normalColor = intent.getIntExtra("normalColor", normalColor);
            }
            if (intent != null && intent.hasExtra("offsetAngle")) {
                offsetAngle = intent.getIntExtra("offsetAngle", offsetAngle);
            }
            if (intent != null && intent.hasExtra("offsetX")) {
                offsetX = intent.getIntExtra("offsetX", offsetX);
            }
            if (intent != null && intent.hasExtra("offsetY")) {
                offsetY = intent.getIntExtra("offsetY", offsetY);
            }
            if (intent != null && intent.hasExtra("bgColor")) {
                bgColor = intent.getIntExtra("bgColor", bgColor);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (updateReceiver != null) {
            unregisterReceiver(updateReceiver);
            updateReceiver = null;
        }
    }
}
