package com.github.mylibdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.yjz.wrap_retrofit.http.ApiResult;
import com.github.yjz.wrap_retrofit.util.RxUtils;
import com.github.mylibdemo.api.DemoApiRequest;
import com.github.mylibdemo.bean.AuthBean;
import com.github.mylibdemo.net.RetrofitMgr;
import com.rxjava.rxlife.RxLife;


public class MainActivity extends AppCompatActivity {

    private Button btnRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RetrofitMgr.init(getApplication());

        btnRequest = findViewById(R.id.btnRequest);


        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRequestEvent();
            }
        });
    }


    private void handleRequestEvent() {
        new DemoApiRequest().requestAccessToken()
                .compose(RxUtils.applySchedulers())
                .as(RxLife.as(this))
                .subscribe(apiResult -> {
                    Log.e("MyRetrofit", "handleRequestEvent1 " + apiResult);

                    if (apiResult.isSuccess()) {
                        //请求成功
                        ApiResult.Success<AuthBean> successResult = apiResult.getApiSuccess();

                    } else if (apiResult.isBizError()) {
                        //业务异常
                        ApiResult.BizError<AuthBean> bizErrorResult = apiResult.getApiBizError();

                        Log.e("MyRetrofit", "BizError " + bizErrorResult);

                    } else if (apiResult.isException()) {
                        //其它异常
                        ApiResult.Exception<AuthBean> exp = apiResult.getApiException();

                        Log.e("MyRetrofit", "Exception " + exp.exp.code);
                    }
                });
    }
}