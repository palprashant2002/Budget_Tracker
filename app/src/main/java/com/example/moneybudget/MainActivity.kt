package com.example.moneybudget

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.moneybudget.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var deletedTrans:Transaction
    private lateinit var transactions:List<Transaction>
    private lateinit var oldtransactions:List<Transaction>
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var linearlayoutManager:LinearLayoutManager
    private lateinit var db:AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        transactions= arrayListOf()

        transactionAdapter= TransactionAdapter(transactions)
        linearlayoutManager= LinearLayoutManager(this)

        db= Room.databaseBuilder(this,
        AppDatabase::class.java,
        "transactions").build()

        binding.recyclerView.apply {
            adapter=transactionAdapter
            layoutManager=linearlayoutManager
        }


        val itemTouchHelper=object:ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    deleteTransaction(transactions[viewHolder.adapterPosition])
            }
        }

        val swipehelper=ItemTouchHelper(itemTouchHelper)
        swipehelper.attachToRecyclerView(binding.recyclerView)

        binding.addBtn.setOnClickListener {
            val intent=Intent(this,AddTransactionActivity::class.java)
            startActivity(intent)
        }

    }



    private fun fetchtrans(){
        GlobalScope.launch {
            transactions=db.transactionDao().getAll()
            runOnUiThread{
                updateDashboard()
                transactionAdapter.setData(transactions)
            }
        }

    }

    private fun updateDashboard(){
        val totalAmt=transactions.map{it.amt}.sum()
        val budgetAmt=transactions.filter { it.amt>0}.map{it.amt}.sum()
        val expenseAmt=totalAmt-budgetAmt
        binding.balance.text="₹ %.2f".format(totalAmt)
        binding.budget.text="₹ %.2f".format(budgetAmt)
        binding.expense.text="₹ %.2f".format(expenseAmt)
    }

    private fun undoDelete(){
        GlobalScope.launch {
            db.transactionDao().insertAll(deletedTrans)
            transactions=oldtransactions
            runOnUiThread{
                transactionAdapter.setData(transactions)
                updateDashboard()
            }
        }
    }

    private fun showSnackbar(){
        val view=findViewById<View>(R.id.coordinator)
        val snackbar=Snackbar.make(view,"Transaction deleted!",Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo"){
            undoDelete()
        }.setActionTextColor(ContextCompat.getColor(this,R.color.red))
            .setTextColor(ContextCompat.getColor(this,R.color.white))
            .show()
    }

    private fun deleteTransaction(transaction: Transaction){
        deletedTrans=transaction
        oldtransactions=transactions
        GlobalScope.launch {
            db.transactionDao().delete(transaction)
            transactions=transactions.filter { it.id!=transaction.id }
            runOnUiThread{
                updateDashboard()
                transactionAdapter.setData(transactions)
                showSnackbar()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchtrans()
    }
}