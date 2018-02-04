package com.followermaze.server

trait SocketServer {
  def start: Unit
  def shutdown: Unit
}
