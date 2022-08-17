package com.lucamesquitaa.controledegastoskotlin


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lucamesquitaa.controledegastoskotlin.model.Item
import java.util.*


open class MainActivity : AppCompatActivity(), OnListClickListener {
    private lateinit var rvMain: RecyclerView
    private var mainItems = mutableListOf<Item>()
    private var count: Int = 0
    private lateinit var adapter: MainAdapter
    private lateinit var textMoney: TextView
    private lateinit var textGood: TextView
    private lateinit var webView: WebView
    private var entries = mutableListOf<Double>()
    private var strTypes = mutableListOf<String>()
    private var isDark = false
    private lateinit var totalGasto: TextView
    private lateinit var totalLucro: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        textMoney = findViewById(R.id.txt_total)
        textGood = findViewById(R.id.text_good)
        rvMain = findViewById(R.id.rv_main)

        totalGasto = findViewById(R.id.txt_gastos)
        totalLucro = findViewById(R.id.txt_lucros)


        val btnAdd: ImageView = findViewById(R.id.btn_add)
        btnAdd.setOnClickListener {
            val intent = Intent(this@MainActivity, AddActivity::class.java)
            startActivityForResult(intent, 1)
        }


        Thread {
            val app = application as App
            val dao = app.db.ItemDao()

            fun goodGreeting(): String {
                val h = Date().hours
                return when (h) {
                    in 1..5 -> "Boa madrugada"
                    in 6..12 -> "Bom dia"
                    in 13..18 -> "Boa tarde"
                    else -> "Boa noite"
                }
            }
            var response = dao.getAllItems()
            if (response.isNotEmpty()) {
                mainItems.clear()
                mainItems.addAll(response)
                runOnUiThread {
                    textGood.text = goodGreeting()
                    setTotalMoney(response)
                    adapter = MainAdapter(mainItems, this@MainActivity)
                    rvMain.adapter = adapter
                    rvMain.layoutManager = LinearLayoutManager(this)
                }
            }else{
                textMoney.text = ""
            }
        }.start()

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            this.count = count++
            val title = data!!.getStringExtra("title").toString()
            val money = data.getDoubleExtra("money", 0.0)
            val type = data.getStringExtra("type").toString()



            Thread {
                val app = application as App
                val dao = app.db.ItemDao()
                dao.insert(
                    Item(
                        title = title,
                        type = type,
                        money = money
                    )
                )
                var response = dao.getAllItems()

                if (response.isNotEmpty()) {
                    mainItems.clear()
                    mainItems.addAll(response)
                    runOnUiThread {
                        setTotalMoney(response)
                        adapter = MainAdapter(mainItems, this@MainActivity)
                        rvMain.adapter = adapter
                        rvMain.layoutManager = LinearLayoutManager(this)
                        adapter.notifyDataSetChanged()
                    }
                }else{
                    textMoney.text = ""
                }
            }.start()


        }

    }

    private fun setTotalMoney(response: List<Item>) {
        if (response.isNotEmpty()) {
            var total: Double = 0.0
            var moneyDouble: Double
            var itemMoney: Double
            var totalGastos: Double = 0.0
            var totalRecebido: Double = 0.0
            for (item in response) {
                moneyDouble = item.money
                if(!item.type.isNullOrEmpty()){
                    itemMoney = "$moneyDouble".toDouble()
                    totalGastos += itemMoney
                }else{
                    itemMoney = item.money
                    totalRecebido += itemMoney
                }
                total += itemMoney
            }
            val totalString = String.format("%.2f", total)
            textMoney.text = "R$ $totalString"
            val totalLucroFormatted =  String.format("%.2f", totalRecebido)
            totalLucro.text = "R$ $totalLucroFormatted"
            val totalGastosFormatted = String.format("%.2f", totalGastos)
            totalGasto.text = "R$ $totalGastosFormatted"
            if(total < 0){
                textMoney.setTextColor(Color.RED);
            }else{
                textMoney.setTextColor(Color.GREEN);
            }
        }
    }


    private inner class MainAdapter(
        private val mainItems: List<Item>,
        private val listener: OnListClickListener
    ) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {
        //especifica qual o layout da tela
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
            val view = layoutInflater.inflate(R.layout.card_layout, parent, false)
            return MainViewHolder(view)
        }

        //dispara sempre que houver uma rolagem na tela e for trocar o conteudo
        override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
            val itemCurrent = mainItems[position]
            holder.bind(itemCurrent)
        }

        //quantas celulas a lista tera
        override fun getItemCount(): Int {
            return mainItems.size
        }


        private inner class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            @SuppressLint("ResourceAsColor")
            fun bind(item: Item) {
                val img: ImageView = itemView.findViewById(R.id.card_img)
                val title: TextView = itemView.findViewById(R.id.card_title)
                val money: TextView = itemView.findViewById(R.id.card_money)

                var icon: Int = 0
                when (item.type) {
                    "Contas" -> icon = R.drawable.ic_conta
                    "Moradia" -> icon = R.drawable.ic_house
                    "Transferência" -> icon = R.drawable.ic_transfer
                    "Alimentação" -> icon = R.drawable.ic_food
                    "Estudo" -> icon = R.drawable.ic_book
                    "Lazer" -> icon = R.drawable.ic_happy
                    "Compras" -> icon = R.drawable.ic_shop
                    else -> icon = R.drawable.ic_money
                }

                val total = item.money
                val totalString = String.format("%.2f", total)
                var moneyString : String
                if(!item.type.isNullOrEmpty()){
                    moneyString = "R$ $totalString"
                }else{
                    moneyString = "R$ $totalString"
                }
                money.text = moneyString
                img.setImageResource(icon)
                title.text = item.title


                val btnClose: ImageView = itemView.findViewById(R.id.btn_delete)
                btnClose.setOnLongClickListener {
                    listener.onLongClick(adapterPosition, item)
                    true
                }
            }
        }
    }

    override fun onLongClick(position: Int, item: Item) {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.delete_message))
            .setNegativeButton(android.R.string.cancel) { dialog, which ->
            }
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                Thread {
                    val app = application as App
                    val dao = app.db.ItemDao()

                    val responseDel = dao.delete(item)

                    if (responseDel > 0) {
                        var response = dao.getAllItems()
                        runOnUiThread {
                            mainItems.removeAt(position)
                            adapter.notifyItemRemoved(position)
                            if (response.isNotEmpty()) {
                                setTotalMoney(response)
                            }else{
                                textMoney.text = "R$ 00,00"
                                totalLucro.text = "R$ 00,00"
                                totalGasto.text = "R$ 00,00"
                            }
                        }


                    }
                }.start()
            }
            .create()
            .show()
    }



}

