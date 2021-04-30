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
  val api_probateIteration = 1600 //40
  val api_sscsIteration = 1600 //40
  val api_divorceIteration = 1600 //40
  val api_iacIteration = 1600 //40
  val api_fplIteration = 1600 //40
  val api_frIteration = 1600 //40
  val api_cmcIteration = 1600 //40

  val ui_PBiteration = 600 //15
  val ui_SSCSiteration = 600 //15
  val ui_CMCiteration = 600 //15

  val caseActivityTotalRepeat = 200 //5
  val caseActivityIteration = 120
  val caseActivityListIteration = 12

  val ccdSearchIteration = 1400 //35
  val elasticSearchIteration = 3600 //90

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
        .exec(WaitforNextIteration.waitforNextIteration)
      }
    }

  val API_ProbateSolicitorCreate = scenario("Probate Solicitor Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_ProbateSolicitor)
      .repeat(api_probateIteration) {
        exec(ccddatastore.CCDAPI_ProbateSolicitorCreate)
        .exec(ccddatastore.CCDAPI_ProbateSolicitorCaseEvents)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
    }

  val API_SSCSCreateCase = scenario("SSCS Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_SSCS)
      .repeat(api_sscsIteration) { //api_sscsIteration
        exec(ccddatastore.CCDAPI_SSCSCreate)
        .exec(ccddatastore.CCDAPI_SSCSCaseEvents)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
    }

  val API_DivorceSolicitorCreateCase = scenario("Divorce Solicitor Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_Divorce)
      .repeat(api_divorceIteration) { //api_divorceIteration
        exec(ccddatastore.CCDAPI_DivorceSolicitorCreate)
        .exec(ccddatastore.CCDAPI_DivorceSolicitorCaseEvents)
        .exec(WaitforNextIteration.waitforNextIteration)
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
        .exec(WaitforNextIteration.waitforNextIteration)
      }
    }

  val API_FRCreateCase = scenario("FR Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_FR)
      .repeat(api_frIteration) { //api_frIteration
        exec(ccddatastore.CCDAPI_FRCreate)
        .exec(ccddatastore.CCDAPI_FRCaseEvents)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
    }

  val API_CMCCreateCase = scenario("CMC Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_CMC)
      .repeat(api_cmcIteration) { //api_cmcIteration
        exec(ccddatastore.CCDAPI_CMCCreate)
        .exec(ccddatastore.CCDAPI_CMCCaseEvents)
        .exec(WaitforNextIteration.waitforNextIteration)
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
        // .exec(CMC.CMCAttachScannedDocs)
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
      .repeat(caseActivityTotalRepeat) {
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
    API_DivorceSolicitorCreateCase.inject(
      incrementConcurrentUsers(100)
        .times(40)
        .eachLevelLasting(5.minutes)
        .separatedByRampsLasting(2.minutes)
        .startingFrom(10)),
    API_ProbateSolicitorCreate.inject(
      incrementConcurrentUsers(100)
        .times(40)
        .eachLevelLasting(5.minutes)
        .separatedByRampsLasting(2.minutes)
        .startingFrom(10)),
    API_CMCCreateCase.inject(
      incrementConcurrentUsers(50)
        .times(40)
        .eachLevelLasting(5.minutes)
        .separatedByRampsLasting(2.minutes)
        .startingFrom(10)),
    API_ProbateCreateCase.inject(
      incrementConcurrentUsers(50)
        .times(40)
        .eachLevelLasting(5.minutes)
        .separatedByRampsLasting(2.minutes)
        .startingFrom(10)),
    API_SSCSCreateCase.inject(
      incrementConcurrentUsers(50)
        .times(40)
        .eachLevelLasting(5.minutes)
        .separatedByRampsLasting(2.minutes)
        .startingFrom(10)),
    // API_IACCreateCase.inject(
    //   incrementConcurrentUsers(1)
    //     .times(40)
    //     .eachLevelLasting(10.minutes)
    //     .separatedByRampsLasting(2.minutes)
    //     .startingFrom(10)),
    // API_FRCreateCase.inject(
    //   incrementConcurrentUsers(1)
    //     .times(40)
    //     .eachLevelLasting(10.minutes)
    //     .separatedByRampsLasting(2.minutes)
    //     .startingFrom(10)),
    // API_FPLCreateCase.inject(
    //   incrementConcurrentUsers(1)
    //     .times(40)
    //     .eachLevelLasting(10.minutes)
    //     .separatedByRampsLasting(2.minutes)
    //     .startingFrom(10)),
    

    UI_CCDProbateScenario.inject(
      incrementConcurrentUsers(50)
        .times(40)
        .eachLevelLasting(5.minutes)
        .separatedByRampsLasting(2.minutes)
        .startingFrom(10)),
    // UI_CCDSSCSScenario.inject(
    //   incrementConcurrentUsers(1)
    //     .times(40)
    //     .eachLevelLasting(10.minutes)
    //     .separatedByRampsLasting(2.minutes)
    //     .startingFrom(10)),
    // UI_CCDCMCScenario.inject(
    //   incrementConcurrentUsers(1)
    //     .times(40)
    //     .eachLevelLasting(10.minutes)
    //     .separatedByRampsLasting(2.minutes)
    //     .startingFrom(10)),
    
    CaseActivityScn.inject(
      incrementConcurrentUsers(50)
        .times(40)
        .eachLevelLasting(5.minutes)
        .separatedByRampsLasting(2.minutes)
        .startingFrom(10)),

    CCDSearchView.inject(
      incrementConcurrentUsers(50)
        .times(40)
        .eachLevelLasting(5.minutes)
        .separatedByRampsLasting(2.minutes)
        .startingFrom(10)),
    CCDElasticSearch.inject(
      incrementConcurrentUsers(50)
        .times(40)
        .eachLevelLasting(5.minutes)
        .separatedByRampsLasting(2.minutes)
        .startingFrom(10)))
    
  .protocols(httpProtocol)
  .maxDuration(120 minutes)
}