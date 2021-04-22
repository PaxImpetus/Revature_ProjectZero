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
import org.postgresql.util.PSQLException
import com.george.Constants
import scala.collection.immutable

object ProgramInteraction{

    def loginToApplication(credits:String):Boolean={
        var passFail = true
        if(credits != "exit")
        {
            val dbInit = new DatabaseBuilder.DatabaseBuilder()
            val elixir = dbInit.dbLaunch()
            val realUser = DAO.confirmExistence(elixir, Constants.userTbl, credits)
            
            if(realUser){
                print("User Found. Provide Password:\t")
                val pwCheck = StdIn.readLine()
                if(pwCheck == "")
                {
                    dbInit.dbClose()
                    passFail = false
                }
                else
                {
                    passFail = DAO.confirmCredits(elixir, pwCheck, credits)
                    Constants.currentUser = credits
                    dbInit.dbClose()
                }
            }
            else
            {
                print("userName Not Found. Create New User (Y/n)?\t")
                val response = StdIn.readLine()
                if(response.toLowerCase.startsWith("y"))
                {
                    newUser(elixir, credits)
                    dbInit.dbClose()
                    passFail = true
                }
                else
                {
                    dbInit.dbClose()
                    passFail = false
                }
            }
        }
        passFail
    }



    def newUser(cnx:Connection, credits:String):Unit={
        var bulkSet = new ArrayBuffer[String]
        print("\nPlease Provide Your First Name:\t")
        val fName = StdIn.readLine()
        bulkSet += s"'$fName'"

        print("\nPlease Provide Your Last Name:\t")
        val lName = StdIn.readLine()
        bulkSet += s"'$lName'"

        print(s"\nUser Name Provided as $credits. Press Enter to Keep or Provide a New Username here:\t")
        var uName = StdIn.readLine()
        if(uName=="") uName = credits
        bulkSet += s"'$uName'"

        print("\nPlease Provide Your Password:\t")
        val pWord = StdIn.readLine()
        bulkSet += s"'$pWord'"

        try{
            DAO.addUser(cnx, Constants.usersTableForm, bulkSet)
            Constants.currentUser = uName
            println(s"I am ${Constants.currentUser}")
        }
        catch   { case e: PSQLException => { println(ExceptionHandler.ExceptionFinder.xMatcher(e)) }
        }
    }


    def printToConsole():Unit={
        try{
            println("\n" + Source.fromResource("intro.txt").getLines().mkString("\n"))
        }
        catch{
            case e: Any => {println(ExceptionHandler.ExceptionFinder.xMatcher(e))} //object xMatcher is not a member of package com.george.ExceptionFinder
        }
    }



    def parseSurvey(userIn:String):Unit={
        val commandSequence:Regex = "\\w*\\s*\\w*\\s*\\w*".r

        userIn.toLowerCase() match{
            case "short" => runSurvey(Constants.smalTbl) //works
            case "medium" => runSurvey(Constants.midlTbl) // works
            case "long" => runSurvey(Constants.longTbl) // works
            case commandSequence(arg0, arg1, arg2) if arg0 == "upload" => { uploadFiles(arg1, arg2) } // Last to Implement
            case commandSequence(arg0, arg1, arg2) if arg0 == "show" => { retrieveStats(arg1) } // Doesn't Fire Right
            case commandSequence(arg0, arg1, arg2) if arg0 == "view" => {
                val stall = new DatabaseBuilder.DatabaseBuilder()
                println(s"${statBallDisplay(DAO.retrieveAllStats(stall.dbLaunch(),arg1))}")
            } // 
            case "exit" => {} //works
            case _ => {println("Nani senpai desu kun?")} //"works lmao"
        }
    }




    def runSurvey(selection:String):Unit={
        var bool = true
        try{
            val surveyFile=Source.fromResource(s"$selection.txt").getLines()
            var container = new ArrayBuffer[Int]
            for(line <- surveyFile){
                if(line.toString.startsWith("---")){ println(line)}
                else{
                    var surveyToken = true
                    do {
                        print(s"$line\t")
                        val reader = StdIn.readLine()
                        if(reader.toFloat.isNaN()){
                            println("\nInput Value is Not a Number. Please Try Again")
                        }
                        else{
                            val readerConversion:Int = reader.toInt 
                            //problematic control, checking string in float form is not a number, then converting string to int
                            container += surveyErrorCorrection(readerConversion)
                            surveyToken = false
                        }
                    }while(surveyToken)
                }
            }
            val toDatabase = new DatabaseBuilder.DatabaseBuilder()
            val connected = toDatabase.dbLaunch()

            selection match{
                case a if (selection == Constants.smalTbl) => {DAO.parseEntityExistence(connected, Constants.smallTableForm, container, Constants.updateSml ,Constants.currentUser, Constants.smalTbl)}
                case b if (selection == Constants.midlTbl) => {DAO.parseEntityExistence(connected, Constants.mediumTableForm, container, Constants.updateMid, Constants.currentUser, Constants.midlTbl)}
                case c if (selection == Constants.longTbl) => {DAO.parseEntityExistence(connected, Constants.largeTableForm, container, Constants.updateLrg, Constants.currentUser,Constants.longTbl)}
            }
            
            toDatabase.dbClose()
        }
        catch{
            case cce: ClassCastException=>{println("\nValue Passed in was not a number")}
            case e: Exception=>{println(ExceptionHandler.ExceptionFinder.xMatcher(e))}

        }
    }



    def uploadFiles(arg0:String, arg1:String):Unit={

        val fileSet = arg0.split(",")
        for(file <- fileSet) {
            val thisFile:File = new File(file)
            if(thisFile.exists()){
                try{
                    val dbBulk = new DatabaseBuilder.DatabaseBuilder()
                    val entity:Connection = dbBulk.dbLaunch()
                    if(file.endsWith(".json")){

                    }
                    else if(file.endsWith(".csv")){
                        val fileReader = new Scanner(thisFile)
                    
                        while(fileReader.hasNext()){
                            val intArray = new ArrayBuffer[Int]
                            val newRecord = fileReader.next()
                            val recSet = newRecord.split(",")
                            for(rec <- recSet){
                                intArray += rec.toInt
                            }
                            arg1 match{
                                case "users" => {DAO.parseEntityExistence(entity, Constants.usersTableForm, intArray, Constants.usersNotes, Constants.currentUser, Constants.userTbl)}
                                case "short" => {DAO.parseEntityExistence(entity, Constants.smallTableForm, intArray, Constants.questFormS, Constants.currentUser, Constants.smalTbl)}
                                case "medium" => {DAO.parseEntityExistence(entity, Constants.mediumTableForm, intArray, Constants.questFormM, Constants.currentUser, Constants.midlTbl)}
                                case "long" => {DAO.parseEntityExistence(entity, Constants.largeTableForm, intArray, Constants.questFormL, Constants.currentUser, Constants.longTbl)}                    
                            }
                        }
                    }
                    else{
                        println("File Not Compatible")
                    }

                    
                    dbBulk.dbClose()
                }
                catch {
                    case e: Exception=> {println(ExceptionHandler.ExceptionFinder.xMatcher(e))}
                }
            }

        }

    }



    def retrieveStats(table:String):Unit={
        //fetch and compute statistics from table
        try {
            val dbBuilder = new DatabaseBuilder.DatabaseBuilder()
            dbBuilder.dbClose()
            var myStats = new StringBuilder("")
            myStats.addAll(s"${DAO.selectMy(dbBuilder.dbLaunch(), "*", table)}")
        }
        catch{
            case e:PSQLException => {println(ExceptionHandler.ExceptionFinder.xMatcher(e))}
        }
    }



    def surveyErrorCorrection(x:Int):Int={
        x match{
            case a if x > 10 => 10
            case b if x < 0 => 0
            case c if (0 < x & x < 10) => x
            case _ => 5
        }
    }


    def statBallDisplay(xyz:StatBall):String={
        var cumulativeString = new StringBuilder("")
            cumulativeString.addAll(s"Column: ${xyz.colName}\n" + 
            s"\tpopulation: ${xyz.counter}\n" + 
            s"\tpopulation mean: ${xyz.popMean}\n" + 
            s"\tpopulation standard deviation: ${xyz.stndDev}\n" +
            s"\tpopulation variance: ${xyz.varianc}\n\n")
        cumulativeString.toString
    }


}