package com.example.aban

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
class PopupUtils : AppCompatActivity() {
    object PopupUtils{
        fun showUnavailablePageDialog(context: Context) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("تنبيه !")
                .setMessage("عذراَ هذه الصفحة غير متوفرة حالياَ")
                .setPositiveButton("موافق") { dialog, _ ->
                    dialog.dismiss()
                }
            val dialog = builder.create()
            dialog.show()
        }
    }
}