package uk.gov.hmcts.ccd.corecasedata.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.checks.{CsrfCheck, CurrentPageUrl}
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

import scala.concurrent.duration._

object Browse {

 val IdamURL = Environment.idamURL
 val CCDEnvurl = Environment.ccdEnvurl
  
 val MinThinkTime = Environment.minThinkTime
 val MaxThinkTime = Environment.maxThinkTime
 val constantThinkTime = Environment.constantthinkTime
 val MinWaitForNextIteration = Environment.minWaitForNextIteration
 val MaxWaitForNextIteration = Environment.maxWaitForNextIteration
    
 val feedUserData = csv("CCDUserData.csv").circular
 val feedUserDataPB = csv("ProbateUserData.csv").circular
 val feedUserDataDV = csv("DivorceUserData.csv").circular
 val feedUserDataSSCS = csv("SSCSUserData.csv").circular
 //val CCDCreateCaseFeeder = csv("CCD_CreateCase_TestData.csv").circular

 val CommonHeader = Environment.commonHeader
 val idam_header = Environment.idam_header

 val Homepage = group("CDM_HomePage") {

   exec(http("CDM_005_HomePage")
     .get(CCDEnvurl + "/")
     .headers(CommonHeader))

   .exec(http("CDM_010_HomePage")
     .get(IdamURL + "/login?response_type=code&client_id=ccd_gateway&redirect_uri=" + CCDEnvurl + "/oauth2redirect")
     .headers(idam_header)
     .check(CurrentPageUrl.save)
     .check(CsrfCheck.save))
 }
    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    //.feed(CCDCreateCaseFeeder)
    .feed(feedUserData)
    .feed(feedUserDataPB)
    .feed(feedUserDataDV)
    .feed(feedUserDataSSCS)

}