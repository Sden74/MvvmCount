package com.example.mvvmcount

import android.app.Application
import android.database.ContentObservable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.util.Timer
import java.util.TimerTask
class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = (application as MyApplication).viewModel
        val textView = findViewById<TextView>(R.id.textView)
        val observable = TextObservable()
        observable.observe(object : TextCallback{
            override fun updateText(str: String) = runOnUiThread {
                textView.text=str
            }
        })
        viewModel.init(observable)
        //val viewModel = ViewModel(observable)
        //viewModel.init()
    }

    override fun onDestroy() {
        viewModel.clear()
        super.onDestroy()
    }
}
class ViewModel(private val model: Model){
    private var textObservable: TextObservable?=null
    private val textCallback = object : TextCallback{
        override fun updateText(str: String) {
            textObservable?.postValue(str)
        }
    }
    fun init(textObservable: TextObservable){
        this.textObservable=textObservable
        model.start(textCallback)
    }
    fun clear(){
        textObservable=null
    }
}

class MyApplication:Application(){
    lateinit var viewModel: ViewModel
    override fun onCreate() {
        super.onCreate()

        viewModel= ViewModel(Model())
    }
}
/*class ViewModel(private val textObservable: TextObservable){
    *//*private val model = Model(object : TextCallback{
        override fun updateText(str: String) {
            textObservable.postValue(str)
        }
    })

    fun init(){model.start()}*//*
    init {
        Model.init(object : TextCallback{
            override fun updateText(str: String) {
                textObservable.postValue(str)
            }
        })
    }
    fun init(){Model.start()}
}*/
class Model{
    private var timer: Timer?=null
    private var count = 0
    private var callback: TextCallback?=null

    private var timerTask = object : TimerTask(){
        override fun run() {
            count++
            callback?.updateText(count.toString())
        }
    }
    fun start(textCallback: TextCallback){
        callback=textCallback
        if (timer==null){
            timer=Timer()
            timer?.scheduleAtFixedRate(timerTask,1000,1000)
        }
    }
}
/*class Model(private val textCallback: TextCallback) {
    private var timer: Timer? = null
    private var count = 0
    fun start(){
        timer?.cancel()
        timer = Timer()
        timer?.scheduleAtFixedRate(object:TimerTask(){
            override fun run() {
                count++
                textCallback.updateText(count.toString())
            }
        },1000,1000)
    }
}*/
/*object Model{
    private lateinit var textCallback: TextCallback
    private var timer: Timer?=null
    private var count = 0
    fun init(callback: TextCallback){
        textCallback = callback
    }
    fun start(){
        timer?.cancel()
        timer = Timer()
        timer?.scheduleAtFixedRate(object:TimerTask(){
            override fun run() {
                count++
                textCallback.updateText(count.toString())
            }
        },1000,1000)
    }
}*/
class TextObservable {
    private lateinit var callback: TextCallback

    fun observe(callback: TextCallback){ this.callback=callback}
    fun postValue(text: String) {callback.updateText(text)}

}

interface TextCallback{
    fun updateText(str: String)
}