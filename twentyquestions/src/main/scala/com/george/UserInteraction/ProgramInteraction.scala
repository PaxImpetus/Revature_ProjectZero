package com.george.UserInteraction

import scala.util.matching.Regex
import scala.io.StdIn
import java.util.Scanner
import scala.io.Source
import java.sql.Connection
import java.io.File
import com.george.ExceptionHandler
import scala.collection.mutable.ArrayBuffer
import com.george.DataBase._
import scala.io.BufferedSource

object ProgramInteraction{
    def printToConsole():Unit={
        try{
            println("\n" + Source.fromResource("intro.txt").getLines().mkString("\n"))
        }
        catch{
            case e: Any => {println(ExceptionHandler.ExceptionFinder.xMatcher(e))} //object xMatcher is not a member of package com.george.ExceptionFinder
        }
    }

    def parseSurvey(userIn:String):Unit={
        val commandSequence:Regex = "\\.+\\s*".r

        userIn.toLowerCase() match{
            case "short" => runSurvey("surveyShort") //works
            case "medium" => runSurvey("surveyMedium") // works
            case "long" => runSurvey("surveyLong") // works
            case commandSequence("upload", arg0, arg1) => {uploadFiles(arg0, arg1)} // need to get it working
                    //arg0      delimited list of files [with directories] to pull data from - must all be directed at the same table
                    //arg1      target table to store contents of 
            case "exit" => {} //works
            case _ => {println("Nani senpai desu kun?")} //"works lmao"
        }
    }

    def runSurvey(selection:String):Unit={
        var bool = true
        try{
            val surveyFile=Source.fromResource(s"$selection.txt").getLines()
            var container:String = ""
            for(line <- surveyFile){
                if(line.toString.startsWith("---")){ println(line)}
                else{
                    print(s"$line\t")
                    val reader = StdIn.readLine()
                    val readerConversion:Int = reader.toInt
                    val readVal = readerConversion match{
                        case a if readerConversion > 10 => "10"
                        case b if readerConversion < 0 => "0"
                        case _ => reader
                    }
                    container += "," + readVal
                }
            }
            container = container.substring(1)
            println(container)
            val toDatabase = new DatabaseBuilder.DatabaseBuilder()
            val connected = toDatabase.dbLaunch()

            selection match{
                case a if (selection == "surveyShort") => {DAO.insertSurveySmall(connected, container)}
                case b if (selection == "surveyMedium") => {DAO.insertSurveyMedium(connected, container)}
                case c if (selection == "surveyLong") => {DAO.insertSurveyLong(connected, container)}
            }
            
            toDatabase.dbClose()
        }
        catch{
            case e: Exception=>{println(ExceptionHandler.ExceptionFinder.xMatcher(e))
            }
        }
    }

    def uploadFiles(arg0:String, arg1:String):Unit={

        val fileSet = arg0.split(",")
        for(file <- fileSet) {

            try{
                val dbBulk = new DatabaseBuilder.DatabaseBuilder()
                val thisFile:File = new File(file)
                val fileReader = new Scanner(thisFile)
                val entity:Connection = dbBulk.dbLaunch()
                while(fileReader.hasNext()){
                    val newRecord = fileReader.next()
                    DAO.insertSurveySmall(entity, newRecord)
                }
                dbBulk.dbClose()
            }
            catch {
                case e: Exception=> {println(ExceptionHandler.ExceptionFinder.xMatcher(e))}
            }

        }

    }

}