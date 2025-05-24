### 对RxJava2请求结果封装

```kotlin
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

```

