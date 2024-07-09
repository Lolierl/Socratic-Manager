package Impl

import APIs.PatientAPI.PatientQueryMessage
import Common.API.{PlanContext, Planner}
import Common.DBAPI.{writeDB, *}
import Common.Object.{ParameterList, SqlParameter}
import Common.ServiceUtils.schemaName
import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto.*
import io.circe.parser.*
import io.circe.syntax.*

case class ReadPeriodicalsMessagePlanner(override val planContext: PlanContext) extends Planner[String]:
  override def plan(using planContext: PlanContext): IO[String] = {
    val PeriodicalsIO: IO[List[Json]] = readDBRows(
      s"SELECT periodical FROM ${schemaName}.periodicals", List()
    )
    PeriodicalsIO.map {periodicals =>
      Json.arr(periodicals: _*).noSpaces
    }
  }

