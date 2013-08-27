/**
 * [Copyright] 
 * @author oujinliang
 * Aug 27, 2013 1:27:44 PM
 */
package com.xiaomi.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 */
@SuppressWarnings("restriction")
public class SimpleHttpServer {
    private int port;
    private boolean started = false;
    private Class<?>[] handlerClasses;

    public SimpleHttpServer(int port, Class<?>... classes) {
        this.port = port;
        this.handlerClasses = classes;
    }
    
    public void start() throws IOException {
        if (started) {
            return;
        }
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new MySimpleHandler(handlerClasses));
        server.setExecutor(null);
        server.start();
        started = true;
    }
    
    
    static class MySimpleHandler implements HttpHandler {
        private static List<Class<?>> ExpectedMethods = Arrays.asList(new Class<?>[] {GET.class, POST.class});
        private List<HttpMethodArgs> httpMethodArgs = new ArrayList<HttpMethodArgs>();
        
        MySimpleHandler(Class<?>[] classes) {
            for(Class<?> clz : classes) {
                httpMethodArgs.addAll(getHttpMethodArgs(clz));
            }
        }
        
        @Override
        public void handle(HttpExchange he) throws IOException {
            int code = 200;
            String response = null;
            try {
                response = call(he);
            } catch (Exception e) {
                code = 404;
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                response = errors.toString();
            }
            
            he.sendResponseHeaders(code, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        
        private String call(HttpExchange e) throws Exception {
            String url = e.getRequestURI().toString();
            for (HttpMethodArgs methodArgs : httpMethodArgs) {
                Map<String, String> map = methodArgs.rule.check(url);
                if (map == null) {
                    continue;
                }
                
                Object[] args = getArgs(map, methodArgs.params);
                // TODO: currently only static method is supported
                Object result = methodArgs.method.invoke(null, args);
                return result == null ? "null" : result.toString();
            }
            
            throw new IllegalArgumentException("Bad request");
        }

        static Object[] getArgs(Map<String, String> map, List<MethodParam> params) {
            List<Object> result = new ArrayList<Object>(params.size());
            for (MethodParam param : params) {
                result.add(Converters.convert(param.clz, map.get(param.name)));
            }
            return result.toArray();
        }
        
        static List<HttpMethodArgs> getHttpMethodArgs(Class<?> clz) {
            List<HttpMethodArgs> result = new ArrayList<HttpMethodArgs>();
            for(Method m : clz.getDeclaredMethods()) {
                Annotation a = getHttpMethodAnnotation(m);
                if (a == null) {
                    continue;
                }
                
                List<MethodParam> params = getParam(m);
                if (a instanceof GET) {
                    result.add(new HttpMethodArgs(m, params, "get", new RouterRule(((GET)a).value())));
                } else if (a instanceof POST) {
                    result.add(new HttpMethodArgs(m, params, "post", new RouterRule(((POST)a).value())));
                } else {
                    throw new IllegalArgumentException("unknown method");
                }
            }
            
            return result;
        }
            
        static Annotation getHttpMethodAnnotation(Method m) {
            for (Annotation a : m.getAnnotations()) {
                if (ExpectedMethods.contains(a.annotationType())) {
                    return a;
                }
            }
            return null;
        }
        
        static List<MethodParam> getParam(Method m) {
            List<MethodParam> result = new ArrayList<MethodParam>();
            Annotation[][] paramAnnotations = m.getParameterAnnotations();
            Class<?>[] paramTypes = m.getParameterTypes();
            for (int i = 0; i < paramTypes.length; ++i) {
                Param param = null;
                for(Annotation a : paramAnnotations[i]) {
                    if (a.annotationType().equals(Param.class)) {
                        param = (Param) a;
                        break;
                    }
                }
                if (param == null) {
                    throw new IllegalArgumentException("minssing param annotation in method " + m);
                }
                
                result.add(new MethodParam(param.value(), paramTypes[i]));
            }
            
            return result;
        }
    }
    
    static class HttpMethodArgs {
        final Method method;
        final List<MethodParam> params;
        final String httpType;
        final RouterRule rule;
        
        HttpMethodArgs(Method method, List<MethodParam> params, String httpType, RouterRule rule) {
            this.method = method;
            this.params = params;
            this.httpType = httpType;
            this.rule = rule;
        }
    }
    
    static class MethodParam {
        final String name;
        final Class<?> clz;
        MethodParam(String name, Class<?> clz) {
            this.name = name;
            this.clz = clz;
        }
    }
}
