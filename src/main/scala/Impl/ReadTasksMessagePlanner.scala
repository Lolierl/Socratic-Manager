package Impl

import APIs.PatientAPI.PatientQueryMessage
import Common.API.{PlanContext, Planner}
import Common.DBAPI.{writeDB, *}
import Common.Object.{ParameterList, SqlParameter}
import Common.ServiceUtils.schemaName
import cats.effect.IO
import io.circe.generic.auto.*
import io.circe.Json
import io.circe.syntax._
import io.circe.generic.auto._
import io.circe.parser._

case class ReadTasksMessagePlanner(override val planContext: PlanContext) extends Planner[String]:
  override def plan(using planContext: PlanContext): IO[String] = {
    // Check if the user is already registered
    // Fetch rows from EditorTasks
    val editorTasksIO: IO[List[Json]] = readDBRows(
      s"SELECT user_name FROM ${schemaName}.EditorTasks", List()
    )
    editorTasksIO.map { managerTasks =>
      Json.arr(managerTasks: _*).noSpaces
    }
  }

