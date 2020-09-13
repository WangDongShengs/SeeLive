package com.wds.seelive.home.recommend.page;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.wds.seelive.data.entity.AlbumNews;

//专辑适配器
public class AlbumAdapter extends ListAdapter<AlbumNews, AlbumAdapter.AlbumHolder> {


    protected AlbumAdapter(@NonNull DiffUtil.ItemCallback<AlbumNews> diffCallback) {
        super(diffCallback);
    }


    @NonNull
    @Override
    public AlbumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class AlbumHolder extends RecyclerView.ViewHolder{

        public AlbumHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
