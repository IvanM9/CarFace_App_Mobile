import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.carface_movil.*
import com.google.gson.Gson
import dev.icerock.moko.socket.SocketEvent
import dev.icerock.moko.socket.SocketOptions
import org.json.JSONObject
import java.util.concurrent.locks.ReentrantLock

class WebSocketManager {
    companion object {

        var context: Context? = null
        val lock = ReentrantLock()
        var notificationId = 0


        var  webSocket = dev.icerock.moko.socket.Socket(
                    endpoint = "http://192.168.0.107:8081",
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
                        lock.lock() // Bloquea el objeto Lock
                        try {
                            /*for (i in WebSocketData.data.indices) {
                                val person: Persona = WebSocketData.data[i]
                                if(persona.ci==person.ci){
                                    repite=true;
                                    break;
                                }
                            }*/
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

        private fun notification(context: Context?, persona: Persona) {
            val powerManager = context?.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "myapp:notification_wakelock"
            )
            wakeLock.acquire(5000) // Enciende la pantalla durante 5 segundos
            val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId = "my_channel_id"
            val channelName = "My Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)

            //AGREGA INTENT CON INFORMACION AL PRESIONAR LA NOTIFICACION
            val intent = Intent(context, EscanearVehiculo::class.java)
            intent.putExtra("persona", persona)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Un vehículo acaba de ingresar.")
                .setContentText("Conductor: "+persona.nombres+" "+persona.apellidos)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
            notificationId++;
            val pattern = longArrayOf(0, 1000, 500, 1000)
            builder.setVibrate(pattern)
            notificationManager.notify(notificationId, builder.build())
        }

        private fun recarga(context: Context?) {
            if(context?.javaClass?.simpleName=="Solicitud"){
                val intent = Intent(context, Solicitud::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                context?.startActivity(intent)
            }
        }
    }
}