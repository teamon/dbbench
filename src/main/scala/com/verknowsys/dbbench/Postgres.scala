package com.verknowsys.dbbench

import org.squeryl._
import org.squeryl.adapters._
import org.squeryl.PrimitiveTypeMode._

class PostgresClient(name: String = "base1") extends Database {
    var conn: java.sql.Connection = null
    SessionFactory.concreteFactory = Some(() => {
        conn = java.sql.DriverManager.getConnection("jdbc:postgresql:" + name, "teamon", "")
        conn.setAutoCommit(false);
        Session.create(conn, new PostgreSqlAdapter)
    })
    
    
    // val conn = java.sql.DriverManager.getConnection("jdbc:postgresql:base1", "teamon", "")
    // conn.setAutoCommit(false);
    val session = SessionFactory.concreteFactory.get()
    session.bindToCurrentThread
    
    RDBMS.close     // drop tables
    RDBMS.create    // create tables
    
    conn.commit()
    
    import RDBMS._
    
    def disconnect {
        session.close
    }
    
    def save(obj: Any) {
        obj match {
            case x: ProcessInfo => 
                RDBMS.processes.insert(x)
        }
    }
    
    def saveList(list: Seq[Any]){
        // inTransaction {
            list match {
                case x: Seq[ProcessInfo] =>
                    RDBMS.processes.insert(x)
                    conn.commit()
            }
        // }
    }
    
    def saveAndCommit(obj: Any) {
        obj match {
            case x: ProcessInfo => 
                RDBMS.processes.insert(x) 
        }
    }
    
    def size = from(processes)(s => compute(count())).toInt
    
    def queryByPID(pid: Int) = {
        processes.where(_.pid === pid).toList
    }
    
    def queryByName(name: String) = {
        processes.where(_.name === name).toList
    }
    
     def queryByTime(from: java.sql.Timestamp, to: java.sql.Timestamp) = {
         processes.where(_.time between(from,to)).toList
     }
     
     def queryByNameAndTime(name: String, from: java.sql.Timestamp, to: java.sql.Timestamp) = {
         processes.where(e => (e.name === name) and (e.time between(from,to))).toList
     }
     
     def queryByTimeAndName(name: String, from: java.sql.Timestamp, to: java.sql.Timestamp) = {
         processes.where(e => (e.time between(from,to)) and (e.name === name)).toList
     }
     
     def sumCPU = from(processes)((s) => compute(sum(s.cpu))).get

     def sumMEM = from(processes)((s) => compute(sum(s.mem))).get

     def avgCPU = from(processes)((s) => compute(avg(s.cpu))).get

     def avgMEM = from(processes)((s) => compute(avg(s.mem))).get
     
}

object RDBMS extends Schema {
    val processes = table[ProcessInfo]
    
    on(processes)(p => declare(
        p.name is(indexed)
    ))
    
    def close = drop
}