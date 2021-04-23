package uk.gov.hmcts.ccd.corecasedata.simulations

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._ //comment out for VM runs, only required for proxy
import uk.gov.hmcts.ccd.corecasedata.scenarios._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._
import scala.concurrent.duration._

class CCD_StressTest extends Simulation  {

  //Iteration Settings
  val api_probateIteration = 40 //40
  val api_sscsIteration = 40 //40
  val api_divorceIteration = 40 //40
  val api_iacIteration = 40 //40
  val api_fplIteration = 40 //40
  val api_frIteration = 40 //40
  val api_cmcIteration = 40 //40

  val ui_PBiteration = 15
  val ui_SSCSiteration = 15
  val ui_CMCiteration = 15

  val caseActivityIteration = 120
  val caseActivityListIteration = 12
  val ccdSearchIteration = 35
  val elasticSearchIteration = 90

  //Gatling specific configs, required for perf testing
  val BaseURL = Environment.baseURL
  val config: Config = ConfigFactory.load()

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .doNotTrackHeader("1")

  /*================================================================================================

  The below scenarios are required for CCD Stress Performance Testing

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
        .exec(WaitforNextIteration.waitforNextIteration)
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
        // .exec(WaitforNextIteration.waitforNextIteration)
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

  //CCD Case Activity Requests
  val CaseActivityScn = scenario("CCD Case Activity Requests")
    .repeat(1) {
      exec(ccdcaseactivity.CDSGetRequest)
      .repeat(5) {
        repeat(caseActivityListIteration) {
          exec(ccdcaseactivity.CaseActivityList)
        }
        .repeat(caseActivityIteration) {
          exec(ccdcaseactivity.CaseActivityRequest)
        }
      }
    }

  //CCD Search Requests (non-Elastic Search)
  val CCDSearchView = scenario("CCD Search and View Cases")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_Ethos)
      .repeat(ccdSearchIteration) {
        exec(ccddatastore.CCDAPI_EthosJourney)
        .exec(WaitforNextIteration.waitforNextIteration)
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
    API_CMCCreateCase.inject(
      incrementConcurrentUsers(100)
        .times(4)
        .eachLevelLasting(15.minutes)
        .separatedByRampsLasting(5.minutes)
        .startingFrom(100)),
    API_DivorceCreateCase.inject(
      incrementConcurrentUsers(100)
        .times(4)
        .eachLevelLasting(15.minutes)
        .separatedByRampsLasting(5.minutes)
        .startingFrom(100)),
    API_ProbateCreateCase.inject(
      incrementConcurrentUsers(100)
        .times(4)
        .eachLevelLasting(15.minutes)
        .separatedByRampsLasting(5.minutes)
        .startingFrom(100)),
    API_SSCSCreateCase.inject(
      incrementConcurrentUsers(100)
        .times(4)
        .eachLevelLasting(15.minutes)
        .separatedByRampsLasting(5.minutes)
        .startingFrom(100)),
    API_IACCreateCase.inject(
      incrementConcurrentUsers(100)
        .times(4)
        .eachLevelLasting(15.minutes)
        .separatedByRampsLasting(5.minutes)
        .startingFrom(100)),
    API_FRCreateCase.inject(
      incrementConcurrentUsers(100)
        .times(4)
        .eachLevelLasting(15.minutes)
        .separatedByRampsLasting(5.minutes)
        .startingFrom(100)),
    API_FPLCreateCase.inject(
      incrementConcurrentUsers(100)
        .times(4)
        .eachLevelLasting(15.minutes)
        .separatedByRampsLasting(5.minutes)
        .startingFrom(100)),
    

    UI_CCDProbateScenario.inject(
      incrementConcurrentUsers(100)
        .times(4)
        .eachLevelLasting(15.minutes)
        .separatedByRampsLasting(5.minutes)
        .startingFrom(100)),
    UI_CCDSSCSScenario.inject(
      incrementConcurrentUsers(100)
        .times(4)
        .eachLevelLasting(15.minutes)
        .separatedByRampsLasting(5.minutes)
        .startingFrom(100)),
    UI_CCDCMCScenario.inject(
      incrementConcurrentUsers(100)
        .times(4)
        .eachLevelLasting(15.minutes)
        .separatedByRampsLasting(5.minutes)
        .startingFrom(100)),
    
    CaseActivityScn.inject(
      incrementConcurrentUsers(100)
        .times(4)
        .eachLevelLasting(15.minutes)
        .separatedByRampsLasting(5.minutes)
        .startingFrom(100)),

    CCDSearchView.inject(
      incrementConcurrentUsers(100)
        .times(4)
        .eachLevelLasting(15.minutes)
        .separatedByRampsLasting(5.minutes)
        .startingFrom(200)),
    CCDElasticSearch.inject(
      incrementConcurrentUsers(100)
        .times(4)
        .eachLevelLasting(15.minutes)
        .separatedByRampsLasting(5.minutes)
        .startingFrom(200)))
    
  .protocols(httpProtocol)
}