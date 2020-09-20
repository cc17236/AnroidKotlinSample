package com.example.applicationkotlinsample.base.background.utils;

import android.util.SparseIntArray;

import com.example.applicationkotlinsample.R;


public class TypeValueHelper {

    public static final SparseIntArray sAppearanceValues = new SparseIntArray();
    public static final SparseIntArray sAppearancePressValues = new SparseIntArray();
    static {
        sAppearanceValues.put(R.styleable.background_shape_bg, R.styleable.background_shape_bg);
        sAppearanceValues.put(R.styleable.background_solid_color, R.styleable.background_solid_color);
        sAppearanceValues.put(R.styleable.background_corners_radius, R.styleable.background_corners_radius);
        sAppearanceValues.put(R.styleable.background_corners_bottomLeftRadius, R.styleable.background_corners_bottomLeftRadius);
        sAppearanceValues.put(R.styleable.background_corners_bottomRightRadius, R.styleable.background_corners_bottomRightRadius);
        sAppearanceValues.put(R.styleable.background_corners_topLeftRadius, R.styleable.background_corners_topLeftRadius);
        sAppearanceValues.put(R.styleable.background_corners_topRightRadius, R.styleable.background_corners_topRightRadius);
        sAppearanceValues.put(R.styleable.background_gradient_angle, R.styleable.background_gradient_angle);
        sAppearanceValues.put(R.styleable.background_gradient_centerX, R.styleable.background_gradient_centerX);
        sAppearanceValues.put(R.styleable.background_gradient_centerY, R.styleable.background_gradient_centerY);
        sAppearanceValues.put(R.styleable.background_gradient_centerColor, R.styleable.background_gradient_centerColor);
        sAppearanceValues.put(R.styleable.background_gradient_endColor, R.styleable.background_gradient_endColor);
        sAppearanceValues.put(R.styleable.background_gradient_startColor, R.styleable.background_gradient_startColor);
        sAppearanceValues.put(R.styleable.background_gradient_gradientRadius, R.styleable.background_gradient_gradientRadius);
        sAppearanceValues.put(R.styleable.background_gradient_type, R.styleable.background_gradient_type);
        sAppearanceValues.put(R.styleable.background_gradient_useLevel, R.styleable.background_gradient_useLevel);
        sAppearanceValues.put(R.styleable.background_padding_left, R.styleable.background_padding_left);
        sAppearanceValues.put(R.styleable.background_padding_top, R.styleable.background_padding_top);
        sAppearanceValues.put(R.styleable.background_padding_right, R.styleable.background_padding_right);
        sAppearanceValues.put(R.styleable.background_padding_bottom, R.styleable.background_padding_bottom);
        sAppearanceValues.put(R.styleable.background_size_width, R.styleable.background_size_width);
        sAppearanceValues.put(R.styleable.background_size_height, R.styleable.background_size_height);
        sAppearanceValues.put(R.styleable.background_stroke_width, R.styleable.background_stroke_width);
        sAppearanceValues.put(R.styleable.background_stroke_color, R.styleable.background_stroke_color);
        sAppearanceValues.put(R.styleable.background_stroke_dashWidth, R.styleable.background_stroke_dashWidth);
        sAppearanceValues.put(R.styleable.background_stroke_dashGap, R.styleable.background_stroke_dashGap);
        sAppearanceValues.put(R.styleable.background_ripple_enable, R.styleable.background_ripple_enable);
        sAppearanceValues.put(R.styleable.background_ripple_color, R.styleable.background_ripple_color);

        sAppearancePressValues.put(R.styleable.background_press_pressed_color, R.styleable.background_press_pressed_color);
        sAppearancePressValues.put(R.styleable.background_press_unpressed_color, R.styleable.background_press_unpressed_color);
    }
}
