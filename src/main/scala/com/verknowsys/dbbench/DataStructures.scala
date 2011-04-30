package com.verknowsys.dbbench

import org.squeryl._

case class ProcessInfo(
    val pid: Int,
    val cpu: Int,
    val mem: Int,
    val s: String
){
    def this() = this(0, 0, 0, "") // required by squeryl
}

// case class Setting(
//     val a: Int, 
//     val b: Option[String], 
//     val c: List[String],
//     val d: Map[String, Int]
// ){
//     def this() = this(0, Some(""), Nil, Map())
// }
// 
// // case class Settings(
// //     val uuid: UUID,
// //     val path: String
// // )
// 
// 
// // squeryl schema


