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
        print("\nPlease Provide Your First Name:\t")
        val fName = StdIn.readLine()

        print("\nPlease Provide Your Last Name:\t")
        val lName = StdIn.readLine()

        print(s"\nUser Name Provided as $credits. Press Enter to Keep or Provide a New Username here:\t")
        var uName = StdIn.readLine()
        if(uName=="") uName = credits

        print("\nPlease Provide Your Password:\t")
        val pWord = StdIn.readLine()

        val bulkSet = s"'$fName','$lName','$uName','$pWord'"
        try{
            DAO.insertInto(cnx, Constants.usersTableForm, bulkSet)
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
        val commandSequence:Regex = "(\\w+)\\s*(.*)\\s*(.*)".r

        userIn.toLowerCase() match{
            case commandSequence(arg0, arg1, arg2) if arg0 == "short" => runSurvey(Constants.smalTbl) //works
            case commandSequence(arg0, arg1, arg2) if arg0 == "medium" => runSurvey(Constants.midlTbl) // works
            case commandSequence(arg0, arg1, arg2) if arg0 == "long" => runSurvey(Constants.longTbl) // works
            case commandSequence(arg0, arg1, arg2) if arg0 == "upload" => {uploadFiles(arg1, arg2)} // not yet working
            case commandSequence("show", "mine", arg2) => {
                try{
                val dbBuilder = new DatabaseBuilder.DatabaseBuilder()
                val dbMine = DAO.selectMy(dbBuilder.dbLaunch(), "*", arg2)
                //do something with dbMine, is a ArrayBuffer[Any], possibly DataEntityShort, DataEntityMedium, DataEntityLong
                println(dbMine)
                dbBuilder.dbClose()
                }
                catch{
                    case e:PSQLException =>{println(ExceptionHandler.ExceptionFinder.xMatcher(e))}
                }
            } // "works", not complete
            case commandSequence("view", arg1, arg2) => {
                println("I hear you, I hear you...")
                /*
                val dbBuilder = new DatabaseBuilder.DatabaseBuilder()
                val tableStats:mutable.ArrayBuffer[StatBall] = DAO.retrieveAllStats(dbBuilder, arg0)
                println(statBallDisplay)
                */
            }
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
                            val readVal = surveyErrorCorrection(readerConversion)
                            container += "," + readVal
                            surveyToken = false
                        }
                    }while(surveyToken)
                }
            }
            container = container.substring(1)
            println(container)
            val toDatabase = new DatabaseBuilder.DatabaseBuilder()
            val connected = toDatabase.dbLaunch()

            selection match{
                case a if (selection == Constants.smalTbl) => {DAO.parseEntityExistence(connected, Constants.smallTableForm, container, Constants.questFormS ,Constants.currentUser)}
                case b if (selection == Constants.midlTbl) => {DAO.parseEntityExistence(connected, Constants.mediumTableForm, container, Constants.questFormM, Constants.currentUser)}
                case c if (selection == Constants.longTbl) => {DAO.parseEntityExistence(connected, Constants.largeTableForm, container, Constants.questFormL, Constants.currentUser)}
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
                            val newRecord = fileReader.next()
                            arg1 match{
                                case "users" => {DAO.parseEntityExistence(entity, Constants.usersTableForm, newRecord, Constants.usersNotes, Constants.currentUser)}
                                case "short" => {DAO.parseEntityExistence(entity, Constants.smallTableForm, newRecord, Constants.questFormS, Constants.currentUser)}
                                case "medium" => {DAO.parseEntityExistence(entity, Constants.mediumTableForm, newRecord, Constants.questFormM, Constants.currentUser)}
                                case "long" => {DAO.parseEntityExistence(entity, Constants.largeTableForm, newRecord, Constants.questFormL, Constants.currentUser)}                    
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



    def retrieveStats(columns:String, tables:String, math:String):Unit={
        //fetch and compute statistics from table
        try {
            val tblSet = tables.split(",")
            val dbPulley = new DatabaseBuilder.DatabaseBuilder()
            val dbx = dbPulley.dbLaunch()
            var myStats = ""
            for (tbl <- tblSet){
                myStats += DAO.selectMy(dbx, columns, tbl)
            }
        }
        catch{
            case e:PSQLException => {println(ExceptionHandler.ExceptionFinder.xMatcher(e))}
        }
    }



    def surveyErrorCorrection(x:Int):String={
        x match{
            case a if x > 10 => "10"
            case b if x < 0 => "0"
            case c if (0 < x & x < 10) => s"$x"
            case _ => "5"
        }
    }


    def statBallDisplay(abc:ArrayBuffer[StatBall]):String={
        var cumulativeString = new StringBuilder("")
        for(xyz <- abc){
            cumulativeString.addAll(s"Column: ${xyz.colName}\n" + 
            s"\tpopulation: ${xyz.counter}\n" + 
            s"\tpopulation mean: ${xyz.popMean}\n" + 
            s"\tpopulation standard deviation: ${xyz.stndDev}\n" +
            s"\tpopulation variance: ${xyz.varianc}\n\n")
        }
        cumulativeString.toString
    }


}