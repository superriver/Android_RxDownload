package com.example.river.download.rxdownload;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * <pre>
 * author : No.1
 * time : 2017/6/21.
 * desc :
 * </pre>
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {

  SparseArray<View> views = new SparseArray<>();
  BaseRecyclerViewAdapter adapter;

  public BaseViewHolder(View itemView) {
    super(itemView);
  }


  public void setAdapter(BaseRecyclerViewAdapter adapter) {
    this.adapter = adapter;
  }

  /**
   * 获取view
   */
  public <V extends View> V getView(@IdRes int viewId) {
    View view = views.get(viewId);
    if (view == null) {
      view = itemView.findViewById(viewId);
      views.put(viewId, view);
    }
    return (V) view;
  }

  /**
   * @param viewId textview id
   * @param values 显示的值
   */
  public BaseViewHolder setText(@IdRes int viewId, String values) {
    TextView textView = getView(viewId);
    if (!TextUtils.isEmpty(values)) {
      textView.setText(values);
    }
    return this;
  }

  /**
   * @param viewId textview id
   * @param values R.string.values
   */
  public BaseViewHolder setText(@IdRes int viewId, @StringRes int values) {
    TextView textView = getView(viewId);
    textView.setText(values);
    return this;
  }

  /**
   * @param viewId imageview id
   * @param resId 资源文件id
   */
  public BaseViewHolder setImage(@IdRes int viewId, int resId) {
    ImageView imageView = getView(viewId);
    imageView.setImageResource(resId);
    return this;
  }

  public BaseViewHolder setImage(@IdRes int viewId, @Nullable Bitmap bitmap) {
    ImageView imageView = getView(viewId);
    imageView.setImageBitmap(bitmap);
    return this;
  }

  public BaseViewHolder setImage(@IdRes int viewId, Drawable dw) {
    ImageView imageView = getView(viewId);
    imageView.setImageDrawable(dw);
    return this;
  }

  public BaseViewHolder addClickListener(@IdRes int viewId) {
    final View view = getView(viewId);
    if (view != null) {
      if (!view.isClickable()) {
        view.setClickable(true);
      }
      view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (adapter.getOnItemChildClickListener() != null) {
            adapter.getOnItemChildClickListener().onItemChildClick(adapter, v, getClickPosition());
          }
        }
      });

    }
    return this;
  }

  private int getClickPosition() {
    return getLayoutPosition() - adapter.getHeaderLayoutCount();
  }

  /**
   * 设置开/关两种状态的按钮
   */

  public BaseViewHolder setCheck(@IdRes int viewId, boolean checked) {
    View view = getView(viewId);
    if (view instanceof CompoundButton) {
      ((CompoundButton) view).setChecked(checked);
    } else if (view instanceof CheckedTextView) {
      ((CheckedTextView) view).setChecked(checked);
    }
    return this;
  }

  public BaseViewHolder setVisible(@IdRes int viewId, boolean visible) {
    View view = getView(viewId);
    view.setVisibility(visible ? View.VISIBLE : View.GONE);
    return this;
  }

  /**
   * @param viewId
   * @param listener
   * @return
   */
  public BaseViewHolder setOnCheckedChangeListener(@IdRes int viewId,
      CompoundButton.OnCheckedChangeListener listener) {
    CompoundButton view = getView(viewId);
    view.setOnCheckedChangeListener(listener);
    return this;
  }

  /**
   * @param viewId
   * @param tag
   * @return
   */
  public BaseViewHolder setTag(@IdRes int viewId, Object tag) {
    View view = getView(viewId);
    view.setTag(tag);
    return this;
  }

  /**
   * @param
   * @param
   * @return
   */
  public BaseViewHolder setOnClickListener(@IdRes int viewId, View.OnClickListener listener) {
    View view = getView(viewId);
    view.setOnClickListener(listener);
    return this;
  }
}