import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.carface_movil.*
import com.google.gson.Gson
import dev.icerock.moko.socket.SocketEvent
import dev.icerock.moko.socket.SocketOptions
import org.json.JSONObject
import java.util.concurrent.locks.ReentrantLock

class WebSocketManager:Runnable {
    companion object {


        var context: Context? = null
        val lock = ReentrantLock()
        private lateinit var sharedPreferences: SharedPreferences
        lateinit var notificationManager:NotificationManager
        var webSocket:dev.icerock.moko.socket.Socket= dev.icerock.moko.socket.Socket(
            endpoint = Constants.SERVER_SOCKET,
            config = SocketOptions(
                queryParams = mapOf("room" to "a"),
                transport = SocketOptions.Transport.WEBSOCKET
            )
        ) {
            on(SocketEvent.Connect) {
                println("connect")
            }

            on(SocketEvent.Connecting) {
                println("connecting")
            }

            on(SocketEvent.Disconnect) {
                println("disconnect")
            }

            on(SocketEvent.Error) {
                println("error $it")
            }

            on("get_message") { args ->
                sharedPreferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)!!
                var rol = sharedPreferences.getString("rol", "NO")
                if (rol=="GUARDIA"){
                    val jsonObj = JSONObject(args)
                    val message = jsonObj.getString("message");
                    val gson = Gson()
                    val persona = gson.fromJson(message, Persona::class.java)
                    var repite=false
                    System.out.println(args);
                    lock.lock() // Bloquea el objeto Lock
                    try {
                        for (i in WebSocketData.data.indices) {
                            val person: Persona = WebSocketData.data[i]
                            if(persona.ci==person.ci){
                                repite=true;
                                break;
                            }
                        }
                        if (!repite){
                            WebSocketData.data.add(persona);
                            val gson = Gson()
                            val json = gson.toJson(WebSocketData.data)
                            with(sharedPreferences.edit()) {
                                putString("data", json)
                                apply()
                            }
                            notification(
                                context,
                                persona
                            );
                            recarga(context)
                        }
                    }finally {
                        lock.unlock() // Desbloquea el objeto Lock
                    }
                }
            }
        };

        private fun notification(context: Context?, persona: Persona) {
            val powerManager = context?.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "myapp:notification_wakelock"
            )
            wakeLock.acquire(5000) // Enciende la pantalla durante 5 segundos
            notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId = "my_channel_id"
            val channelName = "My Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)

            //AGREGA INTENT CON INFORMACION AL PRESIONAR LA NOTIFICACION
            val intent = Intent(context, EscanearVehiculo::class.java)
            intent.putExtra("persona", persona)
            intent.putExtra("evento","SALIDA")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)

            val largeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.signage)
            val builder = NotificationCompat.Builder(context, channelId)
                .setColor(Color.GREEN)
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.drawable.baseline_directions_car_24)
                .setContentTitle("Un vehículo acaba de ingresar.")
                .setContentText("Conductor: "+persona.nombres+" "+persona.apellidos)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
            val pattern = longArrayOf(0, 1000, 500, 1000)
            builder.setVibrate(pattern)
            notificationManager.notify(persona.id_chofer.toInt(), builder.build())
        }

        private fun recarga(context: Context?) {
            if (context != null) {
                if(context.javaClass.name=="Solicitud"){
                    val intent = Intent(context, Solicitud::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    context?.startActivity(intent)
                }
            }
        }
    }

    override fun run() {
        webSocket = dev.icerock.moko.socket.Socket(
            endpoint = "http://192.168.0.100:8081",
            config = SocketOptions(
                queryParams = mapOf("room" to "a"),
                transport = SocketOptions.Transport.WEBSOCKET
            )
        ) {
            on(SocketEvent.Connect) {
                println("connect")
            }

            on(SocketEvent.Connecting) {
                println("connecting")
            }

            on(SocketEvent.Disconnect) {
                println("disconnect")
            }

            on(SocketEvent.Error) {
                println("error $it")
            }

            on("get_message") { args ->
                val jsonObj = JSONObject(args)
                val message = jsonObj.getString("message");
                val gson = Gson()
                val persona = gson.fromJson(message, Persona::class.java)
                var repite=false
                System.out.println(args);
                lock.lock() // Bloquea el objeto Lock
                try {
                    for (i in WebSocketData.data.indices) {
                        val person: Persona = WebSocketData.data[i]
                        if(persona.ci==person.ci){
                            repite=true;
                            break;
                        }
                    }
                    if (!repite){
                        WebSocketData.data.add(persona);
                        notification(
                            context,
                            persona
                        );
                        recarga(context)
                    }
                }finally {
                    lock.unlock() // Desbloquea el objeto Lock
                }
            }
        }
    }
}