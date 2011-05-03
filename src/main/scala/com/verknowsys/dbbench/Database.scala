package com.verknowsys.dbbench

import org.neodatis.odb._
import scala.collection.JavaConversions._

trait Database {
    def save(obj: Any)
    def saveAndCommit(obj: Any)
    def saveList(list: Seq[Any])
    def disconnect
}

class NoopClient extends Database {
    def save(obj: Any) {}
    def saveAndCommit(obj: Any) {}
    def saveList(list: Seq[Any]){}
    def disconnect {}
}
