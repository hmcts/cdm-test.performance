package uk.gov.hmcts.ccd.corecasedata.scenarios

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._

object ccddatastore {

val config: Config = ConfigFactory.load()

//val s2sToken = CcdTokenGenerator.generateS2SToken()
//val IdAMToken = CcdTokenGenerator.generateSIDAMUserTokenInternal()

val IdamURL = Environment.idamURL
val IdamAPI = Environment.idamAPI
val CCDEnvurl = Environment.ccdEnvurl
val s2sUrl = Environment.s2sUrl
val ccdRedirectUri = "https://ccd-data-store-api-perftest.service.core-compute-perftest.internal/oauth2redirect"
val ccdDataStoreUrl = "http://ccd-data-store-api-perftest.service.core-compute-perftest.internal"
val escaseDataUrl = "https://ccd-api-gateway-web-perftest.service.core-compute-perftest.internal"
val ccdClientId = "ccd_gateway"
val ccdGatewayClientSecret = config.getString("ccdGatewayCS")

//  val ccdGatewayClientSecret = "vUstam6brAsT38ranuwRut65rakec4u6"
val ccdScope = "openid profile authorities acr roles openid profile roles"
val feedCSUserData = csv("CaseSharingUsers_Large.csv").circular
val feedCaseSearchData = csv("caseSearchData.csv").random
val feedWorkbasketData = csv("workbasketCaseTypes.csv").circular
val feedXUISearchData = csv("XUISearchData.csv").circular
val feedXUIUserData = csv("XUISearchUsers.csv").circular

val MinThinkTime = Environment.minThinkTime
val MaxThinkTime = Environment.maxThinkTime
val constantThinkTime = Environment.constantthinkTime
val MinWaitForNextIteration = Environment.minWaitForNextIteration
val MaxWaitForNextIteration = Environment.maxWaitForNextIteration

val headers_0 = Map( //Authorization token needs to be generated with idam login
  "Authorization" -> "AdminApiAuthToken ",
  "Content-Type" -> "application/json")

val CDSGetRequest =

  feed(feedCSUserData)

  .exec(http("GetS2SToken")
      .post(s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"ccd_data\"}"))
      .check(bodyString.saveAs("bearerToken")))

  .exec(http("OIDC01_Authenticate")
      .post(IdamAPI + "/authenticate")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("username", "ccdloadtest1@gmail.com") //${userEmail}
      .formParam("password", "Password12")
      .formParam("redirectUri", ccdRedirectUri)
      .formParam("originIp", "0:0:0:0:0:0:0:1")
      .check(status is 200)
      .check(headerRegex("Set-Cookie", "Idam.Session=(.*)").saveAs("authCookie")))

  .exec(http("OIDC02_Authorize_CCD")
      .post(IdamAPI + "/o/authorize?response_type=code&client_id=" + ccdClientId + "&redirect_uri=" + ccdRedirectUri + "&scope=" + ccdScope).disableFollowRedirect
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Cookie", "Idam.Session=${authCookie}")
      .header("Content-Length", "0")
      .check(status is 302)
      .check(headerRegex("Location", "code=(.*)&client_id").saveAs("code")))

    //MkVIBs0dfCwTIBeU-enTRbfGUh0

  .exec(http("OIDC03_Token_CCD")
      .post(IdamAPI + "/o/token?grant_type=authorization_code&code=${code}&client_id=" + ccdClientId +"&redirect_uri=" + ccdRedirectUri + "&client_secret=" + ccdGatewayClientSecret)
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      //.header("Cookie", "Idam.Session=${authCookie}")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("access_token")))

//  .exec {
//      session =>
//        println(session("bearerToken").as[String])
//        println(session("access_token").as[String])
//        session
//    }

  val CitizenLogin = 

    exec(http("GetS2SToken")
      .post(s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"ccd_data\"}"))
      .check(bodyString.saveAs("bearerToken")))

    .exec(http("OIDC01_Authenticate")
      .post(IdamAPI + "/authenticate")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("username", "perftest-citizen@gmail.com") //${userEmail}
      .formParam("password", "Password12")
      .formParam("redirectUri", ccdRedirectUri)
      .formParam("originIp", "0:0:0:0:0:0:0:1")
      .check(status is 200)
      .check(headerRegex("Set-Cookie", "Idam.Session=(.*)").saveAs("authCookie")))

    .exec(http("OIDC02_Authorize_CCD")
      .post(IdamAPI + "/o/authorize?response_type=code&client_id=" + ccdClientId + "&redirect_uri=" + ccdRedirectUri + "&scope=" + ccdScope).disableFollowRedirect
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Cookie", "Idam.Session=${authCookie}")
      .header("Content-Length", "0")
      .check(status is 302)
      .check(headerRegex("Location", "code=(.*)&client_id").saveAs("code")))

    //MkVIBs0dfCwTIBeU-enTRbfGUh0

    .exec(http("OIDC03_Token_CCD")
      .post(IdamAPI + "/o/token?grant_type=authorization_code&code=${code}&client_id=" + ccdClientId +"&redirect_uri=" + ccdRedirectUri + "&client_secret=" + ccdGatewayClientSecret)
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      //.header("Cookie", "Idam.Session=${authCookie}")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("access_token")))

  val XUIIdamLogin =

  feed(feedXUIUserData)

  .exec(http("GetS2SToken")
      .post(s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"ccd_data\"}"))
      .check(bodyString.saveAs("bearerToken")))

  .exec(http("OIDC01_Authenticate")
      .post(IdamAPI + "/authenticate")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("username", "${email}") //${userEmail}
      .formParam("password", "Password12")
      .formParam("redirectUri", ccdRedirectUri)
      .formParam("originIp", "0:0:0:0:0:0:0:1")
      .check(status is 200)
      .check(headerRegex("Set-Cookie", "Idam.Session=(.*)").saveAs("authCookie")))

  .exec(http("OIDC02_Authorize_CCD")
      .post(IdamAPI + "/o/authorize?response_type=code&client_id=" + ccdClientId + "&redirect_uri=" + ccdRedirectUri + "&scope=" + ccdScope).disableFollowRedirect
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Cookie", "Idam.Session=${authCookie}")
      .header("Content-Length", "0")
      .check(status is 302)
      .check(headerRegex("Location", "code=(.*)&client_id").saveAs("code")))

    //MkVIBs0dfCwTIBeU-enTRbfGUh0

  .exec(http("OIDC03_Token_CCD")
      .post(IdamAPI + "/o/token?grant_type=authorization_code&code=${code}&client_id=" + ccdClientId +"&redirect_uri=" + ccdRedirectUri + "&client_secret=" + ccdGatewayClientSecret)
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      //.header("Cookie", "Idam.Session=${authCookie}")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("access_token")))

  val ElasticSearchGet25GoR =

    exec(http("CCD_SearchCaseEndpoint_ElasticSearch")
      .post(ccdDataStoreUrl + "/searchCases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .queryParam("ctid", "GrantOfRepresentation")
      .body(StringBody("{\n\t\"query\": {\n\t\t\"match_all\": {}\n\t\t},\n\t\t\"size\": 25,\n\t\t\"sort\":[ \n      { \n         \"last_modified\":\"desc\"\n      },\n      \"_score\"\n   ]\n}"))
      .check(status in  (200)))

      .pause(Environment.constantthinkTime)

  val CitizenSearch =

    feed(feedWorkbasketData)

    .exec(http("CCD_SearchCaseEndpoint_CitizenSearch")
      .get(ccdDataStoreUrl + "/citizens/1f65a0df-b064-4f9b-85ea-3eec5a28ce86/jurisdictions/${jurisdiction}/case-types/${caseType}/cases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(status in (200)))

      .pause(Environment.constantthinkTime)

  val CaseworkerSearch = 

    feed(feedWorkbasketData)

    .exec(http("CCD_SearchCaseEndpoint_CaseworkerSearch")
      .get(ccdDataStoreUrl + "/caseworkers/539560/jurisdictions/${jurisdiction}/case-types/${caseType}/cases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(status in (200)))

  //   .exec {
  //     session =>
  //     println(session("caseType").as[String])
  //     session
  // }

    .pause(Environment.constantthinkTime)  

  val XUICaseworkerSearch = 

    feed(feedXUISearchData)

    .exec(http("XUI_${jurisdiction}_CaseworkerSearch")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${jurisdiction}/case-types/${caseType}/cases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .queryParam("state", "${state}")
      .queryParam("page", "1")
      .check(status in (200)))

      .pause(Environment.constantthinkTime) 

  val ElasticSearchWorkbasketGoR = 

    exec(http("CCD_SearchCaseEndpoint_ElasticSearch")
      .post(ccdDataStoreUrl + "/searchCases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .queryParam("ctid", "GrantOfRepresentation")
      .queryParam("use_case", "WORKBASKET")
      .queryParam("view", "WORKBASKET")
      .queryParam("page", "1")
      .queryParam("state", "IntCaseCreated")
      .body(StringBody("{\"from\":0,\"query\":{\"bool\":{\"must\":[]}},\"size\":25,\"sort\":[{\"created_date\":\"DESC\"}]}"))
      .check(status in (200)))

      .pause(Environment.constantthinkTime)

  val ElasticSearchWorkbasketSSCS = 

    exec(http("CCD_SearchCaseEndpoint_ElasticSearch")
      .post(ccdDataStoreUrl + "/searchCases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .queryParam("ctid", "Benefit")
      .queryParam("use_case", "WORKBASKET")
      .queryParam("view", "WORKBASKET")
      .queryParam("page", "1")
      .queryParam("case.evidenceHandled", "No")
      .body(StringBody("{\"from\":0,\"query\":{\"bool\":{\"must\":[]}},\"size\":25,\"sort\":[{\"created_date\":\"DESC\"}]}"))
      .check(status in (200)))

      .pause(Environment.constantthinkTime)

  val ElasticSearchGet25Divorce =

    exec(http("CCD_SearchCaseEndpoint_ElasticSearch")
      .post(ccdDataStoreUrl + "/searchCases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .queryParam("ctid", "DIVORCE")
      .body(StringBody("{\n\t\"query\": {\n\t\t\"match_all\": {}\n\t\t},\n\t\t\"size\": 25,\n\t\t\"sort\":[ \n      { \n         \"last_modified\":\"desc\"\n      },\n      \"_score\"\n   ]\n}"))
      .check(status in  (200)))

      .pause(5)

  val ElasticSearchWorkbasket = 

    feed(feedWorkbasketData)

    .exec(http("CCD_SearchCaseEndpoint_ElasticSearch")
      .post(ccdDataStoreUrl + "/searchCases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .queryParam("ctid", "${caseType}")
      .queryParam("use_case", "WORKBASKET")
      .queryParam("view", "WORKBASKET")
      .queryParam("page", "1")
      .body(StringBody("{\"from\":0,\"query\":{\"bool\":{\"must\":[]}},\"size\":25,\"sort\":[{\"created_date\":\"DESC\"}]}"))
      .check(status in (200)))

      .pause(5)

  val ElasticSearchGetRef =

    feed(feedCaseSearchData)

    .exec(http("CCD_SearchCaseEndpoint_ElasticSearch")
      .post(ccdDataStoreUrl + "/searchCases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .queryParam("ctid", "${caseType}") //${caseType}
      .body(StringBody("{ \n   \"query\":{ \n      \"bool\":{ \n         \"filter\":{ \n            \"wildcard\":{ \n               \"reference\":\"${caseId}\"\n            }\n         }\n      }\n   }\n}"))
      .check(status in  (200)))

      .pause(5)

  val ElasticSearchGetByDate =

    exec(http("CCD_SearchCaseEndpoint_ElasticSearch")
      .post(ccdDataStoreUrl + "/searchCases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .queryParam("ctid", "GrantOfRepresentation")
      .body(StringBody("{\n   \"query\":{\n      \"bool\":{\n         \"filter\":{\n            \"term\":{\n               \"created_date\":\"2020-07-20\"\n            }\n         }\n      }\n   }\n}"))
      .check(status in  (200)))

      .pause(5)

  val ElasticSearchEthos =

    exec(http("CCD_SearchCaseEndpoint_ElasticSearch")
      .post(ccdDataStoreUrl + "/searchCases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .queryParam("ctid", "Scotland")
      //.body(StringBody("{\"size\":10000,\"query\":{\"terms\":{\"data.ethosCaseReference.keyword\":[\"4178987/2020\"],\"boost\":1.0}}}"))
      .body(StringBody("{\"from\":0,\"query\":{\"bool\":{\"must\":[]}},\"size\":25,\"sort\":[{\"created_date\":\"DESC\"}]}"))

      .check(status in  (200)))

      .pause(5)

  val CreateCaseForCaseSharing =

    exec(http("GetIdamUserID")
      .get("https://idam-api.perftest.platform.hmcts.net/users?email=perftest-citizen@gmail.com") //1f65a0df-b064-4f9b-85ea-3eec5a28ce86 ${caseSharingUser}
      .headers(headers_0)
      .check(jsonPath("$.id").saveAs("userId")))

    .exec(http("GetEventToken")
      .get(ccdDataStoreUrl + "/citizens/${userId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/event-triggers/solicitorCreateApplication/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("CreateCase")
      .post(ccdDataStoreUrl + "/citizens/${userId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      //.body(StringBody("{\n  \"data\": {\n    \"solsSolicitorFirmName\": \"jon & ola\",\n    \"solsSolicitorAddress\": {\n      \"AddressLine1\": \"Flat 12\",\n      \"AddressLine2\": \"Bramber House\",\n      \"AddressLine3\": \"Seven Kings Way\",\n      \"PostTown\": \"Kingston Upon Thames\",\n      \"County\": \"\",\n      \"PostCode\": \"KT2 5BU\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"solsSolicitorAppReference\": \"test\",\n    \"solsSolicitorEmail\": \"ccdorg-mvgvh_mcccd.user52@mailinator.com\",\n    \"solsSolicitorPhoneNumber\": null,\n    \"organisationPolicy\": {\n      \"OrgPolicyCaseAssignedRole\": \"[Claimant]\",\n      \"OrgPolicyReference\": null,\n      \"Organisation\": {\n        \"OrganisationID\": \"IGWEE4D\",\n        \"OrganisationName\": \"ccdorg-mvgvh\"\n      }\n    }\n  },\n  \"event\": {\n    \"id\": \"solicitorCreateApplication\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .body(StringBody(" {   \n \t\"data\" : {\n      \"TextField\" : \"textField1\",\n      \"TextAreaField\" : \"textAreaField1\",\n      \"AddressField\" : {\n        \"AddressLine1\" : \"102 Petty France\",\n        \"AddressLine2\" : \"CCD\",\n        \"AddressLine3\" : \"c/o HMCTS Reform\",\n        \"Country\" : \"UK\"\n      },\n      \"OrganisationPolicyField1\" : {\n        \"OrgPolicyCaseAssignedRole\" : \"[Claimant]\",\n        \"OrgPolicyReference\" : \"ref\",\n        \"Organisation\" : {\n          \"OrganisationID\" : \"orgID1\",\n          \"OrganisationName\" : \"orgName1\"\n        }\n      },\n      \"OrganisationPolicyField2\" : {\n        \"OrgPolicyCaseAssignedRole\" : \"[Defendant]\",\n        \"OrgPolicyReference\" : \"ref\",\n        \"Organisation\" : {\n          \"OrganisationID\" : \"orgID2\",\n          \"OrganisationName\" : \"orgName2\"\n        }\n      }\n    },\n    \"event\" : {\n      \"id\" : \"createCase\",\n      \"summary\" : \"\",\n      \"description\" : \"\"\n    },\n    \"event_token\" : \"${eventToken}\",\n    \"ignore_warning\" : false,\n    \"draft_id\" : null\n \t\n }"))
      .check(jsonPath("$.id").saveAs("caseId")))

      .pause(5)

  val GetAssignedUsers = 

    exec(http("AAC_010_GetAssignedUsersAndRoles")
      .get(ccdDataStoreUrl + "/case-users")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("experimental","true")
      .queryParam("case_ids", "${caseToShare}"))

    // .exec {
    //   session =>
    //     println(session("userEmail").as[String])
    //   session
    // }
    .pause(Environment.constantthinkTime)
}