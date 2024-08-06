package com.gzhu.dic_platform.common.utils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class BeanCopyUtils {

    private BeanCopyUtils() {
    }

    /**
     * 复制单个对象的属性
     * @param source 源对象
     * @param clazz 目标类的 Class 对象
     * @param <V> 目标对象的类型
     * @return 目标对象
     */
    public static <V> V copyBean(Object source,Class<V> clazz) {
        //创建目标对象
        V result = null;
        try {
            result = clazz.newInstance();
            //实现属性copy
            BeanUtils.copyProperties(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回结果
        return result;
    }

    /**
     * 复制对象列表的属性
     * @param list 源对象列表
     * @param clazz 目标类的 Class 对象
     * @param <O> 源对象的类型
     * @param <V> 目标对象的类型
     * @return 目标对象列表
     */
    public static <O,V> List<V> copyBeanList(List<O> list,Class<V> clazz){
        return list.stream()
                .map(o -> copyBean(o, clazz))
                .collect(Collectors.toList());
    }
}
