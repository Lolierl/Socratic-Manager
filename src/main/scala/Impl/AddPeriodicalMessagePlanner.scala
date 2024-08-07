package Impl

import APIs.EditorAPI.EditorRequestMessage
import Common.API.{PlanContext, Planner}
import Common.DBAPI.{writeDB, *}
import Common.Object.{ParameterList, SqlParameter}
import Common.ServiceUtils.schemaName
import cats.effect.IO
import io.circe.generic.auto.*


case class AddPeriodicalMessagePlanner(periodical: String, override val planContext: PlanContext) extends Planner[String]:
  override def plan(using planContext: PlanContext): IO[String] = {
    // Check if the user is already registered
    val checkPeriodicalExists = readDBBoolean(s"SELECT EXISTS(SELECT 1 FROM ${schemaName}.periodicals WHERE periodical = ?)",
      List(SqlParameter("String", periodical))
    )

    checkPeriodicalExists.flatMap { exists =>
      if (exists) {
        IO.pure("Already exists")
      } else {
        writeDB(
          s"INSERT INTO ${schemaName}.periodicals (periodical) VALUES (?)",
          List(SqlParameter("String", periodical))
        )
      }
    }
  }

