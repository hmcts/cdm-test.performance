package uk.gov.hmcts.ccd.corecasedata.scenarios

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._

object ccdcaseactivity {

val config: Config = ConfigFactory.load()

val IdamURL = Environment.idamURL
val IdamAPI = Environment.idamAPI
val CCDEnvurl = Environment.ccdEnvurl
val s2sUrl = Environment.s2sUrl
val ccdRedirectUri = "https://ccd-data-store-api-perftest.service.core-compute-perftest.internal/oauth2redirect"
val ccdDataStoreUrl = "http://ccd-data-store-api-perftest.service.core-compute-perftest.internal"
val escaseDataUrl = "https://ccd-api-gateway-web-perftest.service.core-compute-perftest.internal"
val ccdCaseActivityUrl = "http://ccd-case-activity-api-perftest.service.core-compute-perftest.internal"
val ccdClientId = "ccd_gateway"
val ccdGatewayClientSecret = config.getString("ccdGatewayCS")
val ccdScope = "openid profile authorities acr roles openid profile roles"
val caseActivityFeeder = csv("CaseActivityData.csv").random
val caseActivityListFeeder = csv("CaseActivityListData.csv").random
val feedXUIUserData = csv("XUISearchUsers.csv").circular

val CDSGetRequest =

  feed(feedXUIUserData)

  .exec(http("GetS2SToken")
      .post(s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"ccd_data\"}"))
      .check(bodyString.saveAs("bearerToken")))
      .exitHereIfFailed

  .exec(http("OIDC01_Authenticate")
      .post(IdamAPI + "/authenticate")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("username", "${email}")
      .formParam("password", "Password12")
      .formParam("redirectUri", ccdRedirectUri)
      .formParam("originIp", "0:0:0:0:0:0:0:1")
      .check(status is 200)
      .check(headerRegex("Set-Cookie", "Idam.Session=(.*)").saveAs("authCookie")))
      .exitHereIfFailed

  .exec(http("OIDC02_Authorize_CCD")
      .post(IdamAPI + "/o/authorize?response_type=code&client_id=" + ccdClientId + "&redirect_uri=" + ccdRedirectUri + "&scope=" + ccdScope).disableFollowRedirect
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Cookie", "Idam.Session=${authCookie}")
      .header("Content-Length", "0")
      .check(status is 302)
      .check(headerRegex("Location", "code=(.*)&client_id").saveAs("code")))
      .exitHereIfFailed

  .exec(http("OIDC03_Token_CCD")
      .post(IdamAPI + "/o/token?grant_type=authorization_code&code=${code}&client_id=" + ccdClientId +"&redirect_uri=" + ccdRedirectUri + "&client_secret=" + ccdGatewayClientSecret)
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      //.header("Cookie", "Idam.Session=${authCookie}")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("access_token")))
      .exitHereIfFailed

val CaseActivityRequest_GET = 

  feed(caseActivityFeeder)

  .exec(http("CaseActivity_GET")
    .get(ccdCaseActivityUrl + "/cases/${caseRef}/activity")
    .header("Content-Type", "application/json")
    .header("ServiceAuthorization", "Bearer ${bearerToken}")
    .header("Authorization", "Bearer ${access_token}"))

val CaseActivityRequest_OPTIONS = 

  exec(http("CaseActivity_OPTIONS")
    .options(ccdCaseActivityUrl + "/cases/${caseRef}/activity")
    .header("Content-Type", "application/json")
    .header("ServiceAuthorization", "Bearer ${bearerToken}")
    .header("Authorization", "Bearer ${access_token}"))

val CaseActivityRequest_POST = 

  exec(http("CaseActivity_POST")
    .post(ccdCaseActivityUrl + "/cases/${caseRef}/activity")
    .header("Content-Type", "application/json")
    .header("ServiceAuthorization", "Bearer ${bearerToken}")
    .header("Authorization", "Bearer ${access_token}")
    .body(StringBody("{\n  \"activity\": \"view\"\n}")))

  .pause(Environment.caseActivityPause)

// Add case list requests
//GET only every 5 seconds

val CaseActivityList = 

  feed(caseActivityListFeeder)

  .exec(http("CaseActivityList_GET")
    .get(ccdCaseActivityUrl + "/cases/${caseList}/activity")
    .header("Content-Type", "application/json")
    .header("ServiceAuthorization", "Bearer ${bearerToken}")
    .header("Authorization", "Bearer ${access_token}"))

  .exec(http("CaseActivityList_OPTIONS")
    .options(ccdCaseActivityUrl + "/cases/${caseList}/activity")
    .header("Content-Type", "application/json")
    .header("ServiceAuthorization", "Bearer ${bearerToken}")
    .header("Authorization", "Bearer ${access_token}"))

  .pause(Environment.caseActivityPause)
}