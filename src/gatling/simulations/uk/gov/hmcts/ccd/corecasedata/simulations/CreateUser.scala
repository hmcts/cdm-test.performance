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

  val CreateIdam = scenario("CCDCreate")
      .repeat(1) {
        exec(CreateUser.IdamAdminLogin)
        .exec(CreateUser.IdamUser)
        .repeat(13) {
          exec(CreateUser.GetAndApplyRole)
        }
      }
      //CreateUser.DeleteUser
      //CreateUser.CreateUserProfile //This requests sets the user profile in CCD, which controls what jurisdictions are visible to the user

  setUp(
    CreateIdam.inject(rampUsers(349) during (30 minutes)))
    .protocols(httpProtocol)
  //.maxDuration(1 minutes)
}