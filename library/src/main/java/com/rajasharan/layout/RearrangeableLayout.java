package com.rajasharan.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by raja on 6/19/15.
 */
public class RearrangeableLayout extends ViewGroup {
    private static final String TAG = "RearrangeableLayout";

    private PointF mStartTouch;
    private View mSelectedChild;
    private float mSelectionZoom;
    private Paint mSelectionPaint;
    private Paint mOutlinePaint;
    private SparseArray<Parcelable> mContainer;

    /* callback to update clients whenever child is dragged */
    private ChildPositionListener mListener;

    /* used by ChildPositionListener callback */
    private Rect mChildStartRect;
    private Rect mChildEndRect;

    public RearrangeableLayout(Context context) {
        this(context, null);
    }

    public RearrangeableLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RearrangeableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mStartTouch = null;
        mSelectedChild = null;
        mContainer = new SparseArray<Parcelable>(5);
        mListener = null;
        mChildStartRect = null;
        mChildEndRect = null;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RearrangeableLayout);
        float strokeWidth = a.getDimension(R.styleable.RearrangeableLayout_outlineWidth, 2.0f);
        int color = a.getColor(R.styleable.RearrangeableLayout_outlineColor, Color.GRAY);
        float alpha = a.getFloat(R.styleable.RearrangeableLayout_selectionAlpha, 0.5f);
        mSelectionZoom = a.getFloat(R.styleable.RearrangeableLayout_selectionZoom, 1.2f);
        a.recycle();

        float[] filter = new float[]{
                1f, 0f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f, 0f,
                0f, 0f, 1f, 0f, 0f,
                0f, 0f, 0f, alpha, 0f
        };
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(new ColorMatrix(filter));

        mOutlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOutlinePaint.setStyle(Paint.Style.STROKE);
        mOutlinePaint.setStrokeWidth(strokeWidth);
        mOutlinePaint.setColor(color);
        mOutlinePaint.setColorFilter(colorFilter);

        mSelectionPaint = new Paint();
        mSelectionPaint.setColorFilter(colorFilter);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return super.checkLayoutParams(p) && p instanceof LayoutParams;
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        width = Math.max(width, getMinimumWidth());
        height = Math.max(height, getMinimumHeight());

        //Log.d(TAG, String.format("onMeasure: (%d, %d)", width, height));
        //measureChildren(widthMeasureSpec, heightMeasureSpec);

        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            LayoutParams mp = (LayoutParams) view.getLayoutParams();
            view.measure(MeasureSpec.makeMeasureSpec(width - mp.leftMargin - mp.rightMargin, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(height - mp.topMargin - mp.bottomMargin, MeasureSpec.AT_MOST));

            //int w = view.getMeasuredWidth();
            //int h = view.getMeasuredHeight();
            //Log.d(TAG, String.format("View #%d: (%d, %d)", i, w, h));
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mSelectedChild == null) {
            doInitialLayout(l, t, r, b, getChildCount());
        }
    }

    private void doInitialLayout(int l, int t, int r, int b, int count) {
        int currentLeft = l;
        int currentTop = t;
        int prevChildBottom = -1;
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            LayoutParams mp = (LayoutParams) view.getLayoutParams();
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();
            int left, top, right, bottom;

            if (view.getVisibility() != View.GONE && !mp.moved) {
                if (currentTop + height > b || l + width > r) {
                    Toast.makeText(getContext(), "Couldn't fit a child View, skipping it", Toast.LENGTH_SHORT)
                            .show();
                    Log.d(TAG, "Couldn't fit a child View, skipping it");
                    continue;
                }
                if (currentLeft + width > r) {
                    left = l + mp.leftMargin;
                    currentTop = prevChildBottom;
                } else {
                    left = currentLeft + mp.topMargin;
                }
                top = currentTop + mp.topMargin;
                right = left + width;
                bottom = top + height;
                //Log.d(TAG, String.format("Layout #%d: (%d, %d, %d, %d)", i, left, top, right, bottom));
                mp.left = left;
                mp.top = top;
                view.layout(left, top, right, bottom);

                currentLeft = right + mp.rightMargin;
                prevChildBottom = bottom + mp.bottomMargin;
            } else if (mp.moved && view != mSelectedChild) {
                int x1 = Math.round(mp.left);
                int y1 = Math.round(mp.top);
                int x2 = Math.round(mp.left) + width;
                int y2 = Math.round(mp.top) + height;
                view.layout(x1, y1, x2, y2);
            }
        }
    }

    /**
     * this method can be used to force layout on a child
     * to recalculate its hit-rect,
     * otherwise outline border of the selected child is
     * drawn at the old position
     */
    private void layoutSelectedChild(LayoutParams lp) {
        int l = Math.round(lp.left);
        int t = Math.round(lp.top);
        int r = l + mSelectedChild.getMeasuredWidth();
        int b = t + mSelectedChild.getMeasuredHeight();

        lp.moved = true;
        mSelectedChild.layout(l, t, r, b);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mSelectedChild != null) {
            mSelectedChild.setVisibility(View.INVISIBLE);
        }
        super.dispatchDraw(canvas);

        if (mSelectedChild != null) {
            Rect rect = new Rect();
            mSelectedChild.getHitRect(rect);

            int restorePoint = canvas.save();
            canvas.scale(mSelectionZoom, mSelectionZoom, rect.centerX(), rect.centerY());
            canvas.drawRect(rect, mOutlinePaint);

            mSelectedChild.setDrawingCacheEnabled(true);
            Bitmap child = mSelectedChild.getDrawingCache();
            if (child != null) {
                LayoutParams lp = (LayoutParams) mSelectedChild.getLayoutParams();
                canvas.drawBitmap(child, lp.left, lp.top, mSelectionPaint);
            } else {
                Log.d(TAG, "drawingCache not found! Maybe because of hardware acceleration");
                mSelectedChild.draw(canvas);
            }
            canvas.restoreToCount(restorePoint);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        if (ev.getActionMasked() == MotionEvent.ACTION_MOVE) {
            prepareTouch(x, y);
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        //mViewDragHelper.processTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                prepareTouch(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mSelectedChild != null && mStartTouch != null) {
                    LayoutParams lp = (LayoutParams) mSelectedChild.getLayoutParams();
                    float dx = x - mStartTouch.x;
                    float dy = y - mStartTouch.y;

                    lp.left = lp.initial.x + dx;
                    if (lp.left < 0.0f) {
                        lp.left = 0.0f;
                    }

                    lp.top = lp.initial.y + dy;
                    if (lp.top < 0.0f) {
                        lp.top = 0.0f;
                    }

                    /* layout child otherwise hit-rect is not recalculated */
                    layoutSelectedChild(lp);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            default:
                if (mSelectedChild != null) {
                    if (mListener != null && mChildStartRect != null) {
                        mChildEndRect = new Rect();
                        mSelectedChild.getHitRect(mChildEndRect);
                        mListener.onChildMoved(mSelectedChild, mChildStartRect, mChildEndRect);
                    }

                    mSelectedChild.setVisibility(View.VISIBLE);
                    mSelectedChild = null;
                }
                break;
        }
        return true;
    }

    private void prepareTouch(float x, float y) {
        mStartTouch = null;
        mSelectedChild = findChildViewInsideTouch(Math.round(x), Math.round(y));
        if (mSelectedChild != null) {
            bringChildToFront(mSelectedChild);
            LayoutParams lp = (LayoutParams) mSelectedChild.getLayoutParams();
            lp.initial = new PointF(lp.left, lp.top);
            mStartTouch = new PointF(x, y);
            if (mChildStartRect == null) {
                mChildStartRect = new Rect();
                mSelectedChild.getHitRect(mChildStartRect);
            }
        }
    }

    /**
     * Search by hightest index to lowest so that the
     * most recently touched child is found first
     *
     * @return selectedChild
     */
    private View findChildViewInsideTouch(int x, int y) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View view = getChildAt(i);
            Rect rect = new Rect();
            view.getHitRect(rect);
            if (rect.contains(x, y)) {
                mChildStartRect = rect;
                return view;
            }
        }
        return null;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        mContainer = ss.container;
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            LayoutParams lp = (LayoutParams) view.getLayoutParams();

            if (view.getId() != NO_ID) {
                SavedState s = (SavedState) mContainer.get(view.getId());
                lp.left = s.left;
                lp.top = s.top;
                lp.moved = s.movedFlag;
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            view.saveHierarchyState(mContainer);

            if (view.getId() != NO_ID) {
                SavedState s = new SavedState(mContainer.get(view.getId()));
                s.left = lp.left;
                s.top = lp.top;
                s.movedFlag = lp.moved;
                mContainer.put(view.getId(), s);
            }
        }
        Parcelable p = super.onSaveInstanceState();
        SavedState ss = new SavedState(p);
        ss.container = mContainer;
        return ss;
    }

    /**
     * set ChildPositionListener to receive updates whenever child is moved
     *
     * @param listener
     */
    public void setChildPositionListener(ChildPositionListener listener) {
        mListener = listener;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder(128);
        out.append(TAG);
        out.append(" mSelectedChild: ");
        if (mSelectedChild != null) {
            out.append(this.mSelectedChild.toString());
        }
        return out.toString();
    }

    public interface ChildPositionListener {
        /**
         * this callback is invoked whenever child is moved
         *
         * @param childView   the current child view that was dragged
         * @param oldPosition the original position from where child was dragged
         * @param newPosition the new position where child is currently laid
         */
        void onChildMoved(View childView, Rect oldPosition, Rect newPosition);
    }

    public static class LayoutParams extends MarginLayoutParams {
        float left;
        float top;
        PointF initial;
        boolean moved;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            left = -1.0f;
            top = -1.0f;
            initial = new PointF(0.0f, 0.0f);
            moved = false;
        }
    }

    private static class SavedState extends BaseSavedState {
        float left, top;
        boolean movedFlag;
        SparseArray<Parcelable> container;

        public SavedState(Parcelable p) {
            super(p);
        }
    }
}
