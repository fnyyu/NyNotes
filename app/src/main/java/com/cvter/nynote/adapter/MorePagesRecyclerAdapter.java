package com.cvter.nynote.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cvter.nynote.R;
import com.cvter.nynote.activity.DrawActivity;
import com.cvter.nynote.presenter.RecyclerTouchPresenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by cvter on 2017/6/5.
 */

public class MorePagesRecyclerAdapter extends RecyclerView.Adapter<MorePagesRecyclerAdapter.PagesViewHolder> implements RecyclerTouchPresenter{

    private DrawActivity mContext;
    private List<Bitmap> mPages;

    public MorePagesRecyclerAdapter(DrawActivity mContext){
        this.mContext = mContext;
        mPages = new ArrayList<>();

    }

    public List<Bitmap> getPages() {
        return mPages;
    }

    @Override
    public PagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView=View.inflate(mContext, R.layout.item_page_recyclerview,null);
        return new PagesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PagesViewHolder holder, final int position) {

        if(mPages != null && mPages.get(position) != null){
            holder.pagesImageView.setImageBitmap(mPages.get(position));
            holder.pagesTextView.setText(Integer.toString(position + 1));
        }

    }

    @Override
    public int getItemCount() {
        if (mPages != null && !mPages.isEmpty()){
            return mPages.size();
        }
        return 0;
    }

    @Override
    public void onItemSwipe(int fromPosition, int toPosition) {
        Collections.swap(mPages, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        notifyItemChanged(fromPosition);
        notifyItemChanged(toPosition);
    }

    @Override
    public void onItemClear(int itemPosition) {
        mPages.remove(itemPosition);
        notifyItemRemoved(itemPosition);
        notifyItemChanged(itemPosition);
        mContext.getAllPagesTextView().setText(Integer.toString(mPages.size()));
    }

    class PagesViewHolder extends RecyclerView.ViewHolder{
        ImageView pagesImageView;
        TextView pagesTextView;
        private PagesViewHolder(View itemView) {
            super(itemView);
            pagesImageView = (ImageView) itemView.findViewById(R.id.page_num_imageView);
            pagesTextView = (TextView) itemView.findViewById(R.id.page_num_textView);
        }
    }

}