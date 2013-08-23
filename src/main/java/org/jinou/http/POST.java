/**
 * [Copyright] 
 * @author oujinliang
 * Aug 23, 2013 4:13:38 PM
 */
package org.jinou.http;

import java.lang.annotation.*;

/**
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface POST {
    /**
     * The Url pattern
     * @return
     */
    String value();
}
