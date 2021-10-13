

package xyz.example.service
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.QueueOfferResult
import akka.stream.scaladsl.SourceQueueWithComplete

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
object CommonWeatherService  {

  def queueRequest(request: HttpRequest,queue: SourceQueueWithComplete[(HttpRequest, Promise[HttpResponse])]): Future[HttpResponse] = {
    val promise = Promise[HttpResponse]()
    queue.offer(request -> promise).flatMap {
      case QueueOfferResult.Enqueued => promise.future
      case QueueOfferResult.Dropped =>
        Future.failed(
          new RuntimeException("Queue overflowed. Try again later."))
      case QueueOfferResult.Failure(ex) => Future.failed(ex)
      case QueueOfferResult.QueueClosed =>
        Future.failed(new RuntimeException(
          "Queue was closed (pool shut down) while running the request. Try again later."))
    }
  }}

