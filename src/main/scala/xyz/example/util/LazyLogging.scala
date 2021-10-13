package xyz.example.util

import org.slf4j.{Logger, LoggerFactory}

trait LazyLogging {

  protected lazy val log: Logger =
    LoggerFactory.getLogger(getClass.getName)
}
