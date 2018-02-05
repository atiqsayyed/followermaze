package com.followermaze.repository

import java.util.concurrent.ConcurrentHashMap

import scala.collection.JavaConverters._

object FollowerRepository {

  private val followers = new ConcurrentHashMap[Int, Set[Int]]().asScala

  def addFollower(to: Int, from: Int) = {
    followers.get(to) match {
      case Some(value) => {
        followers.put(to, value + from)
      }
      case None => {
        followers.put(to, Set(from))
      }
    }
  }

  def removeFollower(to: Int, from: Int) = {
    followers.get(to) match {
      case Some(value) => {
        followers.put(to, value - from)
      }
      case None => {
        followers.remove(to)
      }
    }
  }

  def getAllFollowers(from: Int): Set[Int] = {
    followers.getOrElse(from, Set.empty)
  }

}
