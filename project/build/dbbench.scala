import sbt._

class dbbench(info: ProjectInfo) extends DefaultProject(info) with assembly.AssemblyBuilder {
    val neodatis = "org.neodatis.odb" % "neodatis-odb" % "1.9.30.689"
    val squeryl  = "org.squeryl" % "squeryl_2.8.1" % "0.9.4-RC6"
    val postgres = "postgresql" % "postgresql" % "9.0-801.jdbc4"
    
    override def mainClass = Some("com.verknowsys.dbbench.App")
}
