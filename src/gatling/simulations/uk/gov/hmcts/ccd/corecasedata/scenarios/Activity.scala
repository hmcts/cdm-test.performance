package uk.gov.hmcts.ccd.corecasedata.scenarios
import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Activity {
  val constantThinkTime = 5
    
  val headers_6 = Map(
		"Accept" -> "application/json",
		"Content-Type" -> "application/json")
    
  val getActivity = repeat(3){
            exec(http("TX03_CCD_UISearchAndViewCase_optionsActivity")
			.options("/activity/cases/${Param_Case_Id}/activity")
			.resources(http("TX03_CCD_UISearchAndViewCase_getActivity")
			.get("/activity/cases/${Param_Case_Id}/activity")
			.headers(headers_6)))
		.pause(constantThinkTime seconds)
  }
		
	val searchResultGetActivity = repeat(5){
	      exec(http("TX03_CCD_UISearchAndViewCase_optionsActivity")
	    .options("/activity/cases/${SearchParam_Case_Id}/activity")
			.resources(http("TX03_CCD_UISearchAndViewCase_getActivity")
			.get("/activity/cases/${SearchParam_Case_Id}/activity")
			.headers(headers_6)))
		.pause(constantThinkTime seconds)
	}
		
 val selectCasePostActivity = repeat(3){
       exec(http("TX03_CCD_UISearchAndViewCase_optionsActivity")
			.options("/activity/cases/${SearchParam_Case_Id}/activity")
			.resources(http("TX03_CCD_UISearchAndViewCase_getActivity")
			//.options("/activity/cases/${SearchParam_Case_Id}/activity"),
       //     http("request_79")
			.get("/activity/cases/${SearchParam_Case_Id}/activity")
			.headers(headers_6),
            http("TX03_CCD_UISearchAndViewCase_postActivity")
			.post("/activity/cases/${SearchParam_Case_Id}/activity")
			.headers(headers_6)
			.body(RawFileBody("CCDUISearchNViewCase_request.txt"))))
		.pause(constantThinkTime seconds)
 }
}