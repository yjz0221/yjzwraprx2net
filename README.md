#### <font face="楷体">基于Retrofit的协程、RxJava网络结果封装库</font>

1. <font face="楷体">`Retrofit`直接返回网络请求结果</font>

   - <font face="楷体">`ApiResultGsonConverterFactory`自定义gson解析，根据自己服务器返回的json格式进行解析。可参考`DefaultCustomGsonConverterFactory`对`ApiResultGsonConverterFactory`实现</font>

   - <font face="楷体">为`Retrofit`添加上述自定义好的Gson解析工厂.</font>

     ```kotlin
     Retrofit.addConverterFactory(DefaultCustomGsonConverterFactory.create())
     ```

     

   - <font face="楷体">代码示例：</font>

   ```kotlin
   //IDemoApi.kt
       @FormUrlEncoded
       @POST("OBDService.asmx/RequestData")
       suspend fun flowUserLogin1(@FieldMap map: Map<String, String>): ApiResult<BaseResponse<LoginUserInfo>>
   
   //MainActivity.kt
    private fun flowRequest1(){
           lifecycleScope.launch{
               DemoApiRequest().flowUserLogin1()
                   .applySchedulers()
                   .collect{
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

2. <font face="楷体">将任意的`Flow<T>`转成`Flow<ApiResult<T>>`形式，只需调用`Flow`的扩展方法`applyApiResultSchedulers()`</font>

   ```kotlin
   //DemoApi.kt
   @FormUrlEncoded
   @POST("OBDService.asmx/RequestData")
   suspend fun flowUserLogin(@FieldMap map: Map<String, String>): BaseResponse<LoginUserInfo>
   
   //DemoApiRequest.kt  对flowUserLogin进行一层包装，返回Flow<T>
   suspend fun flowUserLogin(): Flow<BaseResponse<LoginUserInfo>> {
           return flowOf(RetrofitMgr.getDemoApi().flowUserLogin(createLoginParam()))
   }
   
   //MainActivity.kt
   private fun flowRequest() {
           lifecycleScope.launch {
               DemoApiRequest().flowUserLogin()
                   .applyApiResultSchedulers()
                   .collect {
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

3. <font face="楷体">将任意的`Observable<T>`转成`Observable<ApiResult<T>>`形式，只需调用`RxUtils.applyApiResultSchedulers`</font>

   ```kotlin
   //IDemoApi.kt
   @FormUrlEncoded
   @POST("OBDService.asmx/RequestData")
   fun useLogin(@FieldMap map: Map<String, String>): Observable<BaseResponse<LoginUserInfo>>
   
   //MainActivity.kt
   private fun rxRequest() {
           DemoApiRequest().userLogin()
               .compose(RxUtils.applyApiResultSchedulers())
               .`as`(RxLife.`as`(this))
               .subscribe { apiResult ->
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
   ```

4. <font face="楷体">将OkHttp的Call同步请求结果转成ApiResult<T>形式，只需调用`OkHttpCallUtils.responseToApiResult(Call call)`方法</font>

   ```kotlin
   //DemoApiRequest.kt
   fun okCallUserLogin(): ApiResult<BaseResponse<String>> {
           val client = OkHttpClient.Builder()
               .connectTimeout(10, TimeUnit.SECONDS) // 连接超时
               .readTimeout(30, TimeUnit.SECONDS)    // 读取超时
               .writeTimeout(30, TimeUnit.SECONDS)   // 写入超时
               .build()
   
           val formBody = FormBody.Builder()
               .add("Token", "")
               .add("FunctionID", "UserLogin")
               .add("CheckNum", "13361B5D026A112440B3000AB9C1A599")
               .add("Params", "{\"UserName\": \"zhouguojian\",\"Password\": \"Zhougj@55331\"}")
               .build()
   
           val request = Request.Builder()
               .url(NetConstant.BASE_URL + "OBDService.asmx/RequestData")
               .post(formBody)
               .build()
   
           return OkHttpCallUtils.responseToApiResult(client.newCall(request))
   }
   
   //MainActivity.kt
    private fun okCallRequest() {
           lifecycleScope.launch(Dispatchers.IO) {
               DemoApiRequest().okCallUserLogin().let {
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


​	<font face="楷体" color="red">注意：网络结果封装实体类需实现`IBizError`接口，用于告知库如何判断当前是业务异常的情况。可参考`BaseResponse`实现</font>

#### <font face="楷体">依赖方式</font>

1. <font face="楷体">项目脚本文件`build.gradle`添加`jitpack`仓库地址</font>

   ```
   allprojects {
       repositories {
           maven { url 'https://jitpack.io' }
       }
   }
   
   ```

2. <font face="楷体">应用脚本文件`build.gradle`添加依赖</font>

   ```
       implementation 'com.github.yjz0221:yjzwraprx2net:1.0.7' //1.0.7替换成最新版本
   
   ```

<font face="楷体" color = "red">注意：目前支持Rxjava版本2.x</font>
