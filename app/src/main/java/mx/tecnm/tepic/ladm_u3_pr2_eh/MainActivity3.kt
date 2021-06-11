package mx.tecnm.tepic.ladm_u3_pr2_eh

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main3.*

class MainActivity3 : AppCompatActivity() {
    var baseRemota = FirebaseFirestore.getInstance()
    var datos = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        this.setTitle("Consulta de Pedidos")
        btnConsultar.setOnClickListener {
            baseRemota.collection("restaurante")
                .whereEqualTo("telefono",txtBuscar.text.toString())
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if(firebaseFirestoreException!=null){
                        resultado.setText("NO SE PUDO REALIZAR BUSQUEDA")
                        return@addSnapshotListener
                    }
                    datos = ""
                    for(document in querySnapshot!!){
                        var entregado = "entregado"
                        if(!document.getBoolean("pedido.entregado")!!){
                            entregado = "no entregado"
                        }
                        datos+= "Consulta:\n\n" +
                                "       Nombre: "+document.getString("nombre")+"\n"+
                                "       Domicilio: "+document.getString("domicilio")+"\n"+
                                "       Telefono: "+document.getString("telefono")+"\n\n"+
                                "               Producto: "+document.getString("pedido.producto")+"\n"+
                                "               Precio: "+ document.getDouble("pedido.precio")+"\n"+
                                "               Cantidad: " + document.getDouble("pedido.cantidad")+"\n"+
                                "               Estado: "+ entregado + ""
                    }
                    resultado.setText(datos)
                }
        }
        Regresar.setOnClickListener {
            finish()
        }
    }
}
