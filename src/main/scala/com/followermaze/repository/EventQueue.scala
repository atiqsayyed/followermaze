package com.followermaze.repository

import java.util.concurrent.LinkedBlockingQueue

import com.followermaze.entity.Event

object EventQueue {

  private val queue = new LinkedBlockingQueue[Event]()

  def enqueue(event: Event): Boolean = {
    queue.add(event)
  }

  def dequeue(): Event = {
    queue.take()
  }

  def size(): Int = {
    queue.size()
  }

  def clear(): Unit = {
    queue.clear()
  }

}
