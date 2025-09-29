package com.github.mylibdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.lifecycle.lifecycleScope

import com.github.yjz.wrap_retrofit.http.ApiResult;
import com.github.yjz.wrap_retrofit.util.RxUtils;
import com.github.mylibdemo.api.DemoApiRequest;
import com.github.mylibdemo.net.RetrofitMgr;
import com.github.yjz.wrap_retrofit.util.FlowUtils.applyApiResultSchedulers
import com.github.yjz.wrap_retrofit.util.FlowUtils.applySchedulers
import com.rxjava.rxlife.RxLife;
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var btnRequest: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);
        RetrofitMgr.init(getApplication());

        btnRequest = findViewById(R.id.btnRequest);

        btnRequest.setOnClickListener {
            onBtnRequestClick()
        }
    }


    private fun onBtnRequestClick() {
//        MyJsonTest.test()
        flowRequest1();
//        flowRequest()
//        rxRequest()
//        okCallRequest()
//        okCallRequest2()
    }

    /**
     *Retrofit直接返回网络封装实体类
     */
    private fun flowRequest1(){
        lifecycleScope.launch{
            DemoApiRequest().flowUserLogin1()
                .applySchedulers()
                .collect{
                    Log.d("MainActivity", "flowRequest1 $it")
                    when (it) {
                        is ApiResult.Success -> {
                            //请求成功
                        }
                        is ApiResult.BizError -> {
                            //业务异常
                        }
                        is ApiResult.Exception -> {
                            //其它异常
                        }
                    }
                }
        }
    }

    /**
     * 将Flow<T>转成Flow<ApiResult<T>>形式
     */
    private fun flowRequest() {
        lifecycleScope.launch {
            DemoApiRequest().flowUserLogin()
                .applyApiResultSchedulers()
                .collect {
                    Log.d("MainActivity", "flowRequest $it")
                    when (it) {
                        is ApiResult.Success -> {
                            //请求成功
                        }
                        is ApiResult.BizError -> {
                            //业务异常
                        }
                        is ApiResult.Exception -> {
                            //其它异常
                        }
                    }
                }
        }
    }

    /**
     * 将Observable<T>转成ApiResult<T>形式
     */
    private fun rxRequest() {
        DemoApiRequest().userLogin()
            .compose(RxUtils.applyApiResultSchedulers())
            .`as`(RxLife.`as`(this))
            .subscribe { apiResult ->
                Log.d("MainActivity", "rxRequest $apiResult")
                when (apiResult) {
                    is ApiResult.Success -> {
                        //请求成功
                    }
                    is ApiResult.BizError -> {
                        //业务异常
                    }
                    is ApiResult.Exception -> {
                        //其它异常
                    }
                }
            };
    }

    /**
     * 将OkHttp的Call同步请求转成ApiResult<T>形式
     */
    private fun okCallRequest() {
        lifecycleScope.launch(Dispatchers.IO) {
            DemoApiRequest().okCallUserLogin().let {
                Log.d("MainActivity", "okCallRequest $it")
                when (it) {
                    is ApiResult.Success -> {
                        //请求成功
                        Log.d("MainActivity", "okCallRequest ${it.getData().value}")
                    }
                    is ApiResult.BizError -> {
                        //业务异常
                    }
                    is ApiResult.Exception -> {
                        //其它异常
                    }
                }
            }
        }
    }

    private fun okCallRequest2(){
        lifecycleScope.launch(Dispatchers.IO){
            Observable.just("")
                .map {
                    DemoApiRequest().okCallUserLogin2()
                }
                .compose(RxUtils.applyApiResult())
                .subscribe {
                    Log.d("MainActivity", "okCallRequest2 $it")
                    when (it) {
                        is ApiResult.Success -> {
                            //请求成功
                            Log.d("MainActivity", "okCallRequest2 ${it.getData().value}")
                        }
                        is ApiResult.BizError -> {
                            //业务异常
                        }
                        is ApiResult.Exception -> {
                            //其它异常
                        }
                    }
                }
        }
    }
}