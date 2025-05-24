package com.github.mylibdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.lifecycle.lifecycleScope

import com.github.mylibdemo.bean.BaseResponse;
import com.github.mylibdemo.bean.LoginUserInfo;
import com.github.yjz.livedata.SingleLiveData;
import com.github.yjz.wrap_retrofit.http.ApiResult;
import com.github.yjz.wrap_retrofit.util.RxUtils;
import com.github.mylibdemo.api.DemoApiRequest;
import com.github.mylibdemo.net.RetrofitMgr;
import com.github.yjz.wrap_retrofit.util.FlowUtils.applyApiResultSchedulers
import com.github.yjz.wrap_retrofit.util.FlowUtils.applySchedulers
import com.rxjava.rxlife.RxLife;
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import retrofit2.Call

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
        flowRequest1();
//        flowRequest()
//        rxRequest()
//        okCallRequest()
    }

    /**
     * Retrofit基于协程，请求Service直接返回ApiResult<T>形式
     * 需继承ApiResultGsonConverterFactory重写gson解析，根据自己服务器返回的json格式而定。
     * 1.添加ApiResultResponseAdapterFactory适配器
     * 2.添加ApiResultGsonConverterFactory的gson转换器，可参考DefaultCustomGsonConverterFactory实现，设置给Retrofit实例
     */
    private fun flowRequest1(){
        lifecycleScope.launch{
            DemoApiRequest().flowUserLogin1()
                .applySchedulers()
                .collect{
                    Log.d("MainActivity", "flowRequest1 $it")

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