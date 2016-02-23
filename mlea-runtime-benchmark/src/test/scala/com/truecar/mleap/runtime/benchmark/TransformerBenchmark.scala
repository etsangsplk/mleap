package com.truecar.mleap.runtime.benchmark

import java.io.File

import com.truecar.mleap.core.serialization.JsonSerializationSupport._
import com.truecar.mleap.runtime.LocalLeapFrame
import com.truecar.mleap.runtime.serialization.RuntimeJsonSupport._
import com.truecar.mleap.runtime.transformer.Transformer
import org.scalameter.api._
import org.scalameter.picklers.Implicits._

/**
  * Created by hwilkins on 2/23/16.
  */
object TransformerBenchmark extends Bench.ForkedTime {
  lazy override val executor = {
    SeparateJvmsExecutor(
      Executor.Warmer.Zero,
      Aggregator.min[Double],
      new Measurer.Default)
  }

  val classLoader = getClass.getClassLoader
  val linearRegressionFile = new File(classLoader.getResource("transformer/RandomForest.json").getFile)
  val frameFile = new File(classLoader.getResource("data/Frame.json").getFile)

  val linearRegression = linearRegressionFile.parseTo[Transformer].get
  val frame = frameFile.parseTo[LocalLeapFrame].get

  val ranges = for {
    size <- Gen.range("size")(10000, 100000, 20000)
  } yield 0 until size

  measure method "transform" in {
    using(ranges) in {
      size =>
        size.foreach {
          _ => linearRegression.transform(frame)
        }
    }
  }
}
