package com.kmalik.sample

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.classification.LogisticRegressionWithSGD
import org.apache.spark.mllib.classification.LogisticRegressionModel
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.storage.StorageLevel
import com.kmalik.sample.utils.Utils
import com.kmalik.sample.utils.MLUtils
import breeze.macros.expand.args
import com.typesafe.config.Config
import breeze.macros.expand.args

/**
 *
 */
object LogisticRegressionTrain extends Serializable  {
  
  def run(sc:SparkContext, args:Array[String]): Unit = {
    val train = Utils.readArg(args, "train")
    val sep = Utils.readArg(args, "sep", ",")
    val featureIndices = Utils.readArg(args, "features").split(",", -1).map(_.toInt)
    val labelIndex = Utils.readArg(args, "label", 0)
    val numIterations = Utils.readArg(args, "iterations", 10)
    val modelPath = Utils.readArg(args, "modelPath", train + ".model." + System.currentTimeMillis)
    extracted(sc, train, sep, featureIndices, labelIndex, numIterations, modelPath)
  }
  
  def run(sc:SparkContext, config:Config): Unit = {
    val train = Utils.readConfig(config, "train")
    val sep = Utils.readConfig(config, "sep", ",")
    val featureIndices = Utils.readConfig(config, "features").split(",", -1).map(_.toInt)
    val labelIndex = Utils.readConfig(config, "label", 0)
    val numIterations = Utils.readConfig(config, "iterations", 10)
    val modelPath = Utils.readConfig(config, "modelPath", train + ".model." + System.currentTimeMillis)
    extracted(sc, train, sep, featureIndices, labelIndex, numIterations, modelPath)
  }

  private def extracted(sc: org.apache.spark.SparkContext, train: String, sep: String, featureIndices: Array[Int], labelIndex: Int, numIterations: Int, modelPath: String) = {
    val trainData = MLUtils.readLabeledPoints(sc, train, sep, featureIndices, labelIndex)
    
    val model = MLUtils.trainModel(trainData, numIterations)

    model.save(sc, modelPath)
    
    println("Model built and saved at " + modelPath)
  }
}