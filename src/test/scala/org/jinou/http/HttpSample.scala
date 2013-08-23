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

    @GET("/test")
    def testRest1() {
        "This is a test page" 
    }
}