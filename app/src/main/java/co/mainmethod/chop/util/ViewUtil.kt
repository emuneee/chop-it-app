package co.mainmethod.chop.util

import android.view.View

/**
 * Created by evan on 1/17/18.
 */

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}