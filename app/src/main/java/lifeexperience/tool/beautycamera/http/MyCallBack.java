package lifeexperience.tool.beautycamera.http;

import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Request;
import okhttp3.Response;

public abstract class MyCallBack<T> {


    public Type mType;

    static Type getSuperclassTypeParameter(Class<?> subclass)
    {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class)
        {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    public MyCallBack() {
        mType = getSuperclassTypeParameter(getClass());
    }

    public abstract void onLoadingBefore(Request request);
    //第二个参数为传入的泛型，也就是说返回的结果直接就是我们需要的类型
    public abstract void onSuccess(Response response, T result);

    public abstract void onFailure(Request request, Exception e);

    public abstract void onError(Response response);

}

