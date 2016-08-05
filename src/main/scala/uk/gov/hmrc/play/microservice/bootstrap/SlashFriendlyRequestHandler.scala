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

import com.google.inject.Inject
import play.api.http.{HttpConfiguration, HttpErrorHandler, DefaultHttpRequestHandler}
import play.api.mvc.{Handler, RequestHeader, EssentialFilter}
import play.api.routing.Router

@Inject
class SlashFriendlyRequestHandler (router: Router,
                                   errorHandler: HttpErrorHandler,
                                   configuration: HttpConfiguration,
                                   filters: EssentialFilter*)
  extends DefaultHttpRequestHandler(router, errorHandler, configuration, filters:_*) {

  override def routeRequest(request: RequestHeader): Option[Handler] = {
    super.routeRequest(request).orElse {
      if(request.path.endsWith("/"))
        super.routeRequest(request.copy(path = request.path.dropRight(1)))
      else
        None
    }
  }
}