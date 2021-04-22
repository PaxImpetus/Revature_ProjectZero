package com.george.ExceptionHandler

import java.io.FileNotFoundException
import java.sql.SQLException
import org.postgresql.util.PSQLException

object ExceptionFinder{
    def xMatcher(exception:Any):String={
        exception match{
            case nfile: FileNotFoundException => {"File Not Found"}
            case psql: PSQLException => {psql.getMessage()}
            case sqle: SQLException => {sqle.getMessage()}
            case genex: Exception =>{genex.getMessage()} //  class java.lang.Exception is not a value
        }
    }
}