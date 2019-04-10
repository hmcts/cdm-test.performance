package uk.gov.hmcts.ccd.corecasedata.simulations
import scala.concurrent.duration._
import uk.gov.hmcts.ccd.corecasedata.scenarios._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.checks.{CsrfCheck, CurrentPageCheck}
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

class CCDUIPTSimulation extends Simulation  {
  
  val BaseURL = Environment.baseURL
    
	val httpProtocol = Environment.HttpProtocol
		.baseURL(BaseURL)
	  .proxy(Proxy("proxyout.reform.hmcts.net", 8080))
	  
val CCDUIScenario = scenario("CCDUI")
           .exec(Logout.ccdLogout,Browse.loginPage,ExecuteLogin.submitLogin,CreateCaseObj.selectJurisdiction,CreateCaseObj.startNewCaseCreation,
            Validate.validateFirstPage,Validate.validateSecondPage,Validate.validateThirdPage,SubmitCase.checkyouranswersNSubmit,Search.searchRequest,
            Search.searchResult,SelectCase.selectAndViewCase, Logout.ccdLogout)
    
   
            setUp(CCDUIScenario.inject(atOnceUsers(1))).protocols(httpProtocol)
        
}