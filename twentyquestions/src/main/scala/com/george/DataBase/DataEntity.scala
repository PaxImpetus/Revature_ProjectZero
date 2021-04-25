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

    def myShortStatDiplay(abc:DataEntitySmall):String={
        var cumulativeString = new StringBuilder("")
        cumulativeString.addAll(s"\nuser: ${abc.fk}\n" + 
            s"\tI prefer Indoors over Outdoors.: ${abc.ans0}\n" + 
            s"\tI prefer Relationships over Experiences: ${abc.ans1}\n" + 
            s"\tI prefer Wealth over Value: ${abc.ans2}\n" +
            s"\tI prefer Comfort over Luxury: ${abc.ans3}\n" +
            s"\tI prefer Solidarity over Tolerance: ${abc.ans4}\n\n")
        cumulativeString.toString
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
    def myMediumStatDiplay(abc:DataEntityMedium):String={
        var cumulativeString = new StringBuilder("")
        cumulativeString.addAll(s"\nuser: ${abc.fk}\n" + 
            s"\tI prefer Mashed Potatos over French Fries: ${abc.ans0}\n" +
            s"\tIt is best to try new foods as a snack: ${abc.ans1}\n" +
            s"\tIt eat a lot: ${abc.ans2}\n" +
            s"\tIt is customary to eat meat in every meal: ${abc.ans3}\n" +
            s"\tI am a vegan I do not eat any animal products: ${abc.ans4}\n" +
            s"\tI prefer to eat what I want, not what I need: ${abc.ans5}\n" +
            s"\tI consult my dietitian before eating other cultural dishes: ${abc.ans6}\n" +
            s"\tI would rather eat at a a restaurant than cook at home: ${abc.ans7}\n" +
            s"\tI would eat all day if I never had to worry about making money: ${abc.ans8}\n" +
            s"\tIn the event of a famine, I would be the first to go: ${abc.ans9}\n\n")
        cumulativeString.toString
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
    def myLongStatDiplay(abc:DataEntityLong):String={
        var cumulativeString = new StringBuilder("")
        cumulativeString.addAll(s"\nuser: ${abc.fk}\n" + 
            s"\tPolitics contain an important set of social topics to be considered: ${abc.ans0}\n" + 
            s"\tOur nation's foreign policy is the most important factor in our defense strategy: ${abc.ans1}\n" + 
            s"\tFederal policy is more impactful than local: ${abc.ans2}\n" +
            s"\tOwning property is more beneficial than owning a busuiness: ${abc.ans3}\n" +
            s"\tPublic Safety is the most important issue: ${abc.ans4}\n" + 
            s"\tSelf Defense is a Human Right: ${abc.ans5}\n" + 
            s"\tYou would be willing to relocate for marginally better opportunities: ${abc.ans6}\n" +
            s"\tLaws should focus on social norms: ${abc.ans7}\n" +
            s"\tLaws should focus on criminality: ${abc.ans8}\n" + 
            s"\tRules are made to be broken: ${abc.ans9}\n" +
            s"\tIt is never okay to take the life of a fellow citizens: ${abc.ans10}\n" + 
            s"\tWithout a dialog, we can only fight: ${abc.ans11}\n" +
            s"\tPatience requires Discipline: ${abc.ans12}\n" +
            s"\tDiscipline Requires Patience: ${abc.ans13}\n" + 
            s"\tClass is determined by intelligence: ${abc.ans14}\n" + 
            s"\tClass is determined by wealth: ${abc.ans15}\n" +
            s"\tWe value the agreements we propose more than those we take on: ${abc.ans16}\n" +
            s"\tWe value the agreements we take on more than those we propose: ${abc.ans17}\n" + 
            s"\tThe Chicken Came First: ${abc.ans18}\n" +
            s"\tThe Egg Came First: ${abc.ans19}\n\n")
        cumulativeString.toString
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