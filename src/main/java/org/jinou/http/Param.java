/**
 * [Copyright] 
 * @author oujinliang
 * Aug 23, 2013 4:11:46 PM
 */
package org.jinou.http;

import java.lang.annotation.*;
/**
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    /**
     * The Url pattern
     * @return
     */
    String value();
}
