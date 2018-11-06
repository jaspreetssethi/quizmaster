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

      )  :: QuestionAnswer("Arrange the monetary amounts from Most to least",
        "1 Nickle" :: "6 pennies" :: "2 quarters" :: "8 dimes" :: Nil

      )  :: QuestionAnswer("Arrange the units from smallest to biggest",
        "minute" :: "day" :: "week" :: "fortnight" :: Nil

      )  :: QuestionAnswer("Arrange the Continents from smallest to biggest",
        "Australia " :: "Europe" :: "Antarctica" :: "South America" :: Nil

      ) :: QuestionAnswer("Arrange the Books by publication date (Oldest first)",
        "grapes of wrath" :: "1984 " :: "to kill a mockingbird" :: "Harry Potter and the Sorcerer's Stone"  :: Nil

      ) :: QuestionAnswer("Arrange the Star Wars in Episodic order",
        "The Phantom Menace" :: "A New Hope" :: "The Empire Strikes Back" :: "The Force Awakens"  :: Nil

      ) :: QuestionAnswer("Arrange the Operating Systems\\Kernels by release date (Oldest first)",
        "MSDOS" :: "OS/2" :: "Linux" :: "Windows NT"  :: Nil

      ):: QuestionAnswer("Arrange the Web Sites by popularity (October 2018) (least to most hits)",
        "Twitch.tv" :: "Amazon.com" :: "Baidu.com" :: "Google.com"  :: Nil

      ):: QuestionAnswer("Arrange the Web Sites by founding (earliest first)",
        "imdb.com" :: "Yahoo.com" :: "Amazon.com" :: "Google.com"  :: Nil

      ):: QuestionAnswer("Arrange the Programming language by creation date (earliest first)",
        "FORTRAN" :: "LISP" :: "BASIC" :: "C"  :: Nil

      ) :: QuestionAnswer("Arrange the Numbers in alphabetical order",
        "eight"::"five" :: "four" :: "one" :: "six"  :: Nil

      ) :: QuestionAnswer("Arrange the Colors by their letter count (smallest first)",
        "red"::"blue" :: "green" :: "yellow"  :: Nil

      ) :: QuestionAnswer("Arrange the Books by length (shortest to longest word count)",
        "The Lord of the Rings (All 3)" :: "War and Peace" :: "King James Authorized Bible"  :: "Les Miserables" :: Nil

      ):: QuestionAnswer("Arrange the Periods of the earth (earliest first)",
        "Cambrian" :: "Jurassic" :: "Cretaceous" :: "Neogene" :: Nil

      ) :: QuestionAnswer("Arrange the Elements by weight (lightest first)",
        "Hydrogen" :: "neon" :: "Copper" :: "Cesium" :: Nil

      )  :: QuestionAnswer("Arrange the TV shows by airtime (shortest running first)",
        "The Simpsons" :: "	Mister Rogers' Neighborhood" :: "As the World Turns" :: "Meet the Press" :: Nil

      ) :: Nil

  def props = Props[QuestionProviderActor]
}
