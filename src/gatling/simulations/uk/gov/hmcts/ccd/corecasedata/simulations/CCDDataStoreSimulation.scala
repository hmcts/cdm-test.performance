package uk.gov.hmcts.ccd.corecasedata.simulations

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._ //comment out for VM runs, only required for proxy
import uk.gov.hmcts.ccd.corecasedata.scenarios._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._
import scala.concurrent.duration._

class CCDDataStoreSimulation extends Simulation  {

  //Repeat volumes
  val probateIteration = 200
  val sscsIteration = 220
  val divorceIteration = 220
  val caseActivityIteration = 1000
  val elasticSearchIteration = 80
  val caseworkerSearchIteration = 100
  val ethosIteration = 200

  val PBiteration = 14 //7
  val SSCSiteration = 28 //14
  val CMCiteration = 16 //8
  val Diviteration = 16 //8

  val BaseURL = Environment.baseURL
  val config: Config = ConfigFactory.load()

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    // .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080)) //Comment out for VM runs
    .doNotTrackHeader("1")

  val CCDElasticSearch = scenario("CCDES")
    .repeat(1) {
      exec(elasticsearch.CDSGetRequest)
        .repeat(elasticSearchIteration) {
          exec(elasticsearch.ElasticSearchGetRef)
          .exec(elasticsearch.ElasticSearchGetByDate)
          .exec(elasticsearch.ElasticSearchEthos)
          .exec(elasticsearch.ElasticSearchWorkbasket)
          .exec(elasticsearch.ElasticSearchGet25Divorce)
        }
    }

  val CaseworkerSearch = scenario("Caseworker Search")
    .repeat(1) {
      exec(elasticsearch.CDSGetRequest)
      .repeat(caseworkerSearchIteration) {
        exec(elasticsearch.CaseworkerSearch)
      }
    }

  val CreateCase = scenario("CaseCreate")
    .repeat(1) {
      exec(ccddatastore.CDSGetRequest)
      .repeat(1) {
        exec(ccddatastore.CreateCaseForCaseSharing)
      }
    }

  val ETCreateCase = scenario("ETCaseCreate")
    .repeat(1) {
      exec(ccddatastore.CDSGetRequest)
     .repeat(50) {
       exec(ccddatastore.ETGetToken)
       .exec(ccddatastore.ETCreateCase)
     }
    }

  //Respondent Journey Requests//

  val RJUpdateSupplementaryCaseData = scenario("UpdateSupplementaryCaseData")
  .repeat(1) {
      exec(ccddatastore.CDSGetRequest)
        .repeat(200) {
          exec(ccddatastore.RJCreateCase)
          .exec(ccddatastore.RJUpdateSupplementaryCaseData)
        }
    }

  val RJSearchCases = scenario("SearchCases")
  .repeat(1) {
      exec(ccddatastore.CDSGetRequest)
        .repeat(300) { //300
          exec(ccddatastore.RJElasticSearchGetRef)
        }
    } 

  //CCD API Journeys

  val ProbateCreateCase = scenario("Probate Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_Probate)
      .repeat(probateIteration) {
        exec(ccddatastore.CCDAPI_ProbateCreate)
        .exec(ccddatastore.CCDAPI_ProbateCaseEvents)
      }
    }

  val SSCSCreateCase = scenario("SSCS Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_SSCS)
      .repeat(sscsIteration) {
        exec(ccddatastore.CCDAPI_SSCSCreate)
        .exec(ccddatastore.CCDAPI_SSCSCaseEvents)
      }
    }

  val DivorceCreateCase = scenario("Divorce Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_Divorce)
      .repeat(divorceIteration) {
        exec(ccddatastore.CCDAPI_DivorceCreate)
        .exec(ccddatastore.CCDAPI_DivorceCaseEvents)
      }
    }

  val IACCreateCase = scenario("IAC Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_IAC)
      .repeat(1) {
        exec(ccddatastore.CCDAPI_IACCreate)
      }
    }

  val FPLCreateCase = scenario("FPL Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_FPL)
      .repeat(1) {
        exec(ccddatastore.CCDAPI_FPLCreate)
      }
    }

  val FRCreateCase = scenario("FR Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_FR)
      .repeat(1) {
        exec(ccddatastore.CCDAPI_FRCreate)
      }
    }

  val CMCCreateCase = scenario("CMC Case Create")
  .repeat(1) {
    exec(ccddatastore.CCDLogin_CMC)
    .repeat(1) {
      exec(ccddatastore.CCDAPI_CMCCreate)
    }
  }

  val CaseActivityScn = scenario("CCD Case Activity Requests")
    .repeat(1) {
      exec(ccdcaseactivity.CDSGetRequest)
      .repeat(caseActivityIteration) {
        exec(ccdcaseactivity.CaseActivityRequests)
      }
    }

  val EthosSearchView = scenario("Ethos Search and View Cases")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_Ethos)
      .repeat(ethosIteration) {
        exec(ccddatastore.CCDAPI_EthosJourney)
      }
    }

  //CCD UI Journeys

  val CCDProbateScenario = scenario("CCDPB")
    .repeat(1) {
      exec(Browse.Homepage)
      .exec(PBGoR.submitLogin)
      .repeat(PBiteration) {
        exec(PBGoR.PBCreateCase)
        // .exec(PBGoR.PBCaseActivity)
        .exec(PBGoR.PBPaymentSuccessful)
        // .exec(PBGoR.PBCaseActivity)
        .exec(PBGoR.PBDocUpload)
        // .exec(PBGoR.PBCaseActivity)
        .exec(PBGoR.PBStopCase)
        // .exec(PBGoR.PBCaseActivity)
        .exec(PBGoR.PBSearch)
        .exec(PBGoR.PBView)
        // .exec(PBGoR.PBCaseActivity)
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
        // .exec(SSCS.SSCSCaseActivity)
        .exec(SSCS.SSCSDocUpload)
        // .exec(SSCS.SSCSCaseActivity)
        .exec(SSCS.SSCSSearchAndView)
        // .exec(SSCS.SSCSCaseActivity)
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
        // .exec(CMC.CMCCaseActivity)
        .exec(CMC.CMCStayCase)
        // .exec(CMC.CMCCaseActivity)
        .exec(CMC.CMCWaitingTransfer)
        // .exec(CMC.CMCCaseActivity)
        //.exec(CMC.CMCTransfer)
        .exec(CMC.CMCAttachScannedDocs)
        // .exec(CMC.CMCCaseActivity)
        .exec(CMC.CMCSupportUpdate)
        // .exec(CMC.CMCCaseActivity)
        .exec(CMC.CMCSearchAndView)
        // .exec(CMC.CMCCaseActivity)
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

  setUp(
    ProbateCreateCase.inject(rampUsers(1) during(10 minutes)), //250
    // SSCSCreateCase.inject(rampUsers(250) during(10 minutes)),
    // DivorceCreateCase.inject(rampUsers(250) during(10 minutes)),
    // CaseActivityScn.inject(rampUsers(100) during(10 minutes)),
    // CCDElasticSearch.inject(rampUsers(100) during(10 minutes)),
    // EthosSearchView.inject(rampUsers(100) during(10 minutes)),

    // CCDProbateScenario.inject(rampUsers(10) during (10 minutes)), 
    // CCDSSCSScenario.inject(rampUsers(10) during (10 minutes)), 
    // CCDCMCScenario.inject(rampUsers(10) during (10 minutes)), 
    // CCDDivScenario.inject(rampUsers(10) during (10 minutes)), 


    // RJUpdateSupplementaryCaseData.inject(rampUsers(100) during (10 minutes)), //100
    // RJSearchCases.inject(rampUsers(200) during (10 minutes))   //200
  )
    .protocols(httpProtocol)
  //.maxDuration(60 minutes)
}