import sbt._

class dbbench(info: ProjectInfo) extends DefaultProject(info){
    val neodatis = "org.neodatis.odb" % "neodatis-odb" % "1.9.30.689"
}
