package uk.gov.hmcts.ccd.corecasedata.simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import uk.gov.hmcts.ccd.corecasedata.scenarios._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._

import scala.concurrent.duration._

class CCDUIPTSimulation extends Simulation  {

  val BaseURL = Environment.baseURL

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    //.proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
    .doNotTrackHeader("1")

  val CCDUIScenario = scenario("CCDUI").repeat(50)
  {
    exec(
      Browse.Homepage,
      ExecuteLogin.submitLogin,
      CreateCaseObj.setJurisdiction,
      CreateCaseObj.setCaseType,
      CreateCaseObj.selectJurisdiction,
      CreateCaseObj.startNewCaseCreation,
      Validate.validateFirstPage,
      Validate.validateSecondPage,
      Validate.validateThirdPage,
      SubmitCase.checkyouranswersNSubmit,
      Search.searchRequest,
      Search.searchResult,
      SelectCase.selectAndViewCase,
      Logout.ccdLogout,
      WaitforNextIteration.waitforNextIteration
    )
  }

  val CCDProbateScenario = scenario("CCDPB").repeat(1) {
    exec(
      Browse.Homepage,
      ExecuteLogin.submitLogin,
      //ProbateSearch.ProbateLogin,
      PBGoR.PBCreateCase,
      PBGoR.PBPaymentSuccessful,
      PBGoR.PBDocUpload,
      PBGoR.PBSearchAndView,
      Logout.ccdLogout
      //WaitforNextIteration.waitforNextIteration
    )
  }

  val CCDSSCSScenario = scenario("CCDSSCS").repeat(1)
  {
    exec(
      Browse.Homepage,
      ExecuteLogin.submitLogin,
      //SSCS.SSCSLogin,
      SSCS.SSCSCreateCase,
      SSCS.PrintCaseID,
      SSCS.SSCSDocUpload,
      SSCS.SSCSSearchAndView,
      Logout.ccdLogout
      //WaitforNextIteration.waitforNextIteration
    )
  }

  val CCDCMCScenario = scenario("CCDCMC").repeat(1)
  {
    exec(
      Browse.Homepage,
      CMC.setJurisdiction,
      CMC.setCaseType,
      ExecuteLogin.submitLogin,
      //CMC.CMCLogin,
      CMC.CMCCreateCase,
      CMC.CMCSubmitPayment,
      CMC.CMCSearchAndView,
      Logout.ccdLogout
      //WaitforNextIteration.waitforNextIteration
    )
  }

  val CCDDivScenario = scenario("CCDDIV").repeat(1)
  {
    exec(
      Browse.Homepage,
      ExecuteLogin.submitLogin,
      DVExcep.DVCreateCase,
      DVExcep.DVDocUpload,
      DVExcep.DVSearchAndView,
      Logout.ccdLogout,
      //WaitforNextIteration.waitforNextIteration
    )
  }

  val CCDEthosScenario = scenario("CCDEthos").repeat(50)
  {
    exec(
      Browse.Homepage,
      EthosSearchView.submitLogin,
      EthosSearchView.Search,
      EthosSearchView.OpenCase,
      Logout.ccdLogout,
      WaitforNextIteration.waitforNextIteration
    )
  }

  setUp(
      CCDUIScenario.inject(rampUsers(100) during (10 minutes)),
//    CCDProbateScenario.inject(rampUsers(1) during (10 minutes)),
//    CCDSSCSScenario.inject(rampUsers(1) during (10 minutes)),
      CCDEthosScenario.inject(rampUsers(50) during (10 minutes)),
//    CCDCMCScenario.inject(rampUsers(1) during (10 minutes)),
//    CCDDivScenario.inject(rampUsers(1) during (10 minutes))
  )
    .protocols(httpProtocol)
    .maxDuration(20 minutes)
}