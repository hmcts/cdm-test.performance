package uk.gov.hmcts.ccd.corecasedata.simulations

import scala.concurrent.duration._
import uk.gov.hmcts.ccd.corecasedata.scenarios._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.jdbc.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

class CCDUIPTSimulation extends Simulation  {

  val BaseURL = Environment.baseURL

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    //.proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
    .doNotTrackHeader("1")

  val CCDUIScenario = scenario("CCDUI").repeat(1000)
  {
    exec(
      //Logout.ccdLogout,
      Browse.Homepage,
      ExecuteLogin.submitLogin,
      CreateCaseObj.selectJurisdiction,
      CreateCaseObj.startNewCaseCreation,
      Validate.validateFirstPage,
      Validate.validateSecondPage,
      Validate.validateThirdPage,
      SubmitCase.checkyouranswersNSubmit,

      //Different search types required
      Search.searchRequest,
      Search.searchResult,
      SelectCase.selectAndViewCase,
      //Upload & Download document steps
      /*Search.searchRequest,
       Search.searchResult,
       SelectCase.selectAndViewCase,*/
      Logout.ccdLogout,
      WaitforNextIteration.waitforNextIteration)
  }

  //setUp(CCDUIScenario.inject(atOnceUsers(1))).protocols(httpProtocol)
  setUp(CCDUIScenario
    .inject(rampUsers(1) during (1 minutes))
    .protocols(httpProtocol))
    .maxDuration(5 minutes)
}