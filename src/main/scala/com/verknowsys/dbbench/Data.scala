package com.verknowsys.dbbench

import java.util.UUID

class ProcessInfo(
    val pid: Int,
    val cpu: Int,
    val mem: Int,
    val name: String
)

case class Settings(
    val uuid: UUID,
    val path: String
)
