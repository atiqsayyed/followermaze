package com.followermaze.server

import java.io.DataOutputStream
import java.net.Socket

import com.followermaze.distributer.EventDistributer
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

import scala.concurrent.ExecutionContext.Implicits.global

class EventSocketServerTest
    extends WordSpec
    with Matchers
    with MockitoSugar
    with BeforeAndAfterAll {
  val eventDistributer = mock[EventDistributer]
  when(eventDistributer.enqueueEvent(anyString())).thenReturn(Some(true))
  val eventSockerServer = new EventSocketServer(8888, eventDistributer)
  eventSockerServer.start

  "Event Socket server" should {
    "publish every event received from socket input stream" in {
      val eventData =
        List("666|F|60|50", "1|U|12|9", "542532|B", "43|P|32|56", "634|S|32")
      val serverSocket = new Socket("127.0.0.1", 8888)
      val outputStream = new DataOutputStream(serverSocket.getOutputStream)
      val delimiter = "\r\n"

      eventData.foreach(data => {
        outputStream.writeBytes(data + delimiter)
      })
      outputStream.flush()
      outputStream.close()
      Thread.sleep(500)

      verify(eventDistributer, times(5)).enqueueEvent(anyString())
    }
  }

  override def afterAll(): Unit = {
    eventSockerServer.shutdown
  }

}
