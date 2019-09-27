package org.fjodorekstrom.nodekotlin

external fun require(module:String):dynamic

fun main(args: Array<String>) {
    println("Hello Javascript!")

    val express = require("express")
    val app = express()

    app.get("/", { req, res ->
        res.type("text/plain")
        res.send("i am a beautiful butterfly")
    })

    app.get("/bucket/create/:bucketName", {req, res, next -> createBucket(req, res, next)}, { req, res ->
        res.send("think I created a bucket on aws")
    })
    app.get("/bucket/delete/:bucketName", {req, res, next -> removeBucket(req, res, next)}, {req, res ->
      res.send("I have probably removed the bucket")
    })

    app.listen(3000, {
        println("Listening on port 3000")
    })
}

fun removeBucket(req: dynamic, res: dynamic, next: dynamic) {
    val AWS = require("aws-sdk")
    val awsParams: dynamic = object{}
    awsParams["region"] = "eu-west-1"
    val s3 = js("new AWS.S3(awsParams)")
    val s3Params: dynamic = object{}
    val bucketName = req["param"]["bucketName"]
    println("bucketName: $bucketName")

    s3Params["Bucket"] = req.param.bucketName

    s3.deleteBucket(s3Params, { err, data ->
        if(err && !data) {
            val stringerror = js("JSON.stringify(err, null, 2)")
            println("Something went wrong $stringerror")
            next()
        } else {
            val stringdata = js("JSON.stringify(data, null, 2)")
            println("Bucket probably removed: $stringdata")
            next()
        }
    })
}

fun createBucket(req: Map<String, Any?>, res: Map<String, Any?>, next: dynamic) {
    println("Hello AWS!")

    val AWS = require("aws-sdk")
    val awsParams: dynamic = object{}
    awsParams["region"] = "eu-west-1"
    val s3 = js("new AWS.S3(awsParams)")

    val bucketParams: dynamic = object{}
    val bucketName = req["param"]["bucketName"]
    println("bucketName: $bucketName")
    bucketParams["Bucket"] = req.param.bucketname
    val createBucketConfiguration: dynamic = object{}
    createBucketConfiguration["LocationConstraint"] = "eu-west-1"
    bucketParams["CreateBucketConfiguration"] = createBucketConfiguration

    s3.createBucket(bucketParams, { err, data ->
        if (err && !data) {
            val stringerror = js("JSON.stringify(err, null, 2)")
            println("Something went wrong $stringerror")
            next()
        } else {
            val stringdata = js("JSON.stringify(data, null, 2)")
            println("Bucket probably created: $stringdata")
            next()
        }
    })
}