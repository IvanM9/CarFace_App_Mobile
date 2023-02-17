package com.example.carface_movil
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import dev.icerock.moko.socket.Socket
import dev.icerock.moko.socket.SocketEvent
import dev.icerock.moko.socket.SocketOptions


class WebSockets(ip:String) {



    val socket = Socket(
        endpoint = "ws://$ip:8081",
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

        on(SocketEvent.Reconnect) {
            println("reconnect")
        }

        on(SocketEvent.ReconnectAttempt) {
            println("reconnect attempt $it")
        }

        on(SocketEvent.Ping) {
            println("ping")
        }

        on(SocketEvent.Pong) {
            println("pong")
        }

    }
}