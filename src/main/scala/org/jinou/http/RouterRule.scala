/**
 * [Copyright]
 * @author oujinliang
 * Aug 23, 2013 2:02:49 PM
 */
package org.jinou.http

import scala.util.matching._

trait RouterRuleProvider {
    def rule: RouterRule
}
object RouterRule {
    val dynamic = """^\{(<.+>)?(.+)\}$""".r
   
    def check(rules: Seq[RouterRuleProvider], url: String): Option[(RouterRuleProvider, Map[String, String])] = {
        for(rule <- rules) {
            val checkResult = rule.rule.checkUrl(url)
            if (checkResult != None) {
                return Some(rule, checkResult.get)
            }
        }
        None
    }
    
    implicit def convert(rules: Seq[RouterRule]) = rules map {r => new RouterRuleProvider { def rule = r}}
}

trait PathPart 
case class StaticPart(name: String) extends PathPart
case class DynamicPart(name: String, regex: Regex) extends PathPart

case class RouterRule(val rule: String) {
    import RouterRule._
    require(rule != null, "url path cannot be null")

    private val parts = rule.split("/") map {
        case dynamic(regex, name) => 
            DynamicPart(name, if (regex == null) "(.+)".r else regex.replace('<','(').replace('>',')')r)
        case name => 
            StaticPart(name)
    } 
    
    def checkUrl(url: String): Option[Map[String, String]] = {
        require(url != null, "url cannot be null") 
        val urlParts = url.split("/")
        val tmpMap = collection.mutable.HashMap[String, String]()
        val passed = urlParts.corresponds(parts) { case (u, p) =>
           p match {
               case StaticPart(u) => true
               case DynamicPart(name, reg) => u match {
                   case reg(value) => tmpMap += name -> value; true
                   case _ => false
               }
               case _ => false
           } 
        }
        
        if (passed) Some(tmpMap.toMap) else None
    }
}
