package com.Tom.uceva_dengue.utils

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.Tom.uceva_dengue.model.Departamento
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.log

class RepositoryDepartamento (){
    val db = Firebase.firestore
     fun getDepartamentoFlow(){
         db.collection("Departamento")
             .get()
             .addOnSuccessListener { result ->
                 for (document in result) {
                     Log.d(TAG, "${document.id} => ${document.data}")
                 }
             }
             .addOnFailureListener { exception ->
                 Log.w(TAG, "Error getting documents.", exception)
             }
     }

}
