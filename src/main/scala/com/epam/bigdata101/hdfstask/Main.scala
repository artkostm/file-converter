package com.epam.bigdata101.hdfstask

object Main extends App {
  CliParser.parse(args, Configuration()) match {
    case Some(config) => println("Config: " + config)
    case None => ()
  }
}
