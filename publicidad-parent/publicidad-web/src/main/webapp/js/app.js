
function enviar(url, datos, destino) {
    $.ajax({
        url: url,
        type: 'POST',
        data: datos,
        dataType: 'text',   // la respuesta es texto
        success: function (respuestaTexto) {
            if (respuestaTexto.startsWith('OK:')) {
                Swal.fire({
                    title: 'Listo',
                    text: respuestaTexto.replace('OK:', ''),
                    icon: 'success',
                    confirmButtonText: 'Aceptar'
                }).then(function () {
                    window.location.href = destino;
                });
            } else {
                Swal.fire({
                    title: 'Atención',
                    text: respuestaTexto.replace('ERROR:', ''),
                    icon: 'warning'
                });
            }
        },
        error: function () {
            Swal.fire({
                title: 'Error',
                text: 'No se pudo comunicar con el servidor.',
                icon: 'error'
            });
        }
    });
}

$(document).ready(function () {

    // Formularios con class="form-ajax": en vez de recargar la pagina, mando los datos con AJAX.
    // El campo oculto "accion" le dice al servlet que hacer (crear, modificar...).
    $('form.form-ajax').on('submit', function (e) {
        e.preventDefault();
        enviar($(this).attr('action'), $(this).serialize(), $(this).data('destino'));
    });

    // Botones con class="btn-ajax": piden confirmacion y despues mandan sus datos.
    // Los data-* del boton (data-accion, data-legajo, etc.) viajan como parametros.
    $('button.btn-ajax').on('click', function () {
        var boton = $(this);
        var datos = $.extend({}, boton.data());   // copio todos los data-* del boton
        var url = datos.url;
        var pregunta = datos.confirmar;
        delete datos.url;         // estos dos no son parametros para el servidor
        delete datos.confirmar;
        Swal.fire({
            title: pregunta,
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: 'Si',
            cancelButtonText: 'Cancelar'
        }).then(function (resultado) {
            if (resultado.isConfirmed) {
                enviar(url, datos, window.location.href);
            }
        });
    });

    // Mensaje de error del login (lo deja escrito el JSP en un atributo data-error)
    var mensaje = $('#mensaje');
    if (mensaje.length && mensaje.data('error')) {
        Swal.fire({ icon: 'error', title: 'No pudo ingresar', text: String(mensaje.data('error')) });
    }
});
