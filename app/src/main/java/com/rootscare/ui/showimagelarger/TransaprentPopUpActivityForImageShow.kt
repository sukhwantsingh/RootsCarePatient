package com.rootscare.ui.showimagelarger

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.annotation.Nullable
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.ActivityTransparentForShowBinding
import com.rootscare.ui.base.BaseActivity
import com.rootscare.utils.asynctaskutil.GetInputStreamFromUrl
import java.io.InputStream
import java.util.*


class TransaprentPopUpActivityForImageShow : BaseActivity<ActivityTransparentForShowBinding, TransparentActivityForShowViewModel>(),
    TransparentActivityForShowNavigator {



    companion object {
        private val TAG = TransaprentPopUpActivityForImageShow::class.java.simpleName
        fun newIntent(activity: Activity, PassData: String, fileType: String=""): Intent {
            return Intent(activity, TransaprentPopUpActivityForImageShow::class.java).putExtra("PassData", PassData).putExtra("fileType", fileType)
        }

    }

    private var fileUrl: String? = null
    private var fileType: String? = null
    private var mScaleGestureDetector: ScaleGestureDetector? = null
    private var mScaleFactor = 1.0f

    private var activityDoctorListingBinding: ActivityTransparentForShowBinding? = null
    private var doctorListingViewModel: TransparentActivityForShowViewModel? = null


    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_transparent_for_show
    override val viewModel: TransparentActivityForShowViewModel
        get() {
            doctorListingViewModel = ViewModelProviders.of(this).get(TransparentActivityForShowViewModel::class.java)
            return doctorListingViewModel as TransparentActivityForShowViewModel
        }

    override fun reloadPageAfterConnectedToInternet() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doctorListingViewModel?.navigator = this
        activityDoctorListingBinding = viewDataBinding

        with(activityDoctorListingBinding!!) {
            imageViewCross.setOnClickListener {
                onBackPressed()
            }

            fileUrl = intent?.extras?.getString("PassData")
           // fileType = intent?.extras?.getString("fileType")
            if (fileUrl?.lowercase(Locale.ROOT)?.contains("pdf")!!) {
                fileType = "pdf"
            } else {
                fileType = "image"
            }

            if (fileUrl != null) {
                if (fileType?.trim() == "pdf") {
                    imageviewShow.visibility = View.GONE
                    pdfView.visibility = View.VISIBLE
//                    viewInWebView()
                    openPdfToPDFViewer()
//                    pdfView.fromUri(Uri.parse(fileUrl!!))
//                    pdfView.fromStream(AppData.responseBodyForPDF?.byteStream())
                } else {
//                    val circularProgressDrawable = CircularProgressDrawable(this@TransaprentPopUpActivityForImageShow)
//                    circularProgressDrawable.strokeWidth = 5f
//                    circularProgressDrawable.centerRadius = 30f
//                    circularProgressDrawable.setColorSchemeColors(*intArrayOf(R.color.colorAccent, R.color.colorAccent))
//
//                    circularProgressDrawable.start()
//                    val requestOptions = RequestOptions().fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL)
//                    Glide.with(this@TransaprentPopUpActivityForImageShow).load(fileUrl).
//                    apply(RequestOptions().placeholder(circularProgressDrawable)).timeout(1000*60).apply(requestOptions).into(imageviewShow)

                    activityDoctorListingBinding?.loader?.setVisibility(View.VISIBLE)

                    Glide.with(this@TransaprentPopUpActivityForImageShow)
                        .load(fileUrl)
                        .listener(object : RequestListener<Drawable?> {
                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                activityDoctorListingBinding?.loader?.setVisibility(View.GONE)
                                return false
                            }

                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable?>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                activityDoctorListingBinding?.loader?.setVisibility(View.GONE)
                            return false
                            }
                        })
                        .into(imageviewShow)
                    imageviewShow.visibility = View.VISIBLE
//                    webView.visibility = View.GONE
                    pdfView.visibility = View.GONE
                }
            }

            mScaleGestureDetector = ScaleGestureDetector(this@TransaprentPopUpActivityForImageShow, ScaleListener())
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        return super.onTouchEvent(event)
        mScaleGestureDetector?.onTouchEvent(event)
        return true
    }


    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
//            mScaleFactor *= scaleGestureDetector.scaleFactor
//            mScaleFactor = Math.max(0.1f,
//                    Math.min(mScaleFactor, 10.0f))
//            activityDoctorListingBinding?.imageviewShow?.scaleX = mScaleFactor
//            activityDoctorListingBinding?.imageviewShow?.scaleY = mScaleFactor
            return true
        }
    }

    /*@SuppressLint("SetJavaScriptEnabled")
    private fun viewInWebView() {
        with(activityDoctorListingBinding!!) {
            webView.webViewClient = WebViewClient()
//            webView.webViewClient = AppWebViewClients(this@TransaprentPopUpActivityForImageShow, loader)
            webView.settings.setSupportZoom(true)
            webView.settings.javaScriptEnabled = true
            webView.settings.builtInZoomControls = true
            webView.settings.loadWithOverviewMode = true
            webView.settings.useWideViewPort = true
            webView.settings.pluginState = WebSettings.PluginState.ON
//            val url = FileUtils.getPdfUrl()
//            val url = "http://docs.google.com/gview?embedded=true&url=$fileUrl"
            val url = "http://drive.google.com/viewerng/viewer?embedded=true&url=$fileUrl"
            webView.loadUrl(url)
            loader.visibility = View.VISIBLE
            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                    webView.loadUrl(url)
                    return true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    // TODO Auto-generated method stub
                    super.onPageFinished(view, url)
                    Handler().postDelayed({
                        runOnUiThread(Runnable {
                            loader.visibility = View.GONE
                        })
                    }, 5000)
                }
            }
        }
    }*/

    private fun openPdfToPDFViewer(){
        with(activityDoctorListingBinding!!){
            loader.visibility = View.VISIBLE
            GetInputStreamFromUrl(object : GetInputStreamFromUrl.CallBackAfterFetchingInputStream{
                override fun onCallback(result: InputStream?) {
                    Log.d(TAG, "streaammmm ${result}")
                    pdfView.fromStream(result)
                        .enableSwipe(true)
                        .enableDoubletap(true)
                        .onLoad(object : OnLoadCompleteListener {
                            override fun loadComplete(nbPages: Int) {
                                loader.visibility = View.GONE
                            }
                        })
                        .enableAntialiasing(true)
                        .invalidPageColor(Color.WHITE)
                        .load();
                }
            }).execute(fileUrl)
        }
    }
}