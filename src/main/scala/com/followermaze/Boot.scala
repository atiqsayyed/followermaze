package com.followermaze

import com.followermaze.server.{ClientSocketServer, EventSocketServer}

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object Boot extends App {

  implicit val executionContext: ExecutionContext =
    ExecutionContext.fromExecutor(
      java.util.concurrent.Executors.newCachedThreadPool)

  val clientSocketServer = new ClientSocketServer(9099)
  clientSocketServer.start

  val eventSocketServer = new EventSocketServer(9090)
  eventSocketServer.start

  StdIn.readLine()

}
