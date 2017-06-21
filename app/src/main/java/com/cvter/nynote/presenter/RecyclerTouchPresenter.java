package com.cvter.nynote.presenter;

/**
 * Created by cvter on 2017/6/15.
 * RecyclerView item 处理接口
 */

public interface RecyclerTouchPresenter {

    void onItemSwipe(int fromPosition, int toPosition);

    void onItemClear(int itemPosition);

}
