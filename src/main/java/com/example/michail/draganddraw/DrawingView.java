package com.example.michail.draganddraw;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

public class DrawingView extends View {
    public static final String TAG = "BoxDrawView";
    private Shape mCurrentShape;
    private ArrayList<Shape> mBoxen = new ArrayList<>();
    private ArrayList<Shape> mCircle = new ArrayList<>();
    private ArrayList<Shape> mStar = new ArrayList<>();
    private ArrayList<Shape> mTriangle = new ArrayList<>();
    private ArrayList<Stroke> mStroke = new ArrayList<>();
    private ArrayList<PointF> mLastShape = new ArrayList<>();
    private DrawingView mView;
    private int mDrawIndex = 0;
    private Path mPath;
    private static final float TOUCH_TOLERANCE = 4;
    private float mX, mY;


    public static boolean index;
    private Paint mBackgroundPaint;
    private PointF mRotationPoint;

    private static final String SAVED_BOXEN = "SavedBoxen";

    public DrawingView(Context context) {
        super(context);


    }

    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);


        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(Color.WHITE);
    }

    private void onPaint() {
        if (DragAndDrawFragment.mItemName != null) {
            switch (DragAndDrawFragment.mItemName) {
                case "Undo":
                    undo();
                    break;
                case "Clear":
                    clear();
                    break;
                case "Save":
                    showSavePaintingConfirmationDialog();
                    break;
                default:
                    break;
            }
        }
    }

    private void showSavePaintingConfirmationDialog() {
        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(NavigationFragment.PREF_SAVE, true)) {
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(getContext());
            saveDialog.setTitle(R.string.dialog_save_title);
            saveDialog.setMessage(R.string.dialog_save_message);
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    View view = getRootView().findViewById(R.id.view_id);
                    view.setDrawingCacheEnabled(true);

                    String imgSaved = MediaStore.Images.Media.insertImage(
                            getContext().getContentResolver(), view.getDrawingCache(),
                            UUID.randomUUID().toString() + ".png", "drawing");
                    if (imgSaved != null) {
                        Toast savedToast = Toast.makeText(getContext().getApplicationContext(),
                                R.string.toast_positive_save, Toast.LENGTH_SHORT);
                        savedToast.show();
                    } else {
                        Toast unsavedToast = Toast.makeText(getContext().getApplicationContext(),
                                R.string.toast_negative_save, Toast.LENGTH_SHORT);
                        unsavedToast.show();
                    }
                    view.destroyDrawingCache();
                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(NavigationFragment.PREF_SAVE, false).apply();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(mBackgroundPaint);
        onPaint();
        // Log.i(TAG, mDrawIndex + "=  draw index ");
        for (int i = 0; i <= mDrawIndex; i++) {
            BoxItem(canvas, i);
            CircleItem(canvas, i);
            StrokeItem(canvas, i);
            StarItem(canvas, i);
            TriangleItem(canvas, i);
        }
        invalidate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF(event.getX(), event.getY());
        String action = "";

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDrawIndex++;
                action = "ACTION DOWN";
                mLastShape.add(current);
                mCurrentShape = new Shape(current);
                mCurrentShape.setDrawIndex(mDrawIndex);
                Paint itemColor = new Paint();
                itemColor.setColor(PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(NavigationFragment.PREF_COLOR, 0xffff0000));
                mCurrentShape.setColor(itemColor);
                if (DragAndDrawFragment.mItemName != null) {
                    Log.i(TAG, DragAndDrawFragment.mItemName + " ITEM NAME");
                    switch (DragAndDrawFragment.mItemName) {
                        case "Square":
                            mBoxen.add(mCurrentShape);
                            break;
                        case "Circle":
                            mCircle.add(mCurrentShape);
                            break;
                        case "Star":
                            mStar.add(mCurrentShape);
                            break;
                        case "Stroke":
                            touchStart(event.getX(), event.getY(), current, mDrawIndex);
                            break;
                        case "Triangle":
                            mTriangle.add(mCurrentShape);
                            break;
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                event.getPointerId(1);
                PointF rotationPoint = new PointF(event.getX(1), event.getY(1));
                mRotationPoint = rotationPoint;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mCurrentShape != null) {
                    if (event.getPointerCount() == 1) {
                        mCurrentShape.setCurrent(current);
                        if (DragAndDrawFragment.mItemName != null) {
                            Log.i(TAG, DragAndDrawFragment.mItemName + " ITEM NAME");
                            switch (DragAndDrawFragment.mItemName) {
                                case "Stroke":
                                    touchMove(event.getX(), event.getY());
                                    break;
                            }
                        }

                    } else if (event.getPointerCount() == 2) {
                        float xCenter = (mCurrentShape.getOrigin().x + mCurrentShape.getCurrent().x) / 2;
                        float yCenter = (mCurrentShape.getOrigin().y + mCurrentShape.getCurrent().y) / 2;
                        PointF centerOfRotation = new PointF(xCenter, yCenter);
                        mCurrentShape.setCenterOfRotation(centerOfRotation);

                        PointF movePoint = new PointF(event.getX(1), event.getY(1));
                        mCurrentShape.setRotationAngel(onRotation(mCurrentShape.getOrigin(), movePoint));

                    } else {
                        return false;
                    }
                    invalidate();
                }
                action = "ACTION MOVE";
                break;
            case MotionEvent.ACTION_UP:
                if (DragAndDrawFragment.mItemName != null) {
                    Log.i(TAG, DragAndDrawFragment.mItemName + " ITEM NAME");
                    switch (DragAndDrawFragment.mItemName) {
                        case "Stroke":
                            touchUp();
                            break;
                    }
                }

            case MotionEvent.ACTION_POINTER_UP:
                action = "ACTION_UP";
                mCurrentShape = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                mCurrentShape = null;
                action = "ACTION CANCEL";
                break;
        }
        Log.i(TAG, action + " at x=" + current.x + " ,y=" + current.y);

        return true;
    }

    private float onRotation(PointF origin, PointF movePoint) {
        double delta_x = (origin.x - movePoint.x);
        double delta_y = (origin.y - movePoint.y);
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    private void undo() {
        index = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(NavigationFragment.PREF_UNDO, true);
        // Log.i(TAG, mBoxen.size() + "  =  Box size");
        //  Log.i(TAG, mCircle.size() + "  =  Circle size");
        //   Log.i(TAG, mStroke.size() + "  =  Stroke size");
       /* for (int i = 0; i < mLastShape.size(); i++) {
            Log.i(TAG, mLastShape.get(i).x + "LAST");
            for (int j = 0; j < mBoxen.size(); j++) {
                Log.i(TAG, mBoxen.get(j).getOrigin().x + "BOX");
            }
        }*/
        Log.i(TAG, "Undo");
        if (mBoxen.size() > 0 && mLastShape.size() > 0 && mBoxen.get(mBoxen.size() - 1).getOrigin() == mLastShape.get(mLastShape.size() - 1) && index) {

            mBoxen.remove(mBoxen.size() - 1);
            mLastShape.remove(mLastShape.size() - 1);
            //  Log.i(TAG, mBoxen.size() + "  =  Box size11111");
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(NavigationFragment.PREF_UNDO, false).apply();
        } else if (mCircle.size() > 0 && mLastShape.size() > 0 && mCircle.get(mCircle.size() - 1).getOrigin() == mLastShape.get(mLastShape.size() - 1) && index) {

            mCircle.remove(mCircle.size() - 1);
            mLastShape.remove(mLastShape.size() - 1);
            // Log.i(TAG, mCircle.size() + "  =  Circle size1111");
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(NavigationFragment.PREF_UNDO, false).apply();
        } else if (mStroke.size() > 0 && mLastShape.size() > 0 && mStroke.get(mStroke.size() - 1).getOrigin() == mLastShape.get(mLastShape.size() - 1) && index) {

            mStroke.remove(mStroke.size() - 1);
            mLastShape.remove(mLastShape.size() - 1);
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(NavigationFragment.PREF_UNDO, false).apply();
        } else if (mStar.size() > 0 && mLastShape.size() > 0 && mStar.get(mStar.size() - 1).getOrigin() == mLastShape.get(mLastShape.size() - 1) && index) {

            mStar.remove(mStar.size() - 1);
            mLastShape.remove(mLastShape.size() - 1);
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(NavigationFragment.PREF_UNDO, false).apply();
        } else if (mTriangle.size() > 0 && mLastShape.size() > 0 && mTriangle.get(mTriangle.size() - 1).getOrigin() == mLastShape.get(mLastShape.size() - 1) && index) {

            mTriangle.remove(mTriangle.size() - 1);
            mLastShape.remove(mLastShape.size() - 1);
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(NavigationFragment.PREF_UNDO, false).apply();
        }

    }

    private void clear() {
        Log.i(TAG, "Clear");
        mBoxen.clear();
        mCircle.clear();
        mStroke.clear();
        mStar.clear();
        mTriangle.clear();
    }


    private void BoxItem(Canvas canvas, int drawIndex) {
        for (Shape shape : mBoxen) {
            float left = Math.min(shape.getOrigin().x, shape.getCurrent().x);
            float right = Math.max(shape.getOrigin().x, shape.getCurrent().x);
            float top = Math.min(shape.getOrigin().y, shape.getCurrent().y);
            float bottom = Math.max(shape.getOrigin().y, shape.getCurrent().y);
            if (shape.getDrawIndex() == drawIndex) {
                if (shape.getRotationAngel() != 0) {
                    canvas.save();
                    canvas.rotate(shape.getRotationAngel(), shape.getCenterOfRotation().x, shape.getCenterOfRotation().y);
                    canvas.drawRect(left, top, right, bottom, shape.getColor());
                    canvas.restore();
                } else {
                    canvas.drawRect(left, top, right, bottom, shape.getColor());
                }
            }
        }
    }

    private void CircleItem(Canvas canvas, int drawIndex) {
        for (Shape shape : mCircle) {
            float radius = Math.abs(shape.getCurrent().x - shape.getOrigin().x);
            if (shape.getDrawIndex() == drawIndex) {
                canvas.drawCircle(shape.getOrigin().x, shape.getOrigin().y, radius, shape.getColor());
            }
        }
    }

    private void TriangleItem(Canvas canvas, int drawIndex) {
        for (Shape shape : mTriangle) {
            Path path = new Path();
            float x = Math.abs(shape.getOrigin().x - shape.getCurrent().x);
            path.moveTo(shape.getOrigin().x, shape.getOrigin().y);
            path.lineTo(shape.getCurrent().x, shape.getCurrent().y);
            path.lineTo(shape.getOrigin().x-x, shape.getCurrent().y);
            path.lineTo(shape.getOrigin().x, shape.getOrigin().y);
            Paint trianglePaint = new Paint();
            trianglePaint.setStrokeWidth(20f);
            trianglePaint.setStyle(Paint.Style.FILL);
            trianglePaint.setColor(shape.getColor().getColor());
            if (shape.getDrawIndex() == drawIndex) {
                if (shape.getRotationAngel() != 0) {
                    canvas.save();
                    canvas.rotate(shape.getRotationAngel(), shape.getOrigin().x, shape.getOrigin().y);
                    canvas.drawPath(path, trianglePaint);
                    canvas.restore();
                } else {
                    canvas.drawPath(path, trianglePaint);
                }

            }
        }
    }

    private void StarItem(Canvas canvas, int drawIndex) {
        for (Shape shape : mStar) {
            Path path = new Path();
            float radius = Math.abs(shape.getOrigin().x - shape.getCurrent().x);
            path.reset();
            path.moveTo(shape.getOrigin().x + (radius * (float) Math.cos(0 * Math.PI / 180))
                    , shape.getOrigin().y + radius * (float) Math.sin(0 * Math.PI / 180));
            for (int i = 1; i < 5; i++) {
                float x = shape.getOrigin().x + (radius * (float) Math.cos(72 * i * Math.PI / 90));
                float y = shape.getOrigin().y + radius * (float) Math.sin(72 * i * Math.PI / 90);
                path.lineTo(x, y);


            }
            path.lineTo(shape.getOrigin().x + (radius * (float) Math.cos(0 * Math.PI / 180))
                    , shape.getOrigin().y + radius * (float) Math.sin(0 * Math.PI / 180));
            path.close();
            Paint starPaint = new Paint();
            starPaint.setStrokeWidth(20f);
            starPaint.setStyle(Paint.Style.FILL);
            starPaint.setColor(shape.getColor().getColor());

            if (shape.getDrawIndex() == drawIndex) {
                if (shape.getRotationAngel() != 0) {
                    canvas.save();
                    canvas.rotate(shape.getRotationAngel(), shape.getOrigin().x, shape.getOrigin().y);
                    canvas.drawPath(path, starPaint);
                    canvas.restore();

                } else {
                    canvas.drawPath(path, starPaint);
                }
            }
        }
    }

    private void StrokeItem(Canvas canvas, int drawIndex) {

        for (Stroke stroke : mStroke) {
            Paint strokePaint = new Paint();
            strokePaint.setColor(stroke.color);
            strokePaint.setAntiAlias(true);
            strokePaint.setStrokeWidth(stroke.strokeWidth);
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setStrokeJoin(Paint.Join.ROUND);
            strokePaint.setStrokeCap(Paint.Cap.ROUND);
            if (stroke.drawIndex == drawIndex) {
                canvas.drawPath(stroke.path, strokePaint);
            }

        }
    }

    private void touchStart(float x, float y, PointF origin, int drawIndex) {
        mPath = new Path();
        Stroke currentStroke = new Stroke(mCurrentShape.getColor().getColor(),
                (int) PreferenceManager.getDefaultSharedPreferences(getContext()).getFloat(NavigationFragment.PREF_SIZE, 20f), mPath, drawIndex);
        currentStroke.setOrigin(origin);
        mStroke.add(currentStroke);

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        mPath.lineTo(mX, mY);
    }
}
