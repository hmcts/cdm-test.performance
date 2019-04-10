package uk.gov.hmcts.ccd.corecasedata.scenarios.utils

import uk.gov.hmcts.ccd.corecasedata.scenarios._
import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

object Environment {
  
 //val idamURL = "https://idam-test.dev.ccidam.reform.hmcts.net"
  //val ccdEnvurl = "https://www-ccd.sprod.platform.hmcts.net"
  //val baseURL = "https://gateway-ccd.sprod.platform.hmcts.net"
  
  val idamURL = "https://idam-test.dev.ccidam.reform.hmcts.net"
  val ccdEnvurl = "https://ccd-case-management-web-sprod.service.core-compute-sprod.internal"
  val baseURL = "https://ccd-api-gateway-web-sprod.service.core-compute-sprod.internal"
  
 // val baseURL = "https://gateway.ccd.demo.platform.hmcts.net"
  //val idamURL = "https://idam.preprod.ccidam.reform.hmcts.net"
  //val ccdEnvurl = "https://www.ccd.demo.platform.hmcts.net" 
  val minThinkTime = 1
  val maxThinkTime = 2
  val constantthinkTime = 2
  val minWaitForNextIteration = 1
  val maxWaitForNextIteration = 2
  val HttpProtocol = http
    val commonHeader = Map(
		"Accept" -> "application/json",
		"Content-Type" -> "application/json",
		"Origin" -> ccdEnvurl)
		
	 val docCommonHeader = Map(
		"Content-Type" -> "application/pdf",
		"Origin" -> ccdEnvurl)
}