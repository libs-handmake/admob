package common.hoangdz.admob

import android.content.Context
import com.google.android.gms.ads.MobileAds

class AdmobLibs {
   companion object{
       fun initialize(context: Context) {
           MobileAds.initialize(context) {}
       }
   }
}