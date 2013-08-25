/**
 * [Copyright] 
 * @author oujinliang
 * Aug 24, 2013 10:29:05 AM
 */
package org.jinou.http;

import scala.actors.threadpool.Arrays;
import scala.collection.JavaConversions;

/**
 *
 */
public class SimpleHttpServerJava {
    public static void start(int port, Class<?>... classes) {
       new SimpleHttpServer(port, JavaConversions.asScalaBuffer(Arrays.asList(classes))).start();
    }
}
