package org.akka.essentials.zeromq.example1

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.zeromq.Bind
import akka.zeromq.SocketType
import akka.zeromq.ZeroMQExtension
import akka.zeromq._
import akka.actor.Cancellable
import scala.concurrent.duration._
import akka.util.ByteString

case class Tick()

class PublisherActor extends Actor with ActorLogging {
    val pubSocket = ZeroMQExtension(context.system).newSocket(SocketType.Pub, Bind("tcp://127.0.0.1:1234"))
    var count = 0
    var cancellable: Cancellable = null

    override def preStart() {
        cancellable = context.system.scheduler.schedule(1 second, 1 second, self, Tick)
    }

    def receive: Receive = {
        case Tick =>
            count += 1
            val payload = "This is the workload " + count
            pubSocket ! ZMQMessage.withFrames(ByteString("someTopic"), ByteString(payload))
        if (count == 10) {
            cancellable.cancel()
        }
    }
}