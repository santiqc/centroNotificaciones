package com._tcapital.centronotificaciones.application;

import com._tcapital.centronotificaciones.Infrastructure.repository.AddresseeRepository;
import com._tcapital.centronotificaciones.Infrastructure.repository.EmailRepository;
import com._tcapital.centronotificaciones.application.Dto.ArchivoDto;
import com._tcapital.centronotificaciones.domain.Addressee;
import com._tcapital.centronotificaciones.domain.Email;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ProcesamientoServiceImpl implements ProcesamientoService {
    private final EmailRepository emailRepository;
    private final AddresseeRepository addresseeRepository;

    public ProcesamientoServiceImpl(EmailRepository emailRepository, AddresseeRepository addresseeRepository) {
        this.emailRepository = emailRepository;
        this.addresseeRepository = addresseeRepository;
    }


    // Método que se invoca al iniciar la aplicación para leer todos los archivos CSV
    public void procesarArchivosCorreo() throws Exception {
        Path carpeta = Paths.get("src/main/resources/archivoscorreo");
        if (Files.exists(carpeta)) {
            Files.list(carpeta)
                    .filter(path -> path.toString().endsWith(".csv"))
                    .forEach(this::procesarArchivo);
        } else {
            System.out.println("Carpeta no encontrada: " + carpeta);
        }
    }

    // Procesa cada archivo individualmente
    private void procesarArchivo(Path path) {
        try {

            FileReader reader = new FileReader(path.toFile());
            CSVParser csvParser = CSVFormat.DEFAULT
                    .withDelimiter(';')
                    .withFirstRecordAsHeader()
                    .parse(reader);
            Pattern pattern = Pattern.compile("(.+?)\\s*\\(([^)]+)\\)");
            ArrayList<Email> emails = new ArrayList<>();
            ArrayList<Addressee> arrayListAddres = new ArrayList<>();

            for (CSVRecord record : csvParser) {

                Path archivoCuerpoMensaje = obtenerArchivoPorId(Long.parseLong(record.get("Id")));
                if (archivoCuerpoMensaje != null) {
                    Email email = new Email();
                    Addressee addressee = new Addressee();
                    String nombresEmail = record.get("Nombres - Email");
                    Matcher matcher = pattern.matcher(nombresEmail);
                    String correo = "";
                    String nombre = "";

                    if (matcher.find()) {
                        nombre = matcher.group(1).trim();
                        correo = matcher.group(2);
                        addressee.setProcess("historico sendmail");
                        addressee.setName(nombre);
                    } else {
                        correo = "No Email";
                        nombre = "No Name";

                    }

                    // Separar las dos fechas usando el separador '/'
                    String[] fechas = record.get("Fecha").split("/");

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime fechaEnvio = LocalDateTime.parse(fechas[0].trim(), formatter);
                    LocalDateTime fechaEvento = LocalDateTime.parse(fechas[1].trim(), formatter);

                    email.setForTo(correo);
                    email.setSentAt(fechaEnvio);
                    email.setEventdate(fechaEvento);
                    email.setSince(record.get("Agente/Identidad"));
                    email.setSubject(record.get("Asunto"));
                    email.setEvent(record.get("Evento"));
                    email.setReason(record.get("Razon"));
                    email.setIdHistory(Long.parseLong(record.get("Id")));

                    String cuerpoCorreo = leerContenidoArchivo(archivoCuerpoMensaje);
                    email.setBody(cuerpoCorreo);
                    emails.add(email);
                    addressee.setEmail(email);

                    arrayListAddres.add(addressee);
                } else {
                    break;
                }
            }

            emailRepository.saveAll(emails);
            addresseeRepository.saveAll(arrayListAddres);
            System.out.println("Archivo procesado: " + path.getFileName());
        } catch (Exception e) {
            System.err.println("Error al procesar el archivo: " + path.getFileName());
            e.printStackTrace();
        }
    }


    private Path obtenerArchivoPorId(Long idHistory) throws IOException {
        Path carpetaCuerpo = Paths.get("src/main/resources/archivoscorreo/CuerpoMensaje");

        return Files.list(carpetaCuerpo)
                .filter(path -> path.getFileName().toString().equals(idHistory + ".csv"))
                .findFirst()
                .orElse(null);
    }


    private String leerContenidoArchivo(Path archivo) throws IOException {
        StringBuilder contenido = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                contenido.append(linea).append(System.lineSeparator());
            }
        }
        return contenido.toString();
    }


    @Override
    public List<ArchivoDto> buscarArchivosPorIdHistory(Long idHistory) {
        Path carpetaPrincipal = Paths.get("src/main/resources/archivoscorreo/CuerpoMensaje/pdf");
        List<ArchivoDto> archivos = new ArrayList<>();
        Path carpetaHistory = carpetaPrincipal.resolve(idHistory.toString());

        // Verificar si la carpeta existe
        if (Files.exists(carpetaHistory) && Files.isDirectory(carpetaHistory)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(carpetaHistory, "*.pdf")) {
                for (Path path : stream) {
                    String nombreArchivo = path.getFileName().toString();

                    byte[] fileBytes = Files.readAllBytes(path);
                    String fileBase64 = Base64.getEncoder().encodeToString(fileBytes);

                    boolean isWitness = nombreArchivo.equals(idHistory.toString() + ".pdf");
                    archivos.add(new ArchivoDto(nombreArchivo, path.toString(), isWitness, Boolean.TRUE,fileBase64));
                }

            } catch (IOException e) {
                e.printStackTrace(); // Manejo de excepciones
            }
        } else {
            return new ArrayList<>();
        }

        return archivos;
    }

}


