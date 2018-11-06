package com.jps.quizmaster

import akka.actor.ActorSystem
import akka.util.Timeout
import akka.http.scaladsl.server.Directives._
import scala.concurrent.Await
import akka.http.scaladsl.model._
import scala.concurrent.duration.Duration

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.jps.quizmaster.actor.QuizMaster.{Player, Registration}
import com.jps.quizmaster.actor.{PlayerActor, QuestionProviderActor, QuizMaster}

import scala.concurrent.duration._
import scala.io.StdIn

object QuizMasterApp extends App {
  // Create the 'helloAkka' actor system
  implicit val system: ActorSystem = ActorSystem("quizmaster")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
     val route =
      path("health") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>OK</h1>"))
        }
      }~path("hi") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>HI</h1>"))
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")

  implicit val t = Timeout(10 second)
  val provider = system.actorOf(QuestionProviderActor.props)
  val quizmaster = system.actorOf(QuizMaster.props(1, 10 seconds, 1 minute, provider))
  val player = system.actorOf(PlayerActor.props("player1", quizmaster))

  var char = '~'
  while((char = StdIn.readChar()) != '~') {
    player ! char
  }

  system.terminate()
}