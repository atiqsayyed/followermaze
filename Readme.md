# Follower Maze

## Design

The first thing that came to my mind was using Akka Streams.
It's easier to think and manage complex asynchronous processing as with **Akka Streams** using the basic building blocks called **Source**, **Flow** and **Sink**.
The event would flow from Source to Sink via Flow (which would contain your transformation or business logic) working like pipelines where each components could've been reused. It works really well with Akka Actors which gives you ability to handle mutable state and fault tolerance(which is very important when dealing with Network)

However I kept things very basic and ended up using plain Scala code to avoid using the abstraction layer provided by Akka Streams and Actors and implement everything by myself. The implementation is not purely immutable, I had to use mutable HashMaps to maintain the internal states(rather i would've used message passing between Actors for this).

## Components

![enter image description here](https://image.ibb.co/iwCW5c/internal_components_flow.png)

**Boot**

This is the Main file which initiates the Client and Event Socket servers and starts them to consume events on respective ports

**Client Socket Server**

It listens to incoming messages on Client Port and saves the **ClientId and OutputStream** in Subscriber Cache which will later be used by **EventDistributer** for sending relevant events on relevant OutputStream.

**Event Socket Server**

It listens to incoming events on Event Port, it consumes all the **valid events** and drops all the invalid events. The consumed events are then pushed to **EventQueue** in the order EventSocketServer received.

**Event Distributer**

The **EventDistributer** continuously polls the **EventQueue** and collects the unordered events and gives it to **PriorityQueue**.
The elements are picked from PriorityQueue based on the sequence, it will not process the element till the next element in sequence has arrived in PriorityQueue.
The elements are then distributed based on their type. **EventDistributer** requests the Subscriber(if available) and sends the event notification to the **Clients** connected to the OutputStream.

The reason why we have two queues is to separate the Event Collection and Event Distribution.

## Stress Testing

| Total Events       | No of users| concurrencyLevel         | maxEventSourceBatchSize | Time Taken (MM:ss) |
|---------------|-----------|--------------|--------------|------------|
|100000 | 1000(default)       | 100 (default)       | 100 (default)           | 00:09
|10000000 | 1000(default)       | 100 (default)       | 100 (default)           | 10:45
|50000000 | 1000(default)       | 100 (default)       | 100 (default)           |26:57
|100000000 | 1000(default)       | 100 (default)       | 100 (default)           |50:40
|100000 | 5000       | 100 (default)       | 100 (default)           | 25:14
|100000 | 10000       | 100 (default)       | 100 (default)           | 66:42



## Running

In order to run the FollowerMaze app, all you have to do is run the **Boot** file in the src folder.

> sbt runMain com.followermaze.Boot

## Running Tests

I've used scalatests for test cases, to run the test cases you can say

> sbt test

## Future Improvements

 - Would consider using Streams & Actors for better handling of events, mutable states, backpressure and fault tolerance.
 - Measuring GC and other areas and improving performance if there's already room for it.
 - Can introduce some priority between different type of events and these events would then be published on separate channels, and we can merge different events based on priority in ratio
>  for example:
>  Priority Order ( Follow > Status > Broadcast ), we can merge these event in ratio of 10:4:1
>  this will help us to process more priority events faster as compared to other events given the existing capacity of the system.
 - Currently there's no logging in the application, because of which it'll be very difficult to debug and understand what's happening within the system