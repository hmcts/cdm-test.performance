package uk.gov.hmcts.ccd.corecasedata.simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

import scala.concurrent.duration._

class CCDCMCSimulation extends Simulation {

  val BaseURL = Environment.baseURL

  val httpProtocol = Environment.HttpProtocol
  .baseUrl(BaseURL)
  .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
  .doNotTrackHeader("1")

  val CCDCMCScenario = scenario("CCDCMC").repeat(1)
  {
    exec(
      Browse.Homepage,
      //CMC.setJurisdiction,
      //CMC.setCaseType,
      //CMC.CMCLogin,
      CMC.CMCCreateCase,
      CMC.CMCSubmitPayment,
      CMC.CMCSearchAndView,
      Logout.ccdLogout
      //WaitforNextIteration.waitforNextIteration
    )
  }

    //setUp(CCDCMCScenario.inject(atOnceUsers(1))).protocols(httpProtocol)
    setUp(CCDCMCScenario
    .inject(rampUsers(1) during (1 minutes))
    .protocols(httpProtocol))
    //.maxDuration(1 minutes)
}

