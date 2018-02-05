package com.followermaze.repository

import java.io.ByteArrayOutputStream

import com.followermaze.entity.Subscriber
import org.scalatest.{Matchers, WordSpec}

class SubscriberRepositoryTest extends WordSpec with Matchers {

  "Subscriber Repository" should {
    "replace existing subscriber if it exists" in {
      val subscriber1 = new Subscriber(1, new ByteArrayOutputStream())
      val subscriber2 = new Subscriber(2, new ByteArrayOutputStream())
      SubscriberRepository.addSubscriber(1, subscriber1)
      SubscriberRepository.addSubscriber(1, subscriber2)

      SubscriberRepository.getAllSubscribers.size shouldBe 1
      SubscriberRepository.getSubscriber(1) shouldBe Some(subscriber2)
    }

  }

}
