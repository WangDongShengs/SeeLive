package com.wds.seelive.home.splash;

import com.m.k.mvp.data.request.GetRequest;
import com.m.k.mvp.data.request.RequestType;
import com.wds.seelive.Constrant;
import com.wds.seelive.data.entity.SpecialData;
import com.wds.seelive.home.NewsFragment;

//专栏的fragment  继承抽取后的fragment
public class SpecialNewsFragment extends NewsFragment<SpecialData> {
    @Override
    protected void loadData(RequestType type, int start, int number, long pointTime) {
        //获取数据
        GetRequest<SpecialData> request = new GetRequest<>(Constrant.URL.SPECIAL_LIST);
        request.putParams(Constrant.RequestKey.KEY_START,start)
                .putParams(Constrant.RequestKey.KEY_NUMBER,number)
                .putParams(Constrant.RequestKey.KEY_POINT_TIME,pointTime);
        request.setRequestType(type);
        doRequest(request);
    }


    @Override
    public boolean isNeedAddToBackStack() {
        return false;
    }


    @Override
    public boolean isNeedAnimation() {
        return false;
    }

    @Override
    public int getPageType() {
        return PAGE_TYPE_SPECIAL;
    }
}
