package mx.tecnm.tepic.ladm_u3_pr2_eh

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity() {
    var id = ""
    var basedatos = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        var extras = intent.extras

        id = extras!!.getString("id").toString()
        txtnombre2.setText(extras.getString("nombre"))
        txtDomicilio2.setText(extras.getString("domicilio"))
        txtCelular2.setText(extras.getString("telefono"))
        txtProducto2.setText(extras.getString("producto"))
        txtPrecio2.setText(extras.getDouble("precio").toString())
        txtCantidad2.setText(extras.getDouble("cantidad").toString())

        this.setTitle("Actualizar Datos - "+txtCelular2.text.toString())
        checkBox.isChecked = false
        if(extras.getBoolean("entregado")==true){
            checkBox.isChecked = true
        }

        button.setOnClickListener {
            basedatos.collection("restaurante")
                .document(id)
                .update("nombre",txtnombre2.text.toString(),
                    "domicilio",txtDomicilio2.text.toString(),
                    "telefono",txtCelular2.text.toString(),
                    "pedido.producto",txtProducto2.text.toString(),
                    "pedido.precio",txtPrecio2.text.toString().toDouble(),
                    "pedido.cantidad",txtCantidad2.text.toString().toDouble(),
                    "pedido.entregado",checkBox.isChecked)
                .addOnSuccessListener {
                    Toast.makeText(this,"Actualización Realizada Correctamente", Toast.LENGTH_LONG)
                        .show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"Error al actualizar", Toast.LENGTH_LONG)
                        .show()
                }
        }
        button2.setOnClickListener {
            finish()
        }
    }
}