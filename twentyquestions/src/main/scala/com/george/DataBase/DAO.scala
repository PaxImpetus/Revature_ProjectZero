package com.george.DataBase

import java.sql.Connection
import java.sql.ResultSet
import java.sql.PreparedStatement
import java.beans.Statement
import java.sql
import java.util.Date
import com.george.Constants
import com.george.ExceptionHandler
import com.george.UserInteraction.ProgramInteraction
import scala.collection.mutable
import javax.print.attribute.standard.DateTimeAtCreation
import java.text.SimpleDateFormat
import java.util.LinkedHashSet
import java.util.LinkedHashMap

object DAO{
    
    //Determine whether table contains entity and update/insert data appropraitely
    def parseEntityExistence(cnx:Connection, targetTableForm:String, results:mutable.ArrayBuffer[Int], setForm:String, currentUser:String, tblForm:String):Unit={
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
        val confirmation:PreparedStatement = cnx.prepareStatement(s"SELECT userPass FROM users WHERE userName='$userUN';")
        confirmation.execute()
        val rs = confirmation.getResultSet()
        var detectedUN = "" 
        var detectedPW = ""
        if(rs.next()){
            detectedPW = rs.getString("userPass")
        }
        if(detectedPW == userPW){
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
        inSurv.execute()
    }

    // Insert Results to Table
    // This is the one I am sampling with
    def insertInto(cnx:Connection, tblForm:String, results:mutable.ArrayBuffer[Int]):Unit={
        //cnx is the connection object, tbl form is the table i want to put it in, results is my personal set of data input into the database
        val xSchema:PreparedStatement = cnx.prepareStatement("set schema 'Surveys';") // set my schema, idk why but I have to do it for every command
        xSchema.execute() // execute the schema assignment
        var resultsSet = new StringBuilder("") // create a string builder object to contain all my values to be inserted into the target table
        for(res <- results){ // for every item in my array of values to insert
            resultsSet.addAll(s"$res,")// add the value a a string to a string builder object
        }
        val serve = tblForm match{
            case Constants.smallTableForm => "short"
            case Constants.mediumTableForm => "medium"
            case Constants.largeTableForm => "long"
        }
        resultsSet.addAll(s"'${Constants.currentUser}'") // this is another value, relative to my project - the user name of the current user is the foreign key of each survey to userName
        val inSurv:PreparedStatement = cnx.prepareStatement("INSERT INTO "+
            tblForm +
            s" VALUES ($resultsSet) ON CONFLICT ON CONSTRAINT survey${serve}_pkey DO NOTHING;") // reads "INSERT INTO table VALUES (a,b,c,d,e,...);" - my tables for this insert include short, medium, and long

        inSurv.execute() // execute the command
    }

    // addUser is the same, but for the users table


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
        var i = 1
        for(result <- results){
            val corrector = ProgramInteraction.surveyErrorCorrection(result.asInstanceOf[Int])
            inSurv.setInt(i, corrector)
            i+=1
        }
        inSurv.execute()
    }

    def retrieveAllStats(cnx:Connection, tbName:String):String={
        var statBallSet = new StringBuilder("")
        val xSchema:PreparedStatement = cnx.prepareStatement("set schema 'Surveys';") //fine
        xSchema.execute() // fine
        val setForm = tbName match{                         // --
            case "short" => Constants.questFormS      // --
            case "medium" => Constants.questFormM     // -- totally fine, this block
            case "long" => Constants.questFormL       // --
        }                                                   // --
        val loopSet = setForm.split(",")
        for(loop <- loopSet){
            var sltOne:PreparedStatement = cnx.prepareStatement(s"SELECT count($loop), avg($loop), stdDev($loop) FROM survey$tbName;") // get the average value and the std dev from some table
            val resSet:ResultSet = sltOne.executeQuery()
            if(resSet.next){
                val thisBall = StatBall.fromStatSet(loop, resSet)
                statBallSet.addAll(StatBall.statBallDisplay(thisBall))
            }
        }
        statBallSet.toString
    }

    // Retrieve User's Survey from target Table
    def selectMy(cnx:Connection, clmSet:String, tbName:String):String={
        val xSchema:PreparedStatement = cnx.prepareStatement("set schema 'Surveys';")
        xSchema.execute()
        var sltOne:PreparedStatement = cnx.prepareStatement(
            s"SELECT $clmSet FROM users INNER JOIN survey$tbName ON users.userName=survey$tbName.user_key WHERE userName = '${Constants.currentUser}';")
        val resSet:ResultSet = sltOne.executeQuery()
        try {
            surveyReceptorSelector(s"survey$tbName", resSet)
        }
        catch{
            case e:Exception => {ExceptionHandler.ExceptionFinder.xMatcher(e)}
        }
    }

    def surveyReceptorSelector(tbName:String, results:ResultSet):String={
        var myResults = ""
        tbName match{
            case "surveyshort" => {
                if(results.next){
                    val mySet = DataEntitySmall.fromTableSet(results)
                    myResults = DataEntitySmall.myShortStatDiplay(mySet)
                }
            }
            case "surveymedium" => {
                if(results.next){
                    val mySet = DataEntityMedium.fromTableSet(results)
                    myResults = DataEntityMedium.myMediumStatDiplay(mySet)
                }
            }
            case "surveylong" => {
                if(results.next){
                    val mySet = DataEntityLong.fromTableSet(results)
                    myResults = DataEntityLong.myLongStatDiplay(mySet)
                }
            }
        }
        myResults
    }

    def dropMySurvey(cnx:Connection, tbName:String):Unit={
        try{
            val xSchema:PreparedStatement = cnx.prepareStatement("set schema 'Surveys';")
            xSchema.execute()
            var dropMine:PreparedStatement = cnx.prepareStatement(s"DELETE FROM survey$tbName WHERE user_key='${Constants.currentUser}';")
            dropMine.execute()
        }
        catch{
            case e: Exception => {println(ExceptionHandler.ExceptionFinder.xMatcher(e))}
        }
    }

    def dropMe(cnx:Connection, tbName:String):Unit={
        try{
            val xSchema:PreparedStatement = cnx.prepareStatement("set schema 'Surveys';")
            xSchema.execute()
            var dropMine:PreparedStatement = cnx.prepareStatement(s"DELETE FROM $tbName WHERE userName='${Constants.currentUser}';")
            val check = dropMine.execute()
            if(check == true){
                com.george.Main.connectionCondition = false
            }
        }
        catch{
            case e: Exception => {println(ExceptionHandler.ExceptionFinder.xMatcher(e))}
        }
    }

    def addImportedSurveyCSV(cnx:Connection, tblForm:String, dataSet:String):Unit={
        val xSchema:PreparedStatement = cnx.prepareStatement("set schema 'Surveys';")
        xSchema.execute()
        var x = ""
        var thisTbl = ""
        var updates = ""
        tblForm match{
            case "short" => {x="short"; thisTbl=Constants.smallTableForm;updates=Constants.updateSml}
            case "medium" => {x="medium";thisTbl=Constants.mediumTableForm;updates=Constants.updateMid}
            case "long" => {x="long";thisTbl=Constants.largeTableForm;updates=Constants.updateLrg}
        }
        val results = dataSet.split(",")
        val resNum = results.dropRight(1)
        val resUsr = results.last
        var resultsSet = new StringBuilder("")
        for(res <- resNum){
            resultsSet.addAll(s"$res,")
        }
        resultsSet.addAll(s"'$resUsr'")
        val inData:PreparedStatement = cnx.prepareStatement("INSERT INTO "+
            thisTbl +
            s" VALUES (${resultsSet.toString}) ON CONFLICT ON CONSTRAINT survey${x}_pkey DO UPDATE SET $updates;")
        var i = 1
        for(res <- resNum){
            inData.setInt(i,res.toInt)
            i+=1
        }
        inData.execute()  
    }

    def addImportedUserCSV(cnx:Connection, dataSet:String):Unit={
        val xSchema:PreparedStatement = cnx.prepareStatement("set schema 'Surveys';")
        xSchema.execute()
        val results = dataSet.split(",")
        var resultsSet = new StringBuilder("")
        for(res <- results){
            resultsSet.addAll(s"'$res',")
        }
        val inUser:PreparedStatement = cnx.prepareStatement("INSERT INTO "+
            Constants.usersTableForm +
            s" VALUES (${resultsSet.dropRight(1)}) ON CONFLICT ON CONSTRAINT users_pkey DO NOTHING;")
        inUser.execute()  
    }

    def addImportedSurveyJSON(cnx:Connection, tbName:String, values:String):Unit={
        val xSchema:PreparedStatement = cnx.prepareStatement("set schema 'Surveys';")
        xSchema.execute()
        var x = ""
        var thisTbl = ""
        tbName match{
            case "short" => {x="short"; thisTbl=Constants.smallTableForm;}
            case "medium" => {x="medium";thisTbl=Constants.mediumTableForm;}
            case "long" => {x="long";thisTbl=Constants.largeTableForm;}
        }
        /*
        val results = values.split("|")
        var resultsSet = new StringBuilder("")
        for(res <- results){
            resultsSet.addAll(s"($res)")
            if(res!=results.last){
                resultsSet.addOne(',')
            }
        }
        */
        val inData:PreparedStatement = cnx.prepareStatement("INSERT INTO "+
            thisTbl +
            s" VALUES ${values} ON CONFLICT ON CONSTRAINT survey${x}_pkey DO NOTHING;")
        inData.execute() 
    }

    def addImportedUserJSON(cnx:Connection, values:String):Unit={
        val xSchema:PreparedStatement = cnx.prepareStatement("set schema 'Surveys';")
        xSchema.execute()
        /*
        val results = values.split("|")
        var resultsSet = new StringBuilder("")
        for(res <- results){
            resultsSet.addAll(s"($res)")
            if(res != results.last){
                resultsSet.addOne(',')
            }
        }
        */
        val inUser:PreparedStatement = cnx.prepareStatement("INSERT INTO "+
            Constants.usersTableForm +
            s" VALUES ${values} ON CONFLICT ON CONSTRAINT users_pkey DO NOTHING;")
        inUser.execute() 
    }
}