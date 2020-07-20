package uk.gov.hmcts.ccd.corecasedata.scenarios

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._

object CaseSharing {

  val config: Config = ConfigFactory.load()
  //val s2sToken = CcdTokenGenerator.generateS2SToken()
  //val IdAMToken = CcdTokenGenerator.generateSIDAMUserTokenInternal()

  val IdamURL = Environment.idamURL
  val IdamAPI = Environment.idamAPI
  val CCDEnvurl = Environment.ccdEnvurl
  val s2sUrl = Environment.s2sUrl
  val ccdRedirectUri = "https://ccd-data-store-api-perftest.service.core-compute-perftest.internal/oauth2redirect"
  val ccdDataStoreUrl = "http://ccd-data-store-api-perftest.service.core-compute-perftest.internal"
  val xuiRedirectUri = "https://manage-case.perftest.platform.hmcts.net/oauth2/callback"
  val xuiClientId = "xuiwebapp"
  val xuiwebappClientSecret = "yB71mnPeypP3HlcN"
  val xuiScope = "openid profile roles manage-user create-user manage-roles search-user"
  val ccdClientId = "ccd_gateway"
  val ccdGatewayClientSecret = "vUstam6brAsT38ranuwRut65rakec4u6"
  val ccdScope = "openid profile authorities acr roles openid profile roles"
  val feedCSUserData = csv("CaseSharingUsers_1-4.csv").circular

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val constantThinkTime = Environment.constantthinkTime
  val MinWaitForNextIteration = Environment.minWaitForNextIteration
  val MaxWaitForNextIteration = Environment.maxWaitForNextIteration

  val headers_0 = Map( //Authorization token needs to be generated with idam login
    "Authorization" -> "AdminApiAuthToken eyJtb25rZXkiOiJCeVZlcVBQQVpMSEVNczllekxxOHZCUTdBY28uKkFBSlRTUUFDTURJQUFsTkxBQnhZZWt0RU5EbDBXa3RRWW5sUmIwWjNPSEppYUV0d2N6ZEdkazA5QUFSMGVYQmxBQU5EVkZNQUFsTXhBQUl3TkEuLioiLCJyYWJiaXQiOiJzZXNzaW9uLWp3dD1leUowZVhBaU9pSktWMVFpTENKcmFXUWlPaUp2Y0dWdWFXUnRMV3AzZEhObGMzTnBiMjVvYldGakxXdGxlU0lzSW1OMGVTSTZJa3BYVkNJc0ltRnNaeUk2SWtoVE1qVTJJbjAuWlhsS01HVllRV2xQYVVwTFZqRlJhVXhEU25KaFYxRnBUMmxLZWxwWVNqSmFXRWwwV1RKV2VXUkRTWE5KYlZaMVdYbEpOa2xyUlhoTmFtaEVVV3ROZEZOR1RYbE9WRmxwVEVOS2FHSkhZMmxQYVVwVFZUQkZlRmg2VldsbVVTNVNkRTgwWW5sRlZtdDVjbGhtU3pBM1pHOHhlVjh3ZGxneVdsRm1lREkxVEROb2RsQnJjamQzWDFaTWNDMXVabmx6WDBoT1psVTNNMTlOU3pCU1pFUkhNbkk1YVVad2QweE9aMWxqVlhWU05FaE1UVEY0YW1GWVZtWlRSRzVoWkU5UFZVd3pXamw1VjJSdFpEZDFXVTFoZUdJNGVHdzBibU41WVRkeFFURmtiamR1VVRkNk9GTndjblprUTBwd01ISkJlV2RGWTNKbk9VaHlValV6YjBONVgwODJSRkF3U2pSSVdUZGxOMjEzVkhacU5ub3hRakIwUkRVMlNtOVlRbnBLWmtRME9VTjJjMDFuVVhWMVZEbHZPRmxVY21vd05rRTNUbVoxV1dSMlgxODNlR1kyUzBkaFNEaFVWVGRWYVRaeFlUWnZlamROZVhoYU5ETTVkMU5sY3psM1QxOURTMjVqUzFGS1UxZ3RSbWQwYkY5YU1GcGlWVXQwWm5SQllVbExSbGxDUkRWMFVGQnJlRVpMYVhWMVdtMWFjbVYxTW1aQmMxWnRWVjlqZGxWS1VIWjViWFYyTjJsb2NrdHhTVGxQWmxONFpHY3VWamRaWDJwT2FUbHpMVGRFVjNGNFpFOUhNa040VVM1SFFuUmtZVmxDYVVWclVuVkNaWHB4TlVKRE1EQXRXbmRQZUdkd1FXbGpOVmhZT0dkbGNWSnlPRW81VFhNd1p6WmhaVFJFV1RFMmMxWm5URmRqV2toeExXbEpOelV3U1VGQloxaDRNRE40UjFwRllWQlBWMUJtUTBwUllqWktkbXA0VWxKa1ptcEJWMTkzWlc1TWNrUjBUbnBrT0Vzd1RtOTVPV1ZtT0ZOSlZFRlJja0U1VjB0a1pqZ3hXakZNZFhseGF6QlBUbTB4UjB0SlJTMWZTM1JVVGxnMmNFbDZZeTFUYlVvd1NrOU9iVzA1UjBKUVJtbDZiRzR5YTBWelRGcEdibWN5UjAxcFlYVmlkSEppZVUxUU0wMW1OMVpHVDJGaE1VWjFVRTV2Y2tJMWVUTk1WVTQxWkZoYU1EQm9jbFZrZDFCWWFFUm1iMmx6YW0xQ1psRlZNbWx6TmtzMllWWmxYMkpEYm1oU1IzbzVYMkZ0Y201bFRWSnNhbVpSYkhkYWRGQmZXbTlSV1ZGWmJrWndRM281VFRaMVJVeHlUV3BEV0hwTGVVSk9hbm8xYURoUWIzZHJaWEZ2YTFwNlRGWmhjMkoxT1MxTmJFaE9UREpsV1ZJelVEVkVZVTFWYlVRMFZGZEJVMVI2UWpsS1YwWmpVbEJtYjJsNFJ6WjBRazU1V0ROd1VXSXROa1ZmU0hKMFFYVlFVMnhxTkhKaU1pMTVaek13UlZGVFRYb3lSV3BpYTBGU1IxbHJaV2h2Y0hkQlFVWXdXV2xJWWswMFVFUjBUVnB3WDNsalNIUlFXVlpTWlRsNlpYRlhUVzEyWTJ4RWJUVTJWRmhpVDI5VVprOVVZbTlMY0ROVVFVbElRekIyVW1kWlpHMXpUbWxhY0haNFowY3RNVGxyTFVwVllWOHdPRGN6VTFoV1RtRkhaRU13WTNSQldWbFZhVEpFY0VsQloxUnhiMG80U1RKR1FsZHJSWHBwV25NMWIwaHZja2xQWVV0MVFXWklMWFl3YzBwbk5VWkNhbmRIVG10VVJtdDZialo2T0UxbVIzWkZlalpaWDBSNU1XNXVSWFpNTFZwRFNscDJVREZHYTFoNFRqTmFaV0owT1RaNFJ6UmFWemt4UmtsMlRpMU1jMnBwUmtKUVZuRlhRM0pxTjA5Uk16WXlNaTFoUVdFNVdqUnlNVWRKTFZacGRqVlJhVXhNZW1oSFZYVnZSMnN6VWtScGFYcHpTR1JmY200MlVEWlJXRVJDU0RsSmFteEZkMEpoWm0welRscDVTVFJDU0dwWFVXdFdiMEphUjIxMFkwTnRNRlJvVFhCWVQzWkpWM1I0ZUZwd04yRndNRGR0Tm5WRlkzTmZXVXhPWVZsSWEwTlNNRFJ5WldwTVdEZHRkR1J0VUdSVWJpMVlhRW96VDBveFkwaExZa3RJYWtZd05XVXlVMkoxTnpaa01rODBkMHhGTmpSSldIWjRhVlI2U2xkdUxYbGtjSEZMVkRKSldFVnFXSGcxTjBWWlRIRnFMVGt6VVZwdE9YTkxaRXhaZUhBd09DNVdlSFUyTVdaT2RuVkVNamxPVDNkTFQxRnNaWGRSLmEtZjdnbDhuN1hHQ0RBUFNCYll2NW1JaWpkWUE0ZE4zc2JFb1Z6RG95T2s7IFBhdGg9LzsgSHR0cE9ubHkifQ==",
    "Content-Type" -> "application/json")

  val CDSGetRequest =

    feed(feedCSUserData)

    .exec(http("01_Create_GetS2SToken")
      .post(s2sUrl + "/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"ccd_data\"}"))
      .check(bodyString.saveAs("bearerToken")))

    .exec(http("01_Create_OIDC01_Authenticate")
      .post(IdamAPI + "/authenticate")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("username", "${caseSharingUser}")
      .formParam("password", "Pass19word")
      .check(status is 200)
      .check(headerRegex("Set-Cookie", "Idam.Session=(.*)").saveAs("authCookie")))

    .exec(http("01_Create_OIDC02_Authorize_CCD")
      .post(IdamAPI + "/o/authorize?response_type=code&client_id=" + ccdClientId + "&redirect_uri=" + ccdRedirectUri + "&scope=" + ccdScope).disableFollowRedirect
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Cookie", "Idam.Session=${authCookie}")
      .header("Content-Length", "0")
      .check(status is 302)
      .check(headerRegex("Location", "code=(.*)&client_id").saveAs("code")))

    //MkVIBs0dfCwTIBeU-enTRbfGUh0

    .exec(http("01_Create_OIDC03_Token_CCD")
      .post(IdamAPI + "/o/token?grant_type=authorization_code&code=${code}&client_id=" + ccdClientId +"&redirect_uri=" + ccdRedirectUri + "&client_secret=" + ccdGatewayClientSecret)
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      //.header("Cookie", "Idam.Session=${authCookie}")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("access_token")))

    .exec(http("01_Create_GetIdamUserID")
      .get("https://idam-api.perftest.platform.hmcts.net/users?email=${caseSharingUser}")
      .headers(headers_0)
      .check(jsonPath("$.id").saveAs("userId")))

    .exec(http("01_Create_GetEventToken")
      .get(ccdDataStoreUrl + "/caseworkers/${userId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/event-triggers/solicitorCreateApplication/token")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("01_Create_CreateCase")
      .post(ccdDataStoreUrl + "/caseworkers/${userId}/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type","application/json")
      .body(StringBody("{\n  \"data\": {\n    \"solsSolicitorFirmName\": \"jon & ola\",\n    \"solsSolicitorAddress\": {\n      \"AddressLine1\": \"Flat 12\",\n      \"AddressLine2\": \"Bramber House\",\n      \"AddressLine3\": \"Seven Kings Way\",\n      \"PostTown\": \"Kingston Upon Thames\",\n      \"County\": \"\",\n      \"PostCode\": \"KT2 5BU\",\n      \"Country\": \"United Kingdom\"\n    },\n    \"solsSolicitorAppReference\": \"test\",\n    \"solsSolicitorEmail\": \"ccdorg-mvgvh_mcccd.user52@mailinator.com\",\n    \"solsSolicitorPhoneNumber\": null,\n    \"organisationPolicy\": {\n      \"OrgPolicyCaseAssignedRole\": \"[Claimant]\",\n      \"OrgPolicyReference\": null,\n      \"Organisation\": {\n        \"OrganisationID\": \"IGWEE4D\",\n        \"OrganisationName\": \"ccdorg-mvgvh\"\n      }\n    }\n  },\n  \"event\": {\n    \"id\": \"solicitorCreateApplication\",\n    \"summary\": \"\",\n    \"description\": \"\"\n  },\n  \"event_token\": \"${eventToken}\",\n  \"ignore_warning\": false,\n  \"draft_id\": null\n}"))
      .check(jsonPath("$.id").saveAs("caseId")))

  val CaseShareRequest =

    exec(http("02_ACA_GetS2SToken")
      .post("http://rpe-service-auth-provider-perftest.service.core-compute-perftest.internal/testing-support/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"xui_webapp\"}"))
      .check(bodyString.saveAs("bearerToken")))

    //.feed(feedCSUserData)

    .exec(http("02_ACA_OIDC01_Authenticate")
      .post(IdamAPI + "/authenticate")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("username", "${caseSharingUser}")
      .formParam("password", "Pass19word")
      .check(status is 200)
      .check(headerRegex("Set-Cookie", "Idam.Session=(.*)").saveAs("authCookie")))

    .exec(http("02_ACA_OIDC02_Authorize_XUI")
      .post(IdamAPI + "/o/authorize?response_type=code&client_id=" + xuiClientId + "&redirect_uri=" + xuiRedirectUri + "&scope=" + xuiScope).disableFollowRedirect
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Cookie", "Idam.Session=${authCookie}")
      .header("Content-Length", "0")
      .check(status is 302)
      .check(headerRegex("Location", "code=(.*)&client_id").saveAs("code")))

    .exec(http("02_ACA_OIDC03_Token_XUI")
      .post(IdamAPI + "/o/token?grant_type=authorization_code&code=${code}&client_id=" + xuiClientId +"&redirect_uri=" + xuiRedirectUri + "&client_secret=" + xuiwebappClientSecret)
      .header("Content-Type", "application/x-www-form-urlencoded")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("access_token")))

//  .exec {
//      session =>
//        println(session("bearerToken").as[String])
//        println(session("access_token").as[String])
//        session
//    }

    .exec(http("02_ACA_IdamGetAssigneeUserID")
      .get("https://idam-api.perftest.platform.hmcts.net/users?email=ccdorg-mvgvh_mcccd.user53@mailinator.com")
      .headers(headers_0)
      .check(jsonPath("$.id").saveAs("userId")))

    .exec(http("02_ACA_CS_010_PUT")
      .put("http://aac-manage-case-assignment-perftest.service.core-compute-perftest.internal/case-assignments")
      .header("ServiceAuthorization", "Bearer ${bearerToken}")
      .header("Authorization", "Bearer ${access_token}")
      .header("Content-Type", "application/json")
      .header("Accept", "application/json")
      .body(StringBody("{\"assignee_id\":\"${userId}\",\"case_id\":${caseId},\"case_type_id\":\"GrantOfRepresentation\"}")))
}