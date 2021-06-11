package mx.tecnm.tepic.ladm_u3_pr2_eh

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var baseRemota = FirebaseFirestore.getInstance()
    var datosArreglo = ArrayList<String>()
    var arregloID = ArrayList<String>()
    var datosArreglo2 = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.setTitle("Restaurante - Pedidos")
        //consultar
        btnConsultarP.setOnClickListener {
            startActivity(Intent(this, MainActivity3::class.java))
        }

        //Rellenar lista de pedidos
        baseRemota.collection("restaurante")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if(firebaseFirestoreException!=null){
                        Toast.makeText(this,"Error al consultar los datos", Toast.LENGTH_LONG).show()
                        return@addSnapshotListener
                    }
                    //Limpieza
                    datosArreglo.clear()
                    arregloID.clear()
                    datosArreglo2.clear()
                    //rellenado
                    for (document in querySnapshot!!){
                        var entregado = "entregado"
                        if(!document.getBoolean("pedido.entregado")!!){
                            entregado = "no entregado"
                        }
                        var pedidos = "Nombre:"+document.getString("nombre")+"" +
                                "Domicilio:"+document.getString("domicilio")+""+
                                "Telefono:"+document.getString("telefono")+""+
                                "Producto:"+document.getString("pedido.producto")+""+
                                "PEDIDO" +
                                "Precio: "+ document.getDouble("pedido.precio")+""+
                                "Cantidad:" + document.getDouble("pedido.cantidad")+""+
                                "Estatus Entrega: "+entregado+""

                        var pedidoActualizar =
                                "   Nombre:"+document.getString("nombre")+"\n" +
                                        "   Domicilio:"+document.getString("domicilio")+"\n"+
                                        "   Telefono:"+document.getString("telefono")+"\n"+
                                        "   Producto:"+document.getString("pedido.producto")+"\n\n"+
                                        "PEDIDO\n" +
                                        "   Precio: "+ document.getDouble("pedido.precio")+"\n"+
                                        "   Cantidad:" + document.getDouble("pedido.cantidad")+ "\n "+
                                        "   Estatus Entrega: "+entregado+""
                        datosArreglo.add(pedidos)
                        datosArreglo2.add(pedidoActualizar)
                        arregloID.add(document.id)
                    }
                    if(datosArreglo.size==0){
                        datosArreglo.add("No hay datos en Firestore")
                    }
                    var adaptador = ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,datosArreglo)
                    itemLista.adapter = adaptador

                }

        //Acción por objeto
        itemLista.setOnItemClickListener { parent, view, position, id ->
            if(arregloID.size==0){
                return@setOnItemClickListener
            }else{
                eliminarOActualizar(position)
            }

        }
        //Insertar
        btnInsertar.setOnClickListener {
            if(campoVacios()){
                mensaje("Llenar todos los campos")
                return@setOnClickListener
            }
            insertarRegistro()
        }



    }//onCreate

    private fun Actualizar(actualizacionID: String) {
        baseRemota.collection("restaurante")
                .document(actualizacionID)
                .get()
                .addOnSuccessListener {
                    var intentActualizar = Intent(this, MainActivity2::class.java)
                    intentActualizar.putExtra("id", actualizacionID)
                    intentActualizar.putExtra("nombre", it.getString("nombre"))
                    intentActualizar.putExtra("domicilio", it.getString("domicilio"))
                    intentActualizar.putExtra("telefono", it.getString("telefono"))
                    intentActualizar.putExtra("producto", it.getString("pedido.producto"))
                    intentActualizar.putExtra("precio", it.getDouble("pedido.precio"))
                    intentActualizar.putExtra("cantidad", it.getDouble("pedido.cantidad"))
                    intentActualizar.putExtra("entregado", it.getBoolean("pedido.entregado"))
                    startActivity(intentActualizar)
                }
                .addOnFailureListener {
                    Toast.makeText(this,"No se pudo actualizar!", Toast.LENGTH_LONG)
                            .show()
                }
    }//Actualizar

    private fun eliminarOActualizar(position: Int) {
        AlertDialog.Builder(this).setTitle("Acciones")
                .setMessage("Elige acción a realizar con \n${datosArreglo2[position]}")
                .setPositiveButton("ELIMINAR") {d, i ->
                    eliminar(arregloID[position])
                }
                .setNegativeButton("ACTUALIZAR") {d, i ->
                    Actualizar(arregloID[position])
                }
                .setNeutralButton("CANCELAR") {d, i -> }
                .show()
    }//eliminarOActualizar

    private fun eliminar(idEliminar: String) {
        baseRemota.collection("restaurante")
                .document(idEliminar)
                .delete()
                .addOnSuccessListener {
                    mensaje("Se realizó la eliminación correctamente")
                }
                .addOnFailureListener {
                    mensaje("NO se pudo realizar la eliminación")
                }
    }

    private fun campoVacios():Boolean{
        if(txtNombre.text.isEmpty()){
            return true
        }
        if(txtDomicilio.text.isEmpty()){
            return true
        }
        if(txtCelular.text.isEmpty()){
            return true
        }
        if(txtProducto.text.isEmpty()){
            return true
        }
        if(txtPrecio.text.isEmpty()){
            return true
        }
        if(txtCantidad.text.isEmpty()){
            return true
        }
        return false

    }

    private fun insertarRegistro() {
        var data = hashMapOf(
                "nombre" to txtNombre.text.toString(), //cadena
                "domicilio" to txtDomicilio.text.toString(), //cadena
                "telefono" to txtCelular.text.toString(), //cadena
                "pedido" to hashMapOf(
                        "producto" to txtProducto.text.toString(),
                        "precio" to txtPrecio.text.toString().toFloat(),
                        "cantidad" to txtCantidad.text.toString().toFloat(),
                        "entregado" to cbxEntrega.isChecked )
        )
        baseRemota.collection("restaurante")
                .add(data as Any)
                .addOnSuccessListener {
                    mensaje("SE CAPTURÓ")
                    limpiarCampos()
                }
                .addOnFailureListener {
                    mensaje("ERROR! NO SE CAPTURÓ")
                }
    }

    private fun mensaje(s: String) {
        Toast.makeText(this,s, Toast.LENGTH_LONG)
                .show()
    }

    private fun limpiarCampos() {
        txtNombre.setText("");
        txtDomicilio.setText("");
        txtCelular.setText("");
        txtProducto.setText("");
        txtPrecio.setText("");
        txtCantidad.setText("");
    }
}