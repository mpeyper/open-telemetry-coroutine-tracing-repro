package example

import example.tracing.withSpan
import example.tracing.withTracingContext
import io.opentelemetry.instrumentation.annotations.WithSpan
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

object Example {
    @WithSpan
    fun runProcess() = runBlocking {
        withTracingContext {
            suspendingParent()
        }
    }

    private suspend fun suspendingParent() = withSpan{
        blockingChild()
        suspendingChild()
    }

    private suspend fun suspendingChild() = withSpan {
        delay(10)
    }

    @WithSpan
    private fun blockingChild() = Thread.sleep(10)
}