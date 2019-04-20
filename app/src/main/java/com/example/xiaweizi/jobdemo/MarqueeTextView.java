package com.example.xiaweizi.jobdemo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * <pre>
 *     author : xiaweizi
 *     class  : com.example.xiaweizi.jobdemo.MarqueeTextView
 *     e-mail : 1012126908@qq.com
 *     time   : 2019/04/20
 *     desc   :
 * </pre>
 */

public class MarqueeTextView extends android.support.v7.widget.AppCompatTextView {

    public MarqueeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public boolean isFocused() {
        // 强制获取焦点，多个跑马灯才能被激活，同时无需再设置focusable和focusableInTouchMode为true
        return true;
    }

    private void init() {
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setSingleLine();
    }
}
