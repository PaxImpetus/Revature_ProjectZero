package com.george.DataBase

import java.sql.PreparedStatement
import java.sql.Connection

object CustomJSONParser{
    def sortJSON(filePath:String, targetTable:String):String={
        val pathBuilder = new StringBuilder("")
        val fileArrayPrep = filePath.split("/")
        val jsonString = os.read(os.pwd/"src"/"main"/"SampleDocuments"/"JaySun"/s"${fileArrayPrep.last}")
        val objectCounter = jsonString.count(_ == '{')-1
        val data = ujson.read(jsonString)
        var finalString = new StringBuilder("")
        if(targetTable == "users"){
            for(i <- 0 to objectCounter-1)
            {
                finalString.addAll(s"('${data("users")(i)("first_Name").value}','${data("users")(i)("last_Name").value}'," +
                      s"'${data("users")(i)("userName").value}','${data("users")(i)("userPass").value}')")
                if(i != objectCounter-1){
                    finalString.addOne(',')
                }
            }
        }
        else{
            val tableSize = targetTable match {
                case "short" => 5
                case "medium" => 10
                case "long" => 20
            }
            for(j <- 0 to objectCounter-1){
                for(i <- 0 to tableSize-1){
                    if(i ==0){
                        finalString.addOne('(')
                    }
                    finalString.addAll(s"${data(s"survey$targetTable")(j)(s"quest${i}")},")
                }
                val userName = data(s"survey$targetTable")(j)("user_key").str
                finalString.addAll(s"'${userName.replace("\"","")}'")
                finalString.addOne(')')
                if(j != objectCounter-1){
                    finalString.addOne(',')
                }
            }
        }
        finalString.toString
    }
}
