package uk.gov.hmcts.ccd.corecasedata.scenarios

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._

object casesharing {

  val config: Config = ConfigFactory.load()
  val xuiClientId = "xuiwebapp"
  val xuiScope = "openid profile roles manage-user create-user manage-roles search-user"
  val ccdClientId = "ccd_gateway"
  val ccdScope = "openid profile authorities acr roles openid profile roles"
  val feedCSUserDataLarge = csv("CaseSharingUsers_Large.csv").circular
  val feedCSUserDataSmall = csv("CaseSharingUsers_Small.csv").circular

  val headers_0 = Map( //Authorization token needs to be generated with idam login
    "Authorization" -> "AdminApiAuthToken ",
    "Content-Type" -> "application/json")

  val xuiwebappClientSecret = config.getString("xuiwebappCS")
  val ccdGatewayClientSecret = config.getString("ccdGatewayCS")

  val CDSGetRequestLarge =

    feed(feedCSUserDataLarge)

//    .exec {
//        session =>
//          println(session("userEmail").as[String])
//          session
//      }

    .exec(http("01_Create_GetS2SToken")
      .post(Environment.s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"ccd_data\"}"))
      .check(bodyString.saveAs("bearerTokenCreate")))

    .exec(http("01_Create_OIDC01_Authenticate")
      .post(Environment.idamAPI + "/authenticate")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("username", "${userEmail}")
      .formParam("password", "Pass19word")
      .formParam("redirectUri", Environment.xuiMCUrl)
      .formParam("originIp", "0:0:0:0:0:0:0:1")
      .check(status is 200)
      .check(headerRegex("Set-Cookie", "Idam.Session=(.*)").saveAs("authCookie")))

    .exec(http("01_Create_OIDC02_Authorize_CCD")
      .post(Environment.idamAPI + "/o/authorize?response_type=code&client_id=" + ccdClientId + "&redirect_uri=https://" + Environment.ccdDataStoreUrl + "/oauth2redirect&scope=" + ccdScope).disableFollowRedirect
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Cookie", "Idam.Session=${authCookie}")
      .header("Content-Length", "0")
      .check(status is 302)
      .check(headerRegex("Location", "code=(.*)&client_id").saveAs("code")))

    .exec(http("01_Create_OIDC03_Token_CCD")
      .post(Environment.idamAPI + "/o/token?grant_type=authorization_code&code=${code}&client_id=" + ccdClientId +"&redirect_uri=https://" + Environment.ccdDataStoreUrl + "/oauth2redirect&client_secret=" + ccdGatewayClientSecret)
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("access_tokenCreate")))

  val CDSGetRequestSmall =

    feed(feedCSUserDataSmall)

//    .exec {
//      session =>
//        println(session("userEmail").as[String])
//        session
//    }

    .exec(http("01_Create_GetS2SToken")
      .post(Environment.s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"ccd_data\"}"))
      .check(bodyString.saveAs("bearerTokenCreate")))

    .exec(http("01_Create_OIDC01_Authenticate")
      .post(Environment.idamAPI + "/authenticate")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("username", "${userEmail}")
      .formParam("password", "Pass19word")
      .formParam("redirectUri", Environment.xuiMCUrl)
      .formParam("originIp", "0:0:0:0:0:0:0:1")
      .check(status is 200)
      .check(headerRegex("Set-Cookie", "Idam.Session=(.*)").saveAs("authCookie")))

    .exec(http("01_Create_OIDC02_Authorize_CCD")
      .post(Environment.idamAPI + "/o/authorize?response_type=code&client_id=" + ccdClientId + "&redirect_uri=https://" + Environment.ccdDataStoreUrl + "/oauth2redirect&scope=" + ccdScope).disableFollowRedirect
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Cookie", "Idam.Session=${authCookie}")
      .header("Content-Length", "0")
      .check(status is 302)
      .check(headerRegex("Location", "code=(.*)&client_id").saveAs("code")))

    .exec(http("01_Create_OIDC03_Token_CCD")
      .post(Environment.idamAPI + "/o/token?grant_type=authorization_code&code=${code}&client_id=" + ccdClientId +"&redirect_uri=https://" + Environment.ccdDataStoreUrl + "/oauth2redirect&client_secret=" + ccdGatewayClientSecret)
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("access_tokenCreate")))

    val CreateCase =

    exec(http("01_Create_GetEventToken")
      .get("http://" + Environment.ccdDataStoreUrl + "/caseworkers/${idamUserId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/event-triggers/solicitorCreateApplication/token")
      .header("ServiceAuthorization", "Bearer ${bearerTokenCreate}")
      .header("Authorization", "Bearer ${access_tokenCreate}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("01_Create_CreateCase")
      .post("http://" +Environment.ccdDataStoreUrl + "/caseworkers/${idamUserId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases")
      .header("ServiceAuthorization", "Bearer ${bearerTokenCreate}")
      .header("Authorization", "Bearer ${access_tokenCreate}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {\n    \"solsSolicitorFirmName\": \"jon & ola\",\n    \"solsSolicitorAddress\": {\n      \"AddressLine1\": \"Flat 12\",\n      \"AddressLine2\": \"Bramber House\",\n      \"AddressLine3\": \"Seven Kings Way\",\n      \"PostTown\": \"Kingston Upon Thames\",\n      \"County\": \"\",\n      \"PostCode\": \"KT2 5BU\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"solsSolicitorAppReference\": \"test\",\n    \"solsSolicitorEmail\": \"${userEmail}\",\n    \"solsSolicitorPhoneNumber\": null,\n    \"organisationPolicy\": {\n      \"OrgPolicyCaseAssignedRole\": \"[Claimant]\",\n      \"OrgPolicyReference\": null,\n      \"Organisation\": {\n        \"OrganisationID\": \"${orgRefCode}\",\n        \"OrganisationName\": \"${orgName}\"\n      }\n    }\n  },\n  \"event\": {\n    \"id\": \"solicitorCreateApplication\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .check(jsonPath("$.id").saveAs("caseId"))
    )
//      .check(status.saveAs("statusvalue")))
//      .doIf(session=>session("statusvalue").as[String].contains("201")) {
//        exec {
//          session =>
//            val fw = new BufferedWriter(new FileWriter("EmailandCaseID.csv", true))
//            try {
//              fw.write(session("userEmail").as[String] + ","+session("caseId").as[String] + "\r\n")
//            }
//            finally fw.close()
//            session
//        }
//      }

    .pause(Environment.constantthinkTime)

  val CaseShareLoginLarge =

    exec(http("02_ACA_GetS2SToken")
      .post(Environment.s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"xui_webapp\"}"))
      .check(bodyString.saveAs("bearerTokenShare")))

    //.feed(feedCSUserDataLarge)

    .exec(http("02_ACA_OIDC01_Authenticate")
      .post(Environment.idamAPI + "/authenticate")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("username", "${userEmail}")
      .formParam("password", "Pass19word")
      .formParam("redirectUri", Environment.xuiMCUrl)
      .formParam("originIp", "0:0:0:0:0:0:0:1")
      .check(status is 200)
      .check(headerRegex("Set-Cookie", "Idam.Session=(.*)").saveAs("authCookie")))

    .exec(http("02_ACA_OIDC02_Authorize_XUI")
      .post(Environment.idamAPI + "/o/authorize?response_type=code&client_id=" + xuiClientId + "&redirect_uri=" + Environment.xuiMCUrl + "&scope=" + xuiScope).disableFollowRedirect
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Cookie", "Idam.Session=${authCookie}")
      .header("Content-Length", "0")
      .check(status is 302)
      .check(headerRegex("Location", "code=(.*)&client_id").saveAs("code")))

    .exec(http("02_ACA_OIDC03_Token_XUI")
      .post(Environment.idamAPI + "/o/token?grant_type=authorization_code&code=${code}&client_id=" + xuiClientId +"&redirect_uri=" + Environment.xuiMCUrl + "&client_secret=" + xuiwebappClientSecret)
      .header("Content-Type", "application/x-www-form-urlencoded")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("access_tokenShare")))

//  .exec {
//      session =>
//        println(session("bearerToken").as[String])
//        println(session("access_token").as[String])
//        session
//    }

  val CaseShareLoginSmall =

    exec(http("02_ACA_GetS2SToken")
      .post("http://rpe-service-auth-provider-perftest.service.core-compute-perftest.internal/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"xui_webapp\"}"))
      .check(bodyString.saveAs("bearerTokenShare")))

    //.feed(feedCSUserDataSmall)

    .exec(http("02_ACA_OIDC01_Authenticate")
      .post(Environment.idamAPI + "/authenticate")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("username", "${userEmail}")
      .formParam("password", "Pass19word")
      .formParam("redirectUri", Environment.xuiMCUrl)
      .formParam("originIp", "0:0:0:0:0:0:0:1")
      .check(status is 200)
      .check(headerRegex("Set-Cookie", "Idam.Session=(.*)").saveAs("authCookie")))

    .exec(http("02_ACA_OIDC02_Authorize_XUI")
      .post(Environment.idamAPI + "/o/authorize?response_type=code&client_id=" + xuiClientId + "&redirect_uri=" + Environment.xuiMCUrl + "&scope=" + xuiScope).disableFollowRedirect
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Cookie", "Idam.Session=${authCookie}")
      .header("Content-Length", "0")
      .check(status is 302)
      .check(headerRegex("Location", "code=(.*)&client_id").saveAs("code")))

    .exec(http("02_ACA_OIDC03_Token_XUI")
      .post(Environment.idamAPI + "/o/token?grant_type=authorization_code&code=${code}&client_id=" + xuiClientId +"&redirect_uri=" + Environment.xuiMCUrl + "&client_secret=" + xuiwebappClientSecret)
      .header("Content-Type", "application/x-www-form-urlencoded")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("access_tokenShare")))

//  .exec {
//      session =>
//        println(session("bearerToken").as[String])
//        println(session("access_token").as[String])
//        session
//    }

  val CaseSharingPostLarge =

    exec(http("02_CS_010_AssignCaseLargeOrg")
      .post("http://aac-manage-case-assignment-perftest.service.core-compute-perftest.internal/case-assignments")
      .header("ServiceAuthorization", "Bearer ${bearerTokenShare}")
      .header("Authorization", "Bearer ${access_tokenShare}")
      .header("Content-Type", "application/json")
      .header("Accept", "application/json")
      .body(StringBody("{\"assignee_id\":\"${assigneeUser}\",\"case_id\":\"${caseId}\",\"case_type_id\":\"GrantOfRepresentation\"}")))

      .pause(Environment.constantthinkTime)

  val CaseSharingPostSmall =

    exec(http("02_CS_010_AssignCaseSmallOrg")
      .post("http://aac-manage-case-assignment-perftest.service.core-compute-perftest.internal/case-assignments")
      .header("ServiceAuthorization", "Bearer ${bearerTokenShare}")
      .header("Authorization", "Bearer ${access_tokenShare}")
      .header("Content-Type", "application/json")
      .header("Accept", "application/json")
      .body(StringBody("{\"assignee_id\":\"${assigneeUser}\",\"case_id\":\"${caseToShare}\",\"case_type_id\":\"GrantOfRepresentation\"}")))

      .pause(Environment.constantthinkTime)
}