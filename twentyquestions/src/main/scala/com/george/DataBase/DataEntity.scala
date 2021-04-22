package com.george.DataBase

import java.sql.ResultSet

case class User(
    id:Int,
    firstName:String,
    lastName:String,
    userName:String,
    passWord:String
)

case class DataEntity()

case class DataEntitySmall(
    ans0:Int,
    ans1:Int,
    ans2:Int,
    ans3:Int,
    ans4:Int,
    fk:String
)

object DataEntitySmall{
    def fromTableSet(xyz:ResultSet):DataEntitySmall={
        apply(
            xyz.getInt("quest0"),
            xyz.getInt("quest1"),
            xyz.getInt("quest2"),
            xyz.getInt("quest3"),
            xyz.getInt("quest4"),
            xyz.getString("user_key"),
        )
    }
}

case class DataEntityMedium(
    ans0:Int,
    ans1:Int,
    ans2:Int,
    ans3:Int,
    ans4:Int,
    ans5:Int,
    ans6:Int,
    ans7:Int,
    ans8:Int,
    ans9:Int,
    fk:String
)

object DataEntityMedium{
    def fromTableSet(xyz:ResultSet):DataEntityMedium={
        apply(
            xyz.getInt("quest0"),
            xyz.getInt("quest1"),
            xyz.getInt("quest2"),
            xyz.getInt("quest3"),
            xyz.getInt("quest4"),
            xyz.getInt("quest5"),
            xyz.getInt("quest6"),
            xyz.getInt("quest7"),
            xyz.getInt("quest8"),
            xyz.getInt("quest9"),
            xyz.getString("user_key")
        )
    }
}


case class DataEntityLong(
    ans0:Int,
    ans1:Int,
    ans2:Int,
    ans3:Int,
    ans4:Int,
    ans5:Int,
    ans6:Int,
    ans7:Int,
    ans8:Int,
    ans9:Int,
    ans10:Int,
    ans11:Int,
    ans12:Int,
    ans13:Int,
    ans14:Int,
    ans15:Int,
    ans16:Int,
    ans17:Int,
    ans18:Int,
    ans19:Int,
    fk:String
)

object DataEntityLong{
    def fromTableSet(xyz:ResultSet):DataEntityLong={
        apply(
            xyz.getInt("quest0"),
            xyz.getInt("quest1"),
            xyz.getInt("quest2"),
            xyz.getInt("quest3"),
            xyz.getInt("quest4"),
            xyz.getInt("quest5"),
            xyz.getInt("quest6"),
            xyz.getInt("quest7"),
            xyz.getInt("quest8"),
            xyz.getInt("quest9"),
            xyz.getInt("quest10"),
            xyz.getInt("quest11"),
            xyz.getInt("quest12"),
            xyz.getInt("quest13"),
            xyz.getInt("quest14"),
            xyz.getInt("quest15"),
            xyz.getInt("quest16"),
            xyz.getInt("quest17"),
            xyz.getInt("quest18"),
            xyz.getInt("quest19"),
            xyz.getString("user_key")
        )
    }
}

case class StatBall(
    colName:String,
    counter:Double,
    popMean:Double,
    stndDev:Double,
    varianc:Double
)

object StatBall{
    def fromStatSet(colName:String, xyz:ResultSet):StatBall={
        apply(
            colName,
            xyz.getDouble("count"),
            xyz.getDouble("avg"),
            xyz.getDouble("stdDev"),
            scala.math.pow(xyz.getDouble("stdDev"), 2.0)
        )
    }
}