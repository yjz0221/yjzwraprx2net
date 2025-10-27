# ===================================================================================
# == Consumer ProGuard/R8 rules for yjzwraprx2net (wrap-retrofit) library
# == 这些规则会自动应用到依赖此库的项目中
# ===================================================================================

# --- 保留核心 HTTP 模型 ---
# 保留 ApiResult 及其内部类 (Success, BizError 等)
# 保留 exception 包下的类
# 保留 adapter, call, callback, interceptor 包下的类 (以防反射调用或作为公共 API)
-keep class com.github.yjz.wrap_retrofit.http.** { *; }

# --- 保留 Factory ---
# 保留 ApiResultConverterFactory 及其内部类，Retrofit 需要通过反射实例化
-keep class com.github.yjz.wrap_retrofit.http.factory.** { *; }

# --- 保留 Internal 实现 (impl) ---
# (谨慎保留，如果确认无外部反射调用可移除，但保留更安全)
-keep class com.github.yjz.wrap_retrofit.impl.** { *; }

# --- 保留 Listener ---
# 保留监听器接口/基类
-keep class com.github.yjz.wrap_retrofit.listener.** { *; }

# --- 保留 Util ---
# 保留工具类，防止公共静态方法被移除
-keep class com.github.yjz.wrap_retrofit.util.** { *; }

# --- (如果 yjz.livedata 包也是库的一部分) ---
# 保留 SingleLiveData (如果它是公共API或被反射使用)
-keep class com.github.yjz.livedata.** { *; }

# --- 确保保留泛型签名 (极其重要) ---
# 防止 ApiResult<T> 在运行时丢失 <T> 信息
-keepattributes Signature

# --- 忽略关于此库可能产生的警告 ---
# 防止库内部引用问题导致依赖项目构建失败
-dontwarn com.github.yjz.wrap_retrofit.**