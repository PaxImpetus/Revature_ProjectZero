package com.george.DataBase

import java.sql.ResultSet

case class User(
    id:Int,
    firstName:String,
    lastName:String,
    userName:String,
    passWord:String,
    hasShort:Boolean,
    hasMid:Boolean,
    hasLong:Boolean
)

case class DataEntitySmall(
    id:Int,
    fk:Int,
    ans0:Int,
    ans1:Int,
    ans2:Int,
    ans3:Int,
    ans4:Int
)

object DataEntitySmall{
    def fromTableSet(xyz:ResultSet):DataEntitySmall={
        apply(
            xyz.getInt("id"),
            xyz.getInt("userKey"),
            xyz.getInt("general"),
            xyz.getInt("foreignPolicy"),
            xyz.getInt("federalLocal"),
            xyz.getInt("propertyBusiness"),
            xyz.getInt("publicSafety")
        )
    }
}

case class DataEntityMedium(
    id:Int,
    fk:Int,
    ans0:Int,
    ans1:Int,
    ans2:Int,
    ans3:Int,
    ans4:Int,
    ans5:Int,
    ans6:Int,
    ans7:Int,
    ans8:Int,
    ans9:Int
)

object DataEntityMedium{
    def fromTableSet(xyz:ResultSet):DataEntityMedium={
        apply(
            xyz.getInt("id"),
            xyz.getInt("userKey"),
            xyz.getInt("general"),
            xyz.getInt("foreignPolicy"),
            xyz.getInt("federalLocal"),
            xyz.getInt("propertyBusiness"),
            xyz.getInt("publicSafety"),
            xyz.getInt("selfDefense"),
            xyz.getInt("relocation"),
            xyz.getInt("socialLaw"),
            xyz.getInt("criminalLaw"),
            xyz.getInt("breaker")
        )
    }
}


case class DataEntityLong(
    id:Int,
    fk:Int,
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
    ans19:Int
)

object DataEntityLong{
    def fromTableSet(xyz:ResultSet):DataEntityLong={
        apply(
            xyz.getInt("id"),
            xyz.getInt("userKey"),
            xyz.getInt("general"),
            xyz.getInt("foreignPolicy"),
            xyz.getInt("federalLocal"),
            xyz.getInt("propertyBusiness"),
            xyz.getInt("publicSafety"),
            xyz.getInt("selfDefense"),
            xyz.getInt("relocation"),
            xyz.getInt("socialLaw"),
            xyz.getInt("criminalLaw"),
            xyz.getInt("breaker"),
            xyz.getInt("merder"),
            xyz.getInt("kombat"),
            xyz.getInt("PoverD"),
            xyz.getInt("DoverP"),
            xyz.getInt("classI"),
            xyz.getInt("classW"),
            xyz.getInt("makeDeal"),
            xyz.getInt("takeDeal"),
            xyz.getInt("chicken"),
            xyz.getInt("egg")
        )
    }
}
