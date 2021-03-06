package org.akka.essentials.zeromq.example2

import akka.actor.actorRef2Scala
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.zeromq.Bind
import akka.zeromq.HighWatermark
import akka.zeromq.SocketType
import akka.zeromq.ZMQMessage
import akka.zeromq.ZeroMQExtension
import scala.util.Random
import akka.zeromq.Listener
import akka.actor.Cancellable
import scala.concurrent.duration._

case class Tick

class RouterActor extends Actor with ActorLogging {
    val pubSocket = ZeroMQExtension(context.system).newSocket(SocketType.Router, Bind("tcp://127.0.0.1:1234"), Listener(self), HighWatermark(50000))

    var random = new Random(3);

    var count = 0
    var cancellable: Cancellable = null

    override def preStart() {
        cancellable = context.system.scheduler.schedule(1 second, 1 second, self, Tick)
    }

    def receive: Receive = {
        case Tick =>
            count += 1
            if (random.nextBoolean() == true) {
                pubSocket ! ZMQMessage("A", "This is the workload for A")
            } else {
                pubSocket ! ZMQMessage("B", "This is the workload for B")
            }
            if (count == 10) {
                cancellable.cancel()
            }

        case m: ZMQMessage =>
            var replier = m.frame(0)
            var msg = m.frame(1)
            log.info("recived message from {} with mesg -> {}", replier, msg)
    }
}