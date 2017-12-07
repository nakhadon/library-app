package library.service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.view.RedirectView
import java.time.Clock


@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
class Application {

    @Bean fun utcClock(): Clock = Clock.systemUTC()

    @Controller
    class RedirectController {

        @GetMapping("/")
        fun redirectIndexToDocumentation() = RedirectView("/docs/index.html")

        @GetMapping("/docs")
        fun redirectDocsToDocumentation() = RedirectView("/docs/index.html")

        @GetMapping("/help")
        fun redirectHelpToDocumentation() = RedirectView("/docs/index.html")

    }

}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
