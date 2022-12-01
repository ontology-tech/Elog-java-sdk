package io.ont.elog.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: Eric.xu
 * @Description:
 * @ClassName: Topic
 * @Date: 2022/11/4 下午2:46
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Topic {
    String chain();
    String address();
}