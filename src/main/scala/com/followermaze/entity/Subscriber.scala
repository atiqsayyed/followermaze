package com.followermaze.entity

import java.io.{BufferedWriter, OutputStream, OutputStreamWriter, PrintWriter}
import java.nio.charset.Charset

import scala.util.control.NonFatal

class Subscriber(id: Int, connectionStream: OutputStream) {
  private val outputStreamWriter = new PrintWriter(
    new BufferedWriter(
      new OutputStreamWriter(connectionStream, Charset forName "UTF-8")))

  def notify(event: Event): Unit = {
    try {
      outputStreamWriter.print(event.toString)
      outputStreamWriter.print("\r\n")
      outputStreamWriter.flush()
    } catch {
      case NonFatal(e) => e.printStackTrace()
    }
  }

  def shutdown(): Unit = {
    outputStreamWriter.close()
  }
}
