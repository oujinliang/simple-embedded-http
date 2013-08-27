/**
 * [Copyright] 
 * @author oujinliang
 * Aug 27, 2013 1:26:52 PM
 */
package com.xiaomi.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class RouterRule {
    private static final Pattern DYNAMIC = Pattern.compile("^\\{(<.+>)?(.+)\\}$");
    
    private List<PathPart> parts;
    
    public RouterRule(String rule) {
        List<PathPart> pathParts = new ArrayList<PathPart>();
        for(String part: rule.split("/")) {
            Matcher m = DYNAMIC.matcher(part);
            if (m.find()) {
                String constraint = m.group(1);
                String regex = constraint == null ? "(.+)" :  constraint.replace('<','(').replace('>',')'); 
                pathParts.add(new DynamicPart(m.group(2), Pattern.compile(regex)));
            } else {
                pathParts.add(new StaticPart(part));
            }
        }
        this.parts = pathParts;
    }
    
    public Map<String, String> check(String url) {
        String[] urlParts = url.split("/");
        if (urlParts.length != this.parts.size()) {
            return null;
        }
        Map<String, String> result = new HashMap<String, String>();
        for(int i = 0; i < urlParts.length; ++i) {
            PathPart pathPart = this.parts.get(i);
            String urlPart = urlParts[i];
            
            if (pathPart instanceof StaticPart) {
                if (!((StaticPart)pathPart).name.equals(urlPart)) {
                    return null;
                }
            } else if (pathPart instanceof DynamicPart) {
                DynamicPart dyPart = (DynamicPart) pathPart;
                Matcher m = dyPart.regex.matcher(urlPart);
                if (m.find()) {
                    result.put(dyPart.name, m.group(1));
                } else {
                    return null;
                }
            }
        }
        
        return result;
    }
    
    static interface PathPart { /* */ }

    static class StaticPart implements PathPart {
        final String name;
        
        StaticPart(String name) {
            this.name = name;
        }
    }
    
    static class DynamicPart implements PathPart {
        final String name;
        final Pattern regex;

        public DynamicPart(String name, Pattern regex) {
            this.name = name;
            this.regex = regex;
        }
    }
}
