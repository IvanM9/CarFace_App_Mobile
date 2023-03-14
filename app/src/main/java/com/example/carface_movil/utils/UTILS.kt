package com.example.carface_movil.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.carface_movil.Constants
import com.example.carface_movil.R
import com.example.carface_movil.servicios.MyService
import com.example.carface_movil.session.MainActivity

object UTILS {

    fun obtieneToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", "")
    }

    fun obtieneRol(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("rol", "")
    }

    fun detieneServicio(context: Context){
        val intent1 = Intent(context, MyService::class.java)
        context.stopService(intent1)
    }

    fun saveTokenAndRoleToPreferences(context: Context, token: String, role: String) {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.putString("rol", role)
        editor.apply()
    }

    fun muestraMensaje(context: Context, mensaje: String) {
        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
    }

    fun vuelveLogin(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent)
    }

    fun limpiaMemoria(context: Context) {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("token", "")
        editor.putString("rol", "")
        editor.apply()
    }

    fun applyBlur(view: View, layout: ConstraintLayout, radius: Float) {
        val bitmap = Bitmap.createBitmap(Constants.width, Constants.heigth, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        layout.draw(canvas)
        val rs: RenderScript = RenderScript.create(view.context)
        val blur: ScriptIntrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        val input: Allocation = Allocation.createFromBitmap(rs, bitmap)
        val output: Allocation = Allocation.createTyped(rs, input.type)
        blur.setRadius(radius)
        blur.setInput(input)
        blur.forEach(output)
        output.copyTo(bitmap)
        view.background = BitmapDrawable(view.resources, bitmap)
        rs.destroy()
    }

}