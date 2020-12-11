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
      .formParam("username", "${email}") //${email}
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
      .exitHereIfFailed

    .exec(http("OIDC01_Authenticate")
      .post(IdamAPI + "/authenticate")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("username", "ccdloadtest1@gmail.com") //${userEmail}
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

  val XUIIdamLogin =

  feed(feedXUIUserData)

  .exec(http("GetS2SToken")
      .post(s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"ccd_data\"}"))
      .body(StringBody("{\n  \"size\": 25\n}"))
      .check(bodyString.saveAs("bearerToken")))
      .exitHereIfFailed

  .exec(http("OIDC01_Authenticate")
      .post(IdamAPI + "/authenticate")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("username", "${email}") //${userEmail}
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

    //MkVIBs0dfCwTIBeU-enTRbfGUh0

  .exec(http("OIDC03_Token_CCD")
      .post(IdamAPI + "/o/token?grant_type=authorization_code&code=${code}&client_id=" + ccdClientId +"&redirect_uri=" + ccdRedirectUri + "&client_secret=" + ccdGatewayClientSecret)
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      //.header("Cookie", "Idam.Session=${authCookie}")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("access_token")))
      .exitHereIfFailed

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
      .get(ccdDataStoreUrl + "/citizens/539560/jurisdictions/${jurisdiction}/case-types/${caseType}/cases") //1f65a0df-b064-4f9b-85ea-3eec5a28ce86
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(status in (200)))

      .pause(Environment.constantthinkTime)
      .pause(Environment.constantthinkTime)

  val CaseworkerSearch = 

    feed(feedWorkbasketData)

    .exec(http("CCD_SearchCaseEndpoint_CaseworkerSearch")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${jurisdiction}/case-types/${caseType}/cases")
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

    // .exec(http("XUI_${jurisdiction}_CaseworkerSearch")
    //   .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/${jurisdiction}/case-types/${caseType}/cases")
    //   .header("ServiceAuthorization", "Bearer ${bearerToken}")
    //   .header("Authorization", "Bearer ${access_token}")
    //   .header("Content-Type","application/json")
    //   .queryParam("state", "${state}")
    //   .queryParam("page", "1")
    //   .check(status in (200)))

      //.pause(Environment.constantthinkTime) 

    .exec(http("XUI_${jurisdiction}_CaseworkerSearch")
      .post(ccdDataStoreUrl + "/searchCases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .queryParam("ctid", "${caseType}")
      .body(StringBody("{\n\t\"query\": {\n\t\t\"match_all\": {}\n\t\t},\n\t\t\"size\": 25,\n\t\t\"sort\":[ \n      { \n         \"last_modified\":\"desc\"\n      },\n      \"_score\"\n   ]\n}"))
      .check(status in  (200)))

      .pause(Environment.constantthinkTime)
      .pause(Environment.constantthinkTime)
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

  val ElasticSearchWorkbasketGoR1000 = 

    exec(http("CCD_SearchCaseEndpoint_ElasticSearch")
      .post(ccdDataStoreUrl + "/internal/searchCases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .queryParam("ctid", "GrantOfRepresentation")
      //.body(StringBody("{\"from\":0,\"query\":{\"bool\":{\"must\":[]}},\"size\":10000,\"}"))
      .body(StringBody("{\"query\":{\"match_all\":{}},\"size\":10000}"))
      .check(status in (200)))

      .pause(Environment.constantthinkTime)

  val GatewaySearchWorkbasketGoR1000 = 

    exec(http("CCD_SearchCaseEndpoint_ElasticSearch")
      .post("https://gateway-ccd.perftest.platform.hmcts.net/data/internal/searchCases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .queryParam("ctid", "GrantOfRepresentation")
      //.body(StringBody("{\"from\":0,\"query\":{\"bool\":{\"must\":[]}},\"size\":10000,\"}"))
      .body(StringBody("{\"query\":{\"match_all\":{}},\"size\":10000}"))
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


  //Case Sharing Requests - for manage-case-assignment API//

  val CreateCaseForCaseSharing =

    // exec(http("GetIdamUserID")
    //   .get("https://idam-api.perftest.platform.hmcts.net/users?email=${email}") //1f65a0df-b064-4f9b-85ea-3eec5a28ce86 ${caseSharingUser}
    //   .headers(headers_0)
    //   .check(jsonPath("$.id").saveAs("userId")))

    exec(http("PB_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/event-triggers/applyForGrant/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("PB_CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      // .body(StringBody("{\n  \"data\": {\n    \"solsSolicitorFirmName\": \"jon & ola\",\n    \"solsSolicitorAddress\": {\n      \"AddressLine1\": \"Flat 12\",\n      \"AddressLine2\": \"Bramber House\",\n      \"AddressLine3\": \"Seven Kings Way\",\n      \"PostTown\": \"Kingston Upon Thames\",\n      \"County\": \"\",\n      \"PostCode\": \"KT2 5BU\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"solsSolicitorAppReference\": \"test\",\n    \"solsSolicitorEmail\": \"ccdorg-mvgvh_mcccd.user52@mailinator.com\",\n    \"solsSolicitorPhoneNumber\": null,\n    \"organisationPolicy\": {\n      \"OrgPolicyCaseAssignedRole\": \"[Claimant]\",\n      \"OrgPolicyReference\": null,\n      \"Organisation\": {\n        \"OrganisationID\": \"IGWEE4D\",\n        \"OrganisationName\": \"ccdorg-mvgvh\"\n      }\n    }\n  },\n  \"event\": {\n    \"id\": \"solicitorCreateApplication\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      // .body(StringBody(" {   \n \t\"data\" : {\n      \"TextField\" : \"textField1\",\n      \"TextAreaField\" : \"textAreaField1\",\n      \"AddressField\" : {\n        \"AddressLine1\" : \"102 Petty France\",\n        \"AddressLine2\" : \"CCD\",\n        \"AddressLine3\" : \"c/o HMCTS Reform\",\n        \"Country\" : \"UK\"\n      },\n      \"OrganisationPolicyField1\" : {\n        \"OrgPolicyCaseAssignedRole\" : \"[Claimant]\",\n        \"OrgPolicyReference\" : \"ref\",\n        \"Organisation\" : {\n          \"OrganisationID\" : \"orgID1\",\n          \"OrganisationName\" : \"orgName1\"\n        }\n      },\n      \"OrganisationPolicyField2\" : {\n        \"OrgPolicyCaseAssignedRole\" : \"[Defendant]\",\n        \"OrgPolicyReference\" : \"ref\",\n        \"Organisation\" : {\n          \"OrganisationID\" : \"orgID2\",\n          \"OrganisationName\" : \"orgName2\"\n        }\n      }\n    },\n    \"event\" : {\n      \"id\" : \"createCase\",\n      \"summary\" : \"\",\n      \"description\" : \"\"\n    },\n    \"event_token\" : \"${eventToken}\",\n    \"ignore_warning\" : false,\n    \"draft_id\" : null\n \t\n }"))
      .body(StringBody("{\n  \"data\": {},\n  \"event\": {\n    \"id\": \"applyForGrant\",\n    \"summary\": \"test case\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .check(jsonPath("$.id").saveAs("caseId")))

    .pause(1)

    .exec(http("DIV_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/DIVORCE/case-types/DIVORCE/event-triggers/CREATE/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("DIV_CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/DIVORCE/case-types/DIVORCE/cases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {\n    \"D8legalProcess\": null,\n    \"createdDate\": null,\n    \"D8ScreenHasMarriageBroken\": null,\n    \"D8ScreenHasRespondentAddress\": null,\n    \"D8ScreenHasMarriageCert\": null,\n    \"D8ScreenHasPrinter\": null,\n    \"D8DivorceWho\": null,\n    \"D8MarriageIsSameSexCouple\": null,\n    \"D8InferredPetitionerGender\": null,\n    \"D8InferredRespondentGender\": null,\n    \"D8MarriageDate\": null,\n    \"D8MarriedInUk\": null,\n    \"D8CertificateInEnglish\": null,\n    \"D8CertifiedTranslation\": null,\n    \"D8MarriagePlaceOfMarriage\": null,\n    \"D8CountryName\": null,\n    \"D8MarriagePetitionerName\": null,\n    \"D8MarriageRespondentName\": null,\n    \"D8PetitionerNameDifferentToMarriageCert\": null,\n    \"D8PetitionerEmail\": null,\n    \"D8PetitionerPhoneNumber\": null,\n    \"D8PetitionerFirstName\": null,\n    \"D8PetitionerLastName\": null,\n    \"D8DerivedPetitionerCurrentFullName\": null,\n    \"D8PetitionerNameChangedHow\": [],\n    \"D8PetitionerNameChangedHowOtherDetails\": null,\n    \"D8PetitionerContactDetailsConfidential\": null,\n    \"D8PetitionerHomeAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8DerivedPetitionerHomeAddress\": null,\n    \"D8PetitionerCorrespondenceAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8DerivedPetitionerCorrespondenceAddr\": null,\n    \"D8PetitionerCorrespondenceUseHomeAddress\": null,\n    \"D8RespondentNameAsOnMarriageCertificate\": null,\n    \"D8RespondentFirstName\": null,\n    \"D8RespondentLastName\": null,\n    \"D8DerivedRespondentCurrentName\": null,\n    \"D8DerivedRespondentSolicitorDetails\": null,\n    \"D8RespondentHomeAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8DerivedRespondentHomeAddress\": null,\n    \"D8RespondentCorrespondenceAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8DerivedRespondentCorrespondenceAddr\": null,\n    \"D8RespondentSolicitorName\": null,\n    \"D8RespondentSolicitorCompany\": null,\n    \"D8RespondentCorrespondenceSendToSol\": null,\n    \"D8RespondentSolicitorAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8DerivedRespondentSolicitorAddr\": null,\n    \"D8RespondentCorrespondenceUseHomeAddress\": null,\n    \"D8RespondentKnowsHomeAddress\": null,\n    \"D8RespondentLivesAtLastAddress\": null,\n    \"D8LivingArrangementsTogetherSeparated\": null,\n    \"D8LivingArrangementsLastLivedTogether\": null,\n    \"D8LivingArrangementsLiveTogether\": null,\n    \"D8LivingArrangementsLastLivedTogethAddr\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8DerivedLivingArrangementsLastLivedAddr\": null,\n    \"D8LegalProceedings\": null,\n    \"D8LegalProceedingsRelated\": [],\n    \"D8LegalProceedingsDetails\": null,\n    \"D8ReasonForDivorce\": null,\n    \"D8DerivedStatementOfCase\": null,\n    \"D8ReasonForDivorceBehaviourDetails\": null,\n    \"D8ReasonForDivorceDesertionDate\": null,\n    \"D8ReasonForDivorceDesertionAgreed\": null,\n    \"D8ReasonForDivorceDesertionDetails\": null,\n    \"D8ReasonForDivorceSeperationDate\": null,\n    \"D8ReasonForDivorceAdultery3rdPartyFName\": null,\n    \"D8ReasonForDivorceAdultery3rdPartyLName\": null,\n    \"D8DerivedReasonForDivorceAdultery3dPtyNm\": null,\n    \"D8ReasonForDivorceAdulteryDetails\": null,\n    \"D8ReasonForDivorceAdulteryKnowWhen\": null,\n    \"D8ReasonForDivorceAdulteryWishToName\": null,\n    \"D8ReasonForDivorceAdulteryKnowWhere\": null,\n    \"D8ReasonForDivorceAdulteryWhereDetails\": null,\n    \"D8ReasonForDivorceAdulteryWhenDetails\": null,\n    \"D8ReasonForDivorceAdulteryIsNamed\": null,\n    \"D8ReasonForDivorceAdultery3rdAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8FinancialOrder\": null,\n    \"D8FinancialOrderFor\": [],\n    \"D8HelpWithFeesNeedHelp\": null,\n    \"D8HelpWithFeesAppliedForFees\": null,\n    \"D8HelpWithFeesReferenceNumber\": null,\n    \"D8PaymentMethod\": null,\n    \"D8DivorceCostsClaim\": null,\n    \"D8DivorceIsNamed\": null,\n    \"D8DivorceClaimFrom\": [],\n    \"D8JurisdictionConfidentLegal\": null,\n    \"D8JurisdictionConnection\": [],\n    \"D8JurisdictionLastTwelveMonths\": null,\n    \"D8JurisdictionPetitionerDomicile\": null,\n    \"D8JurisdictionPetitionerResidence\": null,\n    \"D8JurisdictionRespondentDomicile\": null,\n    \"D8JurisdictionRespondentResidence\": null,\n    \"D8JurisdictionHabituallyResLast6Months\": null,\n    \"Payments\": [],\n    \"D8DocumentsUploaded\": [],\n    \"D8DocumentsGenerated\": [],\n    \"D8StatementOfTruth\": null,\n    \"D8DivorceUnit\": null,\n    \"D8Cohort\": null\n  },\n  \"event\": {\n    \"id\": \"create\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"event_data\": {\n    \"D8legalProcess\": null,\n    \"createdDate\": null,\n    \"D8ScreenHasMarriageBroken\": null,\n    \"D8ScreenHasRespondentAddress\": null,\n    \"D8ScreenHasMarriageCert\": null,\n    \"D8ScreenHasPrinter\": null,\n    \"D8DivorceWho\": null,\n    \"D8MarriageIsSameSexCouple\": null,\n    \"D8InferredPetitionerGender\": null,\n    \"D8InferredRespondentGender\": null,\n    \"D8MarriageDate\": null,\n    \"D8MarriedInUk\": null,\n    \"D8CertificateInEnglish\": null,\n    \"D8CertifiedTranslation\": null,\n    \"D8MarriagePlaceOfMarriage\": null,\n    \"D8CountryName\": null,\n    \"D8MarriagePetitionerName\": null,\n    \"D8MarriageRespondentName\": null,\n    \"D8PetitionerNameDifferentToMarriageCert\": null,\n    \"D8PetitionerEmail\": null,\n    \"D8PetitionerPhoneNumber\": null,\n    \"D8PetitionerFirstName\": null,\n    \"D8PetitionerLastName\": null,\n    \"D8DerivedPetitionerCurrentFullName\": null,\n    \"D8PetitionerNameChangedHow\": [],\n    \"D8PetitionerNameChangedHowOtherDetails\": null,\n    \"D8PetitionerContactDetailsConfidential\": null,\n    \"D8PetitionerHomeAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8DerivedPetitionerHomeAddress\": null,\n    \"D8PetitionerCorrespondenceAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8DerivedPetitionerCorrespondenceAddr\": null,\n    \"D8PetitionerCorrespondenceUseHomeAddress\": null,\n    \"D8RespondentNameAsOnMarriageCertificate\": null,\n    \"D8RespondentFirstName\": null,\n    \"D8RespondentLastName\": null,\n    \"D8DerivedRespondentCurrentName\": null,\n    \"D8DerivedRespondentSolicitorDetails\": null,\n    \"D8RespondentHomeAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8DerivedRespondentHomeAddress\": null,\n    \"D8RespondentCorrespondenceAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8DerivedRespondentCorrespondenceAddr\": null,\n    \"D8RespondentSolicitorName\": null,\n    \"D8RespondentSolicitorCompany\": null,\n    \"D8RespondentCorrespondenceSendToSol\": null,\n    \"D8RespondentSolicitorAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8DerivedRespondentSolicitorAddr\": null,\n    \"D8RespondentCorrespondenceUseHomeAddress\": null,\n    \"D8RespondentKnowsHomeAddress\": null,\n    \"D8RespondentLivesAtLastAddress\": null,\n    \"D8LivingArrangementsTogetherSeparated\": null,\n    \"D8LivingArrangementsLastLivedTogether\": null,\n    \"D8LivingArrangementsLiveTogether\": null,\n    \"D8LivingArrangementsLastLivedTogethAddr\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8DerivedLivingArrangementsLastLivedAddr\": null,\n    \"D8LegalProceedings\": null,\n    \"D8LegalProceedingsRelated\": [],\n    \"D8LegalProceedingsDetails\": null,\n    \"D8ReasonForDivorce\": null,\n    \"D8DerivedStatementOfCase\": null,\n    \"D8ReasonForDivorceBehaviourDetails\": null,\n    \"D8ReasonForDivorceDesertionDate\": null,\n    \"D8ReasonForDivorceDesertionAgreed\": null,\n    \"D8ReasonForDivorceDesertionDetails\": null,\n    \"D8ReasonForDivorceSeperationDate\": null,\n    \"D8ReasonForDivorceAdultery3rdPartyFName\": null,\n    \"D8ReasonForDivorceAdultery3rdPartyLName\": null,\n    \"D8DerivedReasonForDivorceAdultery3dPtyNm\": null,\n    \"D8ReasonForDivorceAdulteryDetails\": null,\n    \"D8ReasonForDivorceAdulteryKnowWhen\": null,\n    \"D8ReasonForDivorceAdulteryWishToName\": null,\n    \"D8ReasonForDivorceAdulteryKnowWhere\": null,\n    \"D8ReasonForDivorceAdulteryWhereDetails\": null,\n    \"D8ReasonForDivorceAdulteryWhenDetails\": null,\n    \"D8ReasonForDivorceAdulteryIsNamed\": null,\n    \"D8ReasonForDivorceAdultery3rdAddress\": {\n      \"AddressLine1\": null,\n      \"AddressLine2\": null,\n      \"AddressLine3\": null,\n      \"PostCode\": null,\n      \"PostTown\": null,\n      \"County\": null,\n      \"Country\": null\n    },\n    \"D8FinancialOrder\": null,\n    \"D8FinancialOrderFor\": [],\n    \"D8HelpWithFeesNeedHelp\": null,\n    \"D8HelpWithFeesAppliedForFees\": null,\n    \"D8HelpWithFeesReferenceNumber\": null,\n    \"D8PaymentMethod\": null,\n    \"D8DivorceCostsClaim\": null,\n    \"D8DivorceIsNamed\": null,\n    \"D8DivorceClaimFrom\": [],\n    \"D8JurisdictionConfidentLegal\": null,\n    \"D8JurisdictionConnection\": [],\n    \"D8JurisdictionLastTwelveMonths\": null,\n    \"D8JurisdictionPetitionerDomicile\": null,\n    \"D8JurisdictionPetitionerResidence\": null,\n    \"D8JurisdictionRespondentDomicile\": null,\n    \"D8JurisdictionRespondentResidence\": null,\n    \"D8JurisdictionHabituallyResLast6Months\": null,\n    \"Payments\": [],\n    \"D8DocumentsUploaded\": [],\n    \"D8DocumentsGenerated\": [],\n    \"D8StatementOfTruth\": null,\n    \"D8DivorceUnit\": null,\n    \"D8Cohort\": null\n  }\n}"))
      .check(jsonPath("$.id").saveAs("caseId")))

      .pause(3)

  val GetAssignedUsers = 

    exec(http("AAC_010_GetAssignedUsersAndRoles")
      .get(ccdDataStoreUrl + "/case-users")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("experimental","true")
      .queryParam("case_ids", "${caseToShare}"))

    .pause(Environment.constantthinkTime)

  /////////////////////////////////////////////////////////////////////////////////

  //Respondent Journey Requests - Create Case & Update Supplementary Case Data//

  val RJCreateCase = 

    exec(http("GetEventToken")
        .get(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/event-triggers/solicitorCreateApplication/token")
        .header("ServiceAuthorization", "Bearer ${bearerToken}")
        .header("Authorization", "Bearer ${access_token}")
        .header("Content-Type","application/json")
        .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("CreateCase")
        .post(ccdDataStoreUrl + "/caseworkers/${idamId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases")
        .header("ServiceAuthorization", "Bearer ${bearerToken}")
        .header("Authorization", "Bearer ${access_token}")
        .header("Content-Type","application/json")
        .body(StringBody("{\n  \"data\": {\n    \"solsSolicitorFirmName\": \"jon & ola\",\n    \"solsSolicitorAddress\": {\n      \"AddressLine1\": \"Flat 12\",\n      \"AddressLine2\": \"Bramber House\",\n      \"AddressLine3\": \"Seven Kings Way\",\n      \"PostTown\": \"Kingston Upon Thames\",\n      \"County\": \"\",\n      \"PostCode\": \"KT2 5BU\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"solsSolicitorAppReference\": \"test\",\n    \"solsSolicitorEmail\": \"ccdorg-mvgvh_mcccd.user52@mailinator.com\",\n    \"solsSolicitorPhoneNumber\": null,\n    \"organisationPolicy\": {\n      \"OrgPolicyCaseAssignedRole\": \"[Claimant]\",\n      \"OrgPolicyReference\": null,\n      \"Organisation\": {\n        \"OrganisationID\": \"IGWEE4D\",\n        \"OrganisationName\": \"ccdorg-mvgvh\"\n      }\n    }\n  },\n  \"event\": {\n    \"id\": \"solicitorCreateApplication\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
        //.body(StringBody(" {   \n \t\"data\" : {\n      \"TextField\" : \"textField1\",\n      \"TextAreaField\" : \"textAreaField1\",\n      \"AddressField\" : {\n        \"AddressLine1\" : \"102 Petty France\",\n        \"AddressLine2\" : \"CCD\",\n        \"AddressLine3\" : \"c/o HMCTS Reform\",\n        \"Country\" : \"UK\"\n      },\n      \"OrganisationPolicyField1\" : {\n        \"OrgPolicyCaseAssignedRole\" : \"[Claimant]\",\n        \"OrgPolicyReference\" : \"ref\",\n        \"Organisation\" : {\n          \"OrganisationID\" : \"orgID1\",\n          \"OrganisationName\" : \"orgName1\"\n        }\n      },\n      \"OrganisationPolicyField2\" : {\n        \"OrgPolicyCaseAssignedRole\" : \"[Defendant]\",\n        \"OrgPolicyReference\" : \"ref\",\n        \"Organisation\" : {\n          \"OrganisationID\" : \"orgID2\",\n          \"OrganisationName\" : \"orgName2\"\n        }\n      }\n    },\n    \"event\" : {\n      \"id\" : \"createCase\",\n      \"summary\" : \"\",\n      \"description\" : \"\"\n    },\n    \"event_token\" : \"${eventToken}\",\n    \"ignore_warning\" : false,\n    \"draft_id\" : null\n \t\n }"))
        .check(jsonPath("$.id").saveAs("caseId")))

    .pause(Environment.constantthinkTime)

  val RJUpdateSupplementaryCaseData =

    exec(http("CCD_UpdateSupplementaryCaseData")
        .post(ccdDataStoreUrl + "/cases/${caseId}/supplementary-data")
        .header("ServiceAuthorization", "Bearer ${bearerToken}")
        .header("Authorization", "Bearer ${access_token}")
        .header("Content-Type","application/json")
        .body(StringBody("{\n    \"supplementary_data_updates\": {\n        \"$inc\": {\n        \t\"orgs_assigned_users.aca-11\": 1\n        }\n    }\n}")))
        //.body(StringBody("{\n\t\"supplementary_data_updates\": {\n\t\t\"$set\": {\n\t\t\t\"orgs_assigned_users.OrgA\": 22,\n            \"orgs_assigned_users.OrgC\": \"Test\"\n\t\t},\n\t\t\"$inc\": {\n\t\t\t\"orgs_assigned_users.OrgB\": 1\n\t\t}\n\t}\n}")))

    .pause(Environment.constantthinkTime)

  //Respondent Journey Requests - Create Case & Update Supplementary Case Data//

  val RJElasticSearchGetRef =

    feed(feedCaseSearchData)

    .exec(http("CCD_SearchCaseEndpoint_ElasticSearch")
      .post(ccdDataStoreUrl + "/searchCases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .queryParam("ctid", "${caseType}") //${caseType}
      .body(StringBody("{ \n   \"query\":{ \n      \"bool\":{ \n         \"filter\":{ \n            \"wildcard\":{ \n               \"reference\":\"${caseId}\"\n            }\n         }\n      }\n   }\n}"))
      .check(status in  (200)))

    .pause(Environment.constantthinkTime)

  val ETGetToken = 

    exec(http("GetEventToken")
        .get(ccdDataStoreUrl + "/caseworkers/554156/jurisdictions/EMPLOYMENT/case-types/Leeds/event-triggers/initiateCase/token")
        .header("ServiceAuthorization", "Bearer ${bearerToken}")
        .header("Authorization", "Bearer ${access_token}")
        .header("Content-Type","application/json")
        .check(jsonPath("$.token").saveAs("eventToken")))

    .pause(4)

  val feedEthosCaseRef = csv("EthosCaseRef.csv")

  val ETCreateCase =

    feed(feedEthosCaseRef)

    .exec(http("CreateCase")
        .post(ccdDataStoreUrl + "/caseworkers/554156/jurisdictions/EMPLOYMENT/case-types/Leeds/cases")
        .header("ServiceAuthorization", "Bearer ${bearerToken}")
        .header("Authorization", "Bearer ${access_token}")
        .header("Content-Type","application/json")
        .body(ElFileBody("Ethos_SingleCase.json"))
        .check(jsonPath("$.id").saveAs("caseId"))
        .check(status.saveAs("statusvalue")))

//    .doIf(session=>session("statusvalue").as[String].contains("200")) {
//      exec {
//        session =>
//          val fw = new BufferedWriter(new FileWriter("CreateSingles.csv", true))
//          try {
//            fw.write(session("caseId").as[String] + "\r\n")
//          }
//          finally fw.close()
//          session
//      }
//    }

  //val ETCreateMultipleCase =


}