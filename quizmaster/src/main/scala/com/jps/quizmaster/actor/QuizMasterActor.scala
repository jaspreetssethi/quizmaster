package com.jps.quizmaster.actor

import akka.actor.{Actor, ActorLogging, ActorRef, FSM, Props}
import com.jps.quizmaster.actor.QuizMaster._
import com.jps.quizmaster.message._

import scala.concurrent.duration._
import akka.pattern._
import akka.util.Timeout

import scala.util.Random

class QuizMaster(minRegistration: Int, maxReadyWait: FiniteDuration, maxGameDuration: FiniteDuration, questionProvider: ActorRef)(implicit t: Timeout) extends FSM[State, Data] with ActorLogging{
  onTransition{
    case Buzzer -> RegistrationOpen => {
      stateData match {
        case Game(gameState, _, _) => {
          gameState.keys.foreach(_.route ! NewGame)
        }
      }
    }
  }

  startWith(RegistrationOpen, PlayerRegistration(Nil))

  when(RegistrationOpen) {
    case Event(registration: Registration, PlayerRegistration(players))  => {
      val registered = registration :: players
      if(registered.size >= minRegistration) {
        println("Registration closing")
        goto(RegistrationClosing) using ReadyPlayers(registered, Nil)
      } else {
        println("Waiting for more registrations")
        stay using PlayerRegistration(registered)
      }
    }
  }

  onTransition{
    case RegistrationOpen -> RegistrationClosing => {
      nextStateData match {
        case ReadyPlayers(registrations, _) => registrations.map(_.route).foreach(_ ! BecomeReady)
      }
    }
  }

  when(RegistrationClosing, maxReadyWait) {
    case Event(PlayerReady(name), ReadyPlayers(idle, ready)) => {
      val (stillIdle, nowReady) = idle.partition(_.name != name)
      if(stillIdle.isEmpty) {
        println("Players Ready to Play")
        goto(Ready) using ReadyToPlay(nowReady ++ ready)
      } else {
        println("Waiting for more players to be ready")
        stay using ReadyPlayers(stillIdle, nowReady ++ ready)
      }
    }
  }

  onTransition{
    case RegistrationClosing -> Ready => {
      import context._
      (questionProvider ? GetQuestionAnswer).mapTo[QuestionAnswer].map{
        case QuestionAnswer(question, answers) => {
          val options = ('a' to 'z').zip(Random.shuffle(answers)).map(a => Choice(a._1.toString, a._2)).toList
          val ordered = options.sortWith((a, b) => answers.indexOf(a.value) < answers.indexOf(b.value))
          QuestionChoiceAnswer(question, options, ordered)
        }
      } pipeTo context.self
    }
  }

  when(Ready) {
    case Event(qca @ QuestionChoiceAnswer(question, choice, answer), ReadyToPlay(players)) => {
      val qc = QuestionChoice(question, choice)
      val zip = players zip Stream.continually(Nil: List[Choice])
      println("Game has begun")
      goto(Mark) using Game(zip.toMap, 0, qca)
    }
  }

  onTransition {
    case Ready -> Mark => nextStateData match {
      case Game(gameState, _, QuestionChoiceAnswer(question, choice, _)) => {
        gameState.keys.foreach(_.route ! QuestionChoice(question, choice))
      }
    }
  }

  when(Mark) {
    case Event(choice: Choice, state @ Game(gameState, respCount, questionChoiceAnswer)) => {
      println("Got a choice from a player")
      val registration = gameState.keys.find(_.route == sender())
      val newState: Map[Registration, List[Choice]] = registration.map{ playerRegistration =>
        val prvChoices = gameState(playerRegistration)
        if(prvChoices.contains(choice)){
          sender() ! ChoiceRejected("Already Selected", choice)
          gameState
        } else if(prvChoices.size >= questionChoiceAnswer.answer.size){
          sender() ! ChoiceRejected("Complete Answer Already Recieved", choice)
          gameState
        } else {
          val playerChoices = choice :: prvChoices
          sender() ! ChoiceAccepted(choice)
          gameState.updated(playerRegistration, playerChoices)
        }
      }.fold(gameState)(identity)

      val expectedChoices = gameState.keys.size * questionChoiceAnswer.answer.size
      if(respCount + 1 >= expectedChoices) {
        goto(Buzzer) using state.copy(status = newState, recieved = respCount + 1)
      } else {
        stay() using state.copy(status = newState, recieved = respCount + 1)
      }
    }
  }

  onTransition{
    case Mark -> Buzzer => nextStateData match {
      case Game(gameState, _, QuestionChoiceAnswer(_, choice, answer)) => {
        val test = gameState.foreach( entry => {
          val providedAnswer = entry._2.reverse
          if(validateAnswer(providedAnswer, answer)) {
            entry._1.route ! CorrectAnswer
          } else {
            entry._1.route ! WrongAnswer(providedAnswer, answer)
          }
        })
      }
    }
  }

  when(Buzzer, 10 seconds) {
    case Event(StateTimeout, _) => {
      goto(RegistrationOpen) using PlayerRegistration(Nil)
    }
  }

  def validateAnswer(choices: List[Choice], answer: List[Choice]) = {
    choices == answer
  }
}

object QuizMaster {
  sealed trait State
  case object RegistrationOpen extends State
  case object RegistrationClosing extends State
  case object Ready extends State
  case object Mark extends State
  case object Buzzer extends State

  sealed trait Data
  case class PlayerRegistration(players: List[Registration]) extends Data
  case class ReadyPlayers(players: List[Registration], ready: List[Registration]) extends Data
  case class ReadyToPlay(players: List[Registration]) extends Data
  case class Game(status: Map[Registration, List[Choice]], recieved: Long, qca: QuestionChoiceAnswer) extends Data

  sealed trait Message
  case class Registration(name: String, route: ActorRef) extends Message
  case object BecomeReady extends Message
  case class PlayerReady(name: String) extends Message

  case class Player(name: String)
  case class QuestionChoiceAnswer(question: String, choice: List[Choice], answer: List[Choice])

  def props(minRegistration: Int, maxReadyWait: FiniteDuration, maxGameDuration: FiniteDuration, questionProvider: ActorRef)(implicit t: Timeout) = Props(
    new QuizMaster(minRegistration: Int, maxReadyWait: FiniteDuration, maxGameDuration: FiniteDuration, questionProvider: ActorRef)
  )
}