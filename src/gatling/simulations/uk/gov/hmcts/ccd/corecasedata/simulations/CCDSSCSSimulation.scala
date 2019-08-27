package uk.gov.hmcts.ccd.corecasedata.simulations

import scala.concurrent.duration._
import uk.gov.hmcts.ccd.corecasedata.scenarios._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.jdbc.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

class CCDSSCSSimulation extends Simulation {

  val BaseURL = Environment.baseURL

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
    .doNotTrackHeader("1")

  val CCDSSCSScenario = scenario("CCDUI").repeat(1)
  {
    exec(
      Browse.Homepage,
      ExecuteLogin.submitLogin,
      //ProbateSearch.ProbateLogin,
      SSCS.SSCSCreateCase,
      //PBGoR.PrintCaseID,
     // Logout.ccdLogout
    )
    //WaitforNextIteration.waitforNextIteration)
  }

  //setUp(CCDProbateScenario.inject(atOnceUsers(1))).protocols(httpProtocol)
  setUp(CCDSSCSScenario
    .inject(rampUsers(1) during (1 minutes))
    .protocols(httpProtocol))
    .maxDuration(1 minutes)

}
