package com.cvter.nynote.adapter;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cvter.nynote.R;
import com.cvter.nynote.activity.DrawActivity;
import com.cvter.nynote.model.PathInfo;
import com.cvter.nynote.presenter.FilePresenterImpl;
import com.cvter.nynote.presenter.IFilePresenter;
import com.cvter.nynote.presenter.RecyclerTouchPresenter;
import com.cvter.nynote.utils.Constants;
import com.cvter.nynote.utils.ImportListener;
import com.cvter.nynote.utils.SaveListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cvter on 2017/6/5.
 * 多页适配器RecyclerViewAdapter
 */

public class MorePagesRecyclerAdapter extends RecyclerView.Adapter<MorePagesRecyclerAdapter.PagesViewHolder> implements RecyclerTouchPresenter {

    private DrawActivity mContext;
    private LinkedList<Bitmap> mPages;
    private IFilePresenter mPresenter;
    private int MAX_MEM = (int) (Runtime.getRuntime().maxMemory() / 1024);
    private static final String TAG = "MorePagesAdapter";

    private LruCache<Integer, List<PathInfo>> mPathCache;

    private String mType;
    private String mNoteName = "";
    private static int mSavePosition = 1;
    private static int mEditPosition = 1;
    private int mSaveBitmapSize;

    public MorePagesRecyclerAdapter(DrawActivity mContext) {
        this.mContext = mContext;
        mPages = new LinkedList<>();
        if (mPathCache != null) {
            mPathCache.evictAll();
        }
        mPathCache = new LruCache<>(MAX_MEM / 8);

        mType = mContext.getIntent().getExtras().getString("skipType");
        if (mType.equals(Constants.READ_NOTE)) {
            mNoteName = Constants.PATH + "/" + mContext.getIntent().getStringExtra("noteName").replace(".png", "/");
        }

        mPresenter = new FilePresenterImpl(mContext);

    }

    public LinkedList<Bitmap> getPages() {
        return mPages;
    }

    public void setSaveBitmapSize(int mSaveBitmapSize) {
        this.mSaveBitmapSize = mSaveBitmapSize;
    }

    @Override
    public PagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(mContext, R.layout.item_page_recyclerview, null);
        return new PagesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PagesViewHolder holder, final int position) {

        if (mType.equals(Constants.NEW_EDIT) && mPages != null && mPages.get(position) != null) {
            handleNew(holder, position);
        }

        if (!mContext.getDrawPaintView().getIfCanDraw() && mType.equals(Constants.READ_NOTE)) {
            handleRead(holder, position);
        }

        if (mType.equals(Constants.READ_NOTE) && mContext.getDrawPaintView().getIfCanDraw()) {
            handleEdit(holder, position);
        }


    }

    private void handleNew(final PagesViewHolder holder, final int position) {
        final int savePosition = position + 1;
        holder.pagesImageView.setImageBitmap(mPages.get(position));
        holder.pagesTextView.setText(String.valueOf(position + 1));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.getCurPagesTextView().setText(String.valueOf(position + 1));
                List<PathInfo> curList;
                if (mContext.getDrawPaintView().getDrawingList() != null) {
                    curList = new LinkedList<>(mContext.getDrawPaintView().getDrawingList());
                } else {
                    curList = new LinkedList<>();
                }

                mPathCache.put(mSavePosition, curList);
                mPresenter.saveAsXML(mPathCache.get(mSavePosition),
                        Constants.TEMP_XML_PATH + "/" + mSavePosition + ".xml", new SaveListener() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onFail(String toastMessage) {
                                Log.e(TAG, toastMessage);
                            }
                        });

                mPresenter.saveAsImg(mPages.get(position), Constants.TEMP_IMG_PATH + "/" + savePosition + ".png",
                        new SaveListener() {
                            @Override
                            public void onSuccess() {
                                mSavePosition = position + 1;
                                mContext.getDrawPaintView().clear();
                                if (mPathCache.get(savePosition) != null) {
                                    mContext.getDrawPaintView().setDrawingList(mPathCache.get(savePosition));
                                }
                            }

                            @Override
                            public void onFail(String message) {
                                Log.e(TAG, message);
                            }
                        });


            }
        });
    }

    private void handleEdit(final PagesViewHolder holder, final int position) {
        final int savePosition = position + 1;
        if (position < mSaveBitmapSize) {
            Glide.with(mContext).load(mNoteName + "pic/"
                    + savePosition + ".png").into(holder.pagesImageView);
        }
        holder.pagesImageView.setImageBitmap(mPages.get(position));
        holder.pagesTextView.setText(String.valueOf(position + 1));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.getCurPagesTextView().setText(String.valueOf(position + 1));
                final List<PathInfo> curList;
                if (mContext.getDrawPaintView().getDrawingList() != null) {
                    curList = new LinkedList<>(mContext.getDrawPaintView().getDrawingList());
                } else {
                    curList = new LinkedList<>();
                }

                mPathCache.put(mEditPosition, curList);
                mPresenter.saveAsXML(mPathCache.get(mEditPosition),
                        Constants.TEMP_XML_PATH + "/" + mEditPosition + ".xml", new SaveListener() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onFail(String toastMessage) {
                                Log.e(TAG, toastMessage);
                            }
                        });

                mPresenter.saveAsImg(mPages.get(position), Constants.TEMP_IMG_PATH + "/" + savePosition + ".png",
                        new SaveListener() {
                            @Override
                            public void onSuccess() {
                                mEditPosition = position + 1;
                                mContext.getDrawPaintView().clear();
                                if (mPathCache.get(savePosition) != null) {
                                    mContext.getDrawPaintView().setDrawingList(mPathCache.get(savePosition));
                                } else {
                                    mPresenter.importXML(mNoteName + "xml/" + savePosition + ".xml", new ImportListener() {
                                        @Override
                                        public void onSuccess(List<PathInfo> info) {
                                            mContext.getDrawPaintView().setDrawingList(info);
                                            mPathCache.put(savePosition, info);
                                        }

                                        @Override
                                        public void onFail(String toastMessage) {
                                            Log.e(TAG, toastMessage);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFail(String message) {
                                Log.e(TAG, message);
                            }
                        });


            }
        });

    }

    private void handleRead(final PagesViewHolder holder, final int position) {
        final int drawPosition = position + 1;
        Glide.with(mContext).load(mNoteName + "pic/"
                + drawPosition + ".png").into(holder.pagesImageView);
        holder.pagesTextView.setText(String.valueOf(drawPosition));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.getCurPagesTextView().setText(String.valueOf(position + 1));
                mPresenter.importXML(mNoteName + "xml/" + (position + 1) + ".xml", new ImportListener() {
                    @Override
                    public void onSuccess(List<PathInfo> info) {
                        mContext.getDrawPaintView().clear();
                        mContext.getDrawPaintView().setDrawingList(info);
                        mContext.setCanScale();
                    }

                    @Override
                    public void onFail(String toastMessage) {
                        mContext.getDrawPaintView().clear();
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        if (mPages != null && !mPages.isEmpty() && mContext.getDrawPaintView().getIfCanDraw()) {
            return mPages.size();
        } else if (!mContext.getDrawPaintView().getIfCanDraw()) {
            return mSaveBitmapSize;
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
        if (itemPosition > 0) {
            mPages.remove(itemPosition);
            notifyItemRemoved(itemPosition);
            notifyItemChanged(itemPosition);
            mContext.getAllPagesTextView().setText(String.valueOf(mPages.size()));
        }

    }

    class PagesViewHolder extends RecyclerView.ViewHolder {
        ImageView pagesImageView;
        TextView pagesTextView;

        private PagesViewHolder(View itemView) {
            super(itemView);
            pagesImageView = (ImageView) itemView.findViewById(R.id.page_num_imageView);
            pagesTextView = (TextView) itemView.findViewById(R.id.page_num_textView);
        }
    }

}