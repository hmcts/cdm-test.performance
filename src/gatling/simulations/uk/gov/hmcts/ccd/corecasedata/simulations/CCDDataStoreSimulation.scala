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
  val probateIteration = 50
  val sscsIteration = 50
  val divorceIteration = 50
  val caseActivityIteration = 400
  val elasticSearchIteration = 50
  val caseworkerSearchIteration = 100
  val ethosIteration = 100

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
        exec(ccddatastore.CCDAPI_ProbateJourney)
      }
    }

  val SSCSCreateCase = scenario("SSCS Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_SSCS)
      .repeat(sscsIteration) {
        exec(ccddatastore.CCDAPI_SSCSJourney)
      }
    }

  val DivorceCreateCase = scenario("Divorce Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_Divorce)
      .repeat(divorceIteration) {
        exec(ccddatastore.CCDAPI_DivorceJourney)
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

  setUp(
    ProbateCreateCase.inject(rampUsers(250) during(10 minutes)),
    SSCSCreateCase.inject(rampUsers(250) during(10 minutes)),
    DivorceCreateCase.inject(rampUsers(250) during(10 minutes)),
    CaseActivityScn.inject(rampUsers(100) during(10 minutes)),
    CCDElasticSearch.inject(rampUsers(100) during(1 minutes)),
    EthosSearchView.inject(rampUsers(100) during(1 minutes)),
    // RJUpdateSupplementaryCaseData.inject(rampUsers(100) during (10 minutes)), //100
    // RJSearchCases.inject(rampUsers(200) during (10 minutes))   //200
  )
    .protocols(httpProtocol)
  //.maxDuration(60 minutes)
}