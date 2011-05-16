import org.neodatis.odb._

object NeodatisRemoteServer {
    def apply() {
        val server = ODBFactory.openServer(9002)
        server.addBase("base02", "/tmp/base02.neodatis")
        server.startServer(true)
    }
}
