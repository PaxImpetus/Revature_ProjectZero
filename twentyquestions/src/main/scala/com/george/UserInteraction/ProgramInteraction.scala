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
import javax.xml.crypto.Data

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
        val commandSequence:Regex = "(\\w+)\\s+(.+)\\s+(\\w+)".r
        println(userIn)
        //.toLowerCase()
        val dbBuilder = new DatabaseBuilder.DatabaseBuilder()
        val launcher = dbBuilder.dbLaunch()
        userIn match{
            case commandSequence("survey", "short", arg2) => runSurvey(launcher, Constants.smalTbl) //works
            case commandSequence("survey", "medium", arg2)=> runSurvey(launcher, Constants.midlTbl) // works
            case commandSequence("survey", "long", arg2) => runSurvey(launcher, Constants.longTbl) // works
            case commandSequence("upload", arg1, arg2) => {uploadFile(launcher, arg1, arg2)} // Last to Implement
            case commandSequence("show", arg1, arg2)  => { retrieveStats(launcher, arg2) } // works
            case commandSequence("view", arg1, arg2)  => { println(s"${DAO.retrieveAllStats(launcher,arg1)}") } // works
            case commandSequence("drop", "my", arg2) =>{
                if(arg2 == "name"){
                    DAO.dropMe(launcher, "users")
                }
                else{
                    DAO.dropMySurvey(launcher, arg2)
                }
            }
            case "exit" => {} //works
            case _ => {println("Command Sequence Misinterpreted. Make sure the command keywords are lower case.")} //"works lmao"u
        }
        dbBuilder.dbClose()
    }




    def runSurvey(cnx:Connection, selection:String):Unit={
        var bool = true
        try{
            val surveyFile=Source.fromResource(s"$selection.txt").getLines()
            var container = new ArrayBuffer[Int]
            for(line <- surveyFile){
                if(line.toString.startsWith("---")){ println(line)}
                else{
                    print(s"$line\t")
                    val reader = StdIn.readLine()
                    val readerConversion:Int = reader.toInt 
                    container += surveyErrorCorrection(readerConversion)
                }
            }

            selection match{
                case a if (selection == Constants.smalTbl) => {DAO.parseEntityExistence(cnx, Constants.smallTableForm, container, Constants.updateSml ,Constants.currentUser, Constants.smalTbl)}
                case b if (selection == Constants.midlTbl) => {DAO.parseEntityExistence(cnx, Constants.mediumTableForm, container, Constants.updateMid, Constants.currentUser, Constants.midlTbl)}
                case c if (selection == Constants.longTbl) => {DAO.parseEntityExistence(cnx, Constants.largeTableForm, container, Constants.updateLrg, Constants.currentUser,Constants.longTbl)}
            }
        }
        catch{
            case cce: ClassCastException=>{println("\nValue Passed in was not an integer")}
            case e: Exception=>{println(ExceptionHandler.ExceptionFinder.xMatcher(e))}

        }
    }



    

    def uploadFile(cnx:Connection, arg1:String, arg2:String):Unit={
        try{
            val thisFile:File = new File(arg1)
            if(thisFile.exists()){
                if(arg1.endsWith(".json")){
                    //last bit of implementation
                }
                if(arg1.endsWith(".csv")){
                    val fileReader = new Scanner(thisFile)                    
                    while(fileReader.hasNext()){
                        //println(s"\n\nentity variable: $entity\nscanner variable: ${fileReader.nextLine}\ntable target: $arg2\n")
                        if(arg2 == "users"){
                            DAO.addImportedUserCSV(cnx, fileReader.nextLine.trim)
                        }
                        else{
                            DAO.addImportedSurveyCSV(cnx, arg2, fileReader.nextLine.trim)
                        }
                    }
                }
            }
        }
        catch{
            case e: Exception => {println(ExceptionHandler.ExceptionFinder.xMatcher(e))}
        }
    }




    def retrieveStats(cnx:Connection, table:String):Unit={
        //fetch and compute statistics from table
        try {
            var myStats = new StringBuilder("")
            myStats.addAll(s"${DAO.selectMy(cnx, "*", table)}")
            println(myStats)
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

}