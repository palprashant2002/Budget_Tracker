package com.example.moneybudget

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName="transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id:Int,
    val label:String,
    val amt:Double,
    val description:String):Serializable{
}