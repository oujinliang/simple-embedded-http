/**
 * [Copyright] 
 * @author oujinliang
 * Aug 27, 2013 4:20:09 PM
 */
package com.xiaomi.http;

import java.io.IOException;

/**
 *
 */
public class SimpleHttpServerTest {
    public static void main(String[] args) throws IOException {
        new SimpleHttpServer(8888, SimpleHttpServerTest.class).start();
    }

    @GET("/hello/{name}")
    public static String hello(@Param("name") String name) {
        return "Hello " + name;
    }
    
    @GET("/get/{<\\d+>id}/{any}")
    public static String getId(@Param("id") long id, @Param("any") Object any) {
        return String.format("get id %d for any %s", id, any);
    }
    
    @GET("/")
    public static String index() {
        return "<html><head></head><body>" +
                "<a href='/hello/abc'>abc</a><br/>" +
                "<a href='get/1/1'>get id </a><br/>" +
                "</body></html>";
    }
}
