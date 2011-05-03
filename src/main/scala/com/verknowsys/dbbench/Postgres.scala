package com.verknowsys.dbbench

import org.squeryl._
import org.squeryl.adapters._
import org.squeryl.PrimitiveTypeMode._

class PostgresBatchClient(i: Int) extends PostgresClient {
    override def saveBatch {
        (1 to (10000 / i)) foreach { i =>
            saveList((1 to i).map(new ProcessInfo(_, 20, 30, "foo")))
        }
    }
}

class PostgresClient extends Database {
    SessionFactory.concreteFactory = Some(() => {
        val conn = java.sql.DriverManager.getConnection("jdbc:postgresql:base1", "teamon", "")
        conn.setAutoCommit(false);
        Session.create(conn, new PostgreSqlAdapter)
    })
    
    
    // val conn = java.sql.DriverManager.getConnection("jdbc:postgresql:base1", "teamon", "")
    // conn.setAutoCommit(false);
    val session = SessionFactory.concreteFactory.get()
    session.bindToCurrentThread
    
    RDBMS.close     // drop tables
    RDBMS.create    // create tables
    
    
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
        list match {
            case x: Seq[ProcessInfo] =>
                RDBMS.processes.insert(x)
        }
    }
    
    def saveAndCommit(obj: Any) {
        obj match {
            case x: ProcessInfo => 
                inTransaction { 
                    RDBMS.processes.insert(x) 
                }
        }
    }
}

object RDBMS extends Schema {
    val processes = table[ProcessInfo]
    
    // on(processes)(p => declare(
    //     p.pid is(indexed)
    // ))
    
    def close = drop
}