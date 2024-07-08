package Impl

import APIs.PatientAPI.PatientQueryMessage
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
        s"UPDATE ${schemaName}.user_name SET validation = TRUE WHERE user_name = ?",
        List(SqlParameter("String", userName))
      ).as("Validation set to True")
    } else {
      writeDB(
        s"DELETE FROM ${schemaName}.user_name WHERE user_name = ?",
        List(SqlParameter("String", userName))
      ).as("User deleted")
    }
  }
 

