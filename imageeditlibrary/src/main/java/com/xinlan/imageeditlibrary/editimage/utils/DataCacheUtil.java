package com.xinlan.imageeditlibrary.editimage.utils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class DataCacheUtil {

    private static DataCacheUtil instance;

    public static DataCacheUtil getInstance() {
        if (instance == null) {
            synchronized (DataCacheUtil.class) {
                if (instance == null) {
                    instance = new DataCacheUtil();
                }
            }
        }
        return instance;
    }

    private Map<String, WeakReference<Object>> map = new HashMap<>();

    /**
     * 数据存储
     *
     * @param id
     * @param object
     */
    public void saveData(String id, Object object) {
        map.put(id, new WeakReference<>(object));
    }

    /**
     * 获取数据
     *
     * @param id
     * @return
     */
    public Object getData(String id) {
        WeakReference<Object> weakReference = map.get(id);
        return weakReference.get();
    }

    /**
     * 删除对应键的值。
     *
     * @param id
     */
    private void removeData(String id) {
        if (map.containsKey(id)) {
            map.remove(id);
        }
    }
}
