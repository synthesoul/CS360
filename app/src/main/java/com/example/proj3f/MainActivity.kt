package com.example.proj3f

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var addItemButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        recyclerView = findViewById(R.id.recyclerViewItems)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        addItemButton = findViewById(R.id.buttonAddItem)
        addItemButton.setOnClickListener {
            startActivity(Intent(this, AddEditItemActivity::class.java))
        }

        // 🔐 Request SMS permission if not granted
        checkSmsPermission()

        // 📦 Load inventory data
        loadItems()
    }

    override fun onResume() {
        super.onResume()
        loadItems()
    }

    /**
     * Loads inventory items from SQLite database and displays them in RecyclerView.
     */
    private fun loadItems() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM inventory", null)
        val itemList = mutableListOf<InventoryItem>()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val qty = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))
            val desc = cursor.getString(cursor.getColumnIndexOrThrow("description"))

            itemList.add(InventoryItem(id, name, qty, desc))
        }
        cursor.close()

        recyclerView.adapter = InventoryAdapter(itemList)
    }

    /**
     * Checks and requests SMS permission at runtime.
     */
    private fun checkSmsPermission() {
        val permission = Manifest.permission.SEND_SMS
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
        }
    }

    /**
     * Optional: Handles SMS permission result (app still works even if denied).
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted — you can now use SMS features (if implemented)
            } else {
                // Permission denied — app continues to function without SMS
            }
        }
    }
}
