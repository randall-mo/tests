package org.akka.essentials.supervisor.example1

import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.ask
import scala.concurrent.Await
import scala.concurrent.duration._

case class Result()

object MyActorSystem extends App {
    import akka.util.Timeout
    val system = ActorSystem("faultTolerance")
    val log = system.log
    val originalValue: Int = 0;

    val supervisor = system.actorOf(Props[SupervisorActor], name = "supervisor")

    log.info("Sending value 8, no exceptions should be thrown! ");
    var mesg: Int = 8;
    supervisor ! mesg

    implicit val timeout = Timeout(5, SECONDS)
    var future = supervisor ? new Result
    var result = Await.result(future, timeout.duration).asInstanceOf[Int]

    log.info("Value Received->" + result)

    log.info("Sending value -8, ArithmeticException should be thrown! Our Supervisor strategy says resume !")
    mesg = -8
    supervisor ! mesg

    future = supervisor ? new Result
    result = Await.result(future, timeout.duration).asInstanceOf[Int]

    log.info("Value Received->" + result)

    log.info("Sending value null, NullPointerException should be thrown! Our Supervisor strategy says restart !")
    supervisor ! new NullPointerException

    future = supervisor ? new Result
    result = Await.result(future, timeout.duration).asInstanceOf[Integer]

    log.info("Value Received->" + result)

    log.info("Sending value \"String\", IllegalArgumentException should be thrown! Our Supervisor strategy says Stop !")

    supervisor ? "Do Something"

    future = supervisor ? new Result
    result = Await.result(future, timeout.duration).asInstanceOf[Int]

    log.info("Value Received->" + result)

    system.shutdown();

}