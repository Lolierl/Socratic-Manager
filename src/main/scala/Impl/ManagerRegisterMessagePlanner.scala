package Impl

import cats.effect.IO
import io.circe.generic.auto.*
import Common.API.{PlanContext, Planner}
import Common.DBAPI.{writeDB, *}
import Common.Object.{ParameterList, SqlParameter}
import Common.ServiceUtils.schemaName
import APIs.PatientAPI.PatientQueryMessage
import cats.effect.IO
import io.circe.generic.auto.*
import APIs.SuperuserAPI.AuthenManagerMessage
import APIs.UserManagementAPI.CheckUserExistsMessage
import Shared.PasswordHasher.hashPassword

case class ManagerRegisterMessagePlanner(userName: String, password: String,override val planContext: PlanContext) extends Planner[String]:
  override def plan(using planContext: PlanContext): IO[String] = {
    // Check if the user is already registered
      val checkUserExists = CheckUserExistsMessage(userName).send

      checkUserExists.flatMap { exists =>
        if (exists) {
          IO.pure("already registered")
        } else {
          val (passwordHash, salt) = hashPassword(password)
          for {
            _ <- writeDB(
              s"INSERT INTO ${schemaName}.key_buffer (user_name, password_hash, salt) VALUES (?, ?, ?)",
              List(
                SqlParameter("String", userName),
                SqlParameter("String", passwordHash),
                SqlParameter("String", salt)
              ))
            _ <- writeDB(
              s"INSERT INTO ${schemaName}.users (user_name, validation) VALUES (?, FALSE)",
              List(SqlParameter("String", userName))
            )
            _ <- AuthenManagerMessage(userName).send
          } yield "User registered successfully"
        }
    }
  }

