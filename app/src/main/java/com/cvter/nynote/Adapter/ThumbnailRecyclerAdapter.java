package com.cvter.nynote.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cvter.nynote.Model.NoteInfo;
import com.cvter.nynote.R;

import java.util.ArrayList;

/**
 * Created by cvter on 2017/6/5.
 */

public class ThumbnailRecyclerAdapter extends RecyclerView.Adapter<ThumbnailRecyclerAdapter.ThumbnailViewHolder>{

    private Context mContext;
    private ArrayList<NoteInfo> notes;

    public ThumbnailRecyclerAdapter(Context mContext, ArrayList<NoteInfo> notes){
        this.mContext = mContext;
        this.notes = notes;

    }

    @Override
    public ThumbnailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView=View.inflate(mContext, R.layout.item_recyclerview,null);
        ThumbnailViewHolder viewHolder=new ThumbnailViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ThumbnailViewHolder holder, int position) {

        if(notes != null && notes.get(position) != null){
            Glide.with(mContext).load(notes.get(position).getNotePic()).into(holder.thumbnailImageView);
            String name = notes.get(position).getNoteName().replace(".jpg", "");
            holder.thumbnailTextView.setText(name);
        }

    }

    @Override
    public int getItemCount() {
        if (notes != null && notes.size() != 0){
            return notes.size();
        }
        return 0;
    }

    class ThumbnailViewHolder extends RecyclerView.ViewHolder{
        ImageView thumbnailImageView;
        TextView thumbnailTextView;
        private ThumbnailViewHolder(View itemView) {
            super(itemView);
            thumbnailImageView = (ImageView) itemView.findViewById(R.id.thumbnail_imageView);
            thumbnailTextView = (TextView) itemView.findViewById(R.id.thumbnail_textView);
        }
    }
}
