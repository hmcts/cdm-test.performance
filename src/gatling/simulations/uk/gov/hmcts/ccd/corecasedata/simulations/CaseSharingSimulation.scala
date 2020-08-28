package uk.gov.hmcts.ccd.corecasedata.simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._
import scala.concurrent.duration._

class CaseSharingSimulation extends Simulation  {

  val BaseURL = Environment.baseURL

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080)) //Comment out for VM runs
    .doNotTrackHeader("1")

  val CaseSharing = scenario("CCDCS")
    .repeat(1) {
      exec(casesharing.CDSGetRequest)
      .exec(casesharing.CaseShareRequest)
        .repeat(1) {
          exec(casesharing.CaseSharingPUT)
        }
    }


  setUp(
    CaseSharing.inject(rampUsers(1) during(1 minutes))
  )
    .protocols(httpProtocol)
  //.maxDuration(60 minutes)
}