package com.george.DataBase

import java.sql.Connection
import java.sql.ResultSet
import java.sql.PreparedStatement

object DAO{

    // Insert Results to Table
    // This is the one i am sampling with
    def insertSurveySmall(cnx:Connection, results:String):Unit={
        val xSchema:PreparedStatement = cnx.prepareStatement("set schema 'Surveys';")
        xSchema.execute()
        val inSurv:PreparedStatement = cnx.prepareStatement("INSERT INTO "+
            "surveyshort(quest0,quest1,quest2,quest3,quest4) "+
            s"VALUES ($results);")
        //inSurv.setString(1, s"${tbName.toLowerCase()}")
        //inSurv.setString(2, results.toLowerCase())
        inSurv.execute()
    }

    def insertSurveyMedium(cnx:Connection, results:String):Unit={
        val xSchema:PreparedStatement = cnx.prepareStatement("set schema 'Surveys';")
        xSchema.execute()
        val inSurv:PreparedStatement = cnx.prepareStatement("INSERT INTO " +
            "surveymedium(quest0,quest1,quest2,quest3,quest4,quest5,quest6,quest7,quest8,quest9) " +
            s"VALUES ($results);")
        //inSurv.setString(1, s"${tbName.toLowerCase()}")
        //inSurv.setString(2, results.toLowerCase())
        inSurv.execute()
    }

    def insertSurveyLong(cnx:Connection, results:String):Unit={
        val xSchema:PreparedStatement = cnx.prepareStatement("set schema 'Surveys';")
        xSchema.execute()
        val inSurv:PreparedStatement = cnx.prepareStatement("INSERT INTO "+ 
            "surveylong(quest0,quest1,quest2,quest3,quest4,quest5,quest6,quest7,quest8,quest9,quest10,quest11,quest12,quest13,quest14,quest15,quest16,quest17,quest18,quest19) " +
            s"VALUES ($results);")
        //inSurv.setString(1, s"${tbName.toLowerCase()}")
        //inSurv.setString(2, results.toLowerCase())
        inSurv.execute()
    }

    // Insert Account into DB
    def insertBulk(cnx:Connection, tbName:String /*,fileSet:Array[String]*/):Unit={
        var files = ""
        val inUsr:PreparedStatement = cnx.prepareStatement(s"COPY ? FROM ? DELIMITER ',' CSV HEADER;")
        inUsr.setString(1, s"$tbName")
        inUsr.setString(2, s"$files")
        inUsr.execute()
    }
    // Retrieve a Single Survey
    def selectItem(cnx:Connection, dbName:String, uN:String):Unit={
        var sltOne:PreparedStatement = cnx.prepareStatement(s"SELECT * FROM ? WHERE user_Name=?;")
        sltOne.setString(1,s"$dbName")
        sltOne.setString(2,s"'$uN'")
        val resSet:ResultSet = sltOne.executeQuery()
        //val res = sltOne.getResultSet() //value getResultSet is not a member of Boolean
    }
    // Retrieve Table Set
    def selectTableStats(cnx:Connection, tbName:String, uN:String):Unit={
        var sltAll = cnx.prepareStatement(s"SELECT * FROM ?;")
        sltAll.setString(1, s"$tbName")
        val resSet:ResultSet = sltAll.executeQuery()
        while(resSet.next()){

        }
        //val res = sltAll.getResultSet() //value getResultSet is not a member of Boolean
    }

    def selectSampleStats(cnx:Connection, tbName:String, uN:String):Unit={
        var sltSome = cnx.prepareStatement(s"SELECT * FROM ?;")
        sltSome.setString(1, s"$tbName")
        val resSet:ResultSet = sltSome.executeQuery()
        while(resSet.next()){
            
        }
        //val res = sltAll.getResultSet() //value getResultSet is not a member of Boolean
    }
    /**
      *     // Flush Table Contents
      *     def truncateTable(cnx:Connection):Unit={} 
      *     // Not necessary, but possible
      */

}