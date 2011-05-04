package com.verknowsys.dbbench

import org.neodatis.odb._
import scala.collection.JavaConversions._

trait Database {
    def save(obj: Any)
    def saveAndCommit(obj: Any)
    def saveList(list: Seq[Any])
    def disconnect
    def saveBatch {}
}

class NoopClient extends Database {
    def save(obj: Any) {}
    def saveAndCommit(obj: Any) {}
    def saveList(list: Seq[Any]){}
    def disconnect {}

}

class NoopBatchClient(n: Int) extends NoopClient {
    override def saveBatch {
        (1 to (100000 / n)) foreach { i =>
            println(i)
            (1 to n) foreach { k =>
                
            }
        }
    }
}
