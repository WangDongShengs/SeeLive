package com.wds.seelive.home.video;

import com.m.k.mvp.data.request.GetRequest;
import com.m.k.mvp.data.request.RequestType;
import com.wds.seelive.Constrant;
import com.wds.seelive.data.entity.VideoData;
import com.wds.seelive.home.NewsFragment;

//视频类的fragment
public class VideoFragment extends NewsFragment<VideoData> {

//获取数据
    @Override
    protected void loadData(RequestType type, int start, int number, long pointTime) {
        GetRequest<VideoData> request = new GetRequest<>(Constrant.URL.VIDEO_LIST);
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
        return PAGE_TYPE_VIDEO;
    }
}
