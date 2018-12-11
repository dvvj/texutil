package org.ditw.exutil1.poco
import org.ditw.matcher.TCompMatcher

trait TPoco extends Serializable {
  def genMatcher: TCompMatcher
  def check(poco:String):Boolean
}
