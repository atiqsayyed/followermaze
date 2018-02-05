package com.followermaze

import com.followermaze.distributer.EventDistributer
import com.followermaze.server.{ClientSocketServer, EventSocketServer}

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object Boot extends App {

  val executorService = java.util.concurrent.Executors.newCachedThreadPool
  implicit val executionContext: ExecutionContext =
    ExecutionContext.fromExecutor(executorService)

  val clientSocketServer = new ClientSocketServer(9099)
  clientSocketServer.start

  private val eventDistributer = new EventDistributer()
  val eventSocketServer = new EventSocketServer(9090, eventDistributer)
  eventSocketServer.start

  println("Please press enter to shutdown the system!")

  StdIn.readLine()
  clientSocketServer.shutdown
  eventSocketServer.shutdown
  executorService.shutdown()
}
