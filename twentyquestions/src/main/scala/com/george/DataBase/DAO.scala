package com.george.DataBase

import java.sql.Connection
import java.sql.ResultSet
import java.sql.PreparedStatement
import java.beans.Statement
import java.sql
import com.george.Constants
import com.george.ExceptionHandler
import com.george.UserInteraction.ProgramInteraction
import scala.collection.mutable

object DAO{
    
    //Determine whether table contains entity and update/insert data appropraitely
    def parseEntityExistence(cnx:Connection, targetTableForm:String, results:mutable.ArrayBuffer[Int], setForm:String, currentUser:String, tblForm:String):Unit={
        println("is the error here?")
        val isPerson = confirmExistence(cnx, Constants.userTbl, currentUser)
        if(isPerson){
            val isHere = confirmExistence(cnx, tblForm, currentUser)
            if(isHere)  { updateThis(cnx, tblForm, setForm, results) }
            else        { insertInto(cnx, targetTableForm, results) }
        }
    }

    def confirmExistence(cnx:Connection, tblForm:String, currentUser:String):Boolean={
        val xSchema:PreparedStatement = cnx.prepareStatement("set schema 'Surveys';")
        xSchema.execute()
        var tempToken = ""
        if(tblForm == Constants.userTbl){
            tempToken = "userName"
        }
        else{
            tempToken = "user_key"
        }
        val confirmation:PreparedStatement = cnx.prepareStatement(s"SELECT $tempToken FROM $tblForm WHERE $tempToken=?;")
        confirmation.setString(1, s"$currentUser")
        confirmation.execute()
        val rs = confirmation.getResultSet()
        if(rs.next) true
        else false
    }

    def confirmCredits(cnx:Connection, userPW:String, userUN:String):Boolean={
        val xSchema:PreparedStatement = cnx.prepareStatement("set schema 'Surveys';")
        xSchema.execute()
        val confirmation:PreparedStatement = cnx.prepareStatement(s"SELECT userName, userPass FROM users WHERE userName='$userUN';")
        confirmation.execute()
        val rs = confirmation.getResultSet()
        var detectedUN = "" 
        var detectedPW = ""
        if(rs.next()){
            detectedUN = rs.getString("userName")
            detectedPW = rs.getString("userPass")
        }
        if(detectedUN == userUN & detectedPW == userPW){
            println(s"Credentials Confirmed. Welcome, $userUN")
            true
        }
        else{
            println("Credentials Inderterminate. Please Try Again.")
            false
        }
    }

    def addUser(cnx:Connection, tblForm:String, results:mutable.ArrayBuffer[String]):Unit={
        val xSchema:PreparedStatement = cnx.prepareStatement("set schema 'Surveys';")
        xSchema.execute()
        var resultsSet = new StringBuilder("")
        for(res <- results){
            resultsSet.addAll(s"$res,")
        }
        val inSurv:PreparedStatement = cnx.prepareStatement("INSERT INTO "+
            tblForm +
            s" VALUES (${resultsSet.dropRight(1)});")
        println(s"${inSurv.toString}")
        inSurv.execute()
    }

    // Insert Results to Table
    // This is the one I am sampling with
    def insertInto(cnx:Connection, tblForm:String, results:mutable.ArrayBuffer[Int]):Unit={
        val xSchema:PreparedStatement = cnx.prepareStatement("set schema 'Surveys';")
        xSchema.execute()
        var resultsSet = new StringBuilder("")
        for(res <- results){
            resultsSet.addAll(s"$res,")
        }
        resultsSet.addAll(s"'${Constants.currentUser}'")
        val inSurv:PreparedStatement = cnx.prepareStatement("INSERT INTO "+
            tblForm +
            s" VALUES ($resultsSet);")
        println(s"${inSurv.toString}")
        inSurv.execute()
    }


    def updateThis(cnx:Connection, tbl:String, setForm:String, results:mutable.ArrayBuffer[Int]):Unit={
        val xSchema:PreparedStatement = cnx.prepareStatement("set schema 'Surveys';")
        xSchema.execute()
        val tblKey = tbl match{
            case Constants.userTbl => "userName"
            case Constants.smalTbl => "user_key"
            case Constants.midlTbl => "user_key"
            case Constants.longTbl => "user_key"
        }
        val inSurv:PreparedStatement = cnx.prepareStatement("UPDATE "+ tbl +
            " SET " + setForm + s" WHERE $tblKey='${Constants.currentUser}';")
        println("1 make statement shell")
        var i = 1
        println(s"${inSurv.toString}")
        for(result <- results){
            val corrector = ProgramInteraction.surveyErrorCorrection(result.asInstanceOf[Int])
            inSurv.setInt(i, corrector)
            i+=1
        }
        println(s"3 ${inSurv.toString}")
        inSurv.execute()
    }

    def retrieveAllStats(cnx:Connection, tbName:String):StatBall={
        val xSchema:PreparedStatement = cnx.prepareStatement("set schema 'Surveys';") //fine
        xSchema.execute() // fine
        val setForm = tbName match{                         // --
            case "short" => Constants.questFormS      // --
            case "medium" => Constants.questFormM     // -- totally fine, this block
            case "long" => Constants.questFormL       // --
        }                                                   // --
        var sltOne:PreparedStatement = cnx.prepareStatement(s"SELECT count(loop), avg(loop), stdDev(loop) FROM $tbName;") // get the average value and the std dev from some table
        val resSet:ResultSet = sltOne.executeQuery()
        StatBall.fromStatSet(setForm, resSet)
    }

    // Retrieve User's Survey from target Table
    def selectMy(cnx:Connection, clmSet:String, tbName:String):Any={
        val xSchema:PreparedStatement = cnx.prepareStatement("set schema 'Surveys';")
        xSchema.execute()
        var sltOne:PreparedStatement = cnx.prepareStatement(s"SELECT $clmSet FROM users INNER JOIN survey$tbName ON users.userName=$tbName.user_key;")
        println(s"${sltOne.toString}")
        val resSet:ResultSet = sltOne.executeQuery()
        try {
            surveyReceptorSelector(tbName, resSet)
        }
        catch{
            case e:Exception => {println(ExceptionHandler.ExceptionFinder.xMatcher(e))}
        }
    }

    def surveyReceptorSelector(tbName:String, results:ResultSet):Any={
        tbName match{
            case "surveyshort" => {
                val deSmall = mutable.ArrayBuffer[DataEntitySmall]()
                while(results.next){
                    deSmall += DataEntitySmall.fromTableSet(results)
                }
                deSmall
            }
            case "surveymedium" => {
                var deMid = mutable.ArrayBuffer[DataEntityMedium]()
                while(results.next){
                    deMid += DataEntityMedium.fromTableSet(results)
                }
                deMid
            }
            case "surveylong" => {
                var deLong = mutable.ArrayBuffer[DataEntityLong]()
                while(results.next){
                    deLong+= DataEntityLong.fromTableSet(results)
                }
                deLong
            }
        }
    }

}