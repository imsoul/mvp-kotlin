package com.ljb.rxjava.kotlin.protocol

import android.text.TextUtils
import com.ljb.rxjava.kotlin.net.XgoHttpClient
import io.reactivex.Observable

/**
 * Created by L on 2017/6/8.
 */
abstract class BaseHttpProtocol {
    /**
     *  创建一个被观察者(被订阅者)对象
     *  @param url
     *  @param method
     *  @param params
     */
    protected fun createObservable(url: String, method: String, params: Map<String, String>?): Observable<String> {
        return Observable.create<String> {
            val request = XgoHttpClient.getRequest(url, method, params)
            val response = XgoHttpClient.execute(request)
            val json = response.body()?.string()
            if (json.isNullOrBlank()) {
                it.onError(Throwable("not http data"))
            } else {
                it.onNext(json!!)
                it.onComplete()
            }
        }
    }

    protected fun createObservable(url: String, method: String): Observable<String> {
        return createObservable(url, method, null)
    }


    /**
     * 创建指定解析器的Observable
     * */
    protected fun <T> createObservable(url: String, method: String, params: Map<String, String>?, parser: (String) -> T): Observable<T> {
        return Observable.create {
            val request = XgoHttpClient.getRequest(url, method, params)
            val response = XgoHttpClient.execute(request)
            val json = response.body()?.string()
            if (json.isNullOrBlank()) {
                it.onError(Throwable("not http data"))
            } else {
                it.onNext(parser.invoke(json!!))
                it.onComplete()
            }
        }
    }

    protected fun <T> createObservable(url: String, method: String, parser: (String) -> T): Observable<T> {
        return createObservable(url, method, null, parser)
    }


    fun nvl(param: String): String {
        if (TextUtils.isEmpty(param)) {
            return ""
        } else {
            return param.trim { it <= ' ' }
        }
    }

}
