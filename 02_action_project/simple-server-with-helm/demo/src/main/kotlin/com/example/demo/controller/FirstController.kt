package com.example.demo.controller

import com.example.demo.dto.responseData
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import java.util.Random

@RestController
class FirstController {

    @GetMapping("/")
    fun fistController(
        @RequestParam("name") name: String
    ): responseData {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val returnCode = List(name.length) { charset.random() }.joinToString("")

        return responseData(
            name, returnCode,
        )
    }
}
