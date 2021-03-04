package uk.gov.hmcts.ccd.corecasedata.simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef.Proxy
import uk.gov.hmcts.ccd.corecasedata.scenarios.CreateUser
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._
import scala.concurrent.duration._

class CreateUser extends Simulation  {

  val BaseURL = Environment.baseURL

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
    .doNotTrackHeader("1")

  val GrantRole = scenario("Grant idam role")
    .repeat(1) {
      exec(CreateUser.IdamAdminLogin)
      .repeat(1) {
        exec(CreateUser.IdamUser)
      }
      .repeat(1) {
        exec(CreateUser.GetAndApplyRole)
      }
    }

  val DeleteRole = scenario("Remove idam roles")
    .repeat(1) {
      exec(CreateUser.IdamAdminLogin)
      .repeat(1) {
        exec(CreateUser.IdamUser)
      }
      .repeat(1) {
        exec(CreateUser.GetAndRemoveRole)
      }
    }

  setUp(
    GrantRole.inject(rampUsers(1) during (30 minutes)))
    .protocols(httpProtocol)
}