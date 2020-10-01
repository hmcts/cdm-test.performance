package uk.gov.hmcts.ccd.corecasedata.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.LoginHeader
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.ProbateHeader
import scala.concurrent.duration._

object ExuiView {

    val baseURL = Environment.xuiBaseURL
    val IdamUrl = Environment.idamURL

    val manageCasesHomePage =
    tryMax(2) {

      exec(http("XUI_010_005_Homepage")
           .get(baseURL + "/")
           .headers(LoginHeader.headers_0)
           .check(status.in(200,304))).exitHereIfFailed

      .exec(http("XUI_010_010_Homepage")
            .get(baseURL + "/assets/config/config.json")
            .headers(LoginHeader.headers_1))

      .exec(http("XUI_010_015_HomepageTCEnabled")
            .get(baseURL + "/api/configuration?configurationKey=termsAndConditionsEnabled")
            .headers(LoginHeader.headers_1))

      .exec(http("XUI_010_020_HomepageIsAuthenticated")
            .get(baseURL + "/auth/isAuthenticated")
            .headers(LoginHeader.headers_1))

      .exec(http("XUI_010_020_Homepage")
            .get(baseURL + "/auth/login")
            .headers(LoginHeader.headers_4)
            .check(css("input[name='_csrf']", "value").saveAs("csrfToken"))
            .check(regex("manage-user%20create-user&state=(.*)&client").saveAs("state")))
    }

  //==================================================================================
  //Business process : Enter the login details and submit
  //below requests are main login and relavant sub requests as part of the login submission
  //==================================================================================

  val manageCaseslogin =
    tryMax(2) {

      exec(http("XUI_020_005_SignIn")
        //.post(IdamUrl + "/login?response_type=code&client_id=xuiwebapp&redirect_uri=" + baseURL + "/oauth2/callback&scope=profile%20openid%20roles%20manage-user%20create-user")
        .post(IdamUrl + "/login?response_type=code&redirect_uri=" + baseURL + "%2Foauth2%2Fcallback&scope=profile%20openid%20roles%20manage-user%20create-user&state=${state}&client_id=xuiwebapp")
           .formParam("username", "ccdloadtest1@gmail.com")
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

        /*.exec(http("XUI_020_010_Homepage")
              .get("/external/config/ui")
              .headers(LoginHeader.headers_0)
              .check(status.in(200,304)))*/

      .exec(http("XUI_020_015_SignInTCEnabled")
            .get(baseURL + "/api/configuration?configurationKey=termsAndConditionsEnabled")
            .headers(LoginHeader.headers_38)
            .check(status.in(200, 304)))

     /* .exec(http("XUI_020_020_SignInGetUserId")
            .get("/api/userTermsAndConditions/${myUserId}")
            .headers(LoginHeader.headers_tc))*/

     /* .exec(http("XUI_020_025_SignInAcceptTCGet")
            .get("/accept-terms-and-conditions")
            .headers(LoginHeader.headers_tc_get)
            .check(status.in(200, 304)))*/

     /* .exec(http("XUI_020_030_SignInTCEnabled")
            .get("/api/configuration?configurationKey=termsAndConditionsEnabled")
            .headers(LoginHeader.headers_tc))*/

      .repeat(1, "count") {
        exec(http("XUI_020_020_AcceptT&CAccessJurisdictions${count}")
             .get(baseURL + "/aggregated/caseworkers/:uid/jurisdictions?access=read")
             .headers(LoginHeader.headers_access_read)
             .check(status.in(200, 304, 302)))
      }

        .exec(http("XUI_020_025_GetWorkBasketInputs")
              .get(baseURL + "/data/internal/case-types/DIVORCE/work-basket-inputs")
              .headers(LoginHeader.headers_17))

        .exec(http("XUI_020_030_GetPaginationMetaData")
              .get(baseURL + "/data/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE/cases/pagination_metadata?state=Open")
              .headers(LoginHeader.headers_0))

        .exec(http("XUI_020_035_GetDefaultWorkBasketView")
              .get(baseURL + "/aggregated/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE/cases?view=WORKBASKET&state=Open&page=1")
              .headers(LoginHeader.headers_0))

        .pause(Environment.constantthinkTime)

    }

    val searchProbateCase = 

    exec(http("XUI_ProbateSearchResults_PaginationMetadata")
        .get(baseURL + "/data/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases/pagination_metadata")
        .headers(ProbateHeader.headers_search)
        .check(status.in(200,304))
    )

    .exec(http("XUI_ProbateSearchResults_Workbasket")
        .get(baseURL + "/aggregated/caseworkers/:uid/jurisdictions/PROBATE/case-types/GrantOfRepresentation/cases?view=WORKBASKET&page=1")
        .headers(ProbateHeader.headers_search)
        .check(status.in(200,304)))

    .pause(Environment.constantthinkTime)


val searchDivorceCase = 

    exec(http("XUI_DivorceSearchResults_PaginationMetadata")
        .get(baseURL + "/data/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE/cases/pagination_metadata")
        .headers(ProbateHeader.headers_search)
        .check(status.in(200,304))
    )

    .exec(http("XUI_DivorceSearchResults_Workbasket")
        .get(baseURL + "/aggregated/caseworkers/:uid/jurisdictions/DIVORCE/case-types/DIVORCE/cases?view=WORKBASKET&page=1")
        .headers(ProbateHeader.headers_search)
        .check(status.in(200,304)))

    .pause(Environment.constantthinkTime)
}