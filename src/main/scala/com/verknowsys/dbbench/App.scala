package com.verknowsys.dbbench

import org.neodatis.odb._
import java.util.UUID
import org.squeryl.PrimitiveTypeMode._
import Benchmark._

import java.io.File


object App {
    val databases = Map(
        "noop"            -> (() => new NoopClient),
        "neodatis local"  -> (() => new NeodatisLocalClient),
        "neodatis remote" -> (() => new NeodatisRemoteClient),
        "postgres"        -> (() => new PostgresClient)
    )
    
    // def databases = Map(
    //     "1x" -> (() => new PostgresBatchClient(1))
    //     // "10x" -> new PostgresBatchClient(10)
    //     // "100x" -> new PostgresBatchClient(100),
    //     // "1000x" -> new PostgresBatchClient(1000),
    //     // "10000x" -> new PostgresBatchClient(10000)
    //     // "100000x" -> new PostgresBatchClient(100000)
    // )
    
    
    // def benchmarkBatchSave(db: Database){
    //     db.saveBatch
    // }
    // 
    def benchmarkSaveOne(db: Database){
        10000 times { 
            db.save(new ProcessInfo(10, 20, 30, "foo"))
        }
    }
    
    def benchmarkSaveList(db: Database){
        10 times {
            db.saveList((1 to 100).map(new ProcessInfo(_, 20, 30, "foo")))
        }
    }
    
    def benchmarkSaveAndCommit(db: Database){
        10000 times {
            db.saveAndCommit(new ProcessInfo(10, 20, 30, "foo"))
        }
    }
    
    def main(args: Array[String]): Unit = {
        val benchmarks = Map(
            "save_one" -> benchmarkSaveOne _,
            "save_list" -> benchmarkSaveList _,
            "save_and_commit" -> benchmarkSaveAndCommit _
            // "save_batch" -> benchmarkBatchSave _
        )
        
        saveResults(
            benchmarks.mapValues { bench => 
                println()
                println()
                println("[binfo] *** Benchmarking " + bench + " ***")
            
                benchmark(10){
                    databases.toList.map { case(name, dbf) =>
                        val db = dbf()
                        val res = report(name){
                            bench(db)
                        }
                        db.disconnect
                        res
                    }
                }
            }
        )
    }
    
    def saveResults(res: Map[String, Iterable[(String, Iterable[Long], Long)]]){
        res.foreach { case(benchName, results) =>
            val graphfile = "target/bench_" + benchName + ".g"
            val pdffile = "target/bench_" + benchName + ".pdf"
            printToFile(graphfile){ b =>
                b.println("set terminal postscript enhanced color")
                b.println("set output \"| pstopdf -i -o " + pdffile + "\"")
                b.println("set xlabel \"No.\"")
                b.println("set ylabel \"Time (s)\"")
                b.print("plot ")
                
                b.println(results.map { case(name, times, med) => 
                    val filename = "target/" + benchName.replaceAll(" ", "_") + "_" + name + ".dat"
                    printToFile(filename){ p =>
                        times.foreach(p.println)
                    }
                    "'" + filename + "' title '" + name + "' with lines"
                }.mkString(","))
            }
            
            System.err.println("gnuplot -p " + graphfile)
            System.err.println("open " + pdffile)
        }
    }
    
    def printToFile(path: String)(f: java.io.PrintWriter => Unit) {
        val p = new java.io.PrintWriter(new java.io.File(path))
        try { f(p) } finally { p.close() }
    }
}