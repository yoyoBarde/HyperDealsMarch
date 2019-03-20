package com.example.kent.hyperdeals.Model

import android.os.Parcel
import android.os.Parcelable
import com.example.kent.hyperdeals.Model.CategoryParse
import com.example.kent.hyperdeals.Model.SubcategoryParse

class DataModel {
}


 interface myInterfaces{
    fun saveCategoriesBusiness( myCategoryList:ArrayList<CategoryParse>)
}


interface myInterfacesCategories{
    fun addCategoriesUser(myCategoryList:ArrayList<CategoryParse>)

}
interface  myInterfacesAddItem{

    fun addPromoItemBusiness(promoItemListParcelable: ArrayList<promoItemParcelable>)
}



data class promoSubcategory(var subcategoryName:String)


data class promoItem(var itemName:String,
                      var itemOldPirce:Int,
                      var itemNewPrice:Int,
                      var itemImageLink:String
)
class promoItemParcelable():Parcelable{
    var itemName = ""
    var itemOldPirce = 0
    var itemNewPrice = 0
    var itemImageLink = ""

    constructor(parcel: Parcel) : this() {
        itemName = parcel.readString()
        itemOldPirce = parcel.readInt()
        itemNewPrice = parcel.readInt()
        itemImageLink = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(itemName)
        parcel.writeInt(itemOldPirce)
        parcel.writeInt(itemNewPrice)
        parcel.writeString(itemImageLink)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<promoItemParcelable> {
        override fun createFromParcel(parcel: Parcel): promoItemParcelable {
            return promoItemParcelable(parcel)
        }

        override fun newArray(size: Int): Array<promoItemParcelable?> {
            return arrayOfNulls(size)
        }
    }

}

data class userPromoiked(var storeName:String)
class userPromoLikedParce() :Parcelable{
    var storeName = ""

    constructor(parcel: Parcel) : this() {
        storeName = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(storeName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<userPromoLikedParce> {
        override fun createFromParcel(parcel: Parcel): userPromoLikedParce {
            return userPromoLikedParce(parcel)
        }

        override fun newArray(size: Int): Array<userPromoLikedParce?> {
            return arrayOfNulls(size)
        }
    }
}

data class userLike(var userName:String,var liked:Boolean)

 class userLikeParce() :Parcelable

 {
     var userName=" "
     var liked= false

     constructor(parcel: Parcel) : this() {
         userName = parcel.readString()
         liked = parcel.readByte() != 0.toByte()
     }

     override fun writeToParcel(parcel: Parcel, flags: Int) {
         parcel.writeString(userName)
         parcel.writeByte(if (liked) 1 else 0)
     }

     override fun describeContents(): Int {
         return 0
     }

     companion object CREATOR : Parcelable.Creator<userLikeParce> {
         override fun createFromParcel(parcel: Parcel): userLikeParce {
             return userLikeParce(parcel)
         }

         override fun newArray(size: Int): Array<userLikeParce?> {
             return arrayOfNulls(size)
         }
     }
 }




data class promoViews(var promoViews:Int)
class promoViewsParde() :Parcelable{

    var promoViews = 0

    constructor(parcel: Parcel) : this() {
        promoViews = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(promoViews)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<promoViewsParde> {
        override fun createFromParcel(parcel: Parcel): promoViewsParde {
            return promoViewsParde(parcel)
        }

        override fun newArray(size: Int): Array<promoViewsParde?> {
            return arrayOfNulls(size)
        }
    }
}
data class userPromoViews(var date:String)
class userPromoViewsParce():Parcelable{

    var date = " "

    constructor(parcel: Parcel) : this() {
        date = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<userPromoViewsParce> {
        override fun createFromParcel(parcel: Parcel): userPromoViewsParce {
            return userPromoViewsParce(parcel)
        }

        override fun newArray(size: Int): Array<userPromoViewsParce?> {
            return arrayOfNulls(size)
        }
    }
}


