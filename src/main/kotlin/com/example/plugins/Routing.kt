package com.example.plugins

import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*

val client = HttpClient(CIO)

@FlowPreview
fun Application.configureRouting() {

    routing {
        get("/async") {
            val one = async { getOne() }
            val two = async { getTwo() }

            call.respondText("""
                One: ${one.await()}
                Two: ${two.await()}
            """.trimIndent())
        }

        get("/flow") {
            val responses = mutableListOf<String>()

            (1..3).asFlow()
                .flatMapMerge { getSomething(it) }
                .collect { responses.add(it) }

            call.respondText {"""
                Responses: $responses
            """.trimIndent() }
        }
    }
}


suspend fun getOne(): HttpStatusCode {
    val response: HttpResponse = client.get("https://ktor.io/")
    return response.status
}

suspend fun getTwo(): HttpStatusCode {
    val response: HttpResponse = client.get("https://ktor.io/")
    return response.status
}

suspend fun getSomething(i: Int): Flow<String> {
    val response: HttpResponse = client.get("https://ktor.io/")
    return flowOf(response.status.description)
}