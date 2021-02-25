package uk.gov.hmcts.ccd.corecasedata.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils._

object ExuiView {

val baseURL = Environment.xuiBaseURL
val IdamUrl = Environment.idamURL
val feedXUIUserData = csv("XUISearchUsers.csv").circular
val feedXUISearchData = csv("XUISearchData.csv").circular
val caseActivityFeeder = csv("XUICaseActivityData.csv").random
val caseActivityListFeeder = csv("CaseActivityListData.csv").random

  val manageCasesHomePage =
    // tryMax(2) {

      exec(http("XUI_010_005_Homepage")
        .get(baseURL + "/")
        .headers(LoginHeader.headers_0)
        .check(status.in(200,304))).exitHereIfFailed
    
      .exec(http("XUI_010_010_HomepageConfigUI")
        .get(baseURL + "/external/configuration-ui")
        .headers(LoginHeader.headers_1))
    
      .exec(http("XUI_010_015_HomepageConfigJson")
        .get(baseURL + "/assets/config/config.json")
        .headers(LoginHeader.headers_1))
    
      .exec(http("XUI_010_020_HomepageTCEnabled")
        .get(baseURL + "/api/configuration?configurationKey=termsAndConditionsEnabled")
        .headers(LoginHeader.headers_1))
    
      .exec(http("XUI_010_025_HomepageIsAuthenticated")
        .get(baseURL + "/auth/isAuthenticated")
        .headers(LoginHeader.headers_1))
    
      .exec(http("XUI_010_030_AuthLogin")
        .get(baseURL + "/auth/login")
        .headers(LoginHeader.headers_4)
        .check(css("input[name='_csrf']", "value").saveAs("csrfToken"))
        .check(regex("oauth2/callback&state=(.*)&nonce").saveAs("state"))
        .check(regex("&nonce=(.*)&response_type").saveAs("nonce")))
        
    //}

  //==================================================================================
  //Business process : Enter the login details and submit
  //below requests are main login and relavant sub requests as part of the login submission
  //==================================================================================

  val manageCaseslogin =
    tryMax(2) {

      feed(feedXUIUserData)

      /*==========================================
      User Login Steps
      ==========================================*/

      .exec(http("XUI_020_005_SignIn")
        //.post(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri=" + baseURL + "/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
        // .post(IdamUrl + "/login?response_type=code&redirect_uri=" + baseURL + "%2Foauth2%2Fcallback&scope=profile%20openid%20roles%20manage-user%20create-user&state=${state}&client_id=xuiwebapp")
        .post(IdamUrl + "/login?client_id=xuiwebapp&redirect_uri=" + baseURL + "/oauth2/callback&state=${state}&nonce=${nonce}&response_type=code&scope=profile%20openid%20roles%20manage-user%20create-user&prompt=")
           .formParam("username", "${email}")
           .formParam("password", "Password12")
           .formParam("save", "Sign in")
           .formParam("selfRegistrationEnabled", "false")
           .formParam("_csrf", "${csrfToken}")
           .headers(LoginHeader.headers_login_submit)
           .check(status.in(200, 304, 302))).exitHereIfFailed

      // .exec(getCookieValue(
      //   CookieKey("__userid__").withDomain("manage-case.perftest.platform.hmcts.net").saveAs("myUserId")))

      .exec(http("XUI_020_010_Homepage")
            .get(baseURL + "/external/config/ui")
            .headers(LoginHeader.headers_0)
            .check(status.in(200,304)))

      .exec(http("XUI_020_015_SignInTCEnabled")
            .get(baseURL + "/api/configuration?configurationKey=termsAndConditionsEnabled")
            .headers(LoginHeader.headers_38)
            .check(status.in(200, 304)))

      .repeat(1, "count") {
        exec(http("XUI_020_020_AcceptT&CAccessJurisdictions${count}")
             .get(baseURL + "/aggregated/caseworkers/:uid/jurisdictions?access=read")
             .headers(LoginHeader.headers_access_read)
             .check(status.in(200, 304, 302)))
      }

        // .exec(http("XUI_020_025_GetWorkBasketInputs")
        //       .get(baseURL + "/data/internal/case-types/DIVORCE/work-basket-inputs")
        //       .headers(LoginHeader.headers_17))

        // .exec(http("XUI_020_030_GetPaginationMetaData")
        //       .get(baseURL + "/data/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE/cases/pagination_metadata?state=SOTAgreementPayAndSubmitRequired")
        //       .headers(LoginHeader.headers_0))

        // .exec(http("XUI_020_035_GetDefaultWorkBasketView")
        //       .get(baseURL + "/aggregated/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE/cases?view=WORKBASKET&state=SOTAgreementPayAndSubmitRequired&page=1")
        //       .headers(LoginHeader.headers_0))

      .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain("manage-case.perftest.platform.hmcts.net").saveAs("xsrfToken")))

      .pause(Environment.constantthinkTime)

    }

  val CaseActivityList =

    feed(caseActivityListFeeder)

    .exec(http("XUI_CaseActivityList")
      .get(baseURL + "/activity/cases/${caseList}/activity")
      .header("X-XSRF-TOKEN", "${xsrfToken}")
      .headers(XuiHeaders.headers_23)
      .check(responseTimeInMillis.saveAs("listResponseTime")))

    // .exec {
    //   session =>
    //     println(session("listResponseTime").as[String])
    //     session
    // }

    .pause(session => session("listResponseTime").validate[Int].map(i => Environment.caseActivityPause * 1000 - i milliseconds))

  val CaseActivityOpenCase =

    exec(http("XUI_ViewCase")
      .get(baseURL + "/data/internal/cases/${caseRef}")
      .headers(XuiHeaders.headers_5)
      .header("X-XSRF-TOKEN", "${xsrfToken}"))

  val CaseActivitySingle = 

    feed(caseActivityFeeder)

    .exec(http("XUI_CaseActivity_Post")
      .post(baseURL + "/activity/cases/${caseRef}/activity")
      .headers(XuiHeaders.headers_CSPost)
      .header("X-XSRF-TOKEN", "${xsrfToken}")
      .body(StringBody("{\n  \"activity\": \"view\"\n}"))
      .check(responseTimeInMillis.saveAs("responseTimePost")))

    .exec(http("XUI_CaseActivity_Get")
      .get(baseURL + "/activity/cases/${caseRef}/activity")
      .headers(XuiHeaders.headers_CSGet)
      .header("X-XSRF-TOKEN", "${xsrfToken}")
      .check(responseTimeInMillis.saveAs("responseTimeGet")))

    .exec{ session =>
      val responseTimePost = session("responseTimePost").as[Int]
      val responseTimeGet = session("responseTimeGet").as[Int]
      val totalThinktime = Environment.caseActivityPause * 1000 - responseTimePost - responseTimeGet
      session.set("thinktime", totalThinktime)
    }

    // .exec {
    //   session =>
    //     println(session("responseTimePost").as[String])
    //     println(session("responseTimeGet").as[String])
    //     println(session("thinktime").as[String])
    //     session
    // }

    .pause(session => session("thinktime").validate[Int].map(i => i milliseconds))

  val searchCase = 

    feed(feedXUISearchData)

    /*==========================================
    Execute a Search from the Case List View
    ==========================================*/

    .exec(http("XUI_${jurisdiction}SearchResults_WorkbasketMetadata")
        .get(baseURL + "/data/caseworkers/:uid/jurisdictions/${jurisdiction}/case-types/${caseType}/cases/pagination_metadata?state=${state}")
        .headers(ProbateHeader.headers_search)
        .header("X-XSRF-TOKEN", "${xsrfToken}")
        .check(status.in(200,304)))

    .exec(http("XUI_${jurisdiction}SearchResults_WorkbasketUseCase")
        .get(baseURL + "/aggregated/caseworkers/:uid/jurisdictions/${jurisdiction}/case-types/${caseType}/cases?view=WORKBASKET&state=c&page=1")
        //.post(baseURL + "/data/internal/searchCases?ctid=${caseType}&use_case=WORKBASKET&view=WORKBASKET&page=1")
        .headers(ProbateHeader.headers_search)
        .header("X-XSRF-TOKEN", "${xsrfToken}")
        .check(status.in(200,304)))

    .pause(Environment.constantthinkTime)

    /*==========================================
    Click on the Find Case link on top right
    ==========================================*/

    .exec(http("XUI_FindCase_HealthCheck")
        .get(baseURL + "/api/healthCheck?path=%2Fcases%2Fcase-search")
        .headers(ProbateHeader.headers_0))

    .exec(http("XUI_${jurisdiction}SearchResults_SearchMetadata")
        .get(baseURL + "/data/caseworkers/:uid/jurisdictions/${jurisdiction}/case-types/${caseType}/cases/pagination_metadata?state=${caseType}")
        .headers(ProbateHeader.headers_1)
        .header("X-XSRF-TOKEN", "${xsrfToken}"))

    .exec(http("XUI_${jurisdiction}SearchResults_SearchUseCase")
        .get(baseURL + "/aggregated/caseworkers/:uid/jurisdictions/${jurisdiction}/case-types/${caseType}/cases?view=SEARCH&page=1&state=v")
        .headers(ProbateHeader.headers_1)
        .header("X-XSRF-TOKEN", "${xsrfToken}"))

    .exec(http("XUI_FindCase_JurisdictionsRead")
        .get(baseURL + "/aggregated/caseworkers/:uid/jurisdictions?access=read")
        .headers(ProbateHeader.headers_1)
        .header("X-XSRF-TOKEN", "${xsrfToken}"))

    .exec(http("XUI_FindCase_SearchInputs")
        .get(baseURL + "/data/internal/case-types/${caseType}/search-inputs")
        .headers(ProbateHeader.headers_1)
        .header("X-XSRF-TOKEN", "${xsrfToken}")
        .header("experimental", "true")
        .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-search-input-details.v2+json;charset=UTF-8"))

    .pause(Environment.constantthinkTime)

    /*==========================================
    Execute a Search from the Find Case View
    ==========================================*/

    .exec(http("XUI_${jurisdiction}SearchResults_SearchMetadata")
        .get(baseURL + "/data/caseworkers/:uid/jurisdictions/${jurisdiction}/case-types/${caseType}/cases/pagination_metadata?state=${state}")
        .headers(ProbateHeader.headers_search)
        .header("X-XSRF-TOKEN", "${xsrfToken}")
        .check(status.in(200,304)))

    .exec(http("XUI_${jurisdiction}SearchResults_SearchUseCase")
        .get(baseURL + "/aggregated/caseworkers/:uid/jurisdictions/${jurisdiction}/case-types/${caseType}/cases?view=SEARCH&state=${state}&page=1")
        //.post(baseURL + "/data/internal/searchCases?ctid=${caseType}&use_case=SEARCH&view=SEARCH&page=1")
        .headers(ProbateHeader.headers_search)
        .header("X-XSRF-TOKEN", "${xsrfToken}")
        .check(status.in(200,304)))

    .pause(Environment.constantthinkTime)

    /*==========================================
    Click on Case List
    ==========================================*/

    .exec(http("XUI_CaseList_HealthCheck")
        .get(baseURL + "/api/healthCheck?path=%2Fcases")
        .headers(ProbateHeader.headers_0))

    .exec(http("XUI_CaseList_JurisdictionsRead")
        .get(baseURL + "/aggregated/caseworkers/:uid/jurisdictions?access=read")
        .headers(ProbateHeader.headers_1)
        .header("X-XSRF-TOKEN", "${xsrfToken}"))

    .exec(http("XUI_${jurisdiction}SearchResults_WorkbasketMetadata")
        .get(baseURL + "/data/caseworkers/:uid/jurisdictions/${jurisdiction}/case-types/${caseType}/cases/pagination_metadata?state=${state}")
        .headers(ProbateHeader.headers_1)
        .header("X-XSRF-TOKEN", "${xsrfToken}"))

    .exec(http("XUI_CaseList_WorkBasketInputs")
        .get(baseURL + "/data/internal/case-types/${caseType}/work-basket-inputs")
        .headers(ProbateHeader.headers_1)
        .header("experimental", "true")
        .header("X-XSRF-TOKEN", "${xsrfToken}")
        .header("Accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-workbasket-input-details.v2+json;charset=UTF-8"))

    .exec(http("XUI_${jurisdiction}SearchResults_WorkbasketUseCase")
        .get(baseURL + "/aggregated/caseworkers/:uid/jurisdictions/${jurisdiction}/case-types/${caseType}/cases?view=WORKBASKET&state=${state}s&page=1")
        .headers(ProbateHeader.headers_1)
        .header("X-XSRF-TOKEN", "${xsrfToken}"))

    .pause(Environment.constantthinkTime)


//     .exec(http("XUI_ProbateSearchResults_SortFirstname")
//         //.get(baseURL + "/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases?view=WORKBASKET&page=1")
//         .post(baseURL + "/data/internal/searchCases?ctid=GrantOfRepresentation&use_case=SEARCH&view=SEARCH&page=1")
//         .headers(ProbateHeader.headers_search)
//         .header("X-XSRF-TOKEN", "${xsrfToken}")
//         .body(StringBody("{\n  \"sort\": {\n    \"column\": \"deceasedForenames\",\n    \"order\": 1,\n    \"type\": \"Text\"\n  },\n  \"size\": 25\n}"))
//         .check(status.in(200,304)))

//     .pause(Environment.constantthinkTime)


// val searchDivorceCase = 

//     exec(http("XUI_DivorceSearchResults_WorkbasketMetadata")
//         .get(baseURL + "/data/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE/cases/pagination_metadata")
//         .headers(ProbateHeader.headers_search)
//         //.header("X-XSRF-TOKEN", "${xsrfToken}")
//         .check(status.in(200,304)))

//     .exec(http("XUI_DivorceSearchResults_WorkbasketUseCase")
//         .get(baseURL + "/aggregated/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE/cases?view=WORKBASKET&page=1")
//         //.post(baseURL + "/data/internal/searchCases?ctid=DIVORCE&use_case=WORKBASKET&view=WORKBASKET&page=1")
//         .headers(ProbateHeader.headers_search)
//         //.header("X-XSRF-TOKEN", "${xsrfToken}")
//         .check(status.in(200,304)))

//     .pause(Environment.constantthinkTime)

    .exec(http("XUI_DivorceSearchResults_SearchMetadata")
        .get(baseURL + "/data/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE/cases/pagination_metadata")
        .headers(ProbateHeader.headers_search)
        //.header("X-XSRF-TOKEN", "${xsrfToken}")
        .check(status.in(200,304)))

    .exec(http("XUI_DivorceSearchResults_SearchUseCase")
        .get(baseURL + "/aggregated/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE/cases?view=SEARCH&page=1")
        //.post(baseURL + "/data/internal/searchCases?ctid=DIVORCE&use_case=SEARCH&view=SEARCH&page=1")
        .headers(ProbateHeader.headers_search)
        //.header("X-XSRF-TOKEN", "${xsrfToken}")
        .check(status.in(200,304)))

//     .pause(Environment.constantthinkTime)

// //     .exec(http("XUI_DivorceSearchResults_SortCreatedDate")
// //         //.get(baseURL + "/aggregated/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE/cases?view=WORKBASKET&page=1")
// //         .post(baseURL + "/data/internal/searchCases?ctid=DIVORCE&use_case=SEARCH&view=SEARCH&page=1")
// //         .headers(ProbateHeader.headers_search)
// //         .header("X-XSRF-TOKEN", "${xsrfToken}")
// //         .body(StringBody("{\n  \"sort\": {\n    \"column\": \"[CREATED_DATE]\",\n    \"order\": 1,\n    \"type\": \"DateTime\"\n  },\n  \"size\": 25\n}"))
// //         .check(status.in(200,304)))

// //     .pause(Environment.constantthinkTime)

val headers_10 = Map(
    "Pragma" -> "no-cache",
    "Sec-Fetch-Dest" -> "document",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "same-origin",
    "Sec-Fetch-User" -> "?1")

val XUILogout = 

    exec(http("XUI_Logout")
        .get(baseURL + "/auth/logout")
        .headers(headers_10))

    .pause(Environment.constantthinkTime)


val headers_0 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "document",
		"Sec-Fetch-Mode" -> "navigate",
		"Sec-Fetch-Site" -> "none",
		"Sec-Fetch-User" -> "?1",
		"Upgrade-Insecure-Requests" -> "1")

val headers_1 = Map(
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "empty",
		"Sec-Fetch-Mode" -> "cors",
		"Sec-Fetch-Site" -> "same-origin")

val headers_4 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
		"Origin" -> "https://idam-web-public.perftest.platform.hmcts.net",
		"Pragma" -> "no-cache",
		"Sec-Fetch-Dest" -> "document",
		"Sec-Fetch-Mode" -> "navigate",
		"Sec-Fetch-Site" -> "same-origin",
		"Sec-Fetch-User" -> "?1",
		"Upgrade-Insecure-Requests" -> "1")

var uri1 = "https://idam-web-public.perftest.platform.hmcts.net"
val uri2 = "https://administer-orgs.perftest.platform.hmcts.net"

val XUIAdminOrg = 

    exec(http("request_0")
			.get(uri2 + "/")
			.headers(headers_0))

    .exec(http("request_1")
			.get(uri2 + "/api/environment/config")
			.headers(headers_1))

    .exec(http("request_2")
			.get(uri2 + "/auth/isAuthenticated")
			.headers(headers_1))

    .exec(http("request_3")
			.get(uri2 + "/api/user/details")
			.headers(headers_1)
      .check(regex("oauth2/callback&state=(.*)&nonce").saveAs("state"))
      .check(regex("&nonce=(.*)&response_type").saveAs("nonce"))
      .check(css("input[name='_csrf']", "value").saveAs("csrfToken")))

		.pause(3)

		.exec(http("request_4")
			.post(uri1 + "/login?client_id=xuiaowebapp&redirect_uri=https://administer-orgs.perftest.platform.hmcts.net/oauth2/callback&state=${state}&nonce=${nonce}&response_type=code&scope=profile%20openid%20roles%20manage-user%20create-user&prompt=")
			.headers(headers_4)
			.formParam("username", "vmuniganti@mailnesia.com")
			.formParam("password", "Monday01")
			.formParam("save", "Sign in")
			.formParam("selfRegistrationEnabled", "false")
			.formParam("_csrf", "${csrfToken}"))

    .exec(http("request_5")
			.get(uri2 + "/api/environment/config")
			.headers(headers_1))

    .exec(http("request_6")
			.get(uri2 + "/api/user/details")
			.headers(headers_1))

    .exec(http("request_7")
			.get(uri2 + "/auth/isAuthenticated")
			.headers(headers_1))

    .exec(http("request_8")
			.get(uri2 + "/api/organisations?status=PENDING")
			.headers(headers_1))
    
    .exec(http("request_9")
			.get(uri2 + "/api/organisations?status=ACTIVE")
			.headers(headers_1))

}