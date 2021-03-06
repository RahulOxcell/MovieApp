package com.app.movieapp.retrofit


import androidx.lifecycle.MutableLiveData
import com.app.movieapp.model.ResponseData
import com.app.movieapp.utility.NETWORK_ERROR
import com.app.movieapp.utility.SERVER_ERROR
import com.app.movieapp.utility.Utils
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


/**
 * Created by RahulSadhu.
 */
class NetworkManager {
    val apiError = MutableLiveData<String>()
    val apiResponse: MutableLiveData<ResponseData<Any>> = MutableLiveData()
    private lateinit var call: Call<*>
    private lateinit var key: String
    val gson = Gson()

    fun <T> requestData(call: Call<T>, key: String) {
        this.call = call.clone()
        this.key = key
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>?, response: Response<T>?) {
                val data = response as Response<ResponseData<Any>>
                if (response.isSuccessful) {
                    data.body()?.key = key
                    apiResponse.postValue(data.body())
                } else {
                        apiError.postValue(SERVER_ERROR)

                }

            }

            override fun onFailure(call: Call<T>, t: Throwable?) {
                Utils.log("onFailure ${t?.localizedMessage}")
                if (call.isCanceled) {
                    Utils.log("request was cancelled")
                } else {
                    val message: String = when (t) {
                        is ConnectException -> NETWORK_ERROR
                        is UnknownHostException -> NETWORK_ERROR
                        is SocketTimeoutException -> "Please try again later…"
                        else -> SERVER_ERROR
                    }

                    apiError.postValue(message)
                }


            }
        })
    }

    fun retryCall() {
        if (!call.isExecuted) {
            requestData(call, key)
        }
    }
}