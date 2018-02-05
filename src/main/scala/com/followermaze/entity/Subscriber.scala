package com.followermaze.entity

import java.io.{BufferedWriter, OutputStream, OutputStreamWriter, PrintWriter}
import java.nio.charset.Charset

import scala.util.Try

class Subscriber(val id: Int, connectionStream: OutputStream) {
  private val outputStreamWriter = new PrintWriter(
    new BufferedWriter(
      new OutputStreamWriter(connectionStream, Charset forName "UTF-8")))

  def notify(event: Event): Try[Unit] = Try {
    outputStreamWriter.print(event.toString)
    outputStreamWriter.print("\r\n")
    outputStreamWriter.flush()
  }

  def shutdown(): Unit = {
    outputStreamWriter.close()
  }
}
