package com.followermaze.entity

import java.io.OutputStream

case class Client(clientId: Int, connectionStream: OutputStream)
