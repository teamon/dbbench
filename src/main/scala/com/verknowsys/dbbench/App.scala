package com.verknowsys.dbbench

import org.neodatis.odb._
import java.util.UUID

object dbbench {
    final val ODB_NAME = "test.neodatis"
    
    lazy val odb = ODBFactory.open(ODB_NAME)
    
    def run {
        // val x = new ProcessInfo(10, 20, 30, "test")
        // odb.store(x)
        // odb.store(x)
        
        val s1 = Settings(UUID.randomUUID, "foo")
        odb.store(s1)
        val s2 = s1.copy(conf = "bar")
        odb.store(s2)
    }
    
    
    def main(args: Array[String]): Unit = {
        try {
            run
        } finally {
            odb.close
        }
    }
}