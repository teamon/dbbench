package com.verknowsys.dbbench

import org.neodatis.odb._
import java.util.UUID
import org.squeryl.PrimitiveTypeMode._
import Benchmark._

import java.io.File


object App {
    
    def databases = List(
        new NeodatisLocalClient,
        new NeodatisRemoteClient,
        new PostgresClient
    )
    
    def localDatabases = List(
        new NeodatisLocalClient,
        new NeodatisRemoteClient,
        new PostgresClient
    )

    // Intert time
    def benchmark1 {
        val factors = 1 :: 10 :: 100 :: 1000 :: 10000 :: Nil
        val N = 100000
        
        val results = (1 to 3).map { x => 
            factors.map { fact =>
                println("Factor: " + fact)

                val dbs = databases

                val res = dbs.map { db =>
                    val time = measure {
                        (1 to N/fact) foreach { i =>
                            db.saveList((1 to fact).map(new ProcessInfo(_, i, x, "foo")))
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
    
    // Disk space usage
    def benchmark2 {
        val ns = 1000 :: 10000 :: 100000 :: 1000000 :: 10000000 :: Nil
        ns.foreach { n =>
            println("Storing " + n + " objects")
            List(
                new NeodatisLocalClient("huge_base_" + n),
                new PostgresClient("huge_base_" + n)
            ).foreach { db => 
                println(db)
                
                (1 to (n / 1000)) foreach { i =>
                    println("Part " + i)
                    val data = (1 to 1000).map(new ProcessInfo(_, i, 30, "foo"))
                    db.saveList(data)
                }
                
                // Console.readLine("Press enter")
                println()
                db.disconnect
            }
        }
    }
    
    // Disk space usage (PG Only)
    def benchmark2pg {
        var n = 1000000
        
        val db = new PostgresClient("huge_base")
        
        Console.readLine("Press enter")
         var x = 1;
        while(true){
            println("Storing " + n + " objects")
           
            (1 to (n / 1000)) foreach { i =>
                println("Part " + i)
                val data = (1 to 1000).map(new ProcessInfo(_, i, x, "foo"))
                db.saveList(data)
            }
            x += 1
            Console.readLine("Press enter")
            println()
        }
    }
    
    // Query by PID performance
    def benchmark3 {
        println("Query by PID performance")
        val rand = new java.util.Random()
        val pidsAndNames = (1 to 100) map { i => (math.abs(rand.nextInt), new java.math.BigInteger(300, rand).toString(32)) }

        val data = (1 to 100) flatMap { i => pidsAndNames.map(pn => new ProcessInfo(pn._1, i, 30, pn._2)) }
        val dbs = localDatabases

        dbs.foreach { db =>
            db.saveList(data)
            println(db)
        }
        
        (1 to 5) foreach { j => 
            dbs.foreach { db => 
                if(j==5) println(db)
                (1 to 5) foreach { i =>
                    val res = measure{
                        var i = 0
                        while(i < 100){
                            db.queryByPID(pidsAndNames.head._1)
                            i+=1
                        }
                    }
                    if(j==5) println(res)
                }
            }
        }

        dbs.foreach(_.disconnect)
    }
    
    // Query by NAME performance
    def benchmark4 {
        println("Query by NAME performance")
        val rand = new java.util.Random()
        val pidsAndNames = (1 to 100) map { i => (math.abs(rand.nextInt), new java.math.BigInteger(300, rand).toString(32)) }

        val data = (1 to 100) flatMap { i => pidsAndNames.map(pn => new ProcessInfo(pn._1, i, 30, pn._2)) }
        val dbs = localDatabases

        dbs.foreach { db =>
            db.saveList(data)
            println(db)
            db.rebuildIndex
        }
        
        
        
        (1 to 5) foreach { j => 
            dbs.foreach { db => 
                if(j==5) println(db)
                (1 to 5) foreach { i =>
                    val res = measure{
                        var i = 0
                        while(i < 100){
                            db.queryByName(pidsAndNames.head._2)
                            i+=1
                        }
                    }
                    if(j==5) println(res)
                }
            }
        }

        dbs.foreach(_.disconnect)
    }
    
    // Query by TIME performance
    def benchmark5 {
        println("Query by TIME performance")
        val now = new java.util.Date()
        def date(sec: Int) = new java.sql.Timestamp(now.getYear, now.getMonth, now.getDay, now.getHours, now.getMinutes, sec, 0)
        
        val rand = new java.util.Random()
        val pidsAndNames = (1 to 100) map { i => (math.abs(rand.nextInt), new java.math.BigInteger(300, rand).toString(32)) }

        val data = (1 to 100) flatMap { i => pidsAndNames.map(pn => new ProcessInfo(pn._1, i, 30, pn._2, date(i*5))) }
        val dbs = localDatabases

        dbs.foreach { db =>
            db.saveList(data)
            println(db)
            // println("count = " + db.size)
        }
        
        (1 to 5) foreach { j => 
            dbs.foreach { db => 
                if(j==5) println(db)
                (1 to 5) foreach { i =>
                    val res = measure{
                        var i = 0
                        while(i < 100){
                            val res = db.queryByTime(date(5), date(100))
                            // println(res.length)
                            i+=1
                        }
                    }
                    if(j==5) println(res)
                }
            }
        }
        dbs.foreach(_.disconnect)
    }
    
    // Query by NAME and TIME performance
    def benchmark6 {
        println("Query by NAME and TIME performance")
        val now = new java.util.Date()
        def date(sec: Int) = new java.sql.Timestamp(now.getYear, now.getMonth, now.getDay, now.getHours, now.getMinutes, sec, 0)
        
        val rand = new java.util.Random()
        val pidsAndNames = (1 to 100) map { i => (math.abs(rand.nextInt), new java.math.BigInteger(300, rand).toString(32)) }

        val data = (1 to 100) flatMap { i => pidsAndNames.map(pn => new ProcessInfo(pn._1, i, 30, pn._2, date(i*5))) }
        val dbs = localDatabases

        dbs.foreach { db =>
            db.saveList(data)
            println(db)
            // println("count = " + db.size)
        }
        
        (1 to 5) foreach { j => 
            dbs.foreach { db => 
                if(j==5) println(db)
                (1 to 5) foreach { i =>
                    val res = measure{
                        var i = 0
                        while(i < 100){
                            val res = db.queryByNameAndTime(pidsAndNames.head._2, date(5), date(100))
                            // println(res.length)
                            i+=1
                        }
                    }
                    if(j==5) println(res)
                }
            }
        }
        dbs.foreach(_.disconnect)
    }
    
    // Query by TIME and NAME performance
    def benchmark7 {
        println("Query by TIME and NAME performance")
        val now = new java.util.Date()
        def date(sec: Int) = new java.sql.Timestamp(now.getYear, now.getMonth, now.getDay, now.getHours, now.getMinutes, sec, 0)
        
        val rand = new java.util.Random()
        val pidsAndNames = (1 to 100) map { i => (math.abs(rand.nextInt), new java.math.BigInteger(300, rand).toString(32)) }

        val data = (1 to 100) flatMap { i => pidsAndNames.map(pn => new ProcessInfo(pn._1, i, 30, pn._2, date(i*5))) }
        val dbs = localDatabases

        dbs.foreach { db =>
            db.saveList(data)
            println(db)
            // println("count = " + db.size)
        }
        
        (1 to 5) foreach { j => 
            dbs.foreach { db => 
                if(j==5) println(db)
                (1 to 5) foreach { i =>
                    val res = measure{
                        var i = 0
                        while(i < 100){
                            val res = db.queryByTimeAndName(pidsAndNames.head._2, date(5), date(100))
                            // println(res.length)
                            i+=1
                        }
                    }
                    if(j==5) println(res)
                }
            }
        }
        dbs.foreach(_.disconnect)
    }
    
    // Calculate SUM(cpu) & SUM(mem)
    def benchmark8 {
        println("Calculate SUM(cpu) & MEM(mem)")
         
        val rand = new java.util.Random()
        val pidsAndNames = (1 to 100) map { i => (math.abs(rand.nextInt), new java.math.BigInteger(300, rand).toString(32)) }

        val data = (1 to 100) flatMap { i => pidsAndNames.map(pn => new ProcessInfo(pn._1, i, 30, pn._2)) }
        val dbs = localDatabases

        dbs.foreach { db =>
            db.saveList(data)
            println(db)
            // println("count = " + db.size)
        }
        
        (1 to 5) foreach { j => 
            dbs.foreach { db => 
                if(j==5) println(db)
                (1 to 5) foreach { i =>
                    val res = measure{
                        var i = 0
                        while(i < 20){
                            val cpu = db.sumCPU
                            val mem = db.sumMEM
                            // val res = db.queryByTimeAndName(pidsAndNames.head._2, date(5), date(100))
                            // println(res)
                            i+=1
                        }
                    }
                    if(j==5) println(res)
                }
            }
        }
        dbs.foreach(_.disconnect)
    }
    
    // Calculate AVG(cpu) & AVG(mem)
    def benchmark9 {
        println("Calculate AVG(cpu) & AVG(mem)")
         
        val rand = new java.util.Random()
        val pidsAndNames = (1 to 100) map { i => (math.abs(rand.nextInt), new java.math.BigInteger(300, rand).toString(32)) }

        val data = (1 to 100) flatMap { i => pidsAndNames.map(pn => new ProcessInfo(pn._1, i, 30, pn._2)) }
        val dbs = localDatabases

        dbs.foreach { db =>
            db.saveList(data)
            println(db)
            // println("count = " + db.size)
        }
        
        (1 to 5) foreach { j => 
            dbs.foreach { db => 
                if(j==5) println(db)
                (1 to 5) foreach { i =>
                    val res = measure{
                        var i = 0
                        while(i < 20){
                            val cpu = db.avgCPU
                            val mem = db.avgMEM
                            // val res = db.queryByTimeAndName(pidsAndNames.head._2, date(5), date(100))
                            // println(res)
                            i+=1
                        }
                    }
                    if(j==5) println(res)
                }
            }
        }
        dbs.foreach(_.disconnect)
    }


    def main(args: Array[String]): Unit = {
        OdbConfiguration.setLogServerStartupAndShutdown(false)

        benchmark2pg
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