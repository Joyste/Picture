package com.lifeexperience.picture.view.utils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class DataUtil {

        private static DataUtil instance;

        public static DataUtil getInstance(){
            if(instance == null){
                synchronized (DataUtil.class) {
                    if (instance == null) {
                        instance = new DataUtil();
                    }
                }
            }
            return instance;
        }

        private Map<String, WeakReference<Object>> map = new HashMap<>();

        /**
         * 数据存储
         * @param id
         * @param object
         */
        public void saveData(String id, Object object) {
            map.put(id, new WeakReference<>(object));
        }

        /**
         * 获取数据
         * @param id
         * @return
         */
        public Object getData(String id) {
            WeakReference<Object> weakReference = map.get(id);
            return weakReference.get();
        }
}
