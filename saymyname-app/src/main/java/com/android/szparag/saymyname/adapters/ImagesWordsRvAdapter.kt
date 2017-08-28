package com.android.szparag.saymyname.adapters

import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.android.szparag.saymyname.adapters.ImagesWordsRvAdapter.ImagesWordsViewHolder
import com.android.szparag.saymyname.repositories.entities.Image
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.widget.ImageView
import com.android.szparag.saymyname.R
import android.widget.TextView
import com.android.szparag.saymyname.utils.bindView
import java.text.SimpleDateFormat
import java.util.Date


/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 28/08/2017.
 */
class ImagesWordsRvAdapter : RecyclerView.Adapter<ImagesWordsViewHolder>() {

  private var imagesWordsList: List<Image> = emptyList()
  private lateinit var imagesWordsViewHolder : ImagesWordsViewHolder

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesWordsViewHolder {
    val contactView = LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerview_images_words, parent, false)
    imagesWordsViewHolder = ImagesWordsViewHolder(contactView)
    return imagesWordsViewHolder
  }

  fun setImages(list: List<Image>) {
    imagesWordsList = list
  }

  override fun getItemCount(): Int = imagesWordsList.size

  override fun onBindViewHolder(holder: ImagesWordsViewHolder, position: Int) {
    val item = imagesWordsList[position]
    item.imageBase64?.let {
      imagesWordsViewHolder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(item.imageBase64, 0, item.imageBase64!!.size))
      imagesWordsViewHolder.textViewOriginal1.text = item.words[0].original
      imagesWordsViewHolder.textViewOriginal2.text = item.words[1].original
      imagesWordsViewHolder.textViewOriginal3.text = item.words[2].original
      imagesWordsViewHolder.textViewTranslated1.text = item.words[0].translated
      imagesWordsViewHolder.textViewTranslated2.text = item.words[1].translated
      imagesWordsViewHolder.textViewTranslated3.text = item.words[2].translated
      imagesWordsViewHolder.textViewDatetime.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").format(Date(item.dateTime) )
    }
  }

  class ImagesWordsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textViewOriginal1: TextView by bindView(R.id.textview_original_1)
    val textViewOriginal2: TextView by bindView(R.id.textview_original_2)
    val textViewOriginal3: TextView by bindView(R.id.textview_original_3)
    val textViewTranslated1: TextView by bindView(R.id.textview_translated_1)
    val textViewTranslated2: TextView by bindView(R.id.textview_translated_2)
    val textViewTranslated3: TextView by bindView(R.id.textview_translated_3)
    val textViewDatetime: TextView by bindView(R.id.textview_datetime)
    val imageView: ImageView by bindView(R.id.imageview_camera_image)
  }

}
