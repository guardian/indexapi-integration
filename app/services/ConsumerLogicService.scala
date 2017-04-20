package services

import javax.inject.Inject

import com.gu.contentapi.client.model.v1.Content
import com.gu.contentapi.client.model.v1.ContentType.Liveblog
import com.gu.contentapi.firehose.client.StreamListener
import com.gu.crier.model.event.v1.RetrievableContent
import play.api.libs.json._
import play.api.libs.ws.WSClient

class ConsumerLogicService @Inject() (playconfig: play.Configuration, ws: WSClient) extends StreamListener {

  override def contentUpdate(content: Content): Unit = {

    def handleLiveBlogUpdate(content: Content) {

      println("Content update for Article")
      println("  title: ", content.webTitle)
      println("  url: ", content.webUrl)
      println("  amp: ", content.webUrl.replaceFirst("www", "amp"))
      println("  pub: ", content.webPublicationDate.get.dateTime.toString)
      println("  created: ", content.fields.get.firstPublicationDate.get.dateTime.toString)

      implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

      postUpdate(
        content.webTitle,
        content.webUrl,
        content.fields.get.lastModified.get.dateTime,
        content.fields.get.firstPublicationDate.get.dateTime
      ).map { response =>
        println(response.body)
      }

    }

    def handleOther(content: Content): Unit = {
      println("Ignoring content update")
    }

    content.`type` match {
      case Liveblog => handleLiveBlogUpdate(content)
      case _ => handleOther(content)
    }

  }

  def postUpdate(title: String, url: String, updated: Long, created: Long) = {

    println("Posting update to url: " + playconfig.getString("lambdaEndpoint"))

    ws.url(playconfig.getString("lambdaEndpoint")).post(Json.obj(
      "title" -> title,
      "url" -> url,
      "amp" -> url.replaceFirst("www", "amp"),
      "updated" -> updated,
      "created" -> created
    ))
  }

  override def contentTakedown(contentId: String): Unit = {

  }

  override def contentRetrievableUpdate(content: RetrievableContent): Unit = {

  }

}