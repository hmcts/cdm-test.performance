package uk.gov.hmcts.ccd.corecasedata.scenarios.checks

import io.gatling.core.Predef._
import io.gatling.core.check.CheckBuilder
import io.gatling.http.Predef._
import io.gatling.http.check.HttpCheck


object CurrentPageCheck {
    def save: CheckBuilder[HttpCheck, Response, Response, String] = currentLocation.saveAs("currentPage")

    def currentPageTemplate: String = "${currentPage}"
}