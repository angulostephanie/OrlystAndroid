package com.example.stephanieangulo.orlyst;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

public class GalleryItemDecoration extends RecyclerView.ItemDecoration {
    private int spanCount, spacing;
    private boolean includeEdge;
    private Context context;

    public GalleryItemDecoration(int spanCount, int spacing, boolean includeEdge, Context context) {
        this.spanCount = spanCount;
        this.context = context;
        this.spacing = dpToPx(spacing);
        this.includeEdge = includeEdge;

    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // customize how gallery recycler view looks
        int position = parent.getChildAdapterPosition(view); // item position
        //int column = position % spanCount; // item column
        outRect.left = spacing;
        outRect.right = spacing;
        outRect.bottom = spacing;
        outRect.top = spacing;
    }
    private int dpToPx(int dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
