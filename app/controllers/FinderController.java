package controllers;

import play.mvc.Result;
import services.MongoService;

import javax.inject.Inject;
import javax.inject.Singleton;

import static play.mvc.Results.ok;

@Singleton
public class FinderController {

  @Inject
  MongoService mongoService;

  public Result index(final String query, final String sort) {
    return ok(mongoService.getMessage(query, sort)).as("application/json")
            .withHeader("Access-Control-Allow-Origin", "*");
  }
}