package com.black3.app.coroutinesasync_await_withcontext

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        exampleWithContext()

    }

    suspend fun printlnDelayed(message: String) {
        //Complex calculation
        //Thread.sleep(10000) //Block thread but and i want Coroutines
        delay(5000)
        println(message)
    }

    suspend fun calculateHardThings(startNum : Int) : Int{
        delay(1000)
        return startNum * 10
    }

    fun exampleBlocking() = runBlocking {
        println("one")
        printlnDelayed("two")
        println("three")
    }
    fun printlnTest() {
        println("Other one")
        println("Other two")
        println("Other three")
    }
    //Running on another thread but still blocking the main thread
    fun exampleBlockingDispatcher() {
        runBlocking(Dispatchers.Default) {
            println("one - from thread ${Thread.currentThread().name}")
            printlnDelayed("two - from thread ${Thread.currentThread().name}")
        }
        //Outside of runBlocking to show that it´s running in the blocked main thread
        println("three - from thread ${Thread.currentThread().name}")
        //It still runs only after the runBlocking is fully executed
    }
    fun exampleLaunchGlobal() = runBlocking {
        println("one - from thread ${Thread.currentThread().name}")
        GlobalScope.launch { printlnDelayed("two - from thread ${Thread.currentThread().name}") }
        println("three - from thread ${Thread.currentThread().name}")
    }
    fun exampleLaunchGlobalWaiting() = runBlocking {
        println("one - from thread ${Thread.currentThread().name}")
        val job = GlobalScope.launch { "two - from thread ${Thread.currentThread().name}" }
        println("three - from thread ${Thread.currentThread().name}")
        job.join()
    }
    fun exampleLaunchCoroutinesScope() = runBlocking {
        println("one - from thread ${Thread.currentThread().name}")
        val customDispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()
        launch (Dispatchers.Main){printlnDelayed("two - from thread ${Thread.currentThread().name}")}
        println("three - from thread ${Thread.currentThread().name}")
        (customDispatcher.executor as ExecutorService).shutdown()
    }
    fun exampleAsymcAwait() = runBlocking{
        val startTime = System.currentTimeMillis()

        val deferred1 = async {calculateHardThings(10)}
        val deferred2 = async {calculateHardThings(20)}
        val deferred3 = async {calculateHardThings(30)}

        val sum = deferred1.await() + deferred2.await() + deferred3.await()
        println("async/await result = $sum")

        val endtime = System.currentTimeMillis()
        println("Time taken: ${endtime - startTime}")
    }
    fun exampleWithContext() = runBlocking{
        val startTime = System.currentTimeMillis()

        val result1 = withContext(Dispatchers.Default) {calculateHardThings(10)}
        val result2 = withContext(Dispatchers.Default) {calculateHardThings(20)}
        val result3 = withContext(Dispatchers.Default) {calculateHardThings(30)}

        val sum = result1 + result2+ result3
        println("async/await result = $sum")

        val endtime = System.currentTimeMillis()
        println("Time taken: ${endtime - startTime}")
    }
}
