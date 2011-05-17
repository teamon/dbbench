import org.neodatis.odb._

object NeodatisRemoteServer {
    def main(args: Array[String]) {
        val server = ODBFactory.openServer(9002)
        val file = new java.io.File("/tmp/base02.neodatis")
        if(file.exists) file.delete
        server.addBase("base02", "/tmp/base02.neodatis")
        server.startServer(true)
    }
}
