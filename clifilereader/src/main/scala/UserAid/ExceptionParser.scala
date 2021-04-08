package scala.UserAid

import java.io.IOException

object ExceptionsParser{
    def xMatcher(except:Exception):String={
        except match {
            case IOException => {"File Stream Failure."}
            case FileNotFoundException => {"File Not Found."}
            case ClassCastException => {"Assignment Error - Val/Var is not the smae type as input"}
            // ... other cases to check for ...
            case _: NullPointerException => {"Target NotNull Value/Reference is Null"}
        }
    }
}