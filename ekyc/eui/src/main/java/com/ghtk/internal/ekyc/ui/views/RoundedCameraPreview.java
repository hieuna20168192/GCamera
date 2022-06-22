//package vn.ghtk.kyc.ui.views;
//
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.graphics.PorterDuff;
//import android.graphics.PorterDuffXfermode;
//import android.util.AttributeSet;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.camera.view.PreviewView;
//
//import vn.ghtk.kyc.ui.R;
//
//public class RoundedCameraPreview extends PreviewView {
//
//
//    Path clipPath;
//    boolean isRound;
//
//    public RoundedCameraPreview(@NonNull Context context) {
//        super(context);
//    }
//
//    public RoundedCameraPreview(@NonNull Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public RoundedCameraPreview(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        TypedArray a = context.getTheme().obtainStyledAttributes(
//                attrs,
//                R.styleable.PreviewView,
//                0, 0);
//
//        try {
//            isRound = a.getBoolean(R.styleable.PreviewView_isRound, true);
//        } finally {
//            a.recycle();
//        }
//    }
//
//    public boolean isRound() {
//        return isRound;
//    }
//
//    public void setIsRound(boolean isRound) {
//        this.isRound = isRound;
//        invalidate();
//        requestLayout();
//    }
//
//    public RoundedCameraPreview(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }
//
//    @Override
//    protected void dispatchDraw(Canvas canvas) {
//        if (isRound) {
//            clipPath = new Path();
//            //TODO: define the circle you actually want
//            clipPath.addCircle(canvas.getWidth() / 2, canvas.getWidth() / 2, canvas.getWidth() / 2, Path.Direction.CW);
//
//            Paint paint = new Paint();
//            paint.setAntiAlias(true);
//            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//
//            canvas.clipPath(clipPath);
//            canvas.drawPath(clipPath, paint);
//        }
//        super.dispatchDraw(canvas);
//    }
//}