package Process

import Common.API.PlanContext
import Impl.*
import cats.effect.*
import io.circe.generic.auto.*
import io.circe.parser.decode
import io.circe.syntax.*
import org.http4s.*
import org.http4s.client.Client
import org.http4s.dsl.io.*


object Routes:
  private def executePlan(messageType:String, str: String): IO[String]=
    messageType match {
      case "ManagerRequestMessage" =>
        IO(decode[ManagerRequestMessagePlanner](str).getOrElse(throw new Exception("Invalid JSON for ManagerRequestMessage")))
          .flatMap{m=>
            m.fullPlan.map(_.asJson.toString)
          }
      case "ManagerRegisterMessage" =>
        IO(decode[ManagerRegisterMessagePlanner](str).getOrElse(throw new Exception("Invalid JSON for ManagerRegisterMessage")))
          .flatMap{m=>
            m.fullPlan.map(_.asJson.toString)
          }
      case "ManagerLoginMessage" =>
        IO(decode[ManagerLoginMessagePlanner](str).getOrElse(throw new Exception("Invalid JSON for ManagerLoginMessage")))
          .flatMap{m=>
            m.fullPlan.map(_.asJson.toString)
          }
      case "AuthenEditorMessage" =>
        IO(decode[AuthenEditorMessagePlanner](str).getOrElse(throw new Exception("Invalid JSON for AuthenEditorMessage")))
          .flatMap{m=>
            m.fullPlan.map(_.asJson.toString)
          }
      case "ReadTasksMessage" =>
        IO(decode[ReadTasksMessagePlanner](str).getOrElse(throw new Exception("Invalid JSON for ReadTasksMessage")))
          .flatMap{m=>
            m.fullPlan.map(_.asJson.toString)
          }
      case "FinishEditorMessage" =>
        IO(decode[FinishEditorMessagePlanner](str).getOrElse(throw new Exception("Invalid JSON for FinishEditorMessage")))
          .flatMap{m=>
            m.fullPlan.map(_.asJson.toString)
          }
      case "AddPeriodicalMessage" =>
        IO(decode[AddPeriodicalMessagePlanner](str).getOrElse(throw new Exception("Invalid JSON for AddPeriodicalMessage")))
          .flatMap{m=>
            m.fullPlan.map(_.asJson.toString)
          }
      case "ReadPeriodicalsMessage" =>
        IO(decode[ReadPeriodicalsMessagePlanner](str).getOrElse(throw new Exception("Invalid JSON for ReadPeriodicalsMessage")))
          .flatMap{m=>
            m.fullPlan.map(_.asJson.toString)
          }
      case _ =>
        IO.raiseError(new Exception(s"Unknown type: $messageType"))
    }

  val service: HttpRoutes[IO] = HttpRoutes.of[IO]:
    case req @ POST -> Root / "api" / name =>
        println("request received")
        req.as[String].flatMap{executePlan(name, _)}.flatMap(Ok(_))
        .handleErrorWith{e =>
          println(e)
          BadRequest(e.getMessage)
        }
