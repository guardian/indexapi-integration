package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class HealthcheckController @Inject() extends Controller {

  def index = Action {
    Ok("Success")
  }

}
