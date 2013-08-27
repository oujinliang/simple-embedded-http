/**
 * [Copyright] 
 * @author oujinliang
 * Aug 24, 2013 10:26:39 AM
 */
package org.jinou.http;

/**
 *
 */
public class HttpJavaSample {
    public static void main(String[] args) {
        SimpleHttpServerJava.start(8888, HttpJavaSample.class);
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
