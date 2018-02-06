package com.followermaze

import com.followermaze.distributer.EventDistributer
import com.followermaze.server.{ClientSocketServer, EventSocketServer}

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object Boot extends App {

  private val executorService = java.util.concurrent.Executors.newCachedThreadPool
  private implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutor(executorService)
  private val clientPort = 9099
  private val eventsPort = 9090
  private val clientSocketServer = new ClientSocketServer(clientPort)
  private val eventDistributer = new EventDistributer()
  private val eventSocketServer = new EventSocketServer(eventsPort, eventDistributer)
  clientSocketServer.start
  eventSocketServer.start

  println("** Follower maze started, press enter to stop! **")

  StdIn.readLine()
  clientSocketServer.shutdown
  eventSocketServer.shutdown
  executorService.shutdown()
}
