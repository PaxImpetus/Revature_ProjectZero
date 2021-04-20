package com.george

import scala.io.StdIn
import com.george.UserInteraction

object Main{
    def main(args:Array[String]):Unit={
        var connectionCondition = true

        UserInteraction.ProgramInteraction.printToConsole()

        do{
            // get user input
            print("Provide Instruction:\t")
            val input = StdIn.readLine()

            if(input == "exit"){
                connectionCondition=false
            } //leave program when complete

            // Parse User Commands
            UserInteraction.ProgramInteraction.parseSurvey(input)
            println("")
            
        } while(connectionCondition==true)

        sys.exit(0)
    }
}