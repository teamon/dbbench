package com.verknowsys.dbbench

import org.neodatis.odb._
import java.util.UUID
import org.squeryl.PrimitiveTypeMode._
import Benchmark._

import java.io.File


object App {
    def databases = Map(
        "noop"            -> new NoopClient,
        "neodatis local"  -> new NeodatisLocalClient,
        "neodatis remote" -> new NeodatisRemoteClient,
        "postgres"        -> new PostgresClient
    )
    
    def benchmarkSave(dbs: Map[String, Database]) {
        benchmark(50000){
            dbs.toList.map { case(name, db) =>
                report(name){
                    db.save(new ProcessInfo(10, 20, 30, "foo"))
                }
            }
        }
    }
    
    // def run {
    //     val db = new NeodatisDB
    //     
    //     (1 to 7) foreach { p =>
    //         print("10^" + p + ": ")
    //         
    //         val odb = db.odb(p)
    //         (1 to Math.pow(10, p).toInt) foreach { i =>
    //             odb.store(new ProcessInfo(i, 20, 30, "dupa"))
    //         }
    //         
    //         val file = new File("/tmp/base" + p + ".neodatis")
    //         println(file.length)
    //     }
    // }
    // 
    

    
    def main(args: Array[String]): Unit = {
        val benchmarks = List(
            benchmarkSave _
        )
        
        benchmarks.foreach { bench => 
            (1 to 10) foreach { i =>
                val dbs = databases
                bench(dbs)
                dbs.foreach(_._2.disconnect)
            }
        }
    }
}