package com.verknowsys.dbbench

import org.neodatis.odb._
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery
import org.neodatis.odb.core.query.nq.SimpleNativeQuery
import org.neodatis.odb.core.query.criteria.Where
import org.neodatis.odb.impl.core.query.values.ValuesCriteriaQuery
import java.io.File
import scala.collection.JavaConversions._



trait AbstractNeodatisClient extends Database {
    
    val odb: ODB
    
    // index
    def init {
        odb.getClassRepresentation(classOf[ProcessInfo]).addIndexOn("processinfo-index", Array("name"), false)
        
    }
    
    def disconnect {
        odb.close
    }
    
    def save(obj: Any) {
        odb.store(obj)
    }
    
    def saveAndCommit(obj: Any) {
        odb.store(obj)
        odb.commit()
    }
    
    def saveList(list: Seq[Any]){
        list.foreach(odb.store)
        odb.commit()
    }
    
    def size = odb.count(new CriteriaQuery(classOf[ProcessInfo])).intValue
    
    // def queryByPID(pid: Int) = odb.getObjects(new CriteriaQuery(classOf[ProcessInfo], Where.equal("pid", pid))).toList
    
    def queryByPID(pid: Int) = odb.getObjects(new SimpleNativeQuery {
        def `match`(pi: ProcessInfo) = pi.pid == pid
    }).toList
    
    def queryByName(name: String) = odb.getObjects(new SimpleNativeQuery {
        def `match`(pi: ProcessInfo) = pi.name == name
    }).toList
    
    def queryByTime(from: java.sql.Timestamp, to: java.sql.Timestamp) = odb.getObjects(new SimpleNativeQuery {
        def `match`(pi: ProcessInfo) = pi.time.compareTo(from) >= 0 && pi.time.compareTo(to) <= 0
    }).toList
    
    def queryByNameAndTime(name: String, from: java.sql.Timestamp, to: java.sql.Timestamp) = odb.getObjects(new SimpleNativeQuery {
        def `match`(pi: ProcessInfo) = pi.name == name && pi.time.compareTo(from) >= 0 && pi.time.compareTo(to) <= 0
    }).toList
    
    def queryByTimeAndName(name: String, from: java.sql.Timestamp, to: java.sql.Timestamp) = odb.getObjects(new SimpleNativeQuery {
        def `match`(pi: ProcessInfo) = pi.time.compareTo(from) >= 0 && pi.time.compareTo(to) <= 0 && pi.name == name
    }).toList
    
    def sumCPU = odb.getValues(new ValuesCriteriaQuery(classOf[ProcessInfo]).sum("cpu")).getFirst.getByIndex(0).asInstanceOf[java.math.BigDecimal].intValue
    
    def sumMEM = odb.getValues(new ValuesCriteriaQuery(classOf[ProcessInfo]).sum("mem")).getFirst.getByIndex(0).asInstanceOf[java.math.BigDecimal].intValue
    
    def avgCPU = odb.getValues(new ValuesCriteriaQuery(classOf[ProcessInfo]).avg("cpu")).getFirst.getByIndex(0).asInstanceOf[java.math.BigDecimal].doubleValue
    
    def avgMEM = odb.getValues(new ValuesCriteriaQuery(classOf[ProcessInfo]).avg("mem")).getFirst.getByIndex(0).asInstanceOf[java.math.BigDecimal].doubleValue
    
    override def rebuildIndex {
        odb.getClassRepresentation(classOf[ProcessInfo]).rebuildIndex("processinfo-index", false)
    }
    
}

class NeodatisLocalClient(name: String = "base01", port: Int = 9001) extends AbstractNeodatisClient {
    // OdbConfiguration.setReconnectObjectsToSession(true)
    val server = ODBFactory.openServer(port)
    val file = new File("/tmp/"+name+".neodatis")
    if(file.exists) file.delete
    server.addBase(name, "/tmp/"+name+".neodatis")
    server.startServer(true)
    
    val odb = server.openClient(name)
    
    init
    
    override def disconnect {
        super.disconnect
        server.close
    }
}

class NeodatisRemoteClient extends AbstractNeodatisClient {
    val odb = ODBFactory.openClient("localhost", 9002, "base02");
    
    init
}


