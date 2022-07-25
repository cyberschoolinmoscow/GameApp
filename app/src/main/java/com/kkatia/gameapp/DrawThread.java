package com.kkatia.gameapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;

public class DrawThread extends Thread {
    private SurfaceHolder surfaceHolder;

    private volatile boolean running = true;//флаг для остановки потока

//    private Bitmap pic;

    private Sprite playerBird;
    private Sprite enemyBird;

    private int towardPointX, towardPointY;
    private int backColor = Color.BLUE;
    Paint paint;
    private float viewHeight = 1000;
    private int points;
    private float viewWidth = 1000;

    public void setTowardPoint(int x, int y) {
        towardPointX = x;
        towardPointY = y;
    }

    //устанавливаем поля для хранения картинки bitmap, координат касания (towardPointX, towardPointY),
    //цвет фона, определяем конструктор

    public DrawThread(Context context, SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;

//        pic= BitmapFactory.decodeResource(context.getResources(),R.drawable.pic_1);
        paint = new Paint();
        paint.setColor(backColor);
        paint.setStyle(Paint.Style.FILL);
        playerBird = drawChar(context, R.drawable.atlas, 10, 0, 0, 100);
        enemyBird = drawChar(context, R.drawable.atlas2, 2000, 250, -300, 0);
    }

    private Sprite drawChar(Context context, int id, int coordX, int coordY, int vx, int vy) {

        Bitmap b = BitmapFactory.decodeResource(context.getResources(), id);
        int w = b.getWidth() / 5;
        int h = b.getHeight() / 3;
        Rect firstFrame = new Rect(0, 0, w, h);
        Sprite s = new Sprite(coordX, coordY, vx, vy, firstFrame, b);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                if (i == 2 && j == 3) {
                    continue;
                }
                s.addFrame(new Rect(j * w, i * h, j * w + w, i * w + w));
            }

        }
        return s;
    }

    public void requestStop() {
        running = false;
    }

    @Override
    public void run() {
        int smileX = 0;
        int smileY = 0;
        while (running) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {
                try {
//                    canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
//                    canvas.drawBitmap(pic, smileX, smileY, paint);
                    update(50);
                    gameDraw(canvas);
//                    if (smileX + pic.getWidth() / 2 < towardPointX) smileX+=10;
//                    if (smileX + pic.getWidth() / 2 > towardPointX) smileX-=10;
//                    if (smileY + pic.getHeight() / 2 < towardPointY) smileY+=10;
//                    if (smileY + pic.getHeight() / 2 > towardPointY) smileY-=10;
                } finally {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    protected void gameDraw(Canvas canvas) {
        canvas.drawARGB(250, 127, 199, 255);
        playerBird.draw(canvas);
        enemyBird.draw(canvas);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setTextSize(55.0f);
        p.setColor(Color.WHITE);
        canvas.drawText(points + "", viewWidth - 200, 70, p);
    }

    private void update(int timerInterval) {
        playerBird.update(timerInterval);
        enemyBird.update(timerInterval);

        if (playerBird.getY() + playerBird.getFrameHeight() > viewHeight) {
            playerBird.setY(viewHeight - playerBird.getFrameHeight());
            playerBird.setVy(-playerBird.getVy());
            points--;
        } else if (playerBird.getY() < 0) {
            playerBird.setY(0);
            playerBird.setVy(-playerBird.getVy());
            points--;
        }

        if (enemyBird.getX() < -enemyBird.getFrameWidth()) {
            teleportEnemy();
            points += 10;
        }

        if (enemyBird.intersect(playerBird)) {
            teleportEnemy();
            points -= 40;
        }

//        invalidate();
    }

    private void teleportEnemy() {
        enemyBird.setX(viewWidth + Math.random() * 500);
        enemyBird.setY(Math.random() * (viewHeight - enemyBird.getFrameHeight()));
    }


}