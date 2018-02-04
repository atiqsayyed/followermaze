package com.followermaze.entity

import java.io.{BufferedWriter, OutputStream, OutputStreamWriter, PrintWriter}
import java.nio.charset.Charset
import java.util.concurrent.LinkedBlockingQueue

import scala.util.control.NonFatal

class Subscriber(id: Int, connectionStream: OutputStream) {
  private val outputStreamWriter = new PrintWriter(
    new BufferedWriter(
      new OutputStreamWriter(connectionStream, Charset forName "UTF-8")))

  val incomingEventQueue = new LinkedBlockingQueue[Event]()

//  def enqueueEvent(event: Event) = {
//    incomingEventQueue.add(event)
//  }

  def sendMessage(event: Event): Unit = {
    try {
      println(s"sending event: $event")
      outputStreamWriter.print(event.toString)
      outputStreamWriter.print("\r\n")
      outputStreamWriter.flush()
    } catch {
      case NonFatal(e) => e.printStackTrace()
    }
  }

//  @tailrec
//  final def poll():Unit = {
//    val event = incomingEventQueue.take()
//    sendMessage(event)
//    poll()
//  }

  def shutdown(): Unit = {
    outputStreamWriter.close()
  }
}
