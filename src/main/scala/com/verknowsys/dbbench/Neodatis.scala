package com.verknowsys.dbbench

import org.neodatis.odb._
import java.io.File

trait AbstractNeodatisClient extends Database {    
    val odb: ODB
    
    def disconnect {
        odb.close
    }
    
    def save(obj: Any) {
        odb.store(obj)
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

class NeodatisRemoteClient extends AbstractNeodatisClient {
    val odb = ODBFactory.openClient("localhost", 9002, "base02");
}

object NeodatisRemoteServer {
    def main(args: Array[String]) {
        val server = ODBFactory.openServer(9002)
        server.addBase("base02", "/tmp/base02.neodatis")
        server.startServer(true)
    }
}
