package uk.gov.hmcts.ccd.corecasedata.simulations

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._ //comment out for VM runs, only required for proxy
import uk.gov.hmcts.ccd.corecasedata.scenarios._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._
import scala.concurrent.duration._

class CCDDataStoreSimulation extends Simulation  {

  val BaseURL = Environment.baseURL
  val config: Config = ConfigFactory.load()

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080)) //Comment out for VM runs
    .doNotTrackHeader("1")

  val CCDElasticSearch = scenario("CCDES")
    .repeat(1) {
      exec(ccddatastore.CDSGetRequest)
        .repeat(1) {
          exec(ccddatastore.ElasticSearchGetRef)
          .exec(ccddatastore.ElasticSearchGetByDate)
          .exec(ccddatastore.ElasticSearchEthos)
          .exec(ccddatastore.ElasticSearchWorkbasket)
          .exec(ccddatastore.ElasticSearchGet25Divorce)
        }
    }

  val CCDElasticSearchGoR = scenario("CCDESGoR")
    .repeat(1) {
      exec(ccddatastore.CDSGetRequest)
        .repeat(1) {
          exec(ccddatastore.ElasticSearchGet25GoR)
        }
    }

  val CCDElasticSearchGoRState = scenario("CCDESGoRState")
    .repeat(1) {
      exec(ccddatastore.CDSGetRequest)
        .repeat(10) {
          exec(ccddatastore.ElasticSearchWorkbasketGoR)
        }
    }

  val CCDElasticSearchGoR10000 = scenario("CCDESGoR 10000")
    .repeat(1) {
      exec(ccddatastore.CDSGetRequest)
        .repeat(10) {
          exec(ccddatastore.GatewaySearchWorkbasketGoR1000)
        }
    }

  val CCDElasticSearchBenefitEvidenceHandled = scenario("CCDESBenefitEvidenceHandled")
    .repeat(1) {
      exec(ccddatastore.CDSGetRequest)
        .repeat(1) {
          exec(ccddatastore.ElasticSearchWorkbasketSSCS)
        }
    }

  val CreateCase = scenario("CaseCreate")
    .repeat(1) {
      exec(ccddatastore.CitizenLogin)
      .exec(ccddatastore.CreateCaseForCaseSharing)
    }

  val ETCreateCase = scenario("ETCaseCreate")
    .repeat(1) {
      exec(ccddatastore.CDSGetRequest)
//      .repeat(100) {
////        .exec(ccddatastore.ETGetToken)
////        .exec(ccddatastore.ETCreateCase)
//      }
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

  setUp(
    //CCDElasticSearch.inject(rampUsers(1) during(1 minutes)),
    ETCreateCase.inject(rampUsers(1) during(1 minutes))
    //CCDElasticSearchGoR.inject(rampUsers(1) during(1 minutes)),
    //CCDElasticSearchGoRState.inject(rampUsers(1) during(1 minutes)),
    //CCDElasticSearchBenefitEvidenceHandled.inject(rampUsers(1) during(1 minutes))
    //CreateCase.inject(rampUsers(1) during(1 minutes))
    // RJUpdateSupplementaryCaseData.inject(rampUsers(100) during (10 minutes)), //100
    // RJSearchCases.inject(rampUsers(200) during (10 minutes))   //200
  )
    .protocols(httpProtocol)
  //.maxDuration(60 minutes)
}