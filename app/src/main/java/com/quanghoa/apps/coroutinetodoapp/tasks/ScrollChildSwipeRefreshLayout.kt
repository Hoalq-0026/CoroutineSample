package com.quanghoa.apps.coroutinetodoapp.tasks

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import android.view.View

/**
 * Extends [SwipeRefreshLayout] to support non-direct descendant scrolling views
 *
 * [SwipeRefreshLayout] works as expected when a scroll view is a direct child: it triggers
 * the refresh only when the view is on top. This class adds a way(@link #setScrollUpChild} to
 * define which view controls this behavior.
 */
class ScrollChildSwipeRefreshLayout @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null
) : SwipeRefreshLayout(context, attributes) {

    var scrollUpChild: View? = null

    override fun canChildScrollUp(): Boolean = scrollUpChild?.canScrollVertically(-1) ?: super.canChildScrollUp()
}