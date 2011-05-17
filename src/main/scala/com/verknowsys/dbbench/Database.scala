package com.verknowsys.dbbench

import org.neodatis.odb._
import scala.collection.JavaConversions._

trait Database {
    def save(obj: Any)
    def saveAndCommit(obj: Any)
    def saveList(list: Seq[Any])
    def disconnect
    def saveBatch {}
    def size: Int
    def queryByPID(pid: Int): List[Any]
    def queryByName(name: String): List[Any]
    def queryByTime(from: java.sql.Timestamp, to: java.sql.Timestamp): List[Any]
    def rebuildIndex {}
}

class NoopClient extends Database {
    def save(obj: Any) {}
    def saveAndCommit(obj: Any) {}
    def saveList(list: Seq[Any]){}
    def disconnect {}
    def size = 0
    def queryByPID(pid: Int) = Nil
    def queryByName(name: String) = Nil
    def queryByTime(from: java.sql.Timestamp, to: java.sql.Timestamp) = Nil
}
