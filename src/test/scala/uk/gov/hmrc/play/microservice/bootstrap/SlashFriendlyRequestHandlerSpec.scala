/*
 * Copyright 2016 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.play.microservice.bootstrap

import java.security.cert.X509Certificate

import org.mockito.ArgumentMatcher
import org.scalatest.{OneInstancePerTest, WordSpec, MustMatchers}
import org.scalatest.mock.MockitoSugar
import play.api.http.{HttpConfiguration, HttpErrorHandler}
import play.api.mvc.{Headers, Handler, RequestHeader}
import play.api.routing.Router
import org.mockito.Mockito._
import org.mockito.Matchers._

class SlashFriendlyRequestHandlerSpec extends WordSpec with MustMatchers with MockitoSugar with OneInstancePerTest {
  val router = mock[Router]
  val errorHandler = mock[HttpErrorHandler]
  val configuration = HttpConfiguration()
  val handler = mock[Handler]

  val requestHandler = new SlashFriendlyRequestHandler(router, errorHandler, configuration, Seq() :_*)

  val requestWithSlash = mockHeader("/foo/bar/")
  val requestWithoutSlash = mockHeader("/foo/bar")

  "SlashFriendlyRequestHandler" should {
    "not touch a request which is successful and doesn't end in a slash" in {
      when(router.handlerFor(requestWithSlash)).thenReturn(Some(handler))
      requestHandler.routeRequest(requestWithSlash) mustBe Some(handler)
    }

    "Not touch a request which is successful and does end in a slash" in {
      when(router.handlerFor(requestWithoutSlash)).thenReturn(Some(handler))
      requestHandler.routeRequest(requestWithoutSlash) mustBe Some(handler)
    }

    "Not touch a request which fails and doesn't end in a slash" in {
      when(router.handlerFor(hasPath("/foo/bar"))).thenReturn(None)
      requestHandler.routeRequest(requestWithoutSlash) mustBe None
    }

    "try without a slash if the with slash fails, then return a success" in {
      when(router.handlerFor(hasPath("/foo/bar/"))).thenReturn(None)
      when(router.handlerFor(hasPath("/foo/bar"))).thenReturn(Some(handler))
      requestHandler.routeRequest(requestWithSlash) mustBe Some(handler)
    }

    "try without a slash if the with slash fails, then return a failure" in {
      when(router.handlerFor(hasPath("/foo/bar/"))).thenReturn(None)
      when(router.handlerFor(hasPath("/foo/bar"))).thenReturn(None)
      requestHandler.routeRequest(requestWithSlash) mustBe None
    }
  }

  def hasPath(path: String) = argThat(new ArgumentMatcher[RequestHeader]() {
    override def matches(argument: scala.Any): Boolean = {
      argument.isInstanceOf[RequestHeader] && argument.asInstanceOf[RequestHeader].path == path
    }
  })

  def mockHeader(requestPath: String) = {
    new RequestHeader {
      override val path = requestPath

      override def id: Long = 1
      override def secure: Boolean = false
      override def uri: String = null
      override def remoteAddress: String = null
      override def queryString: Map[String, Seq[String]] = null
      override def method: String = null
      override def headers: Headers = null
      override def clientCertificateChain: Option[Seq[X509Certificate]] = null
      override def version: String = null
      override def tags: Map[String, String] = null
    }
  }
}
