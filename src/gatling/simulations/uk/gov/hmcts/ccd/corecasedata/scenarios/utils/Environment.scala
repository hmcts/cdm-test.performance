package uk.gov.hmcts.ccd.corecasedata.scenarios.utils

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Environment {

  val idamURL = "https://idam-web-public.perftest.platform.hmcts.net"
  val idamAPI = "https://idam-api.perftest.platform.hmcts.net"
  val ccdEnvurl = "https://www-ccd.perftest.platform.hmcts.net"
  val ccdDataStoreUrl = "ccd-data-store-api-perftest.service.core-compute-perftest.internal"
  val baseURL = "https://gateway-ccd.perftest.platform.hmcts.net"
  val s2sUrl = "http://rpe-service-auth-provider-perftest.service.core-compute-perftest.internal"
  val xuiMCUrl = "https://manage-case.perftest.platform.hmcts.net/oauth2/callback"
  val xuiBaseURL = "https://manage-case.perftest.platform.hmcts.net"

  val minThinkTime = 5 //20
  val maxThinkTime = 10 //60
  val constantthinkTime = 7
  val minWaitForNextIteration = 5 //120
  val maxWaitForNextIteration = 10 //240
  val HttpProtocol = http

  val commonHeader = Map(
    "Accept" -> "application/json",
    "Content-Type" -> "application/json",
    "Origin" -> ccdEnvurl)

  val docCommonHeader = Map(
    "Content-Type" -> "application/pdf",
    "Origin" -> ccdEnvurl)

  val idam_header = Map(
    "accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Origin" -> idamURL,
    "Upgrade-Insecure-Requests" -> "1",
    "sec-fetch-dest" -> "document",
    "sec-fetch-mode" -> "navigate",
    "sec-fetch-site" -> "same-origin",
    "sec-fetch-user" -> "?1",
    "cache-control" -> "max-age=0")
}
