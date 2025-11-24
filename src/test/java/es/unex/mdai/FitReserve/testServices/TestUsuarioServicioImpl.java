package es.unex.mdai.FitReserve.testServices;

import es.unex.mdai.FitReserve.data.model.Usuario;
import es.unex.mdai.FitReserve.data.repository.UsuarioRepository;
import es.unex.mdai.FitReserve.services.UsuarioServicioImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestUsuarioServicioImpl {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServicioImpl usuarioServicio;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setEmail("test@correo.com");
        usuario.setNombre("John");
        usuario.setApellidos("Doe");
        usuario.setContrasenia("1234");
        usuario.setTelefono("123456789");
    }

    // ---------------------------------------------------------
    // registrarUsuario
    // ---------------------------------------------------------

    @Test
    void registrarUsuario_deberiaRegistrarSiTodoOK() {
        when(usuarioRepository.existsByEmail("test@correo.com")).thenReturn(false);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario resultado = usuarioServicio.registrarUsuario(usuario);

        assertThat(resultado).isEqualTo(usuario);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void registrarUsuario_lanzaExcepcionSiUsuarioEsNulo() {
        assertThatThrownBy(() -> usuarioServicio.registrarUsuario(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El usuario no puede ser nulo.");
    }

    @Test
    void registrarUsuario_lanzaExcepcionSiEmailNuloOVacio() {
        usuario.setEmail("  ");

        assertThatThrownBy(() -> usuarioServicio.registrarUsuario(usuario))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El email no puede estar vacío.");
    }

    @Test
    void registrarUsuario_lanzaExcepcionSiEmailDuplicado() {
        when(usuarioRepository.existsByEmail("test@correo.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioServicio.registrarUsuario(usuario))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El email ya está registrado");
    }

    // ---------------------------------------------------------
    // actualizarUsuario
    // ---------------------------------------------------------

    @Test
    void actualizarUsuario_deberiaActualizarCamposNoNulos() {
        Usuario cambios = new Usuario();
        cambios.setEmail("nuevo@correo.com");
        cambios.setNombre("Nuevo nombre");
        cambios.setContrasenia("abcd");

        when(usuarioRepository.findByIdUsuario(1L))
                .thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmail("nuevo@correo.com"))
                .thenReturn(false);
        when(usuarioRepository.save(usuario))
                .thenReturn(usuario);

        Usuario resultado = usuarioServicio.actualizarUsuario(1L, cambios);

        assertThat(resultado).isEqualTo(usuario);
        assertThat(usuario.getEmail()).isEqualTo("nuevo@correo.com");
        assertThat(usuario.getNombre()).isEqualTo("Nuevo nombre");
        assertThat(usuario.getContrasenia()).isEqualTo("abcd");
    }

    @Test
    void actualizarUsuario_lanzaExcepcionSiIdEsNulo() {
        assertThatThrownBy(() -> usuarioServicio.actualizarUsuario(null, usuario))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El idUsuario no puede ser nulo.");
    }

    @Test
    void actualizarUsuario_lanzaExcepcionSiDatosActualizadosEsNulo() {
        assertThatThrownBy(() -> usuarioServicio.actualizarUsuario(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Los datos actualizados no pueden ser nulos.");
    }

    @Test
    void actualizarUsuario_lanzaExcepcionSiNoExiste() {
        when(usuarioRepository.findByIdUsuario(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioServicio.actualizarUsuario(1L, usuario))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No existe usuario con id: 1");
    }

    @Test
    void actualizarUsuario_lanzaExcepcionSiNuevoEmailYaExiste() {
        Usuario cambios = new Usuario();
        cambios.setEmail("duplicado@correo.com");

        when(usuarioRepository.findByIdUsuario(1L))
                .thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmail("duplicado@correo.com"))
                .thenReturn(true);

        assertThatThrownBy(() -> usuarioServicio.actualizarUsuario(1L, cambios))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El email ya está registrado");
    }

    // ---------------------------------------------------------
    // eliminarUsuario
    // ---------------------------------------------------------

    @Test
    void eliminarUsuario_deberiaEliminarSiExiste() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        usuarioServicio.eliminarUsuario(1L);

        verify(usuarioRepository).deleteByIdUsuario(1L);
    }

    @Test
    void eliminarUsuario_lanzaExcepcionSiIdEsNulo() {
        assertThatThrownBy(() -> usuarioServicio.eliminarUsuario(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El idUsuario no puede ser nulo.");
    }

    @Test
    void eliminarUsuario_lanzaExcepcionSiNoExiste() {
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> usuarioServicio.eliminarUsuario(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No existe usuario con id: 1");
    }

    // ---------------------------------------------------------
    // obtenerUsuarioPorId
    // ---------------------------------------------------------

    @Test
    void obtenerUsuarioPorId_deberiaDevolverUsuario() {
        when(usuarioRepository.findByIdUsuario(1L))
                .thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioServicio.obtenerUsuarioPorId(1L);

        assertThat(resultado).isEqualTo(usuario);
    }

    @Test
    void obtenerUsuarioPorId_lanzaExcepcionSiIdEsNulo() {
        assertThatThrownBy(() -> usuarioServicio.obtenerUsuarioPorId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El idUsuario no puede ser nulo.");
    }

    @Test
    void obtenerUsuarioPorId_lanzaExcepcionSiNoExiste() {
        when(usuarioRepository.findByIdUsuario(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioServicio.obtenerUsuarioPorId(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No existe usuario con id: 1");
    }

    // ---------------------------------------------------------
    // login
    // ---------------------------------------------------------

    @Test
    void login_deberiaDevolverUsuarioSiCredencialesValidas() {
        when(usuarioRepository.findByEmailAndContrasenia("test@correo.com", "1234"))
                .thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioServicio.login("test@correo.com", "1234");

        assertThat(resultado).isEqualTo(usuario);
    }

    @Test
    void login_lanzaExcepcionSiEmailVacioONulo() {
        assertThatThrownBy(() -> usuarioServicio.login(" ", "1234"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El email no puede estar vacío.");

        assertThatThrownBy(() -> usuarioServicio.login(null, "1234"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El email no puede estar vacío.");
    }

    @Test
    void login_lanzaExcepcionSiContraseniaVaciaONula() {
        assertThatThrownBy(() -> usuarioServicio.login("a@b.com", " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La contraseña no puede estar vacía.");

        assertThatThrownBy(() -> usuarioServicio.login("a@b.com", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La contraseña no puede estar vacía.");
    }

    @Test
    void login_lanzaExcepcionSiCredencialesInvalidas() {
        when(usuarioRepository.findByEmailAndContrasenia("test@correo.com", "wrong"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioServicio.login("test@correo.com", "wrong"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Credenciales inválidas");
    }

    // ---------------------------------------------------------
    // cambiarContrasenia
    // ---------------------------------------------------------

    @Test
    void cambiarContrasenia_deberiaActualizarContraseña() {
        when(usuarioRepository.findByIdUsuario(1L))
                .thenReturn(Optional.of(usuario));

        usuarioServicio.cambiarContrasenia(1L, "nueva-pass");

        assertThat(usuario.getContrasenia()).isEqualTo("nueva-pass");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void cambiarContrasenia_lanzaExcepcionSiIdEsNulo() {
        assertThatThrownBy(() -> usuarioServicio.cambiarContrasenia(null, "1234"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El idUsuario no puede ser nulo.");
    }

    @Test
    void cambiarContrasenia_lanzaExcepcionSiNuevaPassEsNulaOVacia() {
        assertThatThrownBy(() -> usuarioServicio.cambiarContrasenia(1L, " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La nueva contraseña no puede estar vacía.");

        assertThatThrownBy(() -> usuarioServicio.cambiarContrasenia(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La nueva contraseña no puede estar vacía.");
    }

    @Test
    void cambiarContrasenia_lanzaExcepcionSiUsuarioNoExiste() {
        when(usuarioRepository.findByIdUsuario(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioServicio.cambiarContrasenia(1L, "aaa"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No existe usuario con id: 1");
    }

}
