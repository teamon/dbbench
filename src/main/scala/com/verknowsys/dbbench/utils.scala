package com.verknowsys.dbbench

object Benchmark {
    def benchmark(n: Int)(f: => List[(String, Long)]) = {
        val results = (1 to n).map { i =>
            println("[binfo] Take " + i)
            f
        }.flatten.groupBy(_._1).mapValues { e => e.map(_._2) }
        
        println("Name                   Times")
        println("=" * (n * 11 + 33))
        
        results.toList.sortBy(_._1).map { case(name, times) =>
            printf("%-20s", name)
            times foreach { i => printf("\t %10f", i / 1000.0) }
            val m = median(times);
            printf(" |\t %10f", m / 1000.0) 
            println()
            (name, times, m)
        }
    }
    
    def median(values: Seq[Long]) = {
        val vals = values.sorted
        if(vals.length % 2 == 0) (vals(vals.length / 2 - 1) + vals(vals.length / 2)) / 2
        else vals(vals.length / 2)
    }
    
    def report(name: String)(f: => Unit) = {
        println("[binfo] Running " + name)
        val start = System.currentTimeMillis
        f
        val time = System.currentTimeMillis - start
        (name, time)
    }
}
