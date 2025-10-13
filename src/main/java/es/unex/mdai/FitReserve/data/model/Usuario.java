package es.unex.mdai.FitReserve.data.model;

import es.unex.mdai.FitReserve.data.enume.TipoUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_usuario;

    @NotNull
    @Column(nullable = false, length = 20)
    private String nombre;

    @NotNull
    @Column(nullable = false, length = 30)
    private String apellidos;

    @NotNull
    @Column(nullable = false, length = 45, unique = true)
    private String email;

    @NotNull
    @Column(nullable = false, length = 20)
    private String contrasenia;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private TipoUsuario tipo_usuario;

    public TipoUsuario getTipo_usuario() {
        return tipo_usuario;
    }

    public void setTipo_usuario(TipoUsuario tipo_usuario) {
        this.tipo_usuario = tipo_usuario;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Long getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Long id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

