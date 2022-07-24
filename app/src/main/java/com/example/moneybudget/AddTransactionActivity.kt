package com.example.moneybudget

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.example.moneybudget.databinding.ActivityAddTransactionBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAddTransactionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityAddTransactionBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.labelinput.addTextChangedListener {
            if(it!!.count()>0)
                binding.labellayout.error=null
        }
        binding.amtinput.addTextChangedListener {
            if(it!!.count()>0)
                binding.amtlayout.error=null
        }

        binding.addtransactionBtn.setOnClickListener {
            val label=binding.labelinput.text.toString()
            val amt=binding.amtinput.text.toString().toDoubleOrNull()
            val desc=binding.descinput.text.toString()
            if(label.isEmpty())
                binding.labellayout.error="Please enter a valid label"
            else if(amt==null)
                binding.amtlayout.error="Please enter a valid amount"
            else{
                val transaction=Transaction(0,label,amt,desc)
                insert(transaction)
            }
        }
        binding.closeBtn.setOnClickListener {
            finish()
        }
    }

    private fun insert(transaction: Transaction){
        val db= Room.databaseBuilder(this,
            AppDatabase::class.java,
            "transactions").build()

        GlobalScope.launch {
            db.transactionDao().insertAll(transaction)
            finish()
        }
    }

}