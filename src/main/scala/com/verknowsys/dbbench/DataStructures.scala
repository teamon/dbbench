package com.verknowsys.dbbench

import org.squeryl._

case class ProcessInfo(
    val pid: Int,
    val cpu: Int,
    val mem: Int,
    val name: String,
    val time: java.sql.Timestamp = new java.sql.Timestamp(new java.util.Date().getTime())
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


//  ~ 1.9.2p136 % psql -c "SELECT pg_database_size('huge_base_1000000')" huge_base_1000000
