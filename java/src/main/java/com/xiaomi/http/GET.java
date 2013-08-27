/**
 * [Copyright] 
 * @author oujinliang
 * Aug 23, 2013 4:11:46 PM
 */
package com.xiaomi.http;

import java.lang.annotation.*;
/**
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GET {
    /**
     * The Url pattern
     * @return
     */
    String value();
}
