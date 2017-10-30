package library.service.messaging.books

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import library.service.business.books.domain.events.*
import library.service.business.books.domain.types.BookId
import library.service.messaging.Channels
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import utils.classification.UnitTest
import java.time.OffsetDateTime
import java.util.*

@UnitTest
internal class MessagingBookEventDispatcherTest {

    val rabbitTemplate = mock<RabbitTemplate>()
    val objectMapper = ObjectMapper()

    val cut = MessagingBookEventDispatcher(rabbitTemplate, objectMapper)

    val uuid = UUID.randomUUID()
    val bookId = BookId.generate()
    val timestamp = OffsetDateTime.now()

    @TestFactory fun `events are send as JSONs`(): List<DynamicTest> {
        val map = mapOf<BookEvent, String>(
                BookAdded(uuid, bookId, timestamp) to "book-added",
                BookRemoved(uuid, bookId, timestamp) to "book-removed",
                BookBorrowed(uuid, bookId, timestamp) to "book-borrowed",
                BookReturned(uuid, bookId, timestamp) to "book-returned"
        )
        return map.map { (event, type) ->
            dynamicTest(event.javaClass.simpleName) {
                cut.dispatch(event)
                val expectedJson = """{"id":"$uuid","bookId":"$bookId","timestamp":"$timestamp","type":"$type"}"""
                verify(rabbitTemplate).convertAndSend(Channels.BOOK_EVENTS, expectedJson)
            }
        }
    }

}