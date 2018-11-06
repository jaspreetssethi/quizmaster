package com.jps.quizmaster.message

sealed trait Message

case class QuestionAnswer(question: String, answer: List[String]) extends Message
case object GetQuestionAnswer extends Message
case class QuestionChoice(question: String, choices: List[Choice]) extends Message
case class Choice(shortCode: String, value: String) extends Message
case class ChoiceRejected(reason: String, choice: Choice) extends Message
case class ChoiceAccepted(choice: Choice) extends Message
case object CorrectAnswer extends Message
case class WrongAnswer(providedAnswer: List[Choice], answer: List[Choice]) extends Message
case object NewGame extends Message