package Impl

import APIs.UserManagementAPI.RegisterMessage
import Common.API.{PlanContext, Planner}
import Common.DBAPI.{writeDB, *}
import Common.Object.{ParameterList, SqlParameter}
import Common.ServiceUtils.schemaName
import cats.effect.IO
import io.circe.generic.auto.*



case class ManagerRequestMessagePlanner(userName: String, allowed:Boolean, override val planContext: PlanContext) extends Planner[String]:
  override def plan(using planContext: PlanContext): IO[String] = {

    if (allowed) {
      writeDB(
        s"UPDATE ${schemaName}.users SET validation = TRUE WHERE user_name = ?",
        List(SqlParameter("String", userName))
      ).flatMap { _ =>
        readDBString(
          s"SELECT password FROM ${schemaName}.users WHERE user_name = ?",
          List(SqlParameter("String", userName))
        ).flatMap { password =>
          RegisterMessage(userName, password, "manager").send

        }
      }
    }else {
      writeDB(
        s"DELETE FROM ${schemaName}.users WHERE user_name = ?",
        List(SqlParameter("String", userName))
      )
      }
    
  }

 

