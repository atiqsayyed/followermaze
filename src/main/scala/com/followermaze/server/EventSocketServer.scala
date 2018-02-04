package com.followermaze.server

import java.io.{BufferedReader, InputStreamReader}
import java.net.ServerSocket

import scala.concurrent.{ExecutionContext, Future}

class EventSocketServer(port: Int)(implicit ex: ExecutionContext) extends SocketServer {
  val serverSocket = new ServerSocket(port)
  override def start: Unit = Future {
    val socket = serverSocket.accept()
    val in = new BufferedReader(new InputStreamReader(socket.getInputStream))
    in.readLine()
  }

  override def shutdown: Unit = ???
}
