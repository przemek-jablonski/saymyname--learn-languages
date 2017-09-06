package com.android.szparag.saymyname.views.widgets.overlays

import android.content.Context
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.android.szparag.saymyname.R
import com.android.szparag.saymyname.utils.bindView
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 06/09/2017.
 */

class BottomSheetSinglePhotoDetails @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

  private val textViewOriginal1: TextView by bindView(R.id.textview_original_1)
  private val textViewOriginal2: TextView by bindView(R.id.textview_original_2)
  private val textViewOriginal3: TextView by bindView(R.id.textview_original_3)
  private val textViewTranslated1: TextView by bindView(R.id.textview_translated_1)
  private val textViewTranslated2: TextView by bindView(R.id.textview_translated_2)
  private val textViewTranslated3: TextView by bindView(R.id.textview_translated_3)
  private val textViewDatetime: TextView by bindView(R.id.textview_datetime)
  private val imageView: ImageView by bindView(R.id.imageview_camera_image)

  init {

  }

  fun setPhotoDetails(imageBytes: ByteArray, textsOriginal: List<String>,
      textsTranslated: List<String>, dateTime: Long) {
    imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size))
    textViewOriginal1.text = textsOriginal[0]
    textViewOriginal2.text = textsOriginal[1]
    textViewOriginal3.text = textsOriginal[2]
    textViewTranslated1.text = textsTranslated[0]
    textViewTranslated2.text = textsTranslated[1]
    textViewTranslated3.text = textsTranslated[2]
    textViewDatetime.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").format(Date(dateTime))
  }

}