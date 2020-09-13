package com.wds.seelive.detail;

import com.m.k.mvp.base.IBaseCallBack;
import com.m.k.mvp.base.p.BasePresenter;
import com.m.k.mvp.data.BaseRepository;
import com.m.k.mvp.data.request.GetRequest;
import com.m.k.mvp.data.request.PostRequest;
import com.m.k.mvp.data.request.RequestType;
import com.m.k.mvp.data.response.MvpResponse;
import com.wds.seelive.Constrant;
import com.wds.seelive.data.entity.Comment;
import com.wds.seelive.data.entity.CommentListData;
import com.wds.seelive.data.entity.RelatedNewsData;
import com.wds.seelive.data.entity.Replay;
//创建P 层
public class DetailPresenter extends BasePresenter<IDetailConstraint.IDetailView> implements IDetailConstraint.IDetailPresenter {


    private BaseRepository mRepository;

    public DetailPresenter() {

        mRepository = new BaseRepository();
    }

    @Override
    public void getRelatedNews(String newsId) {
        GetRequest<RelatedNewsData> request = new GetRequest<>(Constrant.URL.DETAIL_RELATIVE_NEWS);
        request.getParams().put(Constrant.RequestKey.KEY_NEWS_ID,newsId);

        mRepository.doRequest(getLifecycleProvider(), request, new IBaseCallBack<RelatedNewsData>() {
            @Override
            public void onResult(MvpResponse<RelatedNewsData> response) {
                if(mView != null){
                    mView.onRelatedNewsResult(response);
                }
            }
        });

    }

    @Override
    public void getNewsComment(RequestType type, String newsId, long pointTime, int start) {
        GetRequest<CommentListData> request = new GetRequest<>(Constrant.URL.DETAIL_COMMENT_LIST);
        request.getParams().put(Constrant.RequestKey.KEY_NEWS_ID_2,newsId);
        request.getParams().put(Constrant.RequestKey.KEY_START,start);
        request.getParams().put(Constrant.RequestKey.KEY_POINT_TIME,pointTime);
        request.setRequestType(type);

        mRepository.doRequest(getLifecycleProvider(), request, new IBaseCallBack<CommentListData>() {
            @Override
            public void onResult(MvpResponse<CommentListData> response) {
                if(mView != null){
                  mView.onNewsCommentListResult(response);
                }
            }
        });
    }

    @Override
    public void sendShareSuccess(String newsId) {
        PostRequest<String> request = new PostRequest<>(Constrant.URL.DETAIL_SHARE);
        request.getParams().put(Constrant.RequestKey.KEY_NEWS_ID,newsId);

        mRepository.doRequest(getLifecycleProvider(), request, new IBaseCallBack<String>() {
            @Override
            public void onResult(MvpResponse<String> response) {
                if(mView != null){
                    mView.onSendShareResult(response);
                }
            }
        });
    }

    @Override
    public void sendNewsComment(String newsId, String content) {

        PostRequest<Comment> request =  new PostRequest<>(Constrant.URL.DETAIL_COMMENT_NEWS);
        request.addParams(Constrant.RequestKey.KEY_NEWS_ID_2,newsId)
                .addParams(Constrant.RequestKey.KEY_CONTENT,content)
                .setType(Comment.class);


        mRepository.doRequest(getLifecycleProvider(), request, response -> {
            if(mView != null){
                mView.onSendNewsCommentResult(response);
            }
        });
    }

    @Override
    public void sendUserReplay(String commentId,String toId,int type,String replayId,  String newsId,String content) {

        PostRequest<Replay> request =  new PostRequest<>(Constrant.URL.DETAIL_USER_REPLAY);
        request.addParams(Constrant.RequestKey.KEY_NEWS_ID_2,newsId)
                .addParams(Constrant.RequestKey.KEY_CONTENT,content)
                .addParams(Constrant.RequestKey.KEY_COMMENT_ID,commentId)
                .addParams(Constrant.RequestKey.KEY_TO_IOD,toId)
                .addParams(Constrant.RequestKey.KEY_REPLY_TYPE,type)
                .addParams(Constrant.RequestKey.KEY_REPLAY_ID,replayId)
                .setType(Replay.class);
        mRepository.doRequest(getLifecycleProvider(), request, new IBaseCallBack<Replay>() {
            @Override
            public void onResult(MvpResponse<Replay> response) {
                if (mView!=null){
                    mView.onSendUserReplayResult(response);
                }
            }
        });

    }


    @Override
    public void doCommentLike(String commentId) {
        PostRequest<String> request = new PostRequest<>(Constrant.URL.DETAIL_DO_COMMENT_LIKE);
        request.getParams().put(Constrant.RequestKey.KEY_COMMENT_ID,commentId);

        mRepository.doRequest(getLifecycleProvider(), request, new IBaseCallBack<String>() {
            @Override
            public void onResult(MvpResponse<String> response) {
                if(mView != null){
                    mView.onCommentLikeResult(response);
                }
            }
        });
    }

    @Override
    public boolean cancelRequest() {
        return false;
    }
}
