package com.filter

import scala.concurrent.{ExecutionContext, Await}
import scala.concurrent.duration._
import scala.util.Try

/**
  * Created by akhil on 1/7/16.
  */
case class ContentRule(expression: String, toQ: List[String])
case class Rule(fromQ: String, contentRules: List[ContentRule])


trait Tables {
  val profile: slick.driver.JdbcProfile
  import profile.api._

  class RuleMap(tag: Tag) extends Table[(String, String)](tag, "RULE_MAP") {
    def fromQ = column[String]("FROM_Q", O.PrimaryKey)
    def contentRulesJson = column[String]("CONTENT_RULES_JSON")
    def * = (fromQ, contentRulesJson)
  }

  val ruleMap = TableQuery[RuleMap]
}

trait Dao {
  val dbprofile: slick.driver.JdbcProfile
  implicit val ec: ExecutionContext
  import dbprofile.api._

  lazy val tables = new Tables {
    override val profile = dbprofile
  }

  def createDDL(): Unit = {
    val f = DB.run(tables.ruleMap.schema.create)
    Await.result(f, 2 seconds)
  }
  def DB: Database

  def save(rule: Rule): Unit = {
    val f = DB.run(tables.ruleMap.insertOrUpdate(rule.fromQ, JsonHelper.toJson(rule.contentRules)))
    Await.result(f, 2 seconds)
  }
  def getAllRules(): Seq[Rule] = {
    val action = tables.ruleMap.result
    val f = DB.run(action).map(_.map(r => Rule(r._1, JsonHelper.fromJson[List[ContentRule]](r._2))))
    Await.result(f, 5 seconds)
  }
}