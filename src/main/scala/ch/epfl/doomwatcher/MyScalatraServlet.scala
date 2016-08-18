package ch.epfl.doomwatcher

import java.util.Date
import org.json4s.JsonAST._
import org.ocpsoft.prettytime.PrettyTime
import org.scalatra._
import better.files._
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
import org.json4s.jackson.Serialization.{read, write}
import scala.language.postfixOps

case class TrainingInfo(name: String, mdpName: String, trainingName: String, ago: String, active: Boolean, progress: Int, stepCounter: Int, maxStep: Int, configuration: String)

class MyScalatraServlet extends Rl4jDoomWebAppStack with JacksonJsonSupport{

  protected implicit lazy val jsonFormats: Formats = DefaultFormats


  val rootDir = System.getProperty("user.home")+"/rl4j-data/"

  def info(f: File) = {
    val infoFile = f / "info"
    val jsonOpt = parseOpt(infoFile !)

    jsonOpt.map( json => {
      val mdpName = (json \  "mdpName") match {
        case JString(name) => name
        case _ => "No mdpName"
      }

      val trainingName = (json \  "trainingName") match {
        case JString(name) => name
        case _ => "No trainingName"
      }


      val ago = (json \ "millisTime") match {
        case JInt(millis) => new Date(millis.toLong)
        case _ => new Date(0)
      }

      val progress = ((json \ "stepCounter"), (json \\ "maxStep")) match {
        case (JInt(step), JInt(maxStep)) => ((step.toLong*100/maxStep.toLong).toInt, step.toInt, maxStep.toInt)
        case _ => (0, 0, 0)
      }

      val configuration = (json \ "conf") match {
        case a@(JObject(conf)) => pretty(render(a))
        case _ => ""
      }

      val minAgo10 = new Date(System.currentTimeMillis()-1000*60*5)
      TrainingInfo(f.name, mdpName, trainingName, new PrettyTime().format(ago), ago.after(minAgo10), progress._1, progress._2, progress._3, configuration)
    })
  }

  get ("/info/:id") {
    val dir = File(rootDir+params("id"))
    if (!dir.exists)
      NotFound("training not found")
    else {
      contentType = formats("json")
      write(info(dir))
    }
  }

  get("/") {
    contentType="text/html"
    val dir = File(rootDir+"/")
    if (!dir.exists)
      NotFound("rl4j data folder not found")
    else {
      val trainings = dir
        .list
        .filter(x => x / "info" exists())
        .map(info)
        .filter(_.isDefined)
        .map(_.get)
        .toList
        .sortBy(_.name.toInt)
        .reverse

      scaml("home.scaml", "title" -> "List of trainings", "trainings" -> trainings)
    }
  }

  get("/video/:id/:vid") {
    val dir = File(rootDir+params("id")+"/video/")
    if (!dir.exists)
      NotFound("training not found")
    else {
      val search = dir.list.find(x => x.name.startsWith("video-"+params("vid")+"-"))
      search match {
        case Some(file) =>
          contentType="video/mp4"
          file.toJava
        case None =>
          NotFound("Video not found")
      }
    }
  }

  get("/model/:id/:model") {
    val dir = File(rootDir+params("id")+"/model/")
    if (!dir.exists)
      NotFound("training not found")
    else {
      val search = dir.list.find(x => x.name == params("model"))
      search match {
        case Some(file) =>
          contentType="application/octet-stream"
          file.toJava
        case None =>
          NotFound("Model not found")
      }
    }
  }

  def stat(obj: JValue): List[Any] =
    obj.children

  get("/chart/:id"){
    val dir = File(rootDir+params("id")+"/stat")
    if (!dir.exists)
      NotFound("Chart data not found")
    else {
      contentType = formats("json")
      val chart = dir.lines
      val converted = chart.map(parseOpt(_)).filter(_.isDefined).map(_.get).map(stat).toList.transpose
      converted
    }
  }

  get("/training/:id") {
    contentType="text/html"

    val dir = File(rootDir+params("id"))
    val trainingInfo = info(dir)
    if (!dir.exists)
      NotFound("training not found")
    else if (trainingInfo.isEmpty)
      NotFound("missing info on training")
    else {
      val dir_video = dir / "video"
      val video_files = dir_video.list.filter(_.name contains(".mp4")).map(_.name.split("-")(1)).toList.sortBy(x => -x.toInt)
      val dir_model = dir / "model"
      val model_files = dir_model.list.map(_.name).filter(_.endsWith(".model")).toList

      scaml("training.scaml", "layout" -> "WEB-INF/templates/layouts/training.scaml", "title" -> ("Training: #"+params("id")), "trainingInfo" -> trainingInfo.get, "video_files" -> video_files, "model_files" -> model_files)
    }
  }


}
