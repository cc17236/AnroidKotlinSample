package androidx.bug

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable


/**
 * @作者: #Administrator #
 *@日期: #2018/9/6 #
 *@时间: #2018年09月06日 10:08 #
 *@File:Kotlin Class
 */
class EmptyParcelable() : Parcelable, Serializable {
    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EmptyParcelable> {
        override fun createFromParcel(parcel: Parcel): EmptyParcelable {
            return EmptyParcelable(parcel)
        }

        override fun newArray(size: Int): Array<EmptyParcelable?> {
            return arrayOfNulls(size)
        }
    }
}