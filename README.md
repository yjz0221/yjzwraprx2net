### 优雅的对Flow、RxJava2网络请求结果封装

```kotlin
//Flow流代码示例,通过扩展方法applyApiResultSchedulers将结果转换成ApiResult
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

//rx代码示例，通过RxUtils.applyApiResultSchedulers()将结果转换成ApiResult形式
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

### 如何引入依赖
#### 项目级别的build.gradle
```
 repositories {
        maven { url 'https://jitpack.io' }
    }
```
#### 应用模块级别的build.gradle
```
dependencies{
    implementation 'com.github.yjz0221:yjzwraprx2net:1.0.6'
}
```
