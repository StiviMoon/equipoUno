package com.example.pb.data.repository

import com.example.pb.data.model.Reto
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class RetosRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val retosCollection = firestore.collection("retos")

    /**
     * Obtiene los retos sincronizados en tiempo real ordenados por fecha de creación descenciente.
     */
    fun getRetosFlow(): Flow<List<Reto>> = callbackFlow {
        val listenerRegistration = retosCollection
            .orderBy("fechaCreacion", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toReto()
                    }
                    trySend(list)
                }
            }
        awaitClose { listenerRegistration.remove() }
    }

    /**
     * Inserta un nuevo reto en Firestore.
     */
    suspend fun insertReto(reto: Reto): Void? {
        val docRef = retosCollection.document()
        val data = hashMapOf(
            "descripcion" to reto.descripcion,
            "uidUsuario" to reto.uidUsuario,
            "fechaCreacion" to Timestamp.now()
        )
        return docRef.set(data).await()
    }

    /**
     * Actualiza la descripción de un reto existente.
     */
    suspend fun updateReto(reto: Reto): Void? {
        val data = hashMapOf<String, Any>(
            "descripcion" to reto.descripcion
        )
        return retosCollection.document(reto.id).update(data).await()
    }

    /**
     * Elimina un reto.
     */
    suspend fun deleteReto(id: String): Void? {
        return retosCollection.document(id).delete().await()
    }

    /**
     * Obtiene un reto aleatorio de Firestore.
     */
    suspend fun getRandomReto(): Reto? {
        val snapshot = retosCollection.get().await()
        val list = snapshot.documents.mapNotNull { doc -> doc.toReto() }
        return list.randomOrNull()
    }

    /**
     * Función de extensión para mapear un DocumentSnapshot a Reto.
     */
    private fun DocumentSnapshot.toReto(): Reto? {
        return try {
            val idVal = this.id
            val descripcionVal = getString("descripcion") ?: ""
            val uidUsuarioVal = getString("uidUsuario") ?: ""
            val fechaCreacionVal = when (val value = get("fechaCreacion")) {
                is Long -> value
                is Timestamp -> value.toDate().time
                is Number -> value.toLong()
                else -> 0L
            }
            Reto(id = idVal, descripcion = descripcionVal, uidUsuario = uidUsuarioVal, fechaCreacion = fechaCreacionVal)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Helper suspendido para convertir Tasks de GMS en corrutinas de Kotlin.
     */
    private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { cont ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                cont.resume(task.result)
            } else {
                cont.resumeWithException(task.exception ?: RuntimeException("Firestore task failed"))
            }
        }
    }
}
