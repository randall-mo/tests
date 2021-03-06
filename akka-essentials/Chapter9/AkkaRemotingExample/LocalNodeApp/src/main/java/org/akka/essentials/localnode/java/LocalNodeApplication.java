package org.akka.essentials.localnode.java;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * Hello world!
 */
public class LocalNodeApplication {
    public static void main(String[] args) throws Exception {
        ActorSystem _system = ActorSystem.create("LocalNodeApp", ConfigFactory
                .load().getConfig("LocalSys"));
        ActorRef localActor = _system.actorOf(Props.create(LocalActor.class));
        localActor.tell("Hello", ActorRef.noSender());

        Thread.sleep(5000);
        _system.shutdown();
    }
}
