package com.filter

import akka.actor._
import akka.io.IO
import org.json4s.{DefaultFormats, Formats}
import spray.can.Http
import spray.httpx.Json4sJacksonSupport
import spray.routing.HttpService

import scala.concurrent.ExecutionContext

/**
  * Created by akhil on 1/7/16.
  */

trait UI extends HttpService with Dao with Json4sJacksonSupport {

  override implicit def json4sJacksonFormats: Formats = DefaultFormats

  val uiRoute = {
    getFromResourceDirectory("webapp") ~
    path("") {
      getFromResource("webapp/index.html")
    } ~
    pathPrefix("webjars") {
      get {
        getFromResourceDirectory("META-INF/resources/webjars")
      }
    } ~
    path("rule") {
      post {
        entity(as[Rule]) { rule =>
          save(rule)
          complete("OK")
        }
      }
    } ~
    path("rules" ) {
      get {
        complete(getAllRules())
      }
    }
  }
}

trait UIActor extends Actor with UI with ActorLogging {
  override def actorRefFactory: ActorRefFactory = context
  override def receive = runRoute(uiRoute)
}

object Main extends App {

  def doMain() {
    implicit val system = ActorSystem()

    val server = system.actorOf(Props(new UIActor{
      override val ec: ExecutionContext = system.dispatcher
      override val dbprofile: slick.driver.JdbcProfile = slick.driver.H2Driver
      override val DB  = dbprofile.backend.Database.forConfig("h2mem1")
      createDDL()
    }), "http")

    IO(Http) ! Http.Bind(server, "127.0.0.1", 8080)
  }

  doMain()
}