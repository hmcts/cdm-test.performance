package uk.gov.hmcts.ccd.corecasedata.scenarios

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import uk.gov.hmcts.ccd.corecasedata.scenarios.utils.Environment

object DVExcep {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL
  val CCDEnvurl = Environment.ccdEnvurl
  val CommonHeader = Environment.commonHeader
  val idam_header = Environment.idam_header
  val feedUserData = csv("ProbateUserData.csv").circular
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  //login as divorce case worker
  //create exception case
  //upload doc during case create
  //doc view - maybe remove this if doing doc view later?
  //do search
  //open a case
  //doc view, although check above?
  //logout
}
