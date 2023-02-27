import android.os.Parcel
import android.os.Parcelable

data class Persona(
    val nombres: String,
    val apellidos: String,
    val ci: String,
    val tipo_licencia: String,
    val vehiculos: List<Vehiculo>,
    val id_chofer:Long
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Vehiculo.CREATOR)!!,
        parcel.readLong()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nombres)
        parcel.writeString(apellidos)
        parcel.writeString(ci)
        parcel.writeString(tipo_licencia)
        parcel.writeTypedList(vehiculos)
        parcel.writeLong(id_chofer)
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
    val placa: String,
    val id_vehiculo: Long

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(marca)
        parcel.writeString(color)
        parcel.writeString(modelo)
        parcel.writeString(placa)
        parcel.writeLong(id_vehiculo)
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
