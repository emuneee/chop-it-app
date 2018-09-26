package co.mainmethod.chop.common

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import co.mainmethod.chop.app.ChopApp
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Base implementation for my activity.
 * Simply established a relationship between the activity and the presenter.
 * This is all it should do!
 * Created by evan on 11/9/17.
 */
abstract class BaseActivity<out P: BasePresenter<V, VM>, V : BaseView, VM : BaseViewModel> : AppCompatActivity() {

    private var presenter: BasePresenter<V, VM>? = null
    private val subscriptions = CompositeDisposable()

    internal fun subscribe(subscription: Disposable) {
        subscriptions.add(subscription)
    }

    @Suppress("UNCHECKED_CAST")
    internal fun getPresenter(): P = presenter as P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResource())
        presenter = init()
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.clear()
    }

    abstract fun init(): BasePresenter<V, VM>
    abstract fun getLayoutResource(): Int
}