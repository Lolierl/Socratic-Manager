package Impl

import APIs.EditorAPI.EditorRequestMessage
import Common.API.{PlanContext, Planner}
import Common.DBAPI.{writeDB, *}
import Common.Object.{ParameterList, SqlParameter}
import Common.ServiceUtils.schemaName
import cats.effect.IO
import io.circe.generic.auto.*


case class FinishEditorMessagePlanner(userName: String, allowed:Boolean, override val planContext: PlanContext) extends Planner[String]:
  override def plan(using planContext: PlanContext): IO[String] = {
    // Check if the user is already registered
    val checkTaskExists = readDBBoolean(s"SELECT EXISTS(SELECT 1 FROM ${schemaName}.ManagerTasks WHERE user_name = ?)",
      List(SqlParameter("String", userName))
    )

    checkTaskExists.flatMap { exists =>
      if (exists) {
        IO.pure("No such tasks")
      } else {
        val passwordIO = readDBString(
          s"SELECT password FROM ${schemaName}.ManagerTasks WHERE user_name = ?",
          List(SqlParameter("String", userName))
        )
        passwordIO.flatMap { password =>
          val deleteTask = writeDB(
            s"DELETE FROM ${schemaName}.ManagerTasks WHERE user_name = ?",
            List(SqlParameter("String", userName))
          )
          EditorRequestMessage(userName, allowed).send
        }
      }
    }
  }

