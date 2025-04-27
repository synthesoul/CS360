package com.example.proj3f

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddEditItemActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var itemId: Int? = null

    private lateinit var nameInput: EditText
    private lateinit var qtyInput: EditText
    private lateinit var descInput: EditText
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_item)

        dbHelper = DatabaseHelper(this)
        nameInput = findViewById(R.id.editTextItemName)
        qtyInput = findViewById(R.id.editTextQuantity)
        descInput = findViewById(R.id.editTextDescription)
        saveButton = findViewById(R.id.buttonSaveItem)
        deleteButton = findViewById(R.id.buttonDeleteItem)

        itemId = intent.getIntExtra("ITEM_ID", -1).takeIf { it != -1 }

        if (itemId != null) {
            loadItemData(itemId!!)
            deleteButton.isEnabled = true
        } else {
            deleteButton.isEnabled = false
        }

        saveButton.setOnClickListener {
            val name = nameInput.text.toString()
            val qty = qtyInput.text.toString().toIntOrNull() ?: 0
            val desc = descInput.text.toString()

            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("name", name)
                put("quantity", qty)
                put("description", desc)
            }

            if (itemId != null) {
                db.update("inventory", values, "id=?", arrayOf(itemId.toString()))
                Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show()
            } else {
                db.insert("inventory", null, values)
                Toast.makeText(this, "Item added", Toast.LENGTH_SHORT).show()
            }
            finish()
        }

        deleteButton.setOnClickListener {
            if (itemId != null) {
                dbHelper.writableDatabase.delete("inventory", "id=?", arrayOf(itemId.toString()))
                Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun loadItemData(id: Int) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM inventory WHERE id=?", arrayOf(id.toString()))
        if (cursor.moveToFirst()) {
            nameInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")))
            qtyInput.setText(cursor.getInt(cursor.getColumnIndexOrThrow("quantity")).toString())
            descInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("description")))
        }
        cursor.close()
    }
}
