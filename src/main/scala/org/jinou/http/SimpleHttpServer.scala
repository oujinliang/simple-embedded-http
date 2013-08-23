/**
 * [Copyright]
 * @author oujinliang
 * Aug 23, 2013 4:16:46 PM
 */
package org.jinou.http

import com.sun.net.httpserver._
import java.io._
import java.net.InetSocketAddress
import java.lang.reflect.Method

/**
 *
 */
case class SimpleHttpServer(port: Int, classes: Class[_]*) {
    require(port > 0, "port should be greater than 0")
    require(classes.length > 0, "No handler class specified")
    var started = false
    def start() {
        if (started) {
            println("started")
            return
        }
        val server = HttpServer.create(new InetSocketAddress(port), 0)
        server.createContext("/", new MySimpleHandler(classes.toArray))
        server.setExecutor(null) 
        server.start()
        started = true
    }
}

case class HttpMethodPair(method: Method, params: Seq[(String, Class[_])], httpType: String, rule: RouterRule) extends RouterRuleProvider

object Converters {
    val intConverter = (str: String) => Int.box(str.toInt)
    val longConverter = (str: String) => Long.box(str.toLong)
    val floatConverter = (str: String) => Float.box(str.toFloat)
    val doubleConverter = (str: String) => Double.box(str.toDouble)
    val booleanConverter = (str: String) => Boolean.box(str.toBoolean)
    val defaultConverter = (str: String) => str
    
    val converterMap: Map[Class[_], String => AnyRef] = Map(
        classOf[Int] -> intConverter, classOf[Integer] -> intConverter,
        classOf[Long] -> longConverter, classOf[java.lang.Long] -> longConverter,
        classOf[Float] -> floatConverter, classOf[java.lang.Float] -> floatConverter,
        classOf[Double] -> doubleConverter, classOf[java.lang.Double] -> doubleConverter,
        classOf[Boolean] -> booleanConverter, classOf[java.lang.Boolean] -> booleanConverter
    )
    def convert(cls: Class[_], str: String) = converterMap.getOrElse(cls, defaultConverter)(str) 
}
private class MySimpleHandler(classes: Array[Class[_]]) extends HttpHandler {
    import RouterRule._
    
    private val expectedMethods = Array(classOf[GET], classOf[POST])
    private val httpMethodRules = classes map (getHttpHandlerMethods) flatten

    
    @throws(classOf[IOException])
    override def handle(t: HttpExchange) {
        val (code, response) = 
            try { 
                call(t) 
            } catch { 
                case e: Exception => (404, e.toString + "\n" + e.getStackTraceString) 
            }
        val responseStr = if (response == null) "" else response.toString
        t.sendResponseHeaders(code, responseStr.length());
        val os = t.getResponseBody();
        os.write(responseStr.getBytes());
        os.close();
    }
    
    private def call(t: HttpExchange): (Int, Any) = {
       val uri = t.getRequestURI() 
       check(httpMethodRules, uri.toString) map { case (rule, map) =>
           val pair = rule.asInstanceOf[HttpMethodPair]
           val args = (pair.params map { case (name, cls) => Converters.convert(cls, map(name)) }) toArray 

           // TODO: currently only static method is supported
           val method = pair.method
           val field = method.getDeclaringClass().getField("MODULE$");
           val instance = field.get(null);
           val result = pair.method.invoke(instance, args)

           (200, if (result == null) "null" else result.toString) 
       } getOrElse (200, "Bad request")
    }

    private def getHttpHandlerMethods(clz: Class[_]) = {
        (clz.getDeclaredMethods() map { m =>
            val annotations = m.getAnnotations.filter(a => expectedMethods.contains(a.annotationType()))
            if (annotations.isEmpty) None else Some(m, getParams(m), annotations(0)) 
        }).flatten map { case (method, params, httpMethod) =>
            httpMethod match {
                case get: GET   => HttpMethodPair(method, params, "get", RouterRule(get.value()))
                case post: POST => HttpMethodPair(method, params,"post", RouterRule(post.value()))
                case e          => throw new IllegalStateException("unknown method " + e)            
            }  
        }
    }
    
    private def getParams(method: Method) = {
        method.getParameterAnnotations().zip(method.getParameterTypes()) map { case (annotations, paramType) =>
            val params = annotations.filter(a => a.annotationType() == classOf[Param])
            require(params.length > 0, "missing param annotation in method " + method)
            (params(0).asInstanceOf[Param].value(), paramType)
        }
    }
}
