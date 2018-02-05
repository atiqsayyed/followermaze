package com.followermaze.repository

import org.scalatest.{Matchers, WordSpec}

class FollowerRepositoryTest extends WordSpec with Matchers {

  "Adding Follower" should {
    "create new entry if doesn't exists" in {
      FollowerRepository.addFollower(1, 1)

      FollowerRepository.getAllFollowers(1) shouldBe Set(1)
    }

    "should append to existing followers if entry exists" in {
      FollowerRepository.addFollower(1, 1)
      FollowerRepository.addFollower(1, 2)
      FollowerRepository.addFollower(1, 3)

      FollowerRepository.getAllFollowers(1) shouldBe Set(1, 2, 3)
    }

  }

  "Removing Follower" should {
    "remove follower from existing list of followers" in {
      FollowerRepository.addFollower(1, 1)
      FollowerRepository.addFollower(1, 2)
      FollowerRepository.addFollower(1, 3)

      FollowerRepository.removeFollower(1, 2)
      FollowerRepository.getAllFollowers(1) shouldBe Set(1, 3)
    }

    "remove follower entry if only one entry is present" in {
      FollowerRepository.addFollower(2, 2)

      FollowerRepository.removeFollower(2, 2)
      FollowerRepository.getAllFollowers(2) shouldBe Set.empty
    }
  }

  "get all followers should return all followers for specific id" in {
    FollowerRepository.addFollower(4, 8)
    FollowerRepository.addFollower(5, 9)
    FollowerRepository.addFollower(5, 10)

    FollowerRepository.getAllFollowers(5) shouldBe Set(9, 10)
  }

}
