package com.filter

import akka.actor.{Actor, ActorSystem, Props}
import akka.camel._
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.activemq.camel.component.ActiveMQComponent
import org.apache.camel.CamelContext
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.scala.dsl.builder.ScalaRouteBuilder
import org.apache.camel.spring.spi.SpringTransactionPolicy
import org.springframework.jms.connection.JmsTransactionManager

/**
  * Created by akhil on 1/6/16.
  */
class MqConsumer(override val endpointUri: String) extends Consumer {

  //override def endpointUri = "activemq:FOO.BAZZ2"

  override def receive = {
    case msg: CamelMessage =>
      println(msg)
  }
}

class MqProducer extends Actor with Producer with Oneway {
  override def endpointUri = "activemq:queue:FOO.BAR"
}

class MyRouteBuilder(rule: Rule)(implicit override val context: CamelContext, txPolicy: SpringTransactionPolicy) extends ScalaRouteBuilder(context) {
  handle[Exception] {
    to("stream:err")
  }.maximumRedeliveries(10).handled()

  rule.fromQ policy(txPolicy)  choice {
    rule.contentRules.map(r => when(_.jsonpath(r.expression)) --> (r.toQ: _*))
    otherwise  --> ("stream:err")
  }
}


case class Person(name: String, age: Int, address: Address)
case class Address(city: String, zip: String)

object TestC extends App {

  val jmsConnectionFactory: ActiveMQConnectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616")
  val jmsTransactionManager = new JmsTransactionManager(jmsConnectionFactory)
  implicit val required = new SpringTransactionPolicy(jmsTransactionManager)
  required.setPropagationBehaviorName("PROPAGATION_REQUIRED")

  val activeMQComponent = new ActiveMQComponent()
  activeMQComponent.setConnectionFactory(jmsConnectionFactory)
  //activeMQComponent.setTransacted(true)
  //activeMQComponent.setTransactionManager(jmsTransactionManager)

  implicit val context: CamelContext = new DefaultCamelContext()

  context.addComponent("activemq", activeMQComponent)

  val rule = Rule(fromQ = "activemq:FOO.BAR",
    contentRules = List(ContentRule(
      expression = "$.[?(@.name=='akhil')]",
      toQ = List("stream:err", "activemq:FOO.BAZZ"))))
  val rules = List(rule, rule.copy(fromQ = "activemq:FOO.BAZ2"))

  rules.map(r => context.addRoutes(new MyRouteBuilder(r)))
  //context.addRoutes(new MyRouteBuilder(context, required, rules))
  context.start()
  context.startAllRoutes()

  println()
  println("==========>" + context.getRouteDefinitions)
}

object Test extends App {
  val sys = ActorSystem("some-system")
  val camel = CamelExtension(sys)
  val camelContext = camel.context

  camelContext.addComponent("activemq", ActiveMQComponent.activeMQComponent(
    "tcp://localhost:61616"))
  // "vm://localhost?broker.persistent=false"))

  val mqc1 = sys.actorOf(Props(new MqConsumer("activemq:FOO.BAZZ")), "mqconsumer1")
  val mqc2 = sys.actorOf(Props(new MqConsumer("activemq:FOO.BAZZ2")), "mqconsumer2")

  //val f = camel.activationFutureFor(mqc)(timeout = 10 seconds, executor = sys.dispatcher)
  //Await.result(f, 5 second)

  val a = Address("hyd", "07095")
  val p = Person("akhil", 11, a)
  //Thread.sleep(5000)
  val mqP = sys.actorOf(Props[MqProducer], "producer")
  mqP ! CamelMessage(JsonHelper.toJson(p), Map(CamelMessage.MessageExchangeId -> "123"))
  //sys.terminate()
}

object JsonHelper {
  import org.json4s._
  import org.json4s.jackson.JsonMethods._
  import org.json4s.jackson.Serialization._

  implicit val jsonFormats = DefaultFormats

  def fromJson[T](json: String)(implicit mf: Manifest[T]): T = read[T](json)
  def toJson[T<: AnyRef](obj: T): String = write(obj)

}