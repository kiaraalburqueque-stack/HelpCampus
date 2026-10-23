package com.tecsup.ayudacampus.data.local

import com.tecsup.ayudacampus.data.local.entity.Categoria
import com.tecsup.ayudacampus.data.local.entity.Comentario
import com.tecsup.ayudacampus.data.local.entity.Ticket
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.Prioridad
import com.tecsup.ayudacampus.model.Rol

/** Datos ficticios de prueba (Ley 29733: no usamos datos reales). Se cargan solo la primera vez. */
object DatosIniciales {

    private const val HORA = 3_600_000L

    suspend fun sembrar(db: AyudaDatabase) {
        if (db.usuarioDao().contar() != 0) return

        val ahora = System.currentTimeMillis()

        // 1) Categorias (padres de los tickets)
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

        // 2) Usuarios: 1 estudiante y 1 soporte
        val kiara = Usuario(id = 1, nombre = "Kiara Alburqueque", correo = "kiara@campus.test", clave = "1234", rol = Rol.CLIENTE.name)
        val soporte = Usuario(id = 2, nombre = "Soporte TI", correo = "soporte@campus.test", clave = "admin", rol = Rol.ADMIN.name)
        db.usuarioDao().insertarTodos(listOf(kiara, soporte))

        // 3) Tickets de ejemplo en distintos estados y prioridades
        val dao = db.ticketDao()

        val impresora = dao.insertarTicket(
            Ticket(
                clienteId = kiara.id, categoriaId = 5,
                titulo = "No puedo imprimir en biblioteca",
                descripcion = "La impresora de la biblioteca no recibe mis documentos.",
                ubicacion = "Biblioteca",
                prioridad = Prioridad.BAJA.name, estado = EstadoTicket.RESUELTO.name,
                fecha = ahora - 72 * HORA
            )
        ).toInt()

        val clave = dao.insertarTicket(
            Ticket(
                clienteId = kiara.id, categoriaId = 4,
                titulo = "Olvidé mi contraseña del aula virtual",
                descripcion = "No puedo ingresar al aula virtual desde ayer.",
                ubicacion = "Pabellón A",
                prioridad = Prioridad.BAJA.name, estado = EstadoTicket.CERRADO.name,
                fecha = ahora - 120 * HORA, fechaCierre = ahora - 100 * HORA, calificacion = 5
            )
        ).toInt()

        dao.insertarTicket(
            Ticket(
                clienteId = kiara.id, categoriaId = 2,
                titulo = "Instalar Android Studio",
                descripcion = "Necesitamos Android Studio en las PCs del laboratorio 5.",
                ubicacion = "Lab 5",
                prioridad = Prioridad.MEDIA.name, estado = EstadoTicket.ABIERTO.name,
                fecha = ahora - 9 * HORA
            )
        )

        dao.insertarTicket(
            Ticket(
                clienteId = kiara.id, categoriaId = 1,
                titulo = "Teclado no responde",
                descripcion = "El teclado de la PC 12 no responde a ninguna tecla.",
                ubicacion = "Aula 204",
                prioridad = Prioridad.MEDIA.name, estado = EstadoTicket.ABIERTO.name,
                fecha = ahora - 26 * HORA
            )
        )

        val wifi = dao.insertarTicket(
            Ticket(
                clienteId = kiara.id, categoriaId = 3,
                titulo = "Sin Wi-Fi en el laboratorio 3",
                descripcion = "Desde ayer no hay señal de Wi-Fi en todo el laboratorio. Afecta a 30 alumnos.",
                ubicacion = "Lab 3 · Pabellón B",
                prioridad = Prioridad.ALTA.name, estado = EstadoTicket.EN_PROCESO.name,
                fecha = ahora - 2 * HORA
            )
        ).toInt()

        dao.insertarTicket(
            Ticket(
                clienteId = kiara.id, categoriaId = 1,
                titulo = "Servidor del aula 305 caído",
                descripcion = "El servidor del aula 305 no enciende y no podemos dar la práctica.",
                ubicacion = "Aula 305",
                prioridad = Prioridad.ALTA.name, estado = EstadoTicket.ABIERTO.name,
                fecha = ahora - 3 * HORA
            )
        )

        // 4) Algunos comentarios
        fun comentario(ticketId: Int, autor: Usuario, texto: String, haceHoras: Double) = Comentario(
            ticketId = ticketId,
            autorId = autor.id,
            autorNombre = autor.nombre,
            autorRol = autor.rol,
            texto = texto,
            fecha = ahora - (haceHoras * HORA).toLong()
        )
        dao.insertarComentario(comentario(wifi, soporte, "Revisaremos el access point del pabellón B hoy.", 1.5))
        dao.insertarComentario(comentario(wifi, kiara, "Gracias, quedo atento.", 1.0))
        dao.insertarComentario(comentario(impresora, soporte, "Reiniciamos el driver de la impresora. Ya debería imprimir.", 24.0))
        dao.insertarComentario(comentario(clave, soporte, "Te enviamos una nueva contraseña a tu correo institucional.", 101.0))
    }
}
