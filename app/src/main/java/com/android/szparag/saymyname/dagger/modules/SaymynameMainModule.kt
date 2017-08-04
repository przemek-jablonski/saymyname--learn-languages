package com.android.szparag.saymyname.dagger.modules

import android.content.Context
import com.android.szparag.saymyname.R
import com.android.szparag.saymyname.models.SaymynameImageRecognitionModel
import com.android.szparag.saymyname.models.ImageRecognitionModel
import com.android.szparag.saymyname.models.SaymynameTranslationModel
import com.android.szparag.saymyname.models.TranslationModel
import com.android.szparag.saymyname.presenters.RealtimeCameraPreviewPresenter
import com.android.szparag.saymyname.presenters.RealtimeCameraPresenter
import com.android.szparag.saymyname.retrofit.services.SaymynameImageRecognitionNetworkService
import com.android.szparag.saymyname.retrofit.services.SaymynameTranslationNetworkService
import com.android.szparag.saymyname.retrofit.services.contracts.ImageRecognitionNetworkService
import com.android.szparag.saymyname.retrofit.services.contracts.TranslationNetworkService
import com.facebook.stetho.okhttp3.StethoInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/5/2017.
 */

@Module class SaymynameMainModule(private val context: Context) {

  @Named("ImageRecognition.NetworkService.BaseUrl")
  val IMAGE_RECOGNITION_NETWORK_SERVICE_BASEURL = "https://api.clarifai.com/v2/"
  @Named("Translation.NetworkService.BaseUrl")
  val TRANSLATION_NETWORK_SERVICE_BASEURL = "https://translate.yandex.net/api/v1.5/"


  @Provides @Singleton fun provideContext(): Context {
    return context
  }

  @Provides @Singleton fun provideRealtimeCameraPresenter(
      imageRecognitionModel: ImageRecognitionModel, translationModel : TranslationModel): RealtimeCameraPresenter {
    return RealtimeCameraPreviewPresenter(imageRecognitionModel, translationModel)
  }

  @Provides @Singleton fun provideImageRecognitionNetworkService(
      @Named("ImageRecognition.NetworkService.BaseUrl") baseUrl: String,
      @Named(
          "ImageRecognition.NetworkService.ApiKey") apiKey: String): ImageRecognitionNetworkService {
    return SaymynameImageRecognitionNetworkService(provideNetworkServiceRestClient(baseUrl), apiKey)
  }

  @Provides @Singleton fun provideTranslationNetworkService(
      @Named("Translation.NetworkService.BaseUrl") baseUrl: String,
      @Named("Translation.NetworkService.ApiKey") apiKey: String): TranslationNetworkService {
    return SaymynameTranslationNetworkService(provideNetworkServiceRestClient(baseUrl), apiKey)
  }

  @Provides @Singleton fun provideImageRecognitionModel(
      service: ImageRecognitionNetworkService): ImageRecognitionModel {
    return SaymynameImageRecognitionModel(service)
  }

  @Provides @Singleton fun provideTranslationModel(
      service: TranslationNetworkService): TranslationModel {
    return SaymynameTranslationModel(service)
  }

  @Provides @Singleton @Named(
      "Translation.NetworkService.ApiKey") fun provideTranslationNetworkServiceApiKey(
      context: Context): String {
    return context.getString(R.string.yandex_api_key)
  }

  @Provides @Singleton @Named(
      "ImageRecognition.NetworkService.ApiKey") fun provideImageRecognitionNetworkServiceApiKey(
      context: Context): String {
    return context.getString(R.string.clarifai_api_key)
  }

  //todo: unify naming - Translation not Translate, ImageRecognition not ImageProcessing etc
  @Provides @Singleton @Named("ImageRecognition.NetworkService.BaseUrl") fun provideImageRecognitionNetworkServiceBaseUrl() :String {
    return IMAGE_RECOGNITION_NETWORK_SERVICE_BASEURL
  }

  @Provides @Singleton @Named("Translation.NetworkService.BaseUrl") fun provideTranslationNetworkServiceBaseUrl() :String {
    return TRANSLATION_NETWORK_SERVICE_BASEURL
  }

  @Provides @Singleton fun provideNetworkServiceRestClient(
      networkServiceBaseString: String): Retrofit {
    return Retrofit.Builder()
        .baseUrl(networkServiceBaseString)
        .client(OkHttpClient.Builder().addNetworkInterceptor(StethoInterceptor()).build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
  }


}