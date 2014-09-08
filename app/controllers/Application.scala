package controllers

import play.api._
import play.api.mvc._
import models.Example1

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def upload = Action(parse.multipartFormData) { request =>
    request.body.file("file").map { picture =>
      import java.io.File
      val filename = "uploads/" + randomId + "_" + picture.filename
      val contentType = picture.contentType
      picture.ref.moveTo(new File(s"$filename"))
      try {
          val results = Example1(filename).evaluate.flatten
          results match {
            case Nil => Ok
            case list => BadRequest("Results: " + list.mkString)
          }
      }catch {
        case e: Exception => BadRequest(e.getMessage)
      }
      
      
    }.getOrElse {
      Redirect(routes.Application.index).flashing(
        "error" -> "Missing file")
    }
  }  
  
  def randomId = {
    val random = new scala.util.Random
    val alphabet = "abcdefghijklmnopqrstuvwxyz0123456789"
    Stream.continually(random.nextInt(alphabet.size)).map(alphabet).take(20).mkString
  }

}