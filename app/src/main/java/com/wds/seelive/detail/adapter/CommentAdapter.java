package com.wds.seelive.detail.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.m.k.mvp.widgets.CommentsView;

import com.wds.seelive.GlideApp;
import com.wds.seelive.R;
import com.wds.seelive.data.entity.Comment;
import com.wds.seelive.data.entity.Replay;
import com.wds.seelive.databinding.ItemDetialCommentBinding;

import java.util.ArrayList;
import java.util.List;
//评论的适配器
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {

    private OnItemClickListener mOnItemClickListener;

    private ArrayList<Comment> mComments;

    public CommentAdapter(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.mComments = comments;

        notifyItemRangeInserted(0,comments.size());

    }

    public void loadMore(ArrayList<Comment> comments) {

        int start = mComments.size();

        this.mComments.addAll(comments);
        //刷新条目的item
        notifyItemRangeInserted(start,comments.size());

    }

   //
    public void insertComment(Comment comment){
        if(mComments == null){
            mComments = new ArrayList<>();
        }

        mComments.add(0,comment);

        notifyItemInserted(0);
    }


    public void doLike(int position){
        Comment comment = mComments.get(position);
        comment.setIsPraise(1); // 设置为点赞
        comment.addLikeCount();
        notifyItemChanged(position);
    }



    public void insertReplay(Replay replay){

        int position = 0;
        for(Comment comment : mComments){

            if(comment.getCommentId().equals(replay.getComment_id())){
              List<Replay> arrayList =   comment.getReplyList();
              if(arrayList == null){
                  arrayList = new ArrayList<>();
              }
              arrayList.add(replay);

              notifyItemChanged(position);
                break;
            }
            position++;
        }

    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detial_comment,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
            holder.bindData(mComments.get(position));
    }

    @Override
    public int getItemCount() {
        return mComments == null  ? 0 : mComments.size();
    }


    public class CommentHolder extends RecyclerView.ViewHolder{

        ItemDetialCommentBinding binding;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);

            binding = ItemDetialCommentBinding.bind(itemView);

            itemView.setOnClickListener(v ->{
                mOnItemClickListener.onReplayComment(mComments.get(getBindingAdapterPosition()));
            });


            /**
             *  设置监听，监听点击用户名或者点击回复内容
             *  监听有三个参数：
             *  1. 回复列表里面被点击的item的position
             *  2. 如果直接点击的是用户名，那么这个id 就是对应用户的id，如果点击的内容那么这个id 表示 回复那个人的id。
             *
             *  3. 回复列表里面被点击的回复对象
             */
            binding.detailCommentItemReply.setOnItemClickListener(new CommentsView.onItemClickListener() {
                @Override
                public void onItemClick(int position, String clickUserId, CommentsView.ReplayData replayData) {

                }
            });
            binding.detailCommentItemReply.setOnItemClickListener(new CommentsView.onItemClickListener<Replay>() {
                @Override
                public void onItemClick(int position, String clickUserId, Replay replayData) {

                   mOnItemClickListener.onReplayReplay(mComments.get(getBindingAdapterPosition()),clickUserId,replayData);
                }
            });
            binding.detailCommentItemCbLick.setOnClickListener(v -> {
                mOnItemClickListener.onLikeClick(mComments.get(getBindingAdapterPosition()).getCommentId(),getBindingAdapterPosition());
            });
        }


        public void bindData(Comment comment){
            GlideApp.with(itemView).load(comment.getHeadUrl()).circleCrop().into(binding.detailComcomtItemUserHeadPic);

            binding.detailCommentItemTvUsername.setText(comment.getUserName());
            binding.detailCommentItemTvTime.setText(comment.getTimeDescribe());
            binding.detailCommentItemTvContent.setText(comment.getContent());
            binding.detailCommentItemCbLick.setText(String.valueOf(comment.getPraiseCountDescribe()));

         //   binding.detailCommentItemCbLick.setChecked(comment.isPraise());

            if(comment.getReplyMore() == 1){

                binding.detailCommentItemShowMore.setVisibility(View.VISIBLE);
            }else{
                binding.detailCommentItemShowMore.setVisibility(View.GONE);
            }

            // 把回复的集合给它
            binding.detailCommentItemReply.setList(comment.getReplyList());



            // 这行代码不要忘记了，这行代码才会去刷新回复列表，
            binding.detailCommentItemReply.notifyDataSetChanged();


        }
    }



    public interface OnItemClickListener{

        // 点击了主评论
        void onReplayComment(Comment comment);

        // 点击了评论里面的回复
        void onReplayReplay(Comment comment, String userId, Replay replay);

        default void onLikeClick(String commentId, int position){

        }
    }


}
