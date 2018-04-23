package com.example.stephanieangulo.orlyst;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {
//    private static final int PAGE_START = 1;
//
//    private boolean isLoading = false;
//    private boolean isLastPage = false;
//    private int TOTAL_PAGES = 5;
//    private int currentPage = PAGE_START;
//    private static final int LIMIT = 20;
    private GridLayoutManager gridLayoutManager;

    public PaginationScrollListener(GridLayoutManager gridLayoutManager) {
        this.gridLayoutManager = gridLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
//        int visibleItemCount = gridLayoutManager.getChildCount();
//        int totalItemCount = gridLayoutManager.getItemCount();
        int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = gridLayoutManager.findLastVisibleItemPosition();
        if (!isLoading() && !isLastPage()) {
            if(lastVisibleItemPosition - firstVisibleItemPosition > 0) {
                loadMoreItems();
            }
        }
    }
    protected abstract void loadMoreItems();

    public abstract int getTotalPageCount();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();



}
