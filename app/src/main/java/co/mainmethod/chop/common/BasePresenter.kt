package co.mainmethod.chop.common

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Base implementation for the presenter class.
 * Established the relationship between it, the view, and view model
 * Note all things that should be persisted between config changes should be on the view model
 * Created by evan on 12/1/17.
 */
abstract class BasePresenter<V : BaseView, VM: BaseViewModel>(internal val viewModel: VM) {

    private var view: V? = null
    private val subscriptions = CompositeDisposable()

    internal fun subscribe(subscription: Disposable) {
        subscriptions.add(subscription)
    }
    fun attach(view: V) {
        this.view = view
    }

    fun detach() {
        view = null
        subscriptions.clear()
    }

    fun isAttached() = view != null

    fun getView() = view

}