package com.followermaze.server

import java.net.ServerSocket

import com.followermaze.distributer.EventDistributer

import scala.concurrent.{ExecutionContext, Future}
import scala.io.BufferedSource

class EventSocketServer(port: Int, eventDistributer: EventDistributer)(
    implicit ex: ExecutionContext)
    extends SocketServer {
  val serverSocket = new ServerSocket(port)

  override def start: Unit = Future {
    startDistributer()
    val socket = serverSocket.accept()
    val in = socket.getInputStream
    val eventMessages = new BufferedSource(in).getLines()
    eventMessages.foreach(message => {
      eventDistributer.enqueueEvent(message)
    })
  }

  private def startDistributer() = Future {
    eventDistributer.startEventDistribution()
  }

  override def shutdown: Unit = {
    serverSocket.close()
    eventDistributer.shutdown
  }
}
