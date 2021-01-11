package uk.gov.hmcts.ccd.corecasedata.simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import com.typesafe.config.{Config, ConfigFactory}
//import io.gatling.http.Predef._ //comment out for VM runs, only required for proxy
import uk.gov.hmcts.ccd.corecasedata.scenarios._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._
import scala.concurrent.duration._

class CCDUIPTSimulation extends Simulation  {

  val config: Config = ConfigFactory.load()
  val BaseURL = Environment.baseURL
  val PBiteration = 7 //7
  val SSCSiteration = 14 //14
  val CMCiteration = 8 //8
  val Diviteration = 8 //8
  val Fpliteration = 1 //10
  val Ethositeration = 26 //26
  val LFUiteration = 10 //8
  val csIterationLarge = 200
  val csIterationSmall = 200

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    //.proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080)) //Comment out for VM runs
    .doNotTrackHeader("1")

  val CCDProbateScenario = scenario("CCDPB")
    .repeat(1) {
      exec(Browse.Homepage)
      .exec(PBGoR.submitLogin)
      .repeat(PBiteration) {
        exec(PBGoR.PBCreateCase)
        .exec(PBGoR.PBPaymentSuccessful)
        .exec(PBGoR.PBDocUpload)
        .exec(PBGoR.PBStopCase)
        .exec(PBGoR.PBSearch)
        .exec(PBGoR.PBView)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
      .exec(Logout.ccdLogout)
  }

  val CCDSSCSScenario = scenario("CCDSSCS")
    .repeat(1) {
     exec(Browse.Homepage)
      .exec(SSCS.SSCSLogin)
      .repeat(SSCSiteration) {
        exec(SSCS.SSCSCreateCase)
        .exec(SSCS.SSCSDocUpload)
        .exec(SSCS.SSCSSearchAndView)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
      .exec(Logout.ccdLogout)
    }

  val CCDCMCScenario = scenario("CCDCMC")
    .repeat(1) {
      exec(Browse.Homepage)
      .exec(CMC.CMCLogin)
      .repeat(CMCiteration) {
        exec(CMC.CMCCreateCase)
        .exec(CMC.CMCStayCase)
        .exec(CMC.CMCWaitingTransfer)
        //.exec(CMC.CMCTransfer)
        .exec(CMC.CMCAttachScannedDocs)
        .exec(CMC.CMCSupportUpdate)
        .exec(CMC.CMCSearchAndView)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
      .exec(Logout.ccdLogout)
  }

  val CCDDivScenario = scenario("CCDDIV")
    .repeat(1) {
      exec(Browse.Homepage)
        .exec(DVExcep.submitLogin)
        .repeat(Diviteration) {
          exec(DVExcep.DVCreateCase)
          .exec(DVExcep.DVDocUpload)
          .exec(DVExcep.DVSearch)
          .exec(DVExcep.DVView)
          .exec(WaitforNextIteration.waitforNextIteration)
        }
        .exec(Logout.ccdLogout)
    }

  val CCDFPLScenario = scenario("CCDFPL")
    .repeat(1) {
      exec(Browse.Homepage)
        .exec(FPL.FPLLogin)
        .repeat(Fpliteration) {
          exec(FPL.FPLCreateCase)
          .exec(FPL.FPLDocumentUpload)
          .exec(WaitforNextIteration.waitforNextIteration)
        }
        .exec(Logout.ccdLogout)
    }

  val CCDEthosScenario = scenario("CCDEthos")
    .repeat(1) {
      exec(Browse.Homepage)
      .exec(EthosSearchView.submitLogin)
      .repeat(Ethositeration) {
        exec(EthosSearchView.Search)
        .exec(EthosSearchView.OpenCase)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
      .exec(Logout.ccdLogout)
  }

  val CCDLargeFileUpload = scenario("CCDLFU")
      .repeat(1) {
        exec(Browse.Homepage)
        .exec(LargeFileUpload.LFULogin)
        .repeat(LFUiteration) {
          exec(LargeFileUpload.LFU_CreateCase)
          .exec(LargeFileUpload.LFUDocUpload)
          .exec(LargeFileUpload.LFUSearchAndView)
          .exec(WaitforNextIteration.waitforNextIteration)
          }
          .exec(Logout.ccdLogout)
      }

  val UserProfileSearch = scenario("CCDUP")
      .repeat(20) {
        exec(GetUserProfile.SearchJurisdiction)
        .exec(GetUserProfile.SearchAllUsers)
        .exec(WaitforNextIteration.waitforNextIteration)
      }

  val ProbateSearch = scenario("Probate CW Search")
      .repeat(1) {
        exec(Browse.Homepage)
        .exec(PBGoR.submitLogin)
        .repeat(Ethositeration) {
          exec(PBGoR.PBSearch)
          .exec(WaitforNextIteration.waitforNextIteration)
        }
      }

  val DivorceSearch = scenario("Divorce CW Search")
      .repeat(1) {
        exec(Browse.Homepage)
        .exec(DVExcep.submitLogin)
        .repeat(Ethositeration) {
          exec(DVExcep.DVSearch)
          .exec(WaitforNextIteration.waitforNextIteration)
        }
      }


  val XUIAdminScn = scenario("XUI Admin Org Login")
    .repeat(1){
      exec(ExuiView.XUIAdminOrg)
    }

  //CCD Regression UI Scenario
  setUp(
    //These 5 scenarios required for CCD regression testing
    CCDProbateScenario.inject(rampUsers(15) during (20 minutes)), //150
    CCDSSCSScenario.inject(rampUsers(15) during (20 minutes)), //150
    CCDEthosScenario.inject(rampUsers(40) during (20 minutes)), //400
    CCDCMCScenario.inject(rampUsers(15) during (20 minutes)), //150
    CCDDivScenario.inject(rampUsers(15) during (20 minutes)) //150

    // CCDProbateScenario.inject(rampUsers(1) during (20 minutes)), //150
  )
    .protocols(httpProtocol)
    //.maxDuration(60 minutes)
}