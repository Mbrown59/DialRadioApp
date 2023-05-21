package com.example.app2

import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.*
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.*
import android.webkit.*
import android.webkit.CookieSyncManager.createInstance
import android.widget.*
import android.widget.CompoundButton.*
import android.widget.SeekBar.*
import android.widget.TextView.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.CompoundButton
import android.webkit.WebViewClient
import androidx.constraintlayout.widget.ConstraintLayout
import java.lang.Thread.sleep
import kotlin.math.abs


class Delegate : WebViewClient()
{
  override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?)
  {
    super.onPageStarted(view, url, favicon)
    println("started")
  }

  override fun onPageFinished(view: WebView, url: String)
  {
    super.onPageFinished(view, url)
    println("finish")
  }

  override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError)
  {
    println(error.description)
  }

  override fun onReceivedHttpError(
    view: WebView, request: WebResourceRequest, errorResponse: WebResourceResponse
  )
  {
    println(errorResponse.data)
  }

  override fun onReceivedSslError(
    view: WebView, handler: SslErrorHandler,
    error: SslError
  )
  {
    println(error.primaryError)
  }
}

fun isNetworkAvailable(context: Context): Boolean
{
  var result = false
  (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
    {

      result = isCapableNetwork(this, this.activeNetwork)

    } else
    {
      val networkInfos = this.allNetworks
      for (tempNetworkInfo in networkInfos)
      {
        if (isCapableNetwork(this, tempNetworkInfo))
          result = true
      }
    }
  }
  return result
}

fun isCapableNetwork(cm: ConnectivityManager, network: Network?): Boolean
{
  cm.getNetworkCapabilities(network)?.also {
    if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
    {
      return true
    } else if (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    {
      return true
    }
  }
  return false
}


class Handler : View.OnClickListener, OnCheckedChangeListener, OnSeekBarChangeListener,
  DialogInterface.OnClickListener
{
  private var station = ""
  private var fn = ""
  private var imageView: ImageView? = null
  private var CbuttonView: CompoundButton? = null
  private var text = ""
  private var layout: ConstraintLayout? = null
  private var textView : TextView? = null


  constructor()
  {
    imageView = MainActivity.getInstance().findViewById(R.id.nowPlaying)
    CbuttonView = MainActivity.getInstance().findViewById(R.id.AM_FM)
    layout = MainActivity.getInstance().findViewById(R.id.cl)
    textView = MainActivity.getInstance().findViewById(R.id.NowPlaying)

  }


  override fun onClick(v: View?)
  {
    //Get the text – note the typecase to more specific
    var text1 = (v as Button).getText()
    println(text1)

    if (text1 == "Play")
    {
      var s = getStation()
      println("Station: " + s)
      var f = getFn()
      var id = MainActivity.getInstance().resources.getIdentifier(
        f,
        "drawable",
        MainActivity.getInstance().packageName
      )

      var webView: WebView? = null
      webView = MainActivity.getInstance().findViewById(R.id.webView)
      var delegate = Delegate();
      webView?.webViewClient = delegate

      //This will allow the tracing of links
      webView?.getSettings()?.setJavaScriptEnabled(true)
      webView?.getSettings()?.setJavaScriptCanOpenWindowsAutomatically(true)
      imageView?.setImageResource(id)
      println("f " +f)
      var playing = f.uppercase()
      textView?.setText(playing)
      sleep(5000)

      webView.setVisibility(View.VISIBLE)
      webView?.loadUrl(s)
      //onBackPressed(webView)


    }

  }


  override fun onStartTrackingTouch(seekBar: SeekBar?)
  {
    println("start")
  }

  override fun onStopTrackingTouch(seekBar: SeekBar?)
  {
    println("stop")
    var p = getProgress(seekBar)
    var t: Double? = null
    var min: Double? = null
    var max: Double? = null
    println(p.div(10.0))



    if (CbuttonView!!.isChecked() == true)
    {
      var arr = arrayOf(-1000.0, 101.8, 110.6, 113.0, 118.2, 129.8, 136.2, 163.6, 1000.0)

      min = arr.filter { it <= (p.div(10.0)) }.minByOrNull { (p.div(10.0) - it) }
      max = arr.filter { it >= (p.div(10.0)) }.maxByOrNull { (p.div(10.0) - it) }

      println("min " + arr.filter { it <= (p.div(10.0)) }.minByOrNull { (p.div(10.0) - it) })
      println("max " + arr.filter { it >= (p.div(10.0)) }.maxByOrNull { (p.div(10.0) - it) })

      var high = max?.minus((p.div(10.0)))
      var low = min?.minus((p.div(10.0)))

      if (abs(high!!) > abs(low!!))
      {
        t = min
      } else
      {
        t = max
      }
      println("high " + high)
      println("low " + low)
      println("t" + t)

      if (t == 101.8)
      {
        setStation("http://playerservices.streamtheworld.com/api/livestream-redirect/WXYTFM.mp3")
        setFn("wxyt")
      } else if (t == 110.6)
      {
        setStation("http://playerservices.streamtheworld.com/api/livestream-redirect/KURBFM.mp3")
        setFn("kurb")
      } else if (t == 113.0)
      {
        setStation("http://playerservices.streamtheworld.com/api/livestream-redirect/WKIMFM.mp3")
        setFn("wkim")
      } else if (t == 118.3)
      {
        setStation("http://playerservices.streamtheworld.com/api/livestream-redirect/WWTNFM.mp3")
        setFn("wwtn")
      } else if (t == 129.8)
      {
        setStation("http://playerservices.streamtheworld.com/api/livestream-redirect/KDXEFM.mp3")
        setFn("kdxe")
      } else if (t == 136.2)
      {
        setStation("http://playerservices.streamtheworld.com/api/livestream-redirect/KARNFM.mp3")
        setFn("karnfm")
      } else if (t == 163.6)
      {
        setStation("http://playerservices.streamtheworld.com/api/livestream-redirect/KLALFM.mp3")
        setFn("klal")
      }
    } else if (CbuttonView!!.isChecked() == false)
    {
      var arr = arrayOf(-1000.0, 79.6, 95.0, 102.3, 107.1, 117.5, 1000.0)

      min = arr.filter { it <= (p.div(10.0)) }.minByOrNull { (p.div(10.0) - it) }
      max = arr.filter { it >= (p.div(10.0)) }.maxByOrNull { (p.div(10.0) - it) }

      println("min " + arr.filter { it <= (p.div(10.0)) }.minByOrNull { (p.div(10.0) - it) })
      println("max " + arr.filter { it >= (p.div(10.0)) }.maxByOrNull { (p.div(10.0) - it) })

      var high = max?.minus((p.div(10.0)))
      var low = min?.minus((p.div(10.0)))

      if (abs(high!!) > abs(low!!))
      {
        t = min
      } else
      {
        t = max
      }
      println("high " + high)
      println("low " + low)
      println("t" + t)

      if (t == 79.6)
      {
        setStation("http://playerservices.streamtheworld.com/api/livestream-redirect/WFANAM.mp3")
        setFn("wfan")
      } else if (t == 95.0)
      {
        setStation("http://playerservices.streamtheworld.com/api/livestream-redirect/KABCAM.mp3")
        setFn("kabc")
      } else if (t == 102.3)
      {
        setStation("http://playerservices.streamtheworld.com/api/livestream-redirect/WBAPAM.mp3")
        setFn("wbap")
      } else if (t == 107.1)
      {
        setStation("http://playerservices.streamtheworld.com/api/livestream-redirect/WLSAM.mp3")
        setFn("wls")
      } else if (t == 117.5)
      {
        setStation("http://playerservices.streamtheworld.com/api/livestream-redirect/KARNAM.mp3")
        setFn("karnam")
      }

    }

  }

  private fun getProgress(seekBar: SeekBar?): Int
  {
    return seekBar!!.progress
  }

  private fun setFn(fn: String)
  {
    this.fn = fn
  }

  private fun getFn(): String
  {
    return fn
  }

  private fun setStation(station: String)
  {
    this.station = station
  }

  private fun getStation(): String
  {
    return station
  }

  override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)
  {
    progress.div(10)
    //println(progress.toDouble())
    println(progress.div(10.0))

    println(text)

  }


  override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean)
  {
    var text = buttonView?.getText().toString()

    println("Is checked" + buttonView?.isChecked())
    println(text)
    if (text == "AM")
    {
      buttonView?.setText("FM")
    } else
    {
      buttonView?.setText("AM")
    }
  }

  override fun onClick(dialog: DialogInterface?, which: Int)
  {
      if (which == DialogInterface.BUTTON_POSITIVE)
    {
      textView!!.setText("Not Connected")
    }

  }



}


class MainActivity : AppCompatActivity()
{

  companion object
  {
    private var webView: WebView? = null
    private var instance: MainActivity? = null

    public fun getInstance(): MainActivity
    {
      return instance!!
    }
  }

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    instance = this

    supportActionBar?.hide()


    var handler = Handler()

    var dialogBuilder = AlertDialog.Builder(this)
    dialogBuilder.setMessage("Not Connected To Internet")
    dialogBuilder.setPositiveButton("OK", handler)
    val alert1 = dialogBuilder.create()
    alert1.setTitle("Failure")
    if(isNetworkAvailable(this) == false)
    {
      alert1.show()
    }




 


    //println("pressedmm")



    var slider = findViewById<SeekBar>(R.id.seekBar)
    var switch = findViewById<CompoundButton>(R.id.AM_FM)
    var play = findViewById<Button>(R.id.Playbutton)





    slider.setOnSeekBarChangeListener(handler)
    switch.setOnClickListener(handler)
    switch.setOnCheckedChangeListener(handler)
    play.setOnClickListener(handler)


  }
  override fun onBackPressed()
  {

    //if (webView!!.canGoBack())

    //webView?.goBack()
     webView = findViewById<WebView>(R.id.webView)
    webView?.setVisibility(View.INVISIBLE)
    println("back")


  }



}

