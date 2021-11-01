package com.customratingbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class RatingBar extends View {
    private int starSpace; //星星间距
    private int starNum;   //星星个数
    private int starSize;  //星星大小
    private float rating;  //评分几星
    private Bitmap fillBitmap; //评分的星星
    private Drawable emptyDrawable; //背景星星
    private boolean integerStep = false; //评分是否是整数
    private Paint paint;


    public RatingBar(Context context) {
        this(context, null);
    }

    public RatingBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RatingBar);
        this.starSpace = (int) mTypedArray.getDimension(R.styleable.RatingBar_starSpace, 10);
        this.starSize = (int) mTypedArray.getDimension(R.styleable.RatingBar_starSize, 20);
        this.starNum = mTypedArray.getInteger(R.styleable.RatingBar_starNum, 5);
        this.rating = mTypedArray.getFloat(R.styleable.RatingBar_rating, 0f);
        this.emptyDrawable = mTypedArray.getDrawable(R.styleable.RatingBar_starEmpty);
        this.fillBitmap = drawableToBitmap(mTypedArray.getDrawable(R.styleable.RatingBar_starFill));
        mTypedArray.recycle();

        init();
    }

    private void init() {
        setClickable(true);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(fillBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量空间宽高
        setMeasuredDimension(starSize * starNum + starSpace * (starNum - 1), starSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (fillBitmap == null || emptyDrawable == null) {
            return;
        }
        //绘制背景星星
        for (int i = 0; i < starNum; i++) {
            emptyDrawable.setBounds((starSpace + starSize) * i, 0, (starSpace + starSize) * i + starSize, starSize);
            emptyDrawable.draw(canvas);
        }
        //绘制评分的星星
        if (rating > 1) {
            canvas.drawRect(0, 0, starSize, starSize, paint);
            if (rating - (int) (rating) == 0) {
                for (int i = 1; i < rating; i++) {
                    canvas.translate(starSpace + starSize, 0);
                    canvas.drawRect(0, 0, starSize, starSize, paint);
                }
            } else {
                for (int i = 1; i < rating - 1; i++) {
                    canvas.translate(starSpace + starSize, 0);
                    canvas.drawRect(0, 0, starSize, starSize, paint);
                }
                canvas.translate(starSpace + starSize, 0);
                canvas.drawRect(0, 0, starSize * (Math.round((rating - (int) (rating)) * 10) * 1.0f / 10), starSize, paint);
            }
        } else {
            canvas.drawRect(0, 0, starSize * rating, starSize, paint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        if (x < 0) x = 0;
        if (x > getMeasuredWidth()) x = getMeasuredWidth();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                //滑动后设置当前星级
                setRating(x * 1.0f / (getMeasuredWidth() * 1.0f / starNum));
                break;
            case MotionEvent.ACTION_UP: {
                break;
            }
        }
        invalidate();
        return super.onTouchEvent(event);
    }

    /**
     * 评分是否为整数
     *
     * @param integerStep
     */
    public void setIntegerStep(boolean integerStep) {
        this.integerStep = integerStep;
    }

    /**
     * 设置评分
     *
     * @param cRating
     */
    public void setRating(float cRating) {
        if (integerStep) {
            rating = (int) Math.ceil(cRating);
        } else {
            rating = Math.round(cRating * 10) * 1.0f / 10;
        }
        if (this.onRatingChangeListener != null) {
            this.onRatingChangeListener.onRatingChange(rating);
        }
        invalidate();
    }

    /**
     * 获取评分
     *
     * @return
     */
    public float getRating() {
        return rating;
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) return null;
        Bitmap bitmap = Bitmap.createBitmap(starSize, starSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, starSize, starSize);
        drawable.draw(canvas);
        return bitmap;
    }

    private OnRatingChangeListener onRatingChangeListener;

    /**
     * 回调接口
     */
    public interface OnRatingChangeListener {
        void onRatingChange(float rating);
    }

    public void OnRatingChangeListener(OnRatingChangeListener onRatingChangeListener) {
        this.onRatingChangeListener = onRatingChangeListener;
    }
}
