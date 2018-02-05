package com.followermaze.distributer

import java.io.ByteArrayOutputStream

import com.followermaze.entity.Subscriber
import com.followermaze.repository.{EventQueue, SubscriberRepository}
import org.scalatest.{
  BeforeAndAfterAll,
  BeforeAndAfterEach,
  Matchers,
  WordSpec
}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EventDistributerTest
    extends WordSpec
    with Matchers
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  val eventDistributer = new EventDistributer
  "Event Distributer" should {
    "enqueue only the valid events passed to the distributer" in {
      val eventData =
        List("1|F|60|50", "2|U|12|9", "invalid", "3|P|32|56", "")

      eventData.foreach(data => eventDistributer.enqueueEvent(data))

      EventQueue.size() shouldBe 3
    }

    "trigger a notification to the client if it's been followed" in {
      startDistribution
      val followEvent = "1|F|60|50"
      val outputStream = new ByteArrayOutputStream()
      SubscriberRepository.addSubscriber(50, new Subscriber(50, outputStream))
      SubscriberRepository.addSubscriber(60, new Subscriber(60, outputStream))
      eventDistributer.enqueueEvent(followEvent)
      Thread.sleep(100)
      outputStream.toString.trim shouldBe followEvent
    }

    "Notify all the subscribers in case of Broadcase event" in {
      val broadcastEvent = "1|B"
      val outputStream1 = new ByteArrayOutputStream()
      val outputStream2 = new ByteArrayOutputStream()
      SubscriberRepository.addSubscriber(50, new Subscriber(50, outputStream1))
      SubscriberRepository.addSubscriber(60, new Subscriber(60, outputStream2))
      eventDistributer.enqueueEvent(broadcastEvent)
      Thread.sleep(100)
      outputStream1.toString.trim shouldBe broadcastEvent
      outputStream2.toString.trim shouldBe broadcastEvent
    }

    "Send private message from 111 to 222 and only 222 should receive this message" in {
      val outputStream1 = new ByteArrayOutputStream()
      val outputStream2 = new ByteArrayOutputStream()
      val outputStream3 = new ByteArrayOutputStream()
      SubscriberRepository.addSubscriber(111,
                                         new Subscriber(111, outputStream1))
      SubscriberRepository.addSubscriber(222,
                                         new Subscriber(222, outputStream2))
      SubscriberRepository.addSubscriber(333,
                                         new Subscriber(333, outputStream3))

      val privateMessage = "1|P|111|222"
      eventDistributer.enqueueEvent(privateMessage)
      Thread.sleep(100)

      outputStream1.toString.trim shouldBe ""
      outputStream2.toString.trim shouldBe privateMessage
      outputStream3.toString.trim shouldBe ""
    }

    "Notify only the followers of the Client in case of status update" in {
      val outputStream1 = new ByteArrayOutputStream()
      val outputStream2 = new ByteArrayOutputStream()
      val outputStream3 = new ByteArrayOutputStream()
      SubscriberRepository.addSubscriber(111,
                                         new Subscriber(111, outputStream1))
      SubscriberRepository.addSubscriber(222,
                                         new Subscriber(222, outputStream2))
      SubscriberRepository.addSubscriber(333,
                                         new Subscriber(333, outputStream3))
      val events = List("1|F|222|111", "2|F|222|333", "3|U|222|111", "4|S|222")
      events.foreach(event => eventDistributer.enqueueEvent(event))

      Thread.sleep(100)
      outputStream1.toString.trim shouldBe "1|F|222|111"
      outputStream2.toString.trim shouldBe ""
      outputStream3.toString.trim shouldBe "2|F|222|333"

    }

    "Send the events in order even if it received the events in unordered sequence" in {
      val outputStream1 = new ByteArrayOutputStream()
      val outputStream2 = new ByteArrayOutputStream()
      val outputStream3 = new ByteArrayOutputStream()
      SubscriberRepository.addSubscriber(111,
                                         new Subscriber(111, outputStream1))
      SubscriberRepository.addSubscriber(222,
                                         new Subscriber(222, outputStream2))
      SubscriberRepository.addSubscriber(333,
                                         new Subscriber(333, outputStream3))
      val outOfOrderedEvents =
        List("3|U|222|111", "1|F|222|111", "4|S|222", "2|F|222|333")
      outOfOrderedEvents.foreach(event => eventDistributer.enqueueEvent(event))

      Thread.sleep(100)
      outputStream1.toString.trim shouldBe "1|F|222|111"
      outputStream2.toString.trim shouldBe ""
      outputStream3.toString.trim shouldBe "2|F|222|333"

    }
  }

  def startDistribution(): Unit = {
    Future(eventDistributer.startEventDistribution())
  }

  override def afterAll(): Unit = {
    eventDistributer.shutdown
  }

  override def afterEach(): Unit = {
    EventQueue.clear()
  }

}
