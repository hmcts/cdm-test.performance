package uk.gov.hmcts.ccd.corecasedata.scenarios

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

object Logout {
 
  val IdamURL = Environment.idamURL
	val CCDEnvurl = Environment.ccdEnvurl
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
    
  val ccdLogout = group("CDM_Logout") {

		exec(http("CDM_005_Logout")
			.options("/logout"))

		.exec(http("CDM_010_Logout")
			.get("/logout")
			.check(status.in(200, 204, 401)))

		.exec(http("CDM_015_Logout")
			.get(IdamURL + "/login?response_type=code&client_id=ccd_gateway&redirect_uri=" + CCDEnvurl + "%2Foauth2redirect")
			.check(status.in(200, 401)))
	}

		.pause(MinThinkTime seconds, MaxThinkTime seconds)
			
}