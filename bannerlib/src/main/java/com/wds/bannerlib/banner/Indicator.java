package com.wds.bannerlib.banner;

//创建接口指示符    指示器的接口
public interface Indicator {
    void setRadio(int radio);
    //数量
    void setCount(int count);
    void setCurrent(int index);
    void setUnSelectColor(int color);
    void setSelectColor(int color);
    void setMargin(int margin);
    void setId(int id);
    int getId();
}
