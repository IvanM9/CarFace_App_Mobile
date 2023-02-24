import android.os.Parcel
import android.os.Parcelable

data class Persona(
    val nombres: String,
    val apellidos: String,
    val ci: String,
    val tipo_licencia: String,
    val vehiculos: List<Vehiculo>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Vehiculo.CREATOR)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nombres)
        parcel.writeString(apellidos)
        parcel.writeString(ci)
        parcel.writeString(tipo_licencia)
        parcel.writeTypedList(vehiculos)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Persona> {
        override fun createFromParcel(parcel: Parcel): Persona {
            return Persona(parcel)
        }

        override fun newArray(size: Int): Array<Persona?> {
            return arrayOfNulls(size)
        }
    }
}

data class Vehiculo(
    val marca: String,
    val color: String,
    val modelo: String,
    val placa: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(marca)
        parcel.writeString(color)
        parcel.writeString(modelo)
        parcel.writeString(placa)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Vehiculo> {
        override fun createFromParcel(parcel: Parcel): Vehiculo {
            return Vehiculo(parcel)
        }

        override fun newArray(size: Int): Array<Vehiculo?> {
            return arrayOfNulls(size)
        }
    }
}
