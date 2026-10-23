package com.tecsup.ayudacampus.data.local

import com.tecsup.ayudacampus.data.local.entity.Categoria

object DatosIniciales {

    suspend fun sembrar(db: AyudaDatabase) {
        // Ejemplo resuelto: solo las categorias.
        if (db.categoriaDao().contar() == 0) {
            db.categoriaDao().insertarTodas(
                listOf(
                    Categoria(id = 1, nombre = "Hardware", icono = "pc"),
                    Categoria(id = 2, nombre = "Software", icono = "code"),
                    Categoria(id = 3, nombre = "Red / Wi-Fi", icono = "wifi"),
                    Categoria(id = 4, nombre = "Cuenta", icono = "key"),
                    Categoria(id = 5, nombre = "Impresión", icono = "printer")
                )
            )
        }
        // TODO (Paso 3): usuarios, tickets y comentarios de ejemplo
    }
}
