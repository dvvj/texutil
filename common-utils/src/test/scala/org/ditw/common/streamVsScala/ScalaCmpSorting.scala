package org.ditw.common.streamVsScala

object ScalaCmpSorting extends App {

  import collection.JavaConverters._
  val empList = Employee.createTestList().asScala

  println("---- By Employee Number")
  println(empList.sortBy(_.empNo).mkString("\n"))

  println("---- By Employee FirstName")
  println(empList.sortBy(_.firstName).mkString("\n"))

  println("---- By Employee FirstName (case-insensitive)")
  println(empList.sortBy(_.firstName.toLowerCase()).mkString("\n"))

}
