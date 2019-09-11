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
    //.proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080)) //This import is required for Proxy to work, IntelliJ sometimes removes it: import io.gatling.http.Predef._
    .doNotTrackHeader("1")

  //This val controls the load profile PER JURISDICTION; if you set it to 100 users then it will run 500 users as there are 5 jurisdictions
  private val loadProfile = rampUsers(1) during (1 minutes)

  //This val controls how many times each jurisdiction journey repeats
  private val repeatValue = 1

  val CCDUIScenario = scenario("CCDUI").repeat(repeatValue)
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

  val CCDProbateScenario = scenario("CCDPB").repeat(repeatValue)
  {
    exec(
      Browse.Homepage,
      ExecuteLogin.submitLogin,
      PBGoR.PBCreateCase,
      PBGoR.PBPaymentSuccessful,
      PBGoR.PBDocUpload,
      PBGoR.PBSearchAndView,
      Logout.ccdLogout,
      WaitforNextIteration.waitforNextIteration
    )
  }

  val CCDSSCSScenario = scenario("CCDSSCS").repeat(repeatValue)
  {
    exec(
      Browse.Homepage,
      ExecuteLogin.submitLogin,
      SSCS.SSCSCreateCase,
      SSCS.PrintCaseID,
      SSCS.SSCSDocUpload,
      SSCS.SSCSSearchAndView,
      Logout.ccdLogout,
      WaitforNextIteration.waitforNextIteration
    )
  }

  val CCDCMCScenario = scenario("CCDCMC").repeat(repeatValue)
  {
    exec(
      Browse.Homepage,
      CMC.setJurisdiction,
      CMC.setCaseType,
      ExecuteLogin.submitLogin,
      CMC.CMCCreateCase,
      CMC.CMCSubmitPayment,
      CMC.CMCSearchAndView,
      Logout.ccdLogout,
      WaitforNextIteration.waitforNextIteration
    )
  }

  val CCDDivScenario = scenario("CCDDIV").repeat(repeatValue)
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

  setUp(
    CCDUIScenario.inject(loadProfile),
    CCDProbateScenario.inject(loadProfile),
    CCDSSCSScenario.inject(loadProfile),
    CCDCMCScenario.inject(loadProfile),
    CCDDivScenario.inject(loadProfile)
  )
    .protocols(httpProtocol)
    //.maxDuration(80 minutes) //Enable this if you want to run for 1 hour and filter out the ramp up (usually 20 mins)
}