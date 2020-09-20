package androidx.bug;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * @author Kevin
 * Date Created: 3/7/14
 * <p>
 * https://code.google.com/p/android/issues/detail?id=63777
 * <p>
 * When using a translucent status bar on API 19+, the window will not
 * resize to make room for input methods (i.e.
 * {@link android.view.WindowManager.LayoutParams#SOFT_INPUT_ADJUST_RESIZE} and
 * {@link android.view.WindowManager.LayoutParams#SOFT_INPUT_ADJUST_PAN} are
 * ignored).
 * <p>
 * To work around this; override {@link #fitSystemWindows(android.graphics.Rect)},
 * capture and override the system insets, and then call through to FrameLayout's
 * implementation.
 * <p>
 * For reasons yet unknown, modifying the bottom inset causes this workaround to
 * fail. Modifying the top, left, and right insets works as expected.
 */
public class AndroidBugInsetsLinearLayout extends LinearLayout {
    private int[] mInsets = new int[4];

    public AndroidBugInsetsLinearLayout(Context context) {
        super(context);
    }

    public AndroidBugInsetsLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AndroidBugInsetsLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public final int[] getInsets() {
        return mInsets;
    }



    @Override
    protected final boolean fitSystemWindows(Rect insets) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            mInsets[0] = insets.left;
            mInsets[1] = insets.top;
            mInsets[2] = insets.right;
            return super.fitSystemWindows(insets);
        } else {
            return super.fitSystemWindows(insets);
        }
    }
}