### 对Flow、RxJava2请求结果封装

```kotlin
//Flow流代码示例,通过扩展方法applyApiResultSchedulers将结果封装成ApiResult
    private fun flowRequest(){
        lifecycleScope.launch {
            flow {
                val call = DemoApiRequest().syncUserLogin()
                emit(call.execute())
            }.applyApiResultSchedulers().collect {
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

//rx代码示例，通过RxUtils.applyApiResultSchedulers()将结果封装为ApiResult形式
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


```

