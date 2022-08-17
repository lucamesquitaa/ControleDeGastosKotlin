package com.lucamesquitaa.controledegastoskotlin

import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService


class AddActivity : AppCompatActivity() {

    private lateinit var editTitle : EditText
    private lateinit var editMoney : EditText
    private lateinit var radioIn : RadioButton
    private lateinit var radioOut : RadioButton
    private lateinit var radioInOut : RadioGroup
    private lateinit var btnSendAdd: Button
    private lateinit var spinnerOut: Spinner
    private var type: String = ""

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_add)

        editTitle = findViewById(R.id.edit_title)
        editMoney = findViewById(R.id.edit_money)
        radioInOut = findViewById(R.id.radio_in_out)
        btnSendAdd = findViewById(R.id.btn_send_add)
        radioIn = findViewById(R.id.in_)
        radioOut = findViewById(R.id.out_)
        spinnerOut = findViewById(R.id.spinner_out)

        radioInOut.setOnCheckedChangeListener { radioGroup, i ->
            if(radioIn.isChecked) {
                spinnerOut.visibility = View.GONE
            }
            else if(radioOut.isChecked) {
                spinnerOut.visibility = View.VISIBLE
                ArrayAdapter.createFromResource(
                    this@AddActivity,
                    R.array.type,
                    android.R.layout.simple_spinner_item
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerOut.adapter = adapter
                }
            }

        }

        btnSendAdd.setOnClickListener {
            if(radioOut.isChecked)
            type = spinnerOut.selectedItem.toString()

           if(!validate()){
               Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_LONG).show()
                return@setOnClickListener
           }

            val titleString = editTitle.text.toString()
            val money = editMoney.text.toString().toDouble()
            val moneyFormatted: String = String.format("%.2f", money)
            var moneyDouble: Double
            if(radioOut.isChecked) moneyDouble = "-$moneyFormatted".replace(",", ".").toDouble()
            else moneyDouble = "$moneyFormatted".replace(",", ".").toDouble()


            val intent = Intent();
            intent.putExtra("title", titleString)
            intent.putExtra("money", moneyDouble)
            intent.putExtra("type", type)
            setResult(RESULT_OK,intent)

            finish()
        }

    }


    private fun validate(): Boolean {
        val array = resources.getStringArray(R.array.type)
        if (editTitle.text.toString().isNotEmpty()
                && editMoney.text.toString().isNotEmpty()
                && !editMoney.text.toString().startsWith("0")
                && radioInOut.checkedRadioButtonId.toString().isNotEmpty()){
            if(radioIn.isChecked){
                return true
            }else if(type.isNullOrEmpty()){
                return false
            }else if(radioOut.isChecked && type == array[0]) {
                return false
            }
            return true
        }
        else {
            return false
        }
    }
}

