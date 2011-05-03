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
    
    def benchmarkSaveOne(db: Database){
        100 times { 
            db.save(new ProcessInfo(10, 20, 30, "foo"))
        }
    }
    
    def benchmarkSaveList(db: Database){
        1 times {
            db.saveList((1 to 100).map(new ProcessInfo(_, 20, 30, "foo")))
        }
    }
    
    def benchmarkSaveAndCommit(db: Database){
        100 times {
            db.saveAndCommit(new ProcessInfo(10, 20, 30, "foo"))
        }
    }
    
    def main(args: Array[String]): Unit = {
        val benchmarks = Map(
            "save_one" -> benchmarkSaveOne _,
            "save_list" -> benchmarkSaveList _,
            "save_and_commit" -> benchmarkSaveAndCommit _
        )
        
        saveResults(benchmarks.mapValues { bench => 
            println()
            println()
            println("[binfo] *** Benchmarking " + bench + " ***")
            
            (1 to 10) map { i =>
                println()
                println("[binfo] *** Take " + i + " of 10 ***")
                val dbs = databases
                
                val res = benchmark(100){
                    dbs.toList.map { case(name, db) =>
                        report(name){ bench(db) }
                    }
                }
                
                dbs.foreach(_._2.disconnect)
                res
            }
        })
    }
    
    def saveResults(res: Map[String, Seq[List[(String, Long)]]]){
        res.foreach { case(benchName, results) =>
            val graphfile = "target/bench_" + benchName + ".g"
            val pdffile = "target/bench_" + benchName + ".pdf"
            printToFile(graphfile){ b =>
                b.println("set terminal postscript enhanced color")
                b.println("set output \"| pstopdf -i -o " + pdffile + "\"")
                b.println("set xlabel \"No.\"")
                b.println("set ylabel \"Time (s)\"")
                b.print("plot ")
                
                b.println(results.map(_.toMap).foldLeft(Map[String, List[Long]]()) { case(s,e) => 
                    e.foldLeft(Map[String, List[Long]]()){ case(xs,x) => 
                        xs + ((x._1, x._2 :: s.get(x._1).getOrElse(Nil))) 
                    } 
                }.mapValues(_.reverse).toList.map { case(name, values) => 
                    val filename = "target/" + benchName.replaceAll(" ", "_") + "_" + name + ".dat"
                    printToFile(filename){ p =>
                        values.foreach(p.println)
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