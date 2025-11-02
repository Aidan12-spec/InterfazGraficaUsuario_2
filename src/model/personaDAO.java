package modelo;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class personaDAO {

    private File archivo;
    private persona persona;

    public personaDAO(persona persona) {
        this.persona = persona;
        archivo = new File("gestionContactos");
        prepararArchivo();
    }

    // Método privado que permite gestionar el archivo utilizando la clase File
    private void prepararArchivo() {
        // El if comprueba si el directorio existe
        if (!archivo.exists()) { // Sino el directorio lo crea
            archivo.mkdir();
        }

        // Accede al archivo "datosContactos.csv" 
        archivo = new File(archivo.getAbsolutePath(), "datosContactos.csv");
        // El if comprueba si el directorio existe
        if (!archivo.exists()) { // Sino el archivo lo crea
            try {
                archivo.createNewFile();
                //prepara el encabezado del archivo
                String encabezado = String.format("%s;%s;%s;%s;%s", "NOMBRE", "TELEFONO", "EMAIL", "CATEGORIA", "FAVORITO");
                escribir(encabezado);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void escribir(String texto) {
        FileWriter escribir;
        try {
            escribir = new FileWriter(archivo.getAbsolutePath(), true);
            escribir.write(texto + "\n");
            escribir.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método público para poder escribir en el archivo 
    public boolean escribirArchivo() {
        try {
            escribir(persona.datosContacto());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Método público para leer los datos del archivo 
    public List<persona> leerArchivo() throws IOException {
        String contactos = "";
        FileReader leer = new FileReader(archivo.getAbsolutePath());
        int c;
        while ((c = leer.read()) != -1) {
            contactos += String.valueOf((char) c);
        }
        String[] datos = contactos.split("\n");
        List<persona> personas = new ArrayList<>();
        for (int i = 1; i < datos.length; i++) {
            String contacto = datos[i].trim();
            if (!contacto.isEmpty()) {
                String[] partes = contacto.split(";");
                if (partes.length == 5) {
                    try {
                        persona p = new persona();
                        p.setNombre(partes[0]);
                        p.setTelefono(partes[1]);
                        p.setEmail(partes[2]);
                        p.setCategoria(partes[3]);
                        p.setFavorito(Boolean.parseBoolean(partes[4]));
                        personas.add(p);
                    } catch (Exception e) {
                        System.err.println("Error parseando línea: " + contacto);
                    }
                }
            }
        }
        leer.close();
        return personas;
    }

    // Método público que permite guardar los contactos modificados o eliminados 
    public void actualizarContactos(List<persona> personas) throws IOException {
        archivo.delete();
        archivo.createNewFile();
        FileWriter escribir = new FileWriter(archivo, true);
        escribir.write("NOMBRE;TELEFONO;EMAIL;CATEGORIA;FAVORITO\n");
        for (persona p : personas) {
            escribir.write(p.datosContacto() + "\n");
        }
        escribir.close();
    }
}
