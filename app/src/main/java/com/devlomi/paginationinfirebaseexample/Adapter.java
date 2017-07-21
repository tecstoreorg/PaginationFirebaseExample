package com.devlomi.paginationinfirebaseexample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.marcorei.infinitefire.InfiniteFireArray;
import com.marcorei.infinitefire.InfiniteFireRecyclerViewAdapter;

/**
 * Created by Devlomi on 20/07/2017.
 */

public class Adapter extends InfiniteFireRecyclerViewAdapter<Model> {

    private Context context;
    public static final int VIEW_TYPE_CONTENT = 1;
    public static final int VIEW_TYPE_FOOTER = 2;


    /**
     * This is the view holder for the simple header and footer of this example.
     */
    public class LoadingHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        }
    }

    private boolean loadingMore = false;

    /**
     * @param snapshots data source for this adapter.
     */
    public Adapter(InfiniteFireArray snapshots, Context context) {
        super(snapshots, 0, 1);
        this.context = context;
    }


    /**
     * @return status of load-more loading procedures
     */
    public boolean isLoadingMore() {
        return loadingMore;
    }


    public void setLoadingMore(boolean loadingMore) {
        if (loadingMore == this.isLoadingMore()) return;
        this.loadingMore = loadingMore;
        notifyItemChanged(getItemCount() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return VIEW_TYPE_FOOTER;
        }
        return VIEW_TYPE_CONTENT;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPE_CONTENT:
                view = inflater.inflate(R.layout.row, parent, false);
                viewHolder = new MyHolder(view);
                break;
            case VIEW_TYPE_FOOTER:
                view = inflater.inflate(R.layout.list_item_loading, parent, false);
                viewHolder = new LoadingHolder(view);
                break;
            default:
                throw new IllegalArgumentException("Unknown type");
        }
        return viewHolder;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView id, name, email, gender;

        public MyHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.id);
            name = (TextView) itemView.findViewById(R.id.full_name_tv);
            email = (TextView) itemView.findViewById(R.id.email);
            gender = (TextView) itemView.findViewById(R.id.gender);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case VIEW_TYPE_CONTENT:
                Model model = snapshots.getItem(position - indexOffset).getValue();

                MyHolder mHolder = (MyHolder) holder;
                mHolder.id.setText(model.getId().toString());
                mHolder.name.setText(model.getFirstName() + " " + model.getLastName());
                mHolder.email.setText(model.getEmail());
                mHolder.gender.setText(model.getGender());
                break;
            case VIEW_TYPE_FOOTER:
                LoadingHolder footerHolder = (LoadingHolder) holder;
                footerHolder.progressBar.setVisibility((isLoadingMore()) ? View.VISIBLE : View.GONE);
                break;
            default:
                throw new IllegalArgumentException("Unknown type");
        }
    }
}

