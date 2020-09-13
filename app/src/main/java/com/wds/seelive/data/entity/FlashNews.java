package com.wds.seelive.data.entity;

import com.m.k.mvp.widgets.MarqueeView;

import org.jetbrains.annotations.NotNull;

public class FlashNews extends BaseNews implements MarqueeView.MarqueeData {
    @NotNull
    @Override
    public String getString() {
        return getTheme();
    }
}
