package uk.gov.hmcts.ccd.corecasedata.simulations

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.http.Predef._ //comment out for VM runs, only required for proxy
import uk.gov.hmcts.ccd.corecasedata.scenarios._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._
import scala.concurrent.duration._

class CCD_SearchSimulation extends Simulation  {

  val config: Config = ConfigFactory.load()
  val BaseURL = Environment.baseURL

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    // .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080)) //Comment out for VM runs
    .doNotTrackHeader("1")

  val CCDUISearch = scenario("CCDUISearch")
    .repeat(1) {
      exec(Browse.Homepage)
      .exec(LoginAndSearch.Login)
      .repeat(10) {
        exec(LoginAndSearch.Search)
        .exec(WaitforNextIteration.waitforNextIteration)
      }
      .exec(Logout.ccdLogout)
  }

  val CCDElasticSearch = scenario("CCDES")
    .repeat(1) {
      exec(ccddatastore.CDSGetRequest)
        .repeat(35) {
          exec(ccddatastore.ElasticSearchGetRef)
          .exec(ccddatastore.ElasticSearchGetByDate)
          .exec(ccddatastore.ElasticSearchEthos)
          .exec(ccddatastore.ElasticSearchWorkbasket)
          .exec(ccddatastore.ElasticSearchGet25Divorce)
          //.exec(WaitforNextIteration.waitforNextIteration)
        }
    }

  val CCDElasticSearchGoR = scenario("CCDESGoR")
    .repeat(1) {
      exec(ccddatastore.CDSGetRequest)
        .repeat(10) {
          exec(ccddatastore.ElasticSearchGet25GoR)
        }
    }

  val CCDElasticSearchGoRState = scenario("CCDESGoRState")
    .repeat(1) {
      exec(ccddatastore.CDSGetRequest)
        .repeat(1) {
          exec(ccddatastore.ElasticSearchWorkbasketGoR)
        }
    }

  val CCDElasticSearchBenefitEvidenceHandled = scenario("CCDESBenefitEvidenceHandled")
    .repeat(1) {
      exec(ccddatastore.CDSGetRequest)
        .repeat(10) {
          exec(ccddatastore.ElasticSearchWorkbasketSSCS)
        }
    }

  val XUISearch = scenario("XuiSearch")
    .repeat(1) {
      exec(ExuiView.manageCasesHomePage)
      .exec(ExuiView.manageCaseslogin)
        .repeat(20){ //20
          exec(ExuiView.searchCase)
          //.exec(ExuiView.searchDivorceCase)
          .exec(WaitforNextIteration.waitforNextIteration)
        }
        .exec(ExuiView.XUILogout)
    }


  val CitizenSearch = scenario("Citizen")
    .repeat(1) {
      exec(ccddatastore.CitizenLogin)
      .repeat(210) {
        exec(ccddatastore.CitizenSearch)
      }
    }

  val CaseworkerSearch = scenario("Caseworker")
    .repeat(1) {
      exec(ccddatastore.CDSGetRequest)
      .repeat(220) {
        exec(ccddatastore.CaseworkerSearch)
      }
    }

  val XUICaseWorker = scenario("Caseworker XUI API")
    .repeat(1) {
      exec(ccddatastore.CDSGetRequest) //CDSGetRequest XUIIdamLogin
      .repeat(36) { //36
        exec(ccddatastore.XUICaseworkerSearch)
        //.exec(WaitforNextIteration.waitforNextIteration)
      }
    }

  setUp(
    //CCDUISearch.inject(rampUsers(5) during (5 minutes)),
    //CCDElasticSearchGoR.inject(rampUsers(5) during (5 minutes)),
    //XUISearch.inject(rampUsers(300) during (15 minutes))
    CitizenSearch.inject(rampUsers(50) during (20 minutes)),
    CaseworkerSearch.inject(rampUsers(50) during (20 minutes)),
    CCDElasticSearch.inject(rampUsers(50) during (15 minutes)),
    XUICaseWorker.inject(rampUsers(550) during (10 minutes))
  )
    .protocols(httpProtocol)
    .maxDuration(60 minutes)
}