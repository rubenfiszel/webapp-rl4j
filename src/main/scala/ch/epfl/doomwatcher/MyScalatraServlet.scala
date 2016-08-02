package ch.epfl.doomwatcher

import org.scalatra._
import better.files._
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

class MyScalatraServlet extends Rl4jDoomWebAppStack with JacksonJsonSupport{

  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }

  get("/video/:id") {
    val dir = File(Configuration.dir+"doomreplay/")
    val search = dir.list.find(x => x.name == "DoomReplay-"+params("id")+".mp4")
    search match {
      case Some(file) =>
        contentType="video/mp4"
        file.toJava
      case None =>
        NotFound("Sorry file not found")
    }
//    Ok(first)
  }

  get("/chart"){
    contentType = formats("json")
    val chart = File(Configuration.dir+"score").lines
    val converted = chart.map(_.split(" ").map(_.toDouble)).transpose.toList
    val typed = List(converted(1).map(_.toInt.toString).toList.zip(converted(0).map(_.toInt.toString).toList).map(x => List(x._1, x._2)), converted(2) , converted(3))
    typed
  }

  get("/videos") {
    contentType="text/html"
    val dir = File(Configuration.dir+"doomreplay/")
    val files = dir.list.filter(_.name contains(".mp4")).map(_.name.dropRight(4).drop(11)).toList.sortBy(x => -x.toInt)
    scaml("videos.scaml", "files" -> files, "title" -> "Videos", "video_url" -> Configuration.video_url)
  }


}
