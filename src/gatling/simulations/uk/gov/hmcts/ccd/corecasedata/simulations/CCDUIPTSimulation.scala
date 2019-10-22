package uk.gov.hmcts.ccd.corecasedata.simulations

import io.gatling.core.Predef._
//import io.gatling.http.Predef._ //required for proxy, comment out for VM runs
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

  val CCDUIScenario = scenario("CCDUI").repeat(500)
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
      //WaitforNextIteration.waitforNextIteration
    )
  }

  val CCDProbateScenario = scenario("CCDPB").repeat(6) { //repeat 6 times
    exec(
      Browse.Homepage,
      ExecuteLogin.submitLogin,
      //ProbateSearch.ProbateLogin,
      PBGoR.PBCreateCase,
      PBGoR.PBPaymentSuccessful,
      PBGoR.PBDocUpload,
      PBGoR.PBSearchAndView,
      Logout.ccdLogout,
      WaitforNextIteration.waitforNextIteration
    )
  }

  val CCDSSCSScenario = scenario("CCDSSCS").repeat(7) //repeat 7 times
  {
    exec(
      Browse.Homepage,
      ExecuteLogin.submitLogin,
      //SSCS.SSCSLogin,
      SSCS.SSCSCreateCase,
      //SSCS.PrintCaseID,
      SSCS.SSCSDocUpload,
      SSCS.SSCSSearchAndView,
      Logout.ccdLogout,
      WaitforNextIteration.waitforNextIteration
    )
  }

  val CCDCMCScenario = scenario("CCDCMC").repeat(7) //repeat 7 times
  {
    exec(
      Browse.Homepage,
      //CMC.setJurisdiction,
      //CMC.setCaseType,
      ExecuteLogin.submitLogin,
      //CMC.CMCLogin,
      CMC.CMCCreateCase,
      CMC.CMCSubmitPayment,
      CMC.CMCSearchAndView,
      Logout.ccdLogout,
      WaitforNextIteration.waitforNextIteration
    )
  }

  val CCDDivScenario = scenario("CCDDIV").repeat(6) //repeat 6 times
  {
    exec(
      Browse.Homepage,
      ExecuteLogin.submitLogin,
      DVExcep.DVCreateCase,
      DVExcep.DVDocUpload,
      DVExcep.DVSearchAndView,
      Logout.ccdLogout,
      WaitforNextIteration.waitforNextIteration
    )
  }

  val CCDEthosScenario = scenario("CCDEthos").repeat(11) //repeat 11 times
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
      //CCDUIScenario.inject(rampUsers(200) during (20 minutes)),
      CCDProbateScenario.inject(rampUsers(100) during (20 minutes)),
      CCDSSCSScenario.inject(rampUsers(100) during (20 minutes)),
      CCDEthosScenario.inject(rampUsers(100) during (20 minutes)),
      CCDCMCScenario.inject(rampUsers(100) during (20 minutes)),
      CCDDivScenario.inject(rampUsers(100) during (20 minutes))
      //CCDEthosScenario.inject(rampUsers(1) during (1 minutes))
  )
    .protocols(httpProtocol)
    //.maxDuration(60 minutes)
}