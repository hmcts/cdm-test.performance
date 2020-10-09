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
  val PBiteration = 70 //7
  val SSCSiteration = 140 //14
  val CMCiteration = 80 //8
  val Diviteration = 80 //8
  val Fpliteration = 10 //10
  val Ethositeration = 260 //26
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
        .exec(PBGoR.PBSearchAndView)
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
          .exec(DVExcep.DVSearchAndView)
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
        //.exec(WaitforNextIteration.waitforNextIteration)
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

  /*val CaseSharingLarge = scenario("CCDCSLarge")
    .repeat(1) {
      repeat(1) {
        exec(casesharing.CDSGetRequestLarge) //this step only required once per user to generate the token for case creation
        .exec(casesharing.CaseShareLoginLarge) //this step only required once per user to generate the token for case sharing
      }
        .repeat(csIterationLarge) {
            exec(casesharing.CreateCase)
            .exec(casesharing.CaseSharingPostLarge)
        }
    }

  val CaseSharingSmall = scenario("CCDCSSmall")
    .repeat(1) {
      repeat(1) {
        exec(casesharing.CDSGetRequestSmall) //this step only required once per user to generate the token for case creation
        .exec(casesharing.CaseShareLoginSmall) //this step only required once per user to generate the token for case sharing
      }
        .repeat(csIterationSmall) {
          exec(casesharing.CreateCase)
          .exec(casesharing.CaseSharingPostSmall)
        }
    }*/

  setUp(
    //These 5 scenarios required for CCD regression testing
    // CCDProbateScenario.inject(rampUsers(70) during (10 minutes)), //150
    // CCDSSCSScenario.inject(rampUsers(70) during (10 minutes)), //150
    CCDEthosScenario.inject(rampUsers(500) during (10 minutes)), //400
    // CCDCMCScenario.inject(rampUsers(70) during (10 minutes)), //150
    // CCDDivScenario.inject(rampUsers(70) during (10 minutes)) //150
    
    // CaseSharingLarge.inject(rampUsers(100) during(20 minutes)),
    // CaseSharingSmall.inject(rampUsers(100) during(20 minutes))


    //UserProfileSearch.inject(rampUsers(10) during(20 minutes))

    //These scenarios left commented out and used for debugging/script testing etc
    //CCDLargeFileUpload.inject(rampUsers(15) during(15 minutes))
    //CCDSSCSScenario.inject(rampUsers(200) during(200 minutes))
    //CCDCMCScenario.inject(rampUsers(1) during(1 minutes))
    //CaseShare.inject(rampUsers(1) during(30 minutes))
    //CCDEthosScenario.inject(rampUsers(1) during(1 minutes))
  )
    .protocols(httpProtocol)
    .maxDuration(60 minutes)
}