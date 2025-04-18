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
import com.rxjava.rxlife.RxLife;
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import retrofit2.Call

class MainActivity : AppCompatActivity() {

    private lateinit var  btnRequest:Button

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_main);
         RetrofitMgr.init(getApplication());

         btnRequest = findViewById(R.id.btnRequest);


         btnRequest.setOnClickListener{
             onBtnRequestClick()
         }
     }



    private fun onBtnRequestClick() {
        flowRequest()
    }

    private fun flowRequest(){
        lifecycleScope.launch {
            flow {
                val call = DemoApiRequest().syncUserLogin()
                emit(call.execute())
            }.applyApiResultSchedulers().collect {
                Log.d("MainActivity","flowRequest $it")
                when (it) {
                    is ApiResult.Loading -> {
                        //加载状态
                    }

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

     private fun rxRequest(){
          DemoApiRequest().userLogin()
             .compose(RxUtils.applyApiResultSchedulers())
             .`as`(RxLife.`as`(this))
             .subscribe { apiResult ->
                 when (apiResult) {
                     is ApiResult.Loading -> {
                         //加载状态
                     }

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
}