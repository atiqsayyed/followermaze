package com.followermaze.server

import java.io.DataOutputStream
import java.net.Socket

import com.followermaze.repository.SubscriberRepository
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

import scala.concurrent.ExecutionContext.Implicits.global

class ClientSocketServerTest
    extends WordSpec
    with Matchers
    with BeforeAndAfterAll {
  val port = 8888
  val clientSocketServer = new ClientSocketServer(port)
  val serverSocket = new Socket("127.0.0.1", port)
  val outputStream1 = new DataOutputStream(serverSocket.getOutputStream)

  clientSocketServer.start

  "Client Socket Server" should {
    "accept multiple connections and save it as subscribers" in {
      val delimiter = "\r\n"
      outputStream1.writeBytes(s"1$delimiter")
      outputStream1.flush()

      Thread.sleep(500)
      SubscriberRepository.getSubscriber(1).map(_.id) shouldBe Some(1)
    }
  }

  override def afterAll(): Unit = {
    clientSocketServer.shutdown
    serverSocket.close()
    outputStream1.close()
  }

}
