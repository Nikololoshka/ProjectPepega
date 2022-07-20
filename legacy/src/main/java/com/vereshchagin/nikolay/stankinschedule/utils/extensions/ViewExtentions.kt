package com.vereshchagin.nikolay.stankinschedule.utils.extensions

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.ViewStubProxy

/**
 * Возвращает view внутри ViewStub. Если View еще нет, то создает ее.
 */
fun ViewStubProxy.inflateView(): View {
    if (!isInflated) {
        viewStub?.inflate()
    }
    return root
}

/**
 * Создает ViewDataBinding для ViewStub.
 */
fun <T : ViewDataBinding> ViewStubProxy.createBinding(): T? {
    return DataBindingUtil.bind<T>(inflateView())
}