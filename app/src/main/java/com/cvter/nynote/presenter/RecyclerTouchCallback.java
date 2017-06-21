package com.cvter.nynote.presenter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by cvter on 2017/6/15.
 * ItemTouchHelper监听类
 */

public class RecyclerTouchCallback extends ItemTouchHelper.Callback {

    RecyclerTouchPresenter mPresenter;

    public RecyclerTouchCallback(RecyclerTouchPresenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        int dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.DOWN | ItemTouchHelper.UP;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

        mPresenter.onItemSwipe(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mPresenter.onItemClear(viewHolder.getAdapterPosition());

    }
}
