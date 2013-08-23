/**
 * [Copyright]
 * @author oujinliang
 * Aug 23, 2013 4:08:52 PM
 */
package org.jinou.http

import org.junit._

/**
 *
 */
class RouterRuleTest {
    import RouterRule._
    
    @Test
    def testParse() {
        val ruleStrings = Array(
            "/abc/{id}/{name}/",
            "/this/is/a/{<\\d+>id}/{xxxx}"
        )
        
        var rules = ruleStrings map (RouterRule(_)) toList 
        val urls = Array(
            "/abc/1234/oujinliang/",
            "/this/is/a/al;sdfka;s/this_is",
            "/this/is/a/1234/this_is"
        )
        
        urls map { check(rules, _)} foreach (println)
    }
}