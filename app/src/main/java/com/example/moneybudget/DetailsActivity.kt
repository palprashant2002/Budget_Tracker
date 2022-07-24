package com.example.moneybudget

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.example.moneybudget.databinding.ActivityDetailsBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailsActivity : AppCompatActivity() {
    private lateinit var transaction: Transaction
    private lateinit var binding : ActivityDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transaction=intent.getSerializableExtra("transaction") as Transaction
        binding.labelinput.setText(transaction.label)
        binding.amtinput.setText(transaction.amt.toString())
        binding.descinput.setText(transaction.description)

        binding.rootdetail.setOnClickListener{
            this.window.decorView.clearFocus()
            val im=getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(it.windowToken,0)
        }


        binding.labelinput.addTextChangedListener {
            binding.UpdateBtn.visibility=View.VISIBLE
            if(it!!.count()>0)
                binding.labellayout.error=null
        }
        binding.amtinput.addTextChangedListener {
            binding.UpdateBtn.visibility=View.VISIBLE
            if(it!!.count()>0)
                binding.amtlayout.error=null
        }
        binding.descinput.addTextChangedListener {
            binding.UpdateBtn.visibility=View.VISIBLE
        }
        binding.UpdateBtn.setOnClickListener {
            val label=binding.labelinput.text.toString()
            val amt=binding.amtinput.text.toString().toDoubleOrNull()
            val desc=binding.descinput.text.toString()
            if(label.isEmpty())
                binding.labellayout.error="Please enter a valid label"
            else if(amt==null)
                binding.amtlayout.error="Please enter a valid amount"
            else{
                val transaction=Transaction(transaction.id,label,amt,desc)
                update(transaction)
            }
        }
        binding.closeBtn.setOnClickListener {
            finish()
        }
    }

    private fun update(transaction: Transaction){
        val db= Room.databaseBuilder(this,
            AppDatabase::class.java,
            "transactions").build()

        GlobalScope.launch {
            db.transactionDao().update(transaction)
            finish()
        }
    }
}