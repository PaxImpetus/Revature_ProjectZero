package com.george.DataBase

import java.sql.DriverManager
import java.sql.Connection

object DatabaseBuilder{

    val defaultDB = Credentials.defunctDB
    val defaultUN = Credentials.defunctUN
    val defaultPW = Credentials.defunctPW

    def initializeDatabasePort():Unit={

    }

    class DatabaseBuilder(dbName:String, uN:String, pW:String){

        var connex:Connection = null

        def this(dbName:String)={
            this(dbName, defaultUN, defaultPW)
        }

        def this()={
            this(defaultDB, defaultUN, defaultPW)
        }

        def dbLaunch():Connection={
            classOf[org.postgresql.Driver].newInstance()
            this.connex = DriverManager.getConnection(s"$dbName", s"$uN", s"$pW") // value getConnection is not a member of object java.sql.Connection
            connex
        }

        def dbClose():Unit={
            if(this.connex != null) {
                this.connex.close()
            }
        }
    }
}