package scala.Main

import scala.io.StdIn

object Main{

    // enter the program
    def main(args:String[Array]):Unit={
        // wait for user input to read some inputs
        Opening.printOpeningText
        UserOptions.printOptionsToUser
        var input = StdIn.readLine()
        // the rest is used to operate based in inputs
        // ...do stuff
        UserInteaction.execFunc(input)
    }
}