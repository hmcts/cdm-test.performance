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

  val CMCiteration = 100
  val probateIteration = 100
  val sscsIteration = 100
  val divorceIteration = 100
  val iacIteration = 100
  val frIteration = 100
  val fplIteration = 100
  val elasticSearchIteration = 100
  val caseworkerSearchIteration = 100
  val ethosIteration = 200

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080)) //Comment out for VM runs
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
      exec(elasticsearch.CDSGetRequest)
        .repeat(35) {
          exec(elasticsearch.ElasticSearchGetRef)
          .exec(elasticsearch.ElasticSearchGetByDate)
          .exec(elasticsearch.ElasticSearchEthos)
          .exec(elasticsearch.ElasticSearchWorkbasket)
          .exec(elasticsearch.ElasticSearchGet25Divorce)
          //.exec(WaitforNextIteration.waitforNextIteration)
        }
    }

  val CCDElasticSearchGoR = scenario("CCDESGoR")
    .repeat(1) {
      exec(elasticsearch.CDSGetRequest)
        .repeat(10) {
          exec(elasticsearch.ElasticSearchGet25GoR)
        }
    }

  val CCDElasticSearchGoRState = scenario("CCDESGoRState")
    .repeat(1) {
      exec(elasticsearch.CDSGetRequest)
        .repeat(1) {
          exec(elasticsearch.ElasticSearchWorkbasketGoR)
        }
    }

  val CCDElasticSearchBenefitEvidenceHandled = scenario("CCDESBenefitEvidenceHandled")
    .repeat(1) {
      exec(elasticsearch.CDSGetRequest)
        .repeat(10) {
          exec(elasticsearch.ElasticSearchWorkbasketSSCS)
        }
    }

  val CCDElasticSearchWorkBasket = scenario("CCDES - Workbasket")
    .repeat(1) {
      exec(elasticsearch.CDSGetRequest)
        .repeat(elasticSearchIteration) {
          exec(elasticsearch.ElasticSearchWorkbasket)
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
      exec(elasticsearch.CitizenLogin)
      .repeat(210) {
        exec(elasticsearch.CitizenSearch)
      }
    }

  val CaseworkerSearch = scenario("Caseworker")
    .repeat(1) {
      exec(elasticsearch.CDSGetRequest)
      .repeat(220) {
        exec(elasticsearch.CaseworkerSearch)
      }
    }

  val XUICaseWorker = scenario("Caseworker XUI API")
    .repeat(1) {
      exec(elasticsearch.CDSGetRequest) //CDSGetRequest XUIIdamLogin
      .repeat(36) { //36
        exec(elasticsearch.XUICaseworkerSearch)
        //.exec(WaitforNextIteration.waitforNextIteration)
      }
    }


    //Add searches for IAC, FPL & FR

  // API Create scenarios, sometimes required for Elastic Search indexing

  val ProbateCreateCase = scenario("Probate Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_Probate)
      .repeat(probateIteration) {
        exec(ccddatastore.CCDAPI_ProbateCreate)
        // .exec(ccddatastore.CCDAPI_ProbateCaseEvents)
      }
    }

  val SSCSCreateCase = scenario("SSCS Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_SSCS)
      .repeat(sscsIteration) {
        exec(ccddatastore.CCDAPI_SSCSCreate)
        // .exec(ccddatastore.CCDAPI_SSCSCaseEvents)
      }
    }

  val DivorceCreateCase = scenario("Divorce Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_Divorce)
      .repeat(divorceIteration) {
        exec(ccddatastore.CCDAPI_DivorceSolicitorCreate)
        // .exec(ccddatastore.CCDAPI_DivorceCaseEvents)
      }
    }

  val CMCCreateCase = scenario("CMC Case Create")
  .repeat(1) {
    exec(ccddatastore.CCDLogin_CMC)
    .repeat(CMCiteration) {
      exec(ccddatastore.CCDAPI_CMCCreate)
    }
  }

  val IACCreateCase = scenario("IAC Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_IAC)
      .repeat(iacIteration) {
        exec(ccddatastore.CCDAPI_IACCreate)
      }
    }

  val FPLCreateCase = scenario("FPL Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_FPL)
      .repeat(fplIteration) {
        exec(ccddatastore.CCDAPI_FPLCreate)
      }
    }

  val FRCreateCase = scenario("FR Case Create")
    .repeat(1) {
      exec(ccddatastore.CCDLogin_FR)
      .repeat(frIteration) {
        exec(ccddatastore.CCDAPI_FRCreate)
      }
    }


  setUp(
    //CCDUISearch.inject(rampUsers(5) during (5 minutes)),
    //CCDElasticSearchGoR.inject(rampUsers(5) during (5 minutes)),
    //XUISearch.inject(rampUsers(300) during (15 minutes))

    // CitizenSearch.inject(rampUsers(50) during (20 minutes)),
    // CaseworkerSearch.inject(rampUsers(50) during (20 minutes)),
    // CCDElasticSearch.inject(rampUsers(50) during (15 minutes)),
    // XUICaseWorker.inject(rampUsers(550) during (10 minutes)),

    // CCDElasticSearchWorkBasket.inject(rampUsers(100) during(10 minutes)),
    // ProbateCreateCase.inject(rampUsers(50) during(10 minutes)),
    // SSCSCreateCase.inject(rampUsers(50) during(10 minutes)),
    // DivorceCreateCase.inject(rampUsers(50) during(10 minutes)),
    // CMCCreateCase.inject(rampUsers(50) during(10 minutes)),
    // IACCreateCase.inject(rampUsers(50) during(10 minutes)),
    // FPLCreateCase.inject(rampUsers(50) during(10 minutes)),
    FRCreateCase.inject(rampUsers(1) during(10 minutes)),
  )
    .protocols(httpProtocol)
    // .maxDuration(60 minutes)
}