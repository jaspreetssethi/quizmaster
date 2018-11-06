package com.jps.quizmaster

import akka.actor.ActorSystem
import akka.util.Timeout
import com.jps.quizmaster.actor.QuizMaster.{Player, Registration}
import com.jps.quizmaster.actor.{PlayerActor, QuestionProviderActor, QuizMaster}

import scala.concurrent.duration._
import scala.io.StdIn
object QuizMasterApp extends App {
  // Create the 'helloAkka' actor system
  val system: ActorSystem = ActorSystem("quizmaster")

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