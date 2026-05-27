package historia.numeroOcho.ui.dialog


interface EditarRetoDialogListener {
    fun onRetoActualizado(retoId: Long, nuevaDescripcion: String)
    fun onEdicionCancelada()
}