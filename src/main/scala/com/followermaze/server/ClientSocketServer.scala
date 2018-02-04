package com.followermaze.server

import java.io.{BufferedReader, InputStreamReader}
import java.net.ServerSocket

import com.followermaze.entity.Client
import com.followermaze.repository.EventSubscriberRepository

import scala.concurrent.{ExecutionContext, Future}

class ClientSocketServer(port: Int)(implicit ex: ExecutionContext)
    extends SocketServer {
  val serverSocket = new ServerSocket(port)
  override def start: Unit = Future {
    while (true) {
      val socket = serverSocket.accept()
      val in = new BufferedReader(new InputStreamReader(socket.getInputStream))
      val clientId = in.readLine().toInt
      val client = Client(clientId, socket.getOutputStream)
      Future(client)
      EventSubscriberRepository.subscribe(clientId, client)
    }
  }

  override def shutdown: Unit = {
    serverSocket.close()
  }
}
