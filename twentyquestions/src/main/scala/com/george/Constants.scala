package com.george

object Constants{
    val userTbl = "users"
    val smalTbl = "surveyshort"
    val midlTbl = "surveymedium"
    val longTbl = "surveylong"
    val usersTableForm = "users(first_name,last_name,userName,userPass)"
    val smallTableForm = "surveyshort(quest0,quest1,quest2,quest3,quest4)"
    val mediumTableForm = "surveymedium(quest0,quest1,quest2,quest3,quest4,quest5,quest6,quest7,quest8,quest9)"
    val largeTableForm = "surveylong(quest0,quest1,quest2,quest3,quest4,quest5,quest6,quest7,quest8,quest9,quest10,quest11,quest12,quest13,quest14,quest15,quest16,quest17,quest18,quest19)"
    val updateSml = "quest0=?,quest1=?,quest2=?,quest3=?,quest4=?"
    val updateMid = "quest0=?,quest1=?,quest2=?,quest3=?,quest4=?,quest5=?,quest6=?,quest7=?,quest8=?,quest9=?"
    val updateLrg = "quest0=?,quest1=?,quest2=?,quest3=?,quest4=?,quest5=?,quest6=?,quest7=?,quest8=?,quest9=?,"+
                    "quest10=?,quest11=?,quest12=?,quest13=?,quest14=?,quest15=?,quest16=?,quest17=?,quest18=?,quest19=?"
    var currentUser = ""
    val usersNotes = "first_name,last_name,userName,userPass"
    val questFormS = "quest0,quest1,quest2,quest3,quest4"
    val questFormM = "quest0,quest1,quest2,quest3,quest4,quest5,quest6,quest7,quest8,quest9"
    val questFormL = "quest0,quest1,quest2,quest3,quest4,quest5,quest6,quest7,quest8,quest9,quest10,quest11,quest12,quest13,quest14,quest15,quest16,quest17,quest18,quest19"
}