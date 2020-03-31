package computerdatabase.advanced

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class Local2 extends Simulation {
  var URI = "https://localhost:8080"
  var pcAgent = "Mozilla/5.0(WindowsNT6.1;WOW64)AppleWebKit/537.36(KHTML,likeGecko)Chrome/81"
  var iPhoneAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0_1 like Mac OS X) AppleWebKit/602.1.50"
  var androidAgent = "Mozilla/5.0 (Linux; U; Android 2.2.1; en-us; Nexus One Build/FRG83)"

  var httpProtocol =
    http
      .baseUrl(URI)
      .enableHttp2
      .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
      .doNotTrackHeader("1") // DNT Headerの拒否設定
      .inferHtmlResources(BlackList(),WhiteList(".*codelike.info.*")) // htmlからcssやimgを呼び出す
      .acceptLanguageHeader("ja,en-US;q=0.8,en;q=0.6")
      .acceptEncodingHeader("gzip, deflate")
      .header("Cache-Control", "max-age=0")
      .header("Pragma", "no-cache")
      .userAgentHeader(pcAgent)
      .disableFollowRedirect // 自動でリダイレクトしないように設定

  val scn = scenario("BasicSimulation")
              .exec(
                http("category_pu")
                  .get("/category.ksn?categoryCode=PU")
                  .check(status.is(200)))
              .pause(3)
              .exec(
                http("category_mv")
                  .get("/category.ksn?categoryCode=MV")
                  .check(status.is(200)))
              .pause(3)
              .exec(
                http("articke_1")
                  .get("/article.ksn?articleNo=2139873")
                  .check(status.is(200)))
              .pause(3)
              .exec(
                http("articke_2")
                  .get("/article.ksn?articleNo=2139854")
                  .check(status.is(200)))

  setUp(
    scn.inject(
      nothingFor(2 seconds),
      atOnceUsers(5), // 同時接続数
      rampUsers(1) during (10 seconds)
    ).protocols(httpProtocol)
  )
}
