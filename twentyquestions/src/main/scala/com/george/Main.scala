package com.george

import scala.io.StdIn
import com.george.UserInteraction

object Main{
    def main(args:Array[String]):Unit={
        var connectionCondition = true
        var userLogin = ""

        UserInteraction.ProgramInteraction.printToConsole()

        do {
            print("\nPlease Provide a Username:\t")
            userLogin = StdIn.readLine()
            connectionCondition = UserInteraction.ProgramInteraction.loginToApplication(userLogin)
        } while(connectionCondition==false)
    
        if(userLogin == "exit") sys.exit(0)

        while(connectionCondition==true){
            // get user input
            print("Provide Instruction:\t")
            val input = StdIn.readLine()

            if(input == "exit"){
                connectionCondition=false
            } //leave program when complete

            // Parse User Commands
            UserInteraction.ProgramInteraction.parseSurvey(input)
            println("")
            
        } 

        Constants.currentUser = ""
        sys.exit(0)
    }
}