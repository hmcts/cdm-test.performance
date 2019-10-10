package uk.gov.hmcts.ccd.corecasedata.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

object CreateUser {

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val CommonHeader = Environment.commonHeader
  val CCDAPIEnvurl = Environment.baseURL
  val feedUserData = csv("CCDUserData.csv").circular

  val headers_1 = Map( //ServiceAuthorization token can be called from http://rpe-service-auth-provider-perftest.service.core-compute-perftest.internal/testing-support/lease
    "ServiceAuthorization" -> "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjY2RfZGVmaW5pdGlvbiIsImV4cCI6MTU3MDcyNjgwMn0.ku3UBt-yGuAyDBaicV7rP5DNSssARe342KC0mN1m2j-erar37iZQ47Cfi6UpXBzh-r3hiy7XruEIpNAzaWEPyg",
    "Content-Type" -> "application/json",
    "Accept" -> "application/json")

  val headers_0 = Map( //Authorization token needs to be provided by idam team
    "Authorization" -> "AdminApiAuthToken ****** ",
    "Content-Type" -> "application/json")


  val IdamUser = feed(feedUserData)

    .exec(http("request_1")
      .get("https://idam-api.perftest.platform.hmcts.net/users?email=${CCDUserName}")
      //.get("https://idam-api.perftest.platform.hmcts.net/users?email=ccdloadtest6@gmail.com")
      .headers(headers_0)
      .check(jsonPath("$.id").saveAs("userId")))

    //4708 failed

    /*.exec(http("1_caseworker")
      .patch("https://idam-api.perftest.platform.hmcts.net/users/${userId}/roles/f05a01aa-e242-40a7-8ee5-41abe95bac3e")
      .headers(headers_0))

    .exec(http("1_caseworker-autotest1")
      .patch("https://idam-api.perftest.platform.hmcts.net/users/${userId}/roles/d9bb4d1c-01d1-4aa0-9cbd-cb24a6bcada6")
      .headers(headers_0))

    .exec(http("3_caseworker-cmc")
      .patch("https://idam-api.perftest.platform.hmcts.net/users/${userId}/roles/c3bfc801-4725-4939-be2b-c5caa53be3bf")
      .headers(headers_0))

    .exec(http("4_caseworker-sscs")
      .patch("https://idam-api.perftest.platform.hmcts.net/users/${userId}/roles/277e0e74-0dd2-4e80-8238-c49b53da05f5")
      .headers(headers_0))

    .exec(http("5_caseworker-probate")
      .patch("https://idam-api.perftest.platform.hmcts.net/users/${userId}/roles/277a88ed-e429-4dd9-b41b-2f73412eb47b")
      .headers(headers_0))

    .exec(http("6_caseworker-probate-issuer")
      .patch("https://idam-api.perftest.platform.hmcts.net/users/${userId}/roles/119d23b3-8fa2-4d8b-8638-9cd98bf5a03f")
      .headers(headers_0))

    .exec(http("7_caseworker-probate-solicitor")
      .patch("https://idam-api.perftest.platform.hmcts.net/users/${userId}/roles/81a17857-a080-4e06-89bd-c2ee924d9463")
      .headers(headers_0))

    .exec(http("8_caseworker-divorce")
      .patch("https://idam-api.perftest.platform.hmcts.net/users/${userId}/roles/9a54d222-599e-4627-a9a3-11d214adf64e")
      .headers(headers_0))

    .exec(http("9_caseworker-divorce-systemupdate")
      .patch("https://idam-api.perftest.platform.hmcts.net/users/${userId}/roles/684717f4-5b7d-4c97-92cb-54a899c61e6f")
      .headers(headers_0))

    .exec(http("10_caseworker-divorce-courtadmin_beta")
      .patch("https://idam-api.perftest.platform.hmcts.net/users/${userId}/roles/01be891a-a74f-489d-b886-462cb9c5ddec")
      .headers(headers_0))

    .exec(http("11_caseworker-divorce-solicitor")
      .patch("https://idam-api.perftest.platform.hmcts.net/users/${userId}/roles/dce12208-7523-4032-8031-7e1ac7c27651")
      .headers(headers_0))

    .exec(http("12_caseworker-divorce-courtadmin")
      .patch("https://idam-api.perftest.platform.hmcts.net/users/${userId}/roles/75a1264c-603c-4109-beff-00eacc9a1ff4")
      .headers(headers_0))*/

    .exec(http("13_payments")
    .patch("https://idam-api.perftest.platform.hmcts.net/users/${userId}/roles/3b8f1382-cd90-4fb0-96e8-82d3eb537e26")
    .headers(headers_0))

    /*.exec(http("1_caseworker-employment")
      .patch("https://idam-api.perftest.platform.hmcts.net/users/${userId}/roles/c3d17c1b-0426-4e6f-8367-002947097959")
      .headers(headers_0))

    .exec(http("2_caseworker-employment-tribunal-manchester")
      .patch("https://idam-api.perftest.platform.hmcts.net/users/${userId}/roles/5bb6d816-8b30-44bf-b327-c85d20b39339")
      .headers(headers_0))

    .exec(http("3_caseworker-employment-tribunal-manchester-caseofficer")
      .patch("https://idam-api.perftest.platform.hmcts.net/users/${userId}/roles/f262de91-cf3d-4e99-b769-20e1c97d3772")
      .headers(headers_0))

    .exec(http("4_caseworker-employment-tribunal-manchester-casesupervisor")
      .patch("https://idam-api.perftest.platform.hmcts.net/users/${userId}/roles/17bb9101-0e71-4422-aefb-635dfc978383")
      .headers(headers_0))

    .exec(http("5_caseworker-employment-tribunal-glasgow")
      .patch("https://idam-api.perftest.platform.hmcts.net/users/${userId}/roles/9536acf3-b18b-4dc7-aaed-2e028959cf89")
      .headers(headers_0))

    .exec(http("6_caseworker-employment-tribunal-glasgow-caseofficer")
      .patch("https://idam-api.perftest.platform.hmcts.net/users/${userId}/roles/0c2c24c9-bbc0-403f-b6a4-d202bdac8506")
      .headers(headers_0))

    .exec(http("7_caseworker-employment-tribunal-glasgow-casesupervisor")
      .patch("https://idam-api.perftest.platform.hmcts.net/users/${userId}/roles/16c3c33c-c880-4a20-9b09-6eabbd6f7ef2")
      .headers(headers_0))*/

    .pause(1)


  val CreateUserProfile = feed(feedUserData)

    .exec(http("request_4")
      .post("http://ccd-user-profile-api-perftest.service.core-compute-perftest.internal/user-profile/users")
      .headers(headers_1)
      .body(StringBody("{\n    \"id\": \"${CCDUserName}\",\n    \"jurisdictions\": [{\"id\": \"DIVORCE\"},{\"id\": \"AUTOTEST1\"},{\"id\": \"CMC\"},{\"id\": \"PROBATE\"},{\"id\": \"SSCS\"},{\"id\": \"TRIBUNALS\"},{\"id\": \"EMPLOYMENT\"}],\n    \"work_basket_default_jurisdiction\": \"DIVORCE\",\n    \"work_basket_default_case_type\": \"DIVORCE\",\n    \"work_basket_default_state\": \"Submitted\"\n}")))

    .exec {
      session =>
        println(session("CCDUserName").as[String])
        session
    }

    .pause(1)

}
