package uk.gov.hmcts.ccd.corecasedata.simulations

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._ //comment out for VM runs, only required for proxy
import uk.gov.hmcts.ccd.corecasedata.scenarios._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._
import scala.concurrent.duration._

class CCD_PerformanceRegression extends Simulation  {

  //Iteration Settings
  val api_probateIteration = 40
  val api_sscsIteration = 40
  val api_divorceIteration = 40
  val api_iacIteration = 40
  val api_fplIteration = 40
  val api_frIteration = 40
  val api_cmcIteration = 40

  val ui_PBiteration = 60
  val ui_SSCSiteration = 60
  val ui_CMCiteration = 60
  val ui_Diviteration = 60

  val caseActivityIteration = 120
  val caseActivityListIteration = 200
  val ccdSearchIteration = 35
  val elasticSearchIteration = 90

  //Gatling specific configs, required for perf testing
  val BaseURL = Environment.baseURL
  val config: Config = ConfigFactory.load()

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080)) //Comment out for VM runs
    .doNotTrackHeader("1")

  /*================================================================================================

  The below scenarios are required for CCD Regression Performance Testing

  ================================================================================================*/

  //CCD API - Create & Case Event Journeys
  val API_ProbateCreateCase = scenario("Probate Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_Probate)
      .repeat(api_probateIteration) { //api_probateIteration
        exec(ccddatastore.CCDAPI_ProbateCreate)
        .exec(ccddatastore.CCDAPI_ProbateCaseEvents)
      }
    }

  val API_SSCSCreateCase = scenario("SSCS Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_SSCS)
      .repeat(api_sscsIteration) { //api_sscsIteration
        exec(ccddatastore.CCDAPI_SSCSCreate)
        .exec(ccddatastore.CCDAPI_SSCSCaseEvents)
      }
    }

  val API_DivorceCreateCase = scenario("Divorce Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_Divorce)
      .repeat(api_divorceIteration) { //api_divorceIteration
        exec(ccddatastore.CCDAPI_DivorceCreate)
        .exec(ccddatastore.CCDAPI_DivorceCaseEvents)
      }
    }

  val API_IACCreateCase = scenario("IAC Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_IAC)
      .repeat(api_iacIteration) { //api_iacIteration
        exec(ccddatastore.CCDAPI_IACCreate)
      }
    }

  val API_FPLCreateCase = scenario("FPL Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_FPL)
      .repeat(api_fplIteration) { //api_fplIteration
        exec(ccddatastore.CCDAPI_FPLCreate)
        .exec(ccddatastore.CCDAPI_FPLCaseEvents)
      }
    }

  val API_FRCreateCase = scenario("FR Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_FR)
      .repeat(api_frIteration) { //api_frIteration
        exec(ccddatastore.CCDAPI_FRCreate)
        .exec(ccddatastore.CCDAPI_FRCaseEvents)
      }
    }

  val API_CMCCreateCase = scenario("CMC Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_CMC)
      .repeat(api_cmcIteration) { //api_cmcIteration
        exec(ccddatastore.CCDAPI_CMCCreate)
        .exec(ccddatastore.CCDAPI_CMCCaseEvents)
      }
    }

  //CCD UI Requests
  val UI_CCDProbateScenario = scenario("CCDPB")
    .repeat(1) {
      exec(Browse.Homepage)
      .exec(PBGoR.submitLogin)
      .repeat(ui_PBiteration) {
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

  val UI_CCDSSCSScenario = scenario("CCDSSCS")
    .repeat(1) {
     exec(Browse.Homepage)
      .exec(SSCS.SSCSLogin)
      .repeat(ui_SSCSiteration) {
        exec(SSCS.SSCSCreateCase)
        .exec(SSCS.SSCSDocUpload)
        .exec(SSCS.SSCSSearchAndView)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
      .exec(Logout.ccdLogout)
    }

  val UI_CCDCMCScenario = scenario("CCDCMC")
    .repeat(1) {
      exec(Browse.Homepage)
      .exec(CMC.CMCLogin)
      .repeat(ui_CMCiteration) {
        exec(CMC.CMCCreateCase)
        .exec(CMC.CMCStayCase)
        .exec(CMC.CMCAttachScannedDocs)
        .exec(CMC.CMCSupportUpdate)
        .exec(CMC.CMCSearchAndView)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
      .exec(Logout.ccdLogout)
  }

  val UI_CCDDivScenario = scenario("CCDDIV")
    .repeat(1) {
      exec(Browse.Homepage)
        .exec(DVExcep.submitLogin)
        .repeat(ui_Diviteration) {
          exec(DVExcep.DVCreateCase)
          .exec(DVExcep.DVDocUpload)
          .exec(DVExcep.DVSearch)
          .exec(DVExcep.DVView)
          .exec(WaitforNextIteration.waitforNextIteration)
        }
        .exec(Logout.ccdLogout)
    }

  //CCD Case Activity Requests
  val CaseActivityScn = scenario("CCD Case Activity Requests")
    .repeat(1) {
      exec(ccdcaseactivity.CDSGetRequest)
      .repeat(caseActivityListIteration) {
        exec(ccdcaseactivity.CaseActivityList)
      }
      .repeat(caseActivityIteration) {
        exec(ccdcaseactivity.CaseActivityRequest)
      }
    }

  val CaseActivityListScn = scenario("CCD Case Activity List Requests")
    .repeat(1) {
      exec(ccdcaseactivity.CDSGetRequest)
      .repeat(caseActivityListIteration) {
        exec(ccdcaseactivity.CaseActivityList)
      }
    }

  //CCD Search Requests (non-Elastic Search)
  val CCDSearchView = scenario("CCD Search and View Cases")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_Ethos)
      .repeat(ccdSearchIteration) {
        exec(ccddatastore.CCDAPI_EthosJourney)
      }
    }

  //CCD Elastic Search Requests
  val CCDElasticSearch = scenario("CCD - Elastic Search")
    .repeat(1) {
      exec(elasticsearch.CDSGetRequest)
      .repeat(elasticSearchIteration) {
        exec(elasticsearch.ElasticSearchWorkbasket)
      }
    }

  setUp(
    //CCD API scenarios
    // API_ProbateCreateCase.inject(rampUsers(18) during (10 minutes)),
    // API_SSCSCreateCase.inject(rampUsers(18) during (10 minutes)),
    // API_DivorceCreateCase.inject(rampUsers(18) during (10 minutes)),
    // API_IACCreateCase.inject(rampUsers(18) during (10 minutes)),
    // API_FPLCreateCase.inject(rampUsers(12) during (10 minutes)),
    // API_FRCreateCase.inject(rampUsers(18) during (10 minutes)),
    // API_CMCCreateCase.inject(rampUsers(18) during (10 minutes)),
/*
    //CCD UI scenarios
    UI_CCDProbateScenario.inject(rampUsers(5) during (10 minutes)),
    UI_CCDSSCSScenario.inject(rampUsers(5) during (10 minutes)),
    UI_CCDCMCScenario.inject(rampUsers(5) during (10 minutes)),
    UI_CCDDivScenario.inject(rampUsers(5) during (10 minutes)),
*/
    //Case Activity Requests
    CaseActivityScn.inject(rampUsers(50) during (10 minutes)),
    CaseActivityListScn.inject(rampUsers(50) during (10 minutes)), //50

    //CCD Searches
    // CCDSearchView.inject(rampUsers(20) during (20 minutes)),
    // CCDElasticSearch.inject(rampUsers(150) during (10 minutes))
    
    //Debugging requests (leave commented out for test runs please)
    // API_ProbateCreateCase.inject(rampUsers(18) during (10 minutes)),
    // API_SSCSCreateCase.inject(rampUsers(18) during (10 minutes)),
    // API_DivorceCreateCase.inject(rampUsers(18) during (10 minutes)),
    // API_IACCreateCase.inject(rampUsers(18) during (10 minutes)),
    // API_FPLCreateCase.inject(rampUsers(12) during (10 minutes)),
    // API_FRCreateCase.inject(rampUsers(18) during (10 minutes)),
    // API_CMCCreateCase.inject(rampUsers(18) during (10 minutes))  
    )
  .protocols(httpProtocol)
}