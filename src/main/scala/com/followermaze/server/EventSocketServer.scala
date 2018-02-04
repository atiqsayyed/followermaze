package com.followermaze.server

import java.net.ServerSocket

import com.followermaze.publisher.EventPublisher

import scala.concurrent.{ExecutionContext, Future}
import scala.io.BufferedSource

class EventSocketServer(port: Int, eventPublisher: EventPublisher)(
    implicit ex: ExecutionContext)
    extends SocketServer {
  val serverSocket = new ServerSocket(port)

  override def start: Unit = Future {
    val socket = serverSocket.accept()
    val in = socket.getInputStream
    val eventMessages = new BufferedSource(in).getLines()
    eventMessages.foreach(message => {
      eventPublisher.publish(message)
    })
  }

  override def shutdown: Unit = {
    serverSocket.close()
    eventPublisher.shutdown
  }
}
