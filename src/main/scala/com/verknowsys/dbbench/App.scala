package com.verknowsys.dbbench

import org.neodatis.odb._
import java.util.UUID
import org.squeryl.PrimitiveTypeMode._
import Benchmark._

import java.io.File


object App {
    final val N = 1000

    
    def databases = List(
        new NeodatisLocalClient,
        new NeodatisRemoteClient,
        new PostgresClient
    )
    
    // (1 to (App.N / n)) foreach { i =>
    //     // println(i)
    //     saveList((1 to n)
    //     
        
    // 1 -> 100000
    // 10 -> 10000
    // 100 -> 1000
    // 1000 -> 100
    // 10000 -> 10
    
    
    
    def benchmark1 {
        val factors = 1 :: 10 :: 100 :: 1000 :: 10000 :: Nil
        val N = 100000
        
        val results = (1 to 3).map { x => 
            factors.map { fact =>
                println("Factor: " + fact)

                val dbs = databases

                def data = (1 to fact).map(new ProcessInfo(_, 20, 30, "foo"))

                val res = dbs.map { db =>
                    val time = measure {
                        (1 to N/fact) foreach { i =>
                            db.saveList(data)
                        }
                    }

                    db.disconnect

                    time
                }

                println(res.mkString("\t"))

                (fact, res)
            }
        }

        println()
        println()
        println(results.map{l => l.map{e => e._1 + "\t" + e._2.mkString("\t")}.mkString("\n") }.mkString("\n\n"))
        
    }
    
    
    
    def main(args: Array[String]): Unit = {
        OdbConfiguration.setLogServerStartupAndShutdown(false)

        benchmark1
    }
    
    def measure(f: => Unit) = {
        val start = System.currentTimeMillis
        f
        System.currentTimeMillis - start
    }
    
    
    
    
    
    // def benchmarkBatchSave(db: Database){
    //     db.saveBatch
    // }
    // 
    // def benchmarkSaveOne(db: Database){
    //     10000 times { 
    //         db.save(new ProcessInfo(10, 20, 30, "foo"))
    //     }
    // }
    // 
    // def benchmarkSaveList(db: Database){
    //     10 times {
    //         db.saveList((1 to 100).map(new ProcessInfo(_, 20, 30, "foo")))
    //     }
    // }
    // 
    // def benchmarkSaveAndCommit(db: Database){
    //     10000 times {
    //         db.saveAndCommit(new ProcessInfo(10, 20, 30, "foo"))
    //     }
    // }
    
    // def main(args: Array[String]): Unit = {
        // println("automaticallyIncreaseCacheSize         = " + OdbConfiguration.automaticallyIncreaseCacheSize())
        // println("automaticCloseFileOnExit               = " + OdbConfiguration.automaticCloseFileOnExit())
        // println("checkModelCompatibility                = " + OdbConfiguration.checkModelCompatibility())
        // println("checkRuntimeVersion                    = " + OdbConfiguration.checkRuntimeVersion())
        // println("displayWarnings                        = " + OdbConfiguration.displayWarnings())
        // println("enableEmptyConstructorCreation         = " + OdbConfiguration.enableEmptyConstructorCreation())
        // println("hasEncoding                            = " + OdbConfiguration.hasEncoding())
        // println("inPlaceUpdate                          = " + OdbConfiguration.inPlaceUpdate())
        // println("isDebugEnabled                         = " + OdbConfiguration.isDebugEnabled())
        // println("isEnableAfterWriteChecking             = " + OdbConfiguration.isEnableAfterWriteChecking())
        // println("isInfoEnabled                          = " + OdbConfiguration.isInfoEnabled())
        // println("isLogAll                               = " + OdbConfiguration.isLogAll())
        // println("isMonitoringMemory                     = " + OdbConfiguration.isMonitoringMemory())
        // println("isMultiThread                          = " + OdbConfiguration.isMultiThread())
        // println("lockObjectsOnSelect                    = " + OdbConfiguration.lockObjectsOnSelect())
        // println("logServerConnections                   = " + OdbConfiguration.logServerConnections())
        // println("logServerStartupAndShutdown            = " + OdbConfiguration.logServerStartupAndShutdown())
        // println("multiThreadExclusive                   = " + OdbConfiguration.multiThreadExclusive())
        // println("reconnectObjectsToSession              = " + OdbConfiguration.reconnectObjectsToSession())
        // println("retryIfFileIsLocked                    = " + OdbConfiguration.retryIfFileIsLocked())
        // println("saveHistory                            = " + OdbConfiguration.saveHistory())
        // println("shareSameVmConnectionMultiThread       = " + OdbConfiguration.shareSameVmConnectionMultiThread())
        // println("throwExceptionWhenInconsistencyFound   = " + OdbConfiguration.throwExceptionWhenInconsistencyFound())
        // println("useCache                               = " + OdbConfiguration.useCache())
        // println("useIndex                               = " + OdbConfiguration.useIndex())
        // println("useLazyCache                           = " + OdbConfiguration.useLazyCache())
        // println("useMultiBuffer                         = " + OdbConfiguration.useMultiBuffer())
        // println("ClassLoader                            = " + OdbConfiguration.getClassLoader())
        // println("CoreProvider                           = " + OdbConfiguration.getCoreProvider())
        // println("DatabaseCharacterEncoding              = " + OdbConfiguration.getDatabaseCharacterEncoding())
        // println("DatabaseStartupManager                 = " + OdbConfiguration.getDatabaseStartupManager())
        // println("DebugLevel                             = " + OdbConfiguration.getDebugLevel())
        // println("DefaultBufferSizeForData               = " + OdbConfiguration.getDefaultBufferSizeForData())
        // println("DefaultBufferSizeForTransaction        = " + OdbConfiguration.getDefaultBufferSizeForTransaction())
        // println("DefaultFileCreationTime                = " + OdbConfiguration.getDefaultFileCreationTime())
        // println("DefaultIndexBTreeDegree                = " + OdbConfiguration.getDefaultIndexBTreeDegree())
        // println("EncryptionPassword                     = " + OdbConfiguration.getEncryptionPassword())
        // println("ID_BLOCK_REPETITION_SIZE               = " + OdbConfiguration.getID_BLOCK_REPETITION_SIZE())
        // println("IdBlockSize                            = " + OdbConfiguration.getIdBlockSize())
        // println("IOClass                                = " + OdbConfiguration.getIOClass())
        // println("MaxNumberOfObjectInCache               = " + OdbConfiguration.getMaxNumberOfObjectInCache())
        // println("MaxNumberOfWriteObjectPerTransaction   = " + OdbConfiguration.getMaxNumberOfWriteObjectPerTransaction())
        // println("MessageStreamerClass                   = " + OdbConfiguration.getMessageStreamerClass())
        // println("NB_IDS_PER_BLOCK                       = " + OdbConfiguration.getNB_IDS_PER_BLOCK())
        // println("NbBuffers                              = " + OdbConfiguration.getNbBuffers())
        // println("NumberOfRetryToOpenFile                = " + OdbConfiguration.getNumberOfRetryToOpenFile())
        // println("QueryExecutorCallback                  = " + OdbConfiguration.getQueryExecutorCallback())
        // println("RetryTimeout                           = " + OdbConfiguration.getRetryTimeout())
        // println("RoundTypeForAverageDivision            = " + OdbConfiguration.getRoundTypeForAverageDivision())
        // println("ScaleForAverageDivision                = " + OdbConfiguration.getScaleForAverageDivision())
        // println("StringSpaceReserveFactor               = " + OdbConfiguration.getStringSpaceReserveFactor())
        // println("TimeoutToAcquireMutexInMultiThread     = " + OdbConfiguration.getTimeoutToAcquireMutexInMultiThread())
        
        // NeodatisConfig()
        
        // Console.readLine()
        
        // val benchmarks = 
        // (
        //     Map(
        //         "noop"           , () => new NoopClient),
        //         "neodatis local" , () => new NeodatisLocalClient),
        //         "neodatis remote", () => new NeodatisRemoteClient),
        //         "postgres"       , () => new PostgresClient)
        //     ),
        //     Map(
        //         "save_one" -> benchmarkSaveOne _
        //         // "save_list" -> benchmarkSaveList _,
        //         // "save_and_commit" -> benchmarkSaveAndCommit _
        //         // "save_batch" -> benchmarkBatchSave _
        //     )
        // ) :: 
        // (
        //     (
                // ("noop 1x"     , () => new NoopBatchClient(1)) ::
                // ("noop 10x"    , () => new NoopBatchClient(10)) ::
                // ("noop 100x"   , () => new NoopBatchClient(100)) ::
                // ("noop 1000x"  , () => new NoopBatchClient(1000)) ::
                // ("noop 10000x" , () => new NoopBatchClient(10000)) ::
                // ("1x"   , () => new PostgresBatchClient(1)) ::
        //         ("postgres 1x"    , () => new PostgresBatchClient(1)) ::
        //         ("postgres 10x"   , () => new PostgresBatchClient(10)) ::
        //         ("postgres 100x"  , () => new PostgresBatchClient(100)) ::
        //         ("postgres 1000x" , () => new PostgresBatchClient(1000)) ::
        //         ("postgres 10000x", () => new PostgresBatchClient(10000)) ::
        //         
        //         ("neodatis local 1x"    , () => new NeodatisLocalBatchClient(1)) ::
        //         ("neodatis local 10x"   , () => new NeodatisLocalBatchClient(10)) ::
        //         ("neodatis local 100x"  , () => new NeodatisLocalBatchClient(100)) ::
        //         ("neodatis local 1000x" , () => new NeodatisLocalBatchClient(1000)) ::
        //         ("neodatis local 10000x", () => new NeodatisLocalBatchClient(10000)) ::
        //         
        //         ("neodatis remote 1x"    , () => new NeodatisRemoteBatchClient(1)) ::
        //         ("neodatis remote 10x"   , () => new NeodatisRemoteBatchClient(10)) ::
        //         ("neodatis remote 100x"  , () => new NeodatisRemoteBatchClient(100)) ::
        //         ("neodatis remote 1000x" , () => new NeodatisRemoteBatchClient(1000)) ::
        //         ("neodatis remote 10000x", () => new NeodatisRemoteBatchClient(10000)) ::
        //         // ("10000x", () => new PostgresBatchClient(10000)) ::
        //         // ("100000x", () => new PostgresBatchClient(100000)) ::
        //         Nil
        //     ),
        //     (
        //         ("save_batch", benchmarkBatchSave _) :: 
        //         Nil
        //     )
        // ) :: 
        // Nil
        
    //     benchmarks foreach { case(databases, benchs) =>
    //         saveResults(
    //             benchs.map { case(name, bench) => 
    //                 println()
    //                 println()
    //                 println("[binfo] *** Benchmarking " + bench + " ***")
    // 
    //                 (name, benchmark(3){
    //                     databases.map { case(name, dbf) =>
    //                         val db = dbf()
    //                         val res = report(name){
    //                             bench(db)
    //                         }
    //                         db.disconnect
    //                         res
    //                     }
    //                 })
    //             }
    //         )
    //     }
    // }
    
    // def saveResults(res: List[(String, Iterable[(String, Iterable[Long], Long)])]){
    //     res.foreach { case(benchName, results) =>
    //         val graphfile = "target/bench_" + benchName + ".g"
    //         val pdffile = "target/bench_" + benchName + ".pdf"
    //         
    //         printToFile(graphfile){ b =>
    //             b.println("set terminal postscript enhanced color")
    //             b.println("set output \"| pstopdf -i -o " + pdffile + "\"")
    //             b.println("set xlabel \"No.\"")
    //             b.println("set ylabel \"Time (s)\"")
    //             b.print("plot ")
    //             
    //             b.println(results.map { case(name, times, med) => 
    //                 val filename = "target/" + benchName.replaceAll(" ", "_") + "_" + name + ".dat"
    //                 printToFile(filename){ p =>
    //                     times.foreach(p.println)
    //                 }
    //                 "'" + filename + "' title '" + name + "' with lines"
    //             }.mkString(","))
    //         }
    //         
    //         
    //         System.err.println("gnuplot -p " + graphfile)
    //         System.err.println("open " + pdffile)
    //     }
    // }
    // 
    // def printToFile(path: String)(f: java.io.PrintWriter => Unit) {
    //     val p = new java.io.PrintWriter(new java.io.File(path))
    //     try { f(p) } finally { p.close() }
    // }
}