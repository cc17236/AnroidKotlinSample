package com.example.applicationkotlinsample.base.background;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.*;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.example.applicationkotlinsample.R;
import com.example.applicationkotlinsample.base.background.utils.TypeValueHelper;

import org.xmlpull.v1.XmlPullParserException;

import java.lang.reflect.Field;

import static android.graphics.drawable.GradientDrawable.LINEAR_GRADIENT;

public class BackgroundFactory implements LayoutInflater.Factory {

    private LayoutInflater.Factory mViewCreateFactory;

    public void setInterceptFactory(LayoutInflater.Factory factory) {
        mViewCreateFactory = factory;
    }


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = null;
        if (mViewCreateFactory != null) {
            view = mViewCreateFactory.onCreateView(name, context, attrs);
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.background);
        TypedArray pressTa = context.obtainStyledAttributes(attrs, R.styleable.background_press);
        try {
            int attrCount = typedArray.getIndexCount();
            if (attrCount == 0) {
                return null;
            }
            if (view == null) {
                view = createView(context, name, attrs);
            }
            if (view == null) {
                return null;
            }
            GradientDrawable drawable = getDrawable(typedArray);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                    typedArray.hasValue(R.styleable.background_ripple_enable)) {
                if (typedArray.getBoolean(R.styleable.background_ripple_enable, false)) {
                    int color = typedArray.getColor(R.styleable.background_ripple_color, 0);
//                    int enabled = 0, disabled = 0, unchecked = 0, pressed = 0, normal = 0;
//                    if (view instanceof TextView && ((TextView) view).getTextColors() != null) {
//                        ColorStateList cl = ((TextView) view).getTextColors();
//                        int[][] value = null;
//                        int[] colors = null;
//                        try {
//                            value = (int[][]) ReflectUtil.invokeMethod(ColorStateList.class, "getStates", new Class[]{}, null, new Object[]{});
//                            colors = (int[]) ReflectUtil.invokeMethod(ColorStateList.class, "getColors", new Class[]{}, null, new Object[]{});
//                        } catch (Exception e) {
//                        }
//                        if (value != null && colors != null && value.length == colors.length) {
//                            if (value.length > 1) {
//                                for (int i = 0; i < value.length; i++) {
//                                    int[] state = value[i];
//                                    if (state.length == 0) {
//                                        normal = colors[i];
//                                    } else if (state.length == 1) {
//                                        switch (state[0]) {
//                                            case android.R.attr.state_enabled:// enabled
//                                                enabled = colors[i];
//                                                break;
//                                            case -android.R.attr.state_enabled:// disabled
//                                                disabled = colors[i];
//                                                break;
//                                            case -android.R.attr.state_checked:// unchecked
//                                                unchecked = colors[i];
//                                                break;
//                                            case android.R.attr.state_pressed:// pressed
//                                                pressed = colors[i];
//                                                break;
//                                        }
//                                        normal = colors[i];
//                                    } else {
//                                        //无解
//                                    }
//                                }
//                                if (disabled == 0) {
//                                    disabled = Color.parseColor("#DFDFDF");
//                                }
//                            } else {
//                                normal = color;
//                                disabled = Color.parseColor("#DFDFDF");
//                            }
//                        } else {
//                            normal = cl.getDefaultColor();
//                            disabled = Color.parseColor("#DFDFDF");
//                        }
//                    }
//
//                    int[] colorsValue = new int[]{
//                            enabled,
//                            disabled,
//                            unchecked,
//                            pressed,
//                            normal
//                    };
//                    int[] stateValue = new int[]{
//                            android.R.attr.state_enabled,
//                            -android.R.attr.state_enabled,
//                            -android.R.attr.state_checked,
//                            android.R.attr.state_pressed,
//                            10086,
//                    };
//                    List<Integer> list = new ArrayList<>();
//                    for (int i = 0; i < colorsValue.length; i++) {
//                        int value = colorsValue[i];
//                        if (value != 0) {
//                            list.add(i);
//                        }
//                    }
//                    int[][] states = new int[list.size()][];
//                    int[] colors = new int[list.size()];
//                    for (int i = 0; i < list.size(); i++) {
//                        int index = list.get(i);
//                        int state = stateValue[index];
//                        int color_ = colorsValue[index];
//                        if (state == 10086) {
//                            states[i] = new int[]{};
//                        } else {
//                            states[i] = new int[]{state};
//                        }
//                        colors[i] = color_;
//                    }
//                    ColorStateList colorList = new ColorStateList(states, colors);
//
//                    RippleDrawable rippleDrawable = new RippleDrawable(colorList, drawable, getShape());
                    RippleDrawable rippleDrawable = new RippleDrawable(ColorStateList.valueOf(color), drawable, getShape());
                    Drawable origin = view.getBackground();
                    if (origin != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            rippleDrawable.setDrawable(0, origin);
                        }
                    }

//                    view.setClickable(true);
                    view.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                    view.setBackground(rippleDrawable);
                    return view;
                }

            }

            if (pressTa.getIndexCount() > 0) {
                StateListDrawable stateListDrawable = getStateListDrawable(drawable, getDrawable(typedArray), typedArray, pressTa);
                view.setClickable(true);
                view.setBackground(stateListDrawable);
            } else {
                view.setBackground(drawable);
            }
            return view;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            typedArray.recycle();
            pressTa.recycle();
        }

        return view;
    }

    private View createView(Context context, String name, AttributeSet attrs) {
        View view = null;
        try {
            if (-1 == name.indexOf('.')) {
                if ("View".equals(name)) {
                    view = LayoutInflater.from(context).createView(name, "android.view.", attrs);
                }
                if (view == null) {
                    view = LayoutInflater.from(context).createView(name, "android.widget.", attrs);
                }
                if (view == null) {
                    view = LayoutInflater.from(context).createView(name, "android.webkit.", attrs);
                }
            } else {
                view = LayoutInflater.from(context).createView(name, null, attrs);
            }

        } catch (Exception e) {
            view = null;
        }
        return view;
    }

    private boolean hasSetRadius(float[] radius) {
        boolean hasSet = false;
        for (float f : radius) {
            if (f != 0.0f) {
                hasSet = true;
                break;
            }
        }
        return hasSet;
    }

    private GradientDrawable getDrawable(TypedArray typedArray) throws Exception {
        GradientDrawable drawable = new GradientDrawable();
        float[] cornerRadius = new float[8];
        float sizeWidth = 0;
        float sizeHeight = 0;
        float strokeWidth = -1;
        float strokeDashWidth = 0;
        int strokeColor = 1;
        float strokeGap = 0;
        float centerX = 0;
        float centerY = 0;
        int centerColor = 0;
        int startColor = 0;
        int endColor = 0;
        int gradientType = LINEAR_GRADIENT;
        int gradientAngle = 0;
        Rect padding = new Rect();
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int attr = TypeValueHelper.sAppearanceValues.get(typedArray.getIndex(i), -1);
            if (attr == -1) {
                continue;
            }
            int typeIndex = typedArray.getIndex(i);
            if (attr == R.styleable.background_shape_bg) {
                drawable.setShape(typedArray.getInt(typeIndex, 0));
            } else if (attr == R.styleable.background_solid_color) {
                drawable.setColor(typedArray.getColor(typeIndex, 0));
            } else if (attr == R.styleable.background_corners_radius) {
                drawable.setCornerRadius(typedArray.getDimension(typeIndex, 0));
            } else if (attr == R.styleable.background_corners_bottomLeftRadius) {
                cornerRadius[6] = typedArray.getDimension(typeIndex, 0);
                cornerRadius[7] = typedArray.getDimension(typeIndex, 0);
            } else if (attr == R.styleable.background_corners_bottomRightRadius) {
                cornerRadius[4] = typedArray.getDimension(typeIndex, 0);
                cornerRadius[5] = typedArray.getDimension(typeIndex, 0);
            } else if (attr == R.styleable.background_corners_topLeftRadius) {
                cornerRadius[0] = typedArray.getDimension(typeIndex, 0);
                cornerRadius[1] = typedArray.getDimension(typeIndex, 0);
            } else if (attr == R.styleable.background_corners_topRightRadius) {
                cornerRadius[2] = typedArray.getDimension(typeIndex, 0);
                cornerRadius[3] = typedArray.getDimension(typeIndex, 0);
            } else if (attr == R.styleable.background_gradient_angle) {
                gradientAngle = typedArray.getInteger(typeIndex, 0);
            } else if (attr == R.styleable.background_gradient_centerX) {
                centerX = typedArray.getFloat(typeIndex, -1);
            } else if (attr == R.styleable.background_gradient_centerY) {
                centerY = typedArray.getFloat(typeIndex, -1);
            } else if (attr == R.styleable.background_gradient_centerColor) {
                centerColor = typedArray.getColor(typeIndex, 0);
            } else if (attr == R.styleable.background_gradient_endColor) {
                endColor = typedArray.getColor(typeIndex, 0);
            } else if (attr == R.styleable.background_gradient_startColor) {
                startColor = typedArray.getColor(typeIndex, 0);
            } else if (attr == R.styleable.background_gradient_gradientRadius) {
                drawable.setGradientRadius(typedArray.getDimension(typeIndex, 0));
            } else if (attr == R.styleable.background_gradient_type) {
                gradientType = typedArray.getInt(typeIndex, 0);
                drawable.setGradientType(gradientType);
            } else if (attr == R.styleable.background_gradient_useLevel) {
                drawable.setUseLevel(typedArray.getBoolean(typeIndex, false));
            } else if (attr == R.styleable.background_padding_left) {
                padding.left = (int) typedArray.getDimension(typeIndex, 0);
            } else if (attr == R.styleable.background_padding_top) {
                padding.top = (int) typedArray.getDimension(typeIndex, 0);
            } else if (attr == R.styleable.background_padding_right) {
                padding.right = (int) typedArray.getDimension(typeIndex, 0);
            } else if (attr == R.styleable.background_padding_bottom) {
                padding.bottom = (int) typedArray.getDimension(typeIndex, 0);
            } else if (attr == R.styleable.background_size_width) {
                sizeWidth = typedArray.getDimension(typeIndex, 0);
            } else if (attr == R.styleable.background_size_height) {
                sizeHeight = typedArray.getDimension(typeIndex, 0);
            } else if (attr == R.styleable.background_stroke_width) {
                strokeWidth = typedArray.getDimension(typeIndex, 0);
            } else if (attr == R.styleable.background_stroke_color) {
                strokeColor = typedArray.getColor(typeIndex, 0);
            } else if (attr == R.styleable.background_stroke_dashWidth) {
                strokeDashWidth = typedArray.getDimension(typeIndex, 0);
            } else if (attr == R.styleable.background_stroke_dashGap) {
                strokeGap = typedArray.getDimension(typeIndex, 0);
            }
        }
        if (hasSetRadius(cornerRadius)) {
            drawable.setCornerRadii(cornerRadius);
        }
        if (typedArray.hasValue(R.styleable.background_size_width) &&
                typedArray.hasValue(R.styleable.background_size_height)) {
            drawable.setSize((int) sizeWidth, (int) sizeHeight);
        }
        if (typedArray.hasValue(R.styleable.background_stroke_width) &&
                typedArray.hasValue(R.styleable.background_stroke_color)) {
            drawable.setStroke((int) strokeWidth, strokeColor, strokeDashWidth, strokeGap);
        }
        if (typedArray.hasValue(R.styleable.background_gradient_centerX) &&
                typedArray.hasValue(R.styleable.background_gradient_centerY)) {
            drawable.setGradientCenter(centerX, centerY);
        }

        if (typedArray.hasValue(R.styleable.background_gradient_startColor) &&
                typedArray.hasValue(R.styleable.background_gradient_endColor)) {
            int[] colors;
            if (typedArray.hasValue(R.styleable.background_gradient_centerColor)) {
                colors = new int[3];
                colors[0] = startColor;
                colors[1] = centerColor;
                colors[2] = endColor;
            } else {
                colors = new int[2];
                colors[0] = startColor;
                colors[1] = endColor;
            }
            drawable.setColors(colors);
        }
        if (gradientType == LINEAR_GRADIENT &&
                typedArray.hasValue(R.styleable.background_gradient_angle)) {
            gradientAngle %= 360;
            if (gradientAngle % 45 != 0) {
                throw new XmlPullParserException(typedArray.getPositionDescription()
                        + "<gradient> tag requires 'angle' attribute to "
                        + "be a multiple of 45");
            }
            GradientDrawable.Orientation mOrientation = GradientDrawable.Orientation.LEFT_RIGHT;
            switch (gradientAngle) {
                case 0:
                    mOrientation = GradientDrawable.Orientation.LEFT_RIGHT;
                    break;
                case 45:
                    mOrientation = GradientDrawable.Orientation.BL_TR;
                    break;
                case 90:
                    mOrientation = GradientDrawable.Orientation.BOTTOM_TOP;
                    break;
                case 135:
                    mOrientation = GradientDrawable.Orientation.BR_TL;
                    break;
                case 180:
                    mOrientation = GradientDrawable.Orientation.RIGHT_LEFT;
                    break;
                case 225:
                    mOrientation = GradientDrawable.Orientation.TR_BL;
                    break;
                case 270:
                    mOrientation = GradientDrawable.Orientation.TOP_BOTTOM;
                    break;
                case 315:
                    mOrientation = GradientDrawable.Orientation.TL_BR;
                    break;
            }
            drawable.setOrientation(mOrientation);
        }

        if (typedArray.hasValue(R.styleable.background_padding_left) &&
                typedArray.hasValue(R.styleable.background_padding_top) &&
                typedArray.hasValue(R.styleable.background_padding_right) &&
                typedArray.hasValue(R.styleable.background_padding_bottom)) {
            Field paddingField = drawable.getClass().getField("mPadding");
            paddingField.setAccessible(true);
            paddingField.set(drawable, padding);
        }
        return drawable;
    }

    private StateListDrawable getStateListDrawable(GradientDrawable drawable, GradientDrawable pressDrawable, TypedArray array, TypedArray typedArray) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int attr = TypeValueHelper.sAppearancePressValues.get(typedArray.getIndex(i), -1);
            if (attr == -1) {
                continue;
            }
            int typeIndex = typedArray.getIndex(i);

            if (attr == R.styleable.background_press_pressed_color) {
                int color = typedArray.getColor(typeIndex, 0);
                pressDrawable.setColor(color);
                stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressDrawable);
                try {
                    Drawable disable = getDisableDrawable(array);
                    stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, disable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (attr == R.styleable.background_press_unpressed_color) {
                int color = typedArray.getColor(typeIndex, 0);
                drawable.setColor(color);
                stateListDrawable.addState(new int[]{-android.R.attr.state_pressed}, drawable);
                try {
                    Drawable disable = getDisableDrawable(array);
                    stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, disable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return stateListDrawable;
    }

    private Drawable getDisableDrawable(TypedArray typedArray) throws Exception {
        GradientDrawable drawable = new GradientDrawable();
        float[] cornerRadius = new float[8];
        float sizeWidth = 0;
        float sizeHeight = 0;
        float strokeWidth = -1;
        float strokeDashWidth = 0;
        int strokeColor = 1;
        float strokeGap = 0;
        float centerX = 0;
        float centerY = 0;
        int centerColor = 0;
        int startColor = 0;
        int endColor = 0;
        int gradientType = LINEAR_GRADIENT;
        int gradientAngle = 0;
        Rect padding = new Rect();
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int attr = TypeValueHelper.sAppearanceValues.get(typedArray.getIndex(i), -1);
            if (attr == -1) {
                continue;
            }
            int typeIndex = typedArray.getIndex(i);
            if (attr == R.styleable.background_shape_bg) {
                drawable.setShape(typedArray.getInt(typeIndex, 0));
            } else if (attr == R.styleable.background_solid_color) {
//                drawable.setColor(typedArray.getColor(typeIndex, 0));
            } else if (attr == R.styleable.background_corners_radius) {
                drawable.setCornerRadius(typedArray.getDimension(typeIndex, 0));
            } else if (attr == R.styleable.background_corners_bottomLeftRadius) {
                cornerRadius[6] = typedArray.getDimension(typeIndex, 0);
                cornerRadius[7] = typedArray.getDimension(typeIndex, 0);
            } else if (attr == R.styleable.background_corners_bottomRightRadius) {
                cornerRadius[4] = typedArray.getDimension(typeIndex, 0);
                cornerRadius[5] = typedArray.getDimension(typeIndex, 0);
            } else if (attr == R.styleable.background_corners_topLeftRadius) {
                cornerRadius[0] = typedArray.getDimension(typeIndex, 0);
                cornerRadius[1] = typedArray.getDimension(typeIndex, 0);
            } else if (attr == R.styleable.background_corners_topRightRadius) {
                cornerRadius[2] = typedArray.getDimension(typeIndex, 0);
                cornerRadius[3] = typedArray.getDimension(typeIndex, 0);
            } else if (attr == R.styleable.background_gradient_angle) {
                gradientAngle = typedArray.getInteger(typeIndex, 0);
            } else if (attr == R.styleable.background_gradient_centerX) {
                centerX = typedArray.getFloat(typeIndex, -1);
            } else if (attr == R.styleable.background_gradient_centerY) {
                centerY = typedArray.getFloat(typeIndex, -1);
            } else if (attr == R.styleable.background_gradient_centerColor) {
                centerColor = typedArray.getColor(typeIndex, 0);
            } else if (attr == R.styleable.background_gradient_endColor) {
                endColor = typedArray.getColor(typeIndex, 0);
            } else if (attr == R.styleable.background_gradient_startColor) {
                startColor = typedArray.getColor(typeIndex, 0);
            } else if (attr == R.styleable.background_gradient_gradientRadius) {
                drawable.setGradientRadius(typedArray.getDimension(typeIndex, 0));
            } else if (attr == R.styleable.background_gradient_type) {
                gradientType = typedArray.getInt(typeIndex, 0);
                drawable.setGradientType(gradientType);
            } else if (attr == R.styleable.background_gradient_useLevel) {
                drawable.setUseLevel(typedArray.getBoolean(typeIndex, false));
            } else if (attr == R.styleable.background_padding_left) {
                padding.left = (int) typedArray.getDimension(typeIndex, 0);
            } else if (attr == R.styleable.background_padding_top) {
                padding.top = (int) typedArray.getDimension(typeIndex, 0);
            } else if (attr == R.styleable.background_padding_right) {
                padding.right = (int) typedArray.getDimension(typeIndex, 0);
            } else if (attr == R.styleable.background_padding_bottom) {
                padding.bottom = (int) typedArray.getDimension(typeIndex, 0);
            } else if (attr == R.styleable.background_size_width) {
                sizeWidth = typedArray.getDimension(typeIndex, 0);
            } else if (attr == R.styleable.background_size_height) {
                sizeHeight = typedArray.getDimension(typeIndex, 0);
            } else if (attr == R.styleable.background_stroke_width) {
                strokeWidth = typedArray.getDimension(typeIndex, 0);
            } else if (attr == R.styleable.background_stroke_color) {
                strokeColor = typedArray.getColor(typeIndex, 0);
            } else if (attr == R.styleable.background_stroke_dashWidth) {
                strokeDashWidth = typedArray.getDimension(typeIndex, 0);
            } else if (attr == R.styleable.background_stroke_dashGap) {
                strokeGap = typedArray.getDimension(typeIndex, 0);
            }
        }
        if (hasSetRadius(cornerRadius)) {
            drawable.setCornerRadii(cornerRadius);
        }
        if (typedArray.hasValue(R.styleable.background_size_width) &&
                typedArray.hasValue(R.styleable.background_size_height)) {
            drawable.setSize((int) sizeWidth, (int) sizeHeight);
        }
        if (typedArray.hasValue(R.styleable.background_stroke_width) &&
                typedArray.hasValue(R.styleable.background_stroke_color)) {
            drawable.setStroke((int) strokeWidth, strokeColor, strokeDashWidth, strokeGap);
        }
        if (typedArray.hasValue(R.styleable.background_gradient_centerX) &&
                typedArray.hasValue(R.styleable.background_gradient_centerY)) {
            drawable.setGradientCenter(centerX, centerY);
        }


        if (gradientType == LINEAR_GRADIENT &&
                typedArray.hasValue(R.styleable.background_gradient_angle)) {
            gradientAngle %= 360;
            if (gradientAngle % 45 != 0) {
                throw new XmlPullParserException(typedArray.getPositionDescription()
                        + "<gradient> tag requires 'angle' attribute to "
                        + "be a multiple of 45");
            }
            GradientDrawable.Orientation mOrientation = GradientDrawable.Orientation.LEFT_RIGHT;
            switch (gradientAngle) {
                case 0:
                    mOrientation = GradientDrawable.Orientation.LEFT_RIGHT;
                    break;
                case 45:
                    mOrientation = GradientDrawable.Orientation.BL_TR;
                    break;
                case 90:
                    mOrientation = GradientDrawable.Orientation.BOTTOM_TOP;
                    break;
                case 135:
                    mOrientation = GradientDrawable.Orientation.BR_TL;
                    break;
                case 180:
                    mOrientation = GradientDrawable.Orientation.RIGHT_LEFT;
                    break;
                case 225:
                    mOrientation = GradientDrawable.Orientation.TR_BL;
                    break;
                case 270:
                    mOrientation = GradientDrawable.Orientation.TOP_BOTTOM;
                    break;
                case 315:
                    mOrientation = GradientDrawable.Orientation.TL_BR;
                    break;
            }
            drawable.setOrientation(mOrientation);
        }

        if (typedArray.hasValue(R.styleable.background_padding_left) &&
                typedArray.hasValue(R.styleable.background_padding_top) &&
                typedArray.hasValue(R.styleable.background_padding_right) &&
                typedArray.hasValue(R.styleable.background_padding_bottom)) {
            Field paddingField = drawable.getClass().getField("mPadding");
            paddingField.setAccessible(true);
            paddingField.set(drawable, padding);
        }
        drawable.setColor(Color.parseColor("#DFDFDF"));
        return drawable;
    }



    private Drawable getShape() {
        return new ShapeDrawable(new RectShape() {
            @Override
            public void draw(Canvas canvas, Paint paint) {
                final float width = this.getWidth();
                final float height = this.getHeight();
                Path path = new Path();
                path.moveTo(0, 0);
                path.lineTo(width, 0);
                path.lineTo(width, height);
                path.lineTo(0, height);
                path.lineTo(0, 0);
                path.close();
                canvas.drawPath(path, paint);
            }
        });
    }

}
