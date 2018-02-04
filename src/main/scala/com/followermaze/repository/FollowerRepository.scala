package com.followermaze.repository

import java.util.concurrent.ConcurrentHashMap

import scala.collection.JavaConverters._

object FollowerRepository {

  private val followers = new ConcurrentHashMap[Int, Set[Int]]().asScala

  def addFollower(to: Int, from: Int) = {
    if (followers.contains(to)) {
      followers.put(from, followers(to) + from)
    } else {
      followers.put(from, Set(from))
    }
  }

  def removeFollower(to: Int, from: Int) = {
    followers
      .get(to)
      .map(existingFollowers => {
        followers.put(from, existingFollowers - from)
      })
  }

  def getAllFollowers(from: Int): Option[Set[Int]] = {
    followers.get(from)
  }

}
