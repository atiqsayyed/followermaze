package com.followermaze.entity

import org.scalatest.{Matchers, WordSpec}

class EventTest extends WordSpec with Matchers {
  "For valid input" should {
    "convert string to Follow event type" in {
      Event("666|F|60|50") should be(Some(Follow(666, 60, 50)))
    }
    "convert string to UnFollow event type" in {
      Event("222|U|10|11") should be(Some(Unfollow(222, 10, 11)))
    }
    "convert string to Broadcast event type" in {
      Event("1|B") should be(Some(Broadcast(1)))
    }
    "convert string to Private event type" in {
      Event("2|P|1|2") should be(Some(Private(2, 1, 2)))
    }
    "convert string to Status event type" in {
      Event("3|S|4") should be(Some(StatusUpdate(3, 4)))
    }
  }
  "For invalid input" should {
    "convert string to Follow event type" in {
      Event("666|asdf|60|50") should be(None)
      Event("Foo") should be(None)
      Event("\n\r") should be(None)
      Event("") should be(None)
      Event("123") should be(None)
    }
  }
}
