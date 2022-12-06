package example.tracing

import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.StatusCode
import io.opentelemetry.extension.kotlin.asContextElement
import io.opentelemetry.extension.kotlin.getOpenTelemetryContext
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

suspend inline fun <Result> withSpan(
  name: String = getDefaultSpanName(),
  crossinline block: suspend (span: Span?) -> Result
): Result {
  val localTracer = GlobalOpenTelemetry.getTracer(object{}.javaClass.packageName)
  val span: Span = localTracer.spanBuilder(name).run {
    setParent(coroutineContext.getOpenTelemetryContext())
    coroutineContext[CoroutineName]?.let { setAttribute("coroutine.name", it.name) }
    startSpan()
  }

  return withContext(span.asContextElement()) {
    try {
      block(span)
    } catch (throwable: Throwable) {
      span.setStatus(StatusCode.ERROR)
      span.recordException(throwable)
      throw throwable
    } finally {
      span.end()
    }
  }
}

@Suppress("NOTHING_TO_INLINE") // inlining to remove this function from the stack trace
inline fun getDefaultSpanName(): String {
  val callingStackFrame = Thread.currentThread().stackTrace[1]

  val simpleClassName = Class.forName(callingStackFrame.className).simpleName
  val methodName = callingStackFrame.methodName

  return "$simpleClassName.$methodName"
}
