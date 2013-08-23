/**
 * [Copyright]
 * @author oujinliang
 * Aug 23, 2013 4:35:36 PM
 */
package org.jinou.http


/**
 *
 */
object HttpSample {

    def main(args: Array[String]): Unit = {
        SimpleHttpServer(8888, getClass).start
    }

    @GET("/")
    def index() = "<html><body>Hello</body></html>"
        
    @GET("/test/{id}")
    def testRest1(@Param("id") id: String) = {
        "This is a test page " + id 
    }
    
    @GET("/test/{id}/{user}")
    def testRest2(@Param("id") id: String, @Param("user") u: String) = {
        "hello " + u + ": " + id
    }
    
}