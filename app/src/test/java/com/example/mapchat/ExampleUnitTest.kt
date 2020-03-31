package com.example.mapchat

import com.google.firebase.auth.FirebaseAuth
import convertToAscii
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {


    @Test
    fun StringToNumberConvertUnitTest() {

        println(convertToAscii("03l6oRVeQRhrDgXy2acYdBwogDk2", "08ISaxAZ6PdzjSLv7Mjtn7bYwOf2"))
        println(convertToAscii("08ISaxAZ6PdzjSLv7Mjtn7bYwOf2", "03l6oRVeQRhrDgXy2acYdBwogDk2"))

        val uniqueNumber = convertToAscii("sarim", "khan")
        println(uniqueNumber.javaClass.name)
    }
}
