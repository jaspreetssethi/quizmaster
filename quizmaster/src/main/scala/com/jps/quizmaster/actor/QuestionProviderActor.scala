package com.jps.quizmaster.actor

import akka.actor.{Actor, Props}
import com.jps.quizmaster.message.{GetQuestionAnswer, QuestionAnswer}

import scala.util.Random

class QuestionProviderActor extends Actor{
  override def receive: Receive = {
    case GetQuestionAnswer => {
      sender() ! Random.shuffle(QuestionProviderActor.questions).head
    }
  }
}

object QuestionProviderActor {
  val questions =
      QuestionAnswer("Arrange the following organisms based on their normal size. Smallest to Largest.",
         "Mosquito" :: "Lizard" :: "Lion" :: "Elephant" :: Nil
      ) :: QuestionAnswer("Arrange the following body parts in count. From least to most.",
        "Neck" :: "Legs" :: "Fingers" :: "Hair" :: Nil
      ) :: QuestionAnswer("Arrange the following fruit in ascending alphabetical order.",
        "Apple" :: "Banana" :: "Orange" :: "Pineapple" :: Nil
      ) :: QuestionAnswer("Arrange the shapes in ascending order of edges.",
        "Triangle" :: "Square" :: "Pentagon" :: "dodecagon" :: Nil

      ):: QuestionAnswer("List the following four inventions in order staring with the earliest",
        "Battery" :: "Jeans" :: "Deisel Engine" :: "Hovercraft" :: Nil

      ) :: QuestionAnswer("List the following 4 lengths in order starting with the smallest",
        "25cm" :: "10 inches" :: "1 Yard" :: "1000mm" :: Nil

      ) :: QuestionAnswer("List the disney films in order from oldest to most recent",
        "Snow White and the Seven Dwarfs" :: "Pinocchio" :: "Cinderella" :: "Sleeping Beauty" :: Nil

      )  :: QuestionAnswer("List the order of population of the followoing countries (least to most)",
        "Greece" :: "Brasil" :: "USA" :: "China" :: Nil

      )  :: QuestionAnswer("Arrange the monetary amounts from ost to least",
        "1 Nickle" :: "6 pennies" :: "2 quarters" :: "8 dimes" :: Nil

      ) :: Nil

  def props = Props[QuestionProviderActor]
}
