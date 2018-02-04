package com.followermaze

import com.followermaze.publisher.EventPublisher
import com.followermaze.server.{ClientSocketServer, EventSocketServer}

import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

object Boot extends App {

  val executorService = java.util.concurrent.Executors.newCachedThreadPool
  implicit val executionContext: ExecutionContext =
    ExecutionContext.fromExecutor(executorService)

  val clientSocketServer = new ClientSocketServer(9099)
  clientSocketServer.start

  private val eventPublisher = new EventPublisher()
  val eventSocketServer = new EventSocketServer(9090, eventPublisher)
  eventSocketServer.start

  Future(eventPublisher.startPublishing())

  println("Please press enter to shutdown the system!")

  StdIn.readLine()
  clientSocketServer.shutdown
  eventPublisher.shutdown
  eventSocketServer.shutdown
  executorService.shutdown()
}
