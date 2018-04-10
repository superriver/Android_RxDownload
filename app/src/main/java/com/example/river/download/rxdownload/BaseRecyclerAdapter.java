package com.example.river.download.rxdownload;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.river.download.R;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:
 * Author:oubowu<p>
 * Fuction: RecyclerView通用适配器<p>
 * CreateDate:2016/2/16 22:47<p>
 * UpdateUser:<p>
 * UpdateDate:<p>
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewHolder> {

    public static final int TYPE_HEADER = 1;
    public static final int TYPE_ITEM = 2;
    public static final int TYPE_MORE = 3;
    public static final int TYPE_EMPTY = 4;
    private static final int TYPE_MORE_FAIL = 5;

    protected List<T> mData;
    protected Context mContext;
    protected boolean mUseAnimation;
    protected LayoutInflater mInflater;
    protected OnItemClickListener mClickListener;
    protected boolean mShowLoadMoreView;
    protected boolean mShowEmptyView;

    private RecyclerView.LayoutManager mLayoutManager;

    private int mLastPosition = -1;
    private String mExtraMsg;
    private OnEmptyClickListener mEmptyClickListener;
    private int mMoreItemCount;

    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean mEnableLoadMore;

    public BaseRecyclerAdapter(Context context, List<T> data) {
        this(context, data, true);
    }

    public BaseRecyclerAdapter(Context context, List<T> data, boolean useAnimation) {
        this(context, data, useAnimation, null);
    }

    public BaseRecyclerAdapter(Context context, List<T> data, boolean useAnimation, RecyclerView.LayoutManager layoutManager) {
        mContext = context;
        mUseAnimation = useAnimation;
        mLayoutManager = layoutManager;
        mData = data == null ? new ArrayList<T>() : data;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_MORE) {
            return new BaseRecyclerViewHolder(mContext, mInflater.inflate(R.layout.load_more_layout, parent, false));
        } else if (viewType == TYPE_MORE_FAIL) {
            final BaseRecyclerViewHolder holder = new BaseRecyclerViewHolder(mContext, mInflater.inflate(R.layout.item_load_more_failed, parent, false));
            if (mOnLoadMoreListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mEnableLoadMore = true;
                        mShowLoadMoreView = true;
                        notifyItemChanged(getItemCount() - 1);
                        holder.itemView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mOnLoadMoreListener.loadMore();
                            }
                        }, 300);
                    }
                });
            }
            return holder;
        } else if (viewType == TYPE_EMPTY) {
            final BaseRecyclerViewHolder holder = new BaseRecyclerViewHolder(mContext, mInflater.inflate(R.layout.item_empty_view, parent, false));
            if (mEmptyClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mEmptyClickListener.onEmptyClick();
                    }
                });
            }
            return holder;
        } else {
            final BaseRecyclerViewHolder holder = new BaseRecyclerViewHolder(mContext, mInflater.inflate(getItemLayoutId(viewType), parent, false));
            if (mClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                            mClickListener.onItemClick(v, holder.getAdapterPosition());
                        }
                    }
                });
            }
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_MORE) {
            fullSpan(holder, TYPE_MORE);
        } else if (getItemViewType(position) == TYPE_MORE_FAIL) {
            fullSpan(holder, TYPE_MORE_FAIL);
            holder.setText(R.id.tv_failed, mExtraMsg + "请点击重试！");
        } else if (getItemViewType(position) == TYPE_EMPTY) {
            fullSpan(holder, TYPE_EMPTY);
            holder.setText(R.id.tv_error, mExtraMsg);
        } else {
            bindData(holder, position, mData.get(position));
            if (mUseAnimation) {
                setAnimation(holder.itemView, position);
            }
        }

        if (!mShowEmptyView && mOnLoadMoreListener != null && mEnableLoadMore && !mShowLoadMoreView && position == getItemCount() - 1 && getItemCount() >= mMoreItemCount) {
            mShowLoadMoreView = true;
            holder.itemView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mOnLoadMoreListener.loadMore();
                    notifyItemInserted(getItemCount());
                }
            }, 300);
        }

    }

    private void fullSpan(BaseRecyclerViewHolder holder, final int type) {
        if (mLayoutManager != null) {
            if (mLayoutManager instanceof StaggeredGridLayoutManager) {
                if (((StaggeredGridLayoutManager) mLayoutManager).getSpanCount() != 1) {
                    StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
                    params.setFullSpan(true);
                }
            } else if (mLayoutManager instanceof GridLayoutManager) {
                final GridLayoutManager gridLayoutManager = (GridLayoutManager) mLayoutManager;
                final GridLayoutManager.SpanSizeLookup oldSizeLookup = gridLayoutManager.getSpanSizeLookup();
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (getItemViewType(position) == type) {
                            return gridLayoutManager.getSpanCount();
                        }
                        if (oldSizeLookup != null) {
                            return oldSizeLookup.getSpanSize(position);
                        }
                        return 1;
                    }
                });
            }
        }
    }

    protected void setAnimation(View viewToAnimate, int position) {
        //if (position > mLastPosition) {
        //    Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.item_bottom_in);
        //    viewToAnimate.startAnimation(animation);
        //    mLastPosition = position;
        //}
    }

    @Override
    public void onViewDetachedFromWindow(BaseRecyclerViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (mUseAnimation && holder.itemView.getAnimation() != null && holder.itemView.getAnimation().hasStarted()) {
            holder.itemView.clearAnimation();
        }
    }

    public void add(int pos, T item) {
        mData.add(pos, item);
        notifyItemInserted(pos);
    }

    public void delete(int pos) {
        mData.remove(pos);
        notifyItemRemoved(pos);
    }

    public void addMoreData(List<T> data) {
        int startPos = mData.size();
        mData.addAll(data);
        notifyItemRangeInserted(startPos, data.size());
    }

    public List<T> getData() {
        return mData;
    }

    public void setData(List<T> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {

        if (mShowEmptyView) {
            return TYPE_EMPTY;
        }

        if (mOnLoadMoreListener != null && mEnableLoadMore && mShowLoadMoreView && getItemCount() - 1 == position) {
            return TYPE_MORE;
        }

        if (mOnLoadMoreListener != null && !mShowLoadMoreView && !mEnableLoadMore && getItemCount() - 1 == position) {
            return TYPE_MORE_FAIL;
        }

        return bindViewType(position);
    }

    @Override
    public int getItemCount() {
        int i = mOnLoadMoreListener == null ? 0 : (mEnableLoadMore && mShowLoadMoreView) || (!mShowLoadMoreView && !mEnableLoadMore) ? 1 : 0;
        return mShowEmptyView ? 1 : mData != null ? mData.size() + i : 0;
    }

    public abstract int getItemLayoutId(int viewType);

    public abstract void bindData(BaseRecyclerViewHolder holder, int position, T item);

    protected int bindViewType(int position) {
        return 0;
    }

    public void showEmptyView(boolean showEmptyView, @NonNull String msg) {
        mShowEmptyView = showEmptyView;
        mExtraMsg = msg;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void setOnEmptyClickListener(OnEmptyClickListener listener) {
        mEmptyClickListener = listener;
    }

    public void setOnLoadMoreListener(int moreItemCount, @NonNull OnLoadMoreListener onLoadMoreListener) {
        mMoreItemCount = moreItemCount;
        mOnLoadMoreListener = onLoadMoreListener;
        mEnableLoadMore = true;
    }

    public void loadMoreSuccess() {
        mEnableLoadMore = true;
        mShowLoadMoreView = false;
        notifyItemRemoved(getItemCount());
    }

    public void loadMoreFailed(String errorMsg) {
        mEnableLoadMore = false;
        mShowLoadMoreView = false;
        mExtraMsg = errorMsg;
        notifyItemChanged(getItemCount() - 1);
    }

    public void enableLoadMore(boolean enableLoadMore) {
        mEnableLoadMore = enableLoadMore;
    }
    public interface OnEmptyClickListener {

        void onEmptyClick();

    }
    /**
     * ClassName: OnItemClickListener<p>
     * Author:oubowu<p>
     * Fuction: 点击长按的接口<p>
     * CreateDate:2016/2/14 1:48<p>
     * UpdateUser:<p>
     * UpdateDate:<p>
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    /**
     * Created by Oubowu on 2016/9/16 21:40.
     */
    public interface OnLoadMoreListener {

        void loadMore();

    }

}
