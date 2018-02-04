package com.followermaze.server

import java.io.{BufferedReader, InputStreamReader}
import java.net.ServerSocket

import com.followermaze.entity.Subscriber
import com.followermaze.repository.SubscriberRepository

import scala.concurrent.{ExecutionContext, Future}

class ClientSocketServer(port: Int)(implicit ex: ExecutionContext)
    extends SocketServer {
  val serverSocket = new ServerSocket(port)
  override def start: Unit = Future {
    while (true) {
      val socket = serverSocket.accept()
      val in = new BufferedReader(new InputStreamReader(socket.getInputStream))
      val clientId = in.readLine().toInt
      val subscriber = new Subscriber(clientId, socket.getOutputStream)
      SubscriberRepository.subscribe(clientId, subscriber)
    }
  }

  override def shutdown: Unit = {
    serverSocket.close()
  }
}
