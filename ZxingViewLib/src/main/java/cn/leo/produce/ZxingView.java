package cn.leo.produce;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatActivity;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import cn.leo.produce.config.Utils;
import cn.leo.produce.config.ZvParams;
import cn.leo.produce.decode.ResultCallBack;
import cn.leo.produce.lifecycle.AttachFragment;
import cn.leo.produce.lifecycle.PermissionListener;

/**
 * @description: QRCode or barcode recognition view
 * @author: fanrunqi
 * @date: 2019/4/3 16:26
 */
public class ZxingView extends FrameLayout {

    private AppCompatActivity activity;
    private boolean useDefaultInteractiveView;

    public ZxingView(@NonNull Context context) {
        this(context, null);
    }

    public ZxingView(@NonNull Context context,
                     @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZxingView(@NonNull Context context,
                     @Nullable AttributeSet attrs,
                     @AttrRes int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ZxingView(@NonNull Context context,
                     @Nullable AttributeSet attrs,
                     @AttrRes int defStyleAttr,
                     @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ZxingView);
        String interactiveViewColor = ta.getString(R.styleable.ZxingView_interactiveViewColor);
        if (!"".equals(interactiveViewColor) && interactiveViewColor != null) {
            ZvParams.InteractiveViewColor = Color.parseColor(interactiveViewColor);
        }

        String interactiveViewMaskColor = ta.getString(R.styleable.ZxingView_interactiveViewMaskColor);
        if (!"".equals(interactiveViewMaskColor) && interactiveViewMaskColor != null) {
            ZvParams.InteractiveViewMaskColor = Color.parseColor(interactiveViewMaskColor);
        }

        useDefaultInteractiveView = ZvParams.useDefaultInteractiveView = ta.getBoolean(
                R.styleable.ZxingView_useDefaultInteractiveView,
                ZvParams.useDefaultInteractiveView);

        int recognizeWidthInDp = ta.getInt(R.styleable.ZxingView_recognizeRectWidthInDp,
                ZvParams.RecognizeRectWidthInDp);
        int recognizeHeightInDp = ta.getInt(R.styleable.ZxingView_recognizeRectHeightInDp,
                ZvParams.RecognizeRectHeightInDp);
        ta.recycle();

        ZvParams.recognizeRectWidthInPx = Utils.dip2px(context, recognizeWidthInDp);
        ZvParams.recognizeRectHeightInPx = Utils.dip2px(context, recognizeHeightInDp);
    }



    /**
     * Setting configuration
     */

    public ZxingView bind(AppCompatActivity activity) {
        this.activity = activity;
        return this;
    }

    public void subscribe(ResultCallBack callBack) {
        Utils.validateMainThread();
        ZvParams.resultCallBack = callBack;
        addLifeCycle();
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (ZvParams.recognizeRectWidthInPx > w || ZvParams.recognizeRectWidthInPx <= 0) {
            ZvParams.recognizeRectWidthInPx = w;
        }
        if (ZvParams.recognizeRectHeightInPx > h || ZvParams.recognizeRectHeightInPx <= 0) {
            ZvParams.recognizeRectHeightInPx = h;
        }
    }

    /**
     * create CameraView & ForegroundView
     */
    private void addLifeCycle() {
        new AttachFragment()
                .bind(activity)
                .checkCameraPermission(new PermissionListener() {
                    @Override
                    public void granted(AttachFragment fragment) {
                        new CameraPreview(activity, ZxingView.this);
                        if (useDefaultInteractiveView) {
                            new InteractiveView(activity, ZxingView.this);
                        }
                    }
                });
    }

}
