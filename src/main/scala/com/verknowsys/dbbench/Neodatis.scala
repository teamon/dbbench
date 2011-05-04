package com.verknowsys.dbbench

import org.neodatis.odb._
import java.io.File

object NeodatisConfig {
    def apply(){
        // OdbConfiguration.setMaxNumberOfObjectInCache(10)
        // OdbConfiguration.setUseCache(false)
    }
}

trait AbstractNeodatisClient extends Database {
    
    val odb: ODB
    
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
    }
}


class NeodatisLocalClient extends AbstractNeodatisClient {
    // OdbConfiguration.setReconnectObjectsToSession(true)
    val server = ODBFactory.openServer(9001)
    val file = new File("/tmp/base01.neodatis")
    if(file.exists) file.delete
    server.addBase("base01", "/tmp/base01.neodatis")
    server.startServer(true)
    
    val odb = server.openClient("base01")
    
    override def disconnect {
        super.disconnect
        server.close
    }
}

class NeodatisLocalBatchClient(n: Int) extends NeodatisLocalClient {
    override def saveBatch {
        (1 to (100000 / n)) foreach { i =>
            println(i)
            (1 to n) foreach { k =>
                odb.store(new ProcessInfo(k, 20, 30, "foo"))
            }
            odb.commit
        }
    }
}

class NeodatisRemoteClient extends AbstractNeodatisClient {
    val odb = ODBFactory.openClient("localhost", 9002, "base02");
}

class NeodatisRemoteBatchClient(n: Int) extends NeodatisRemoteClient {
    override def saveBatch {
        (1 to (100000 / n)) foreach { i =>
            println(i)
            (1 to n) foreach { k =>
                odb.store(new ProcessInfo(k, 20, 30, "foo"))
            }
            odb.commit
        }
        
        // (1 to n) foreach { i =>
        //     println(i)
        //     odb.store(new ProcessInfo(i, 20, 30, "foo"))
        //     odb.commit
        // }
    }
}

object NeodatisRemoteServer {
    def main(args: Array[String]) {
        NeodatisConfig()
        val server = ODBFactory.openServer(9002)
        server.addBase("base02", "/tmp/base02.neodatis")
        server.startServer(true)
    }
}
