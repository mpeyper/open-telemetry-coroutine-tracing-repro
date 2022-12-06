package example.tracing

import io.opentelemetry.api.trace.Span
import io.opentelemetry.extension.kotlin.asContextElement
import kotlinx.coroutines.withContext

suspend fun <TResult> withTracingContext(block: suspend () -> TResult): TResult {
  val span = Span.current()
  return withContext(span.asContextElement()) {
      block()
  }
}
