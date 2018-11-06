package com.jps.quizmaster.actor

import akka.actor.{Actor, ActorRef, Props}
import com.jps.quizmaster.actor.QuizMaster.{BecomeReady, PlayerReady, Registration}
import com.jps.quizmaster.message.{NewGame, QuestionChoice}

class PlayerActor(name: String, quizMaster: ActorRef) extends Actor{
  var qc: Option[QuestionChoice] = None

  quizMaster ! Registration("player1", self)

  override def receive: Receive = {
    case BecomeReady => {
      sender() ! PlayerReady(name)
    }
    case a: QuestionChoice => {
      qc = Some(a)
      println(a.question)
      println(a.choices)
    }
    case c: Char => {
      val choice = qc.map(_.choices) match {
        case Some(choices) => choices.find(_.shortCode == s"${c.toLower}")
      }
      choice match {
        case Some(ch) => quizMaster ! ch
      }
    }
    case NewGame => {
      quizMaster ! Registration("player1", self)
    }
    case x => println(x)
  }
}

object PlayerActor {
  def props(name: String, quizMaster: ActorRef) = Props(new PlayerActor(name, quizMaster))
}