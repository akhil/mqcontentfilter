package com.filter

import akka.actor._
import akka.io.IO
import spray.can.Http
import spray.routing.HttpService

/**
  * Created by akhil on 1/7/16.
  */

trait UI extends HttpService {

  val uiRoute = {
    getFromResourceDirectory("webapp") ~
    path("") {
      getFromResource("webapp/index.html")
    } ~
    pathPrefix("webjars") {
      get {
        getFromResourceDirectory("META-INF/resources/webjars")
      }
    }
  }
}

class UIActor extends Actor with UI with ActorLogging {
  override def actorRefFactory: ActorRefFactory = context
  override def receive = runRoute(uiRoute)
}

object Main extends App {

  def doMain() {
    implicit val system = ActorSystem()

    val server = system.actorOf(Props[UIActor], "http")

    IO(Http) ! Http.Bind(server, "127.0.0.1", 8080)
  }

  doMain()
}