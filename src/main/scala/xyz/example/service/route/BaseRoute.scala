package xyz.example.service.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

trait BaseRoute {

  def route: Route
}

object BaseRoute {

  def fromRoutes(routes: List[BaseRoute]): BaseRoute = new BaseRoute {
    override def route: Route =
      routes.map(_.route).toSet.reduce((prev, curr) => prev ~ curr)
  }
}
