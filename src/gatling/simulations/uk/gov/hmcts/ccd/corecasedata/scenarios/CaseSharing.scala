package uk.gov.hmcts.ccd.corecasedata.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.checks._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._
import scala.concurrent.duration._
import com.typesafe.config.{Config, ConfigFactory}

object CaseSharing {

val config: Config = ConfigFactory.load()
//val s2sToken = CcdTokenGenerator.generateS2SToken()
//val IdAMToken = CcdTokenGenerator.generateSIDAMUserTokenInternal()

val IdamURL = Environment.idamURL
val IdamAPI = Environment.idamAPI
val CCDEnvurl = Environment.ccdEnvurl
val s2sUrl = Environment.s2sUrl
val redirectUri = "https://manage-case.perftest.platform.hmcts.net/oauth2/callback"
//val redirectUri = "https://xui-webapp-perftest.service.core-compute-perftest.internal/oauth2/callback"
val clientId = "xuiwebapp"
val clientSecret = "yB71mnPeypP3HlcN"
val ccdGatewayClientSecret = "vUstam6brAsT38ranuwRut65rakec4u6"

val MinThinkTime = Environment.minThinkTime
val MaxThinkTime = Environment.maxThinkTime
val constantThinkTime = Environment.constantthinkTime
val MinWaitForNextIteration = Environment.minWaitForNextIteration
val MaxWaitForNextIteration = Environment.maxWaitForNextIteration

val CaseShareRequest = 

exec(http("GetS2SToken")
    .post("http://rpe-service-auth-provider-perftest.service.core-compute-perftest.internal/testing-support/lease")
    .header("Content-Type", "application/json")
    .body(StringBody("{\"microservice\":\"ccd_data\"}"))
    .check(bodyString.saveAs("bearerToken")))

.exec(http("OIDC01_Authenticate")
    .post(IdamAPI + "/authenticate")
    .header("Content-Type", "application/x-www-form-urlencoded")
    .formParam("username", "ccdloadtest1@gmail.com")
    .formParam("password", "Password12")
    .check(status is 200)
    .check(headerRegex("Set-Cookie", "Idam.Session=(.*)").saveAs("authCookie")))

.exec(http("OIDC02_Authorize")
    .post(IdamAPI + "/o/authorize?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirectUri + "&scope=openid profile roles manage-user create-user manage-roles search-user").disableFollowRedirect
    .header("Content-Type", "application/x-www-form-urlencoded")
    .header("Cookie", "Idam.Session=${authCookie}")
    .header("Content-Length", "0")
    // .formParam("response_type", "code")
    // .formParam("client_id", clientId)
    // .formParam("redirect_uri", redirectUri)
    // .formParam("state", "state")
    // .formParam("nonce", "nonce")
    // .formParam("scope", "openid profile roles manage-user create-user manage-roles search-user")
    .check(status is 302)
    //.check(headerRegex("Location", "(?<=code=)(.*)(?=&scope)").saveAs("code")))
    .check(headerRegex("Location", "code=(.*)&client_id").saveAs("code")))

.exec(http("OIDC03_Token")
    //.post(IdamAPI + "/o/token")
    .post(IdamAPI + "/o/token?grant_type=authorization_code&code=${code}&client_id=" + clientId +"&redirect_uri=" + redirectUri + "&client_secret=" + clientSecret)
    .header("Content-Type", "application/x-www-form-urlencoded")
    // .formParam("code", "${code}")
    // .formParam("redirect_uri", redirectUri)
    // .formParam("client_id", clientId)
    // .formParam("client_secret", clientId)
    // .formParam("grant_type", "authorization_code")
    .check(status is 200)
    .check(jsonPath("$.access_token").saveAs("access_token")))

/*.exec(http("GetSiDAMToken")
    .post("https://idam-api.perftest.platform.hmcts.net/loginUser")
    .header("Content-Type", "application/x-www-form-urlencoded")
    .header("accept", "application/json")
    .formParam("username", "ccdloadtest1@gmail.com")
    .formParam("password", "Password12")
    .check(regex("""api_auth_token":"(.+)"}""").saveAs("sidamToken")))*/

.exec {
    session =>
      println(session("bearerToken").as[String])
      println(session("access_token").as[String])
      session
  }

//   .exec(http("TX03_CCD_SearchCaseEndpoint_searchcases")
//       .get("http://ccd-data-store-api-perftest.service.core-compute-perftest.internal/searchCases")
//       .header("ServiceAuthorization", "${bearerToken}")
//       .header("Authorization", "${access_token}")
//       .header("Content-Type","application/json")
//       .check(status in  (200)))

.exec(http("CS_010_PUT")
    .put("/case-assignments")
    .header("ServiceAuthorization", "${bearerToken}")
    .header("Authorization", "${access_token}")
    .body(StringBody("json-stuff-goes-here")))

}