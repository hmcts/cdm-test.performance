package uk.gov.hmcts.ccd.corecasedata.simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import uk.gov.hmcts.ccd.corecasedata.scenarios._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

import scala.concurrent.duration._

class CCDUIPTSimulation extends Simulation  {

  val BaseURL = Environment.baseURL

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    //.proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
    .doNotTrackHeader("1")

  val CCDUIScenario = scenario("CCDUI").repeat(1000)
  {
    exec(
      Browse.Homepage,
      ExecuteLogin.submitLogin,
      CreateCaseObj.selectJurisdiction,
      CreateCaseObj.startNewCaseCreation,
      Validate.validateFirstPage,
      Validate.validateSecondPage,
      Validate.validateThirdPage,
      SubmitCase.checkyouranswersNSubmit,
      Search.searchRequest,
      Search.searchResult,
      SelectCase.selectAndViewCase,
<<<<<<< Updated upstream
      Logout.ccdLogout,
      WaitforNextIteration.waitforNextIteration
     )
  }

  //setUp(CCDUIScenario.inject(atOnceUsers(1))).protocols(httpProtocol)
  setUp(CCDUIScenario
    .inject(rampUsers(1000) during (20 minutes))
    .protocols(httpProtocol))
    .maxDuration(80 minutes)
}
=======
      Logout.ccdLogout)
  }

  val CCDProbateScenario = scenario("CCDPB").repeat(1) {
    exec(
      Browse.Homepage,
      ProbateSearch.ProbateLogin,
      PBGoR.PBCreateCase,
      PBGoR.PBPaymentSuccessful,
      PBGoR.PBDocUpload,
      PBGoR.PBSearchAndView,
      Logout.ccdLogout)
  }

  val CCDSSCSScenario = scenario("CCDSSCS").repeat(1)
  {
    exec(
      Browse.Homepage,
      SSCS.SSCSLogin,
      SSCS.SSCSCreateCase,
      SSCS.PrintCaseID,
      SSCS.SSCSDocUpload,
      SSCS.SSCSSearchAndView,
      Logout.ccdLogout)
  }

  setUp(
    CCDUIScenario.inject(rampUsers(1) during (1 minutes)),
    CCDProbateScenario.inject(rampUsers(1) during (1 minutes)),
    CCDSSCSScenario.inject(rampUsers(1) during (1 minutes)))
    .protocols(httpProtocol)
    //.maxDuration(1 minutes)
}
>>>>>>> Stashed changes
