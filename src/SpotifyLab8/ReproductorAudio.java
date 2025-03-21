/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
*/
package SpotifyLab8;
/**
 *
 * @author danilos
 */
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.io.File;

public class ReproductorAudio {
    private MediaPlayer mediaPlayer;
    private boolean pausado;
    private Duration posicionPausa;
    
    public ReproductorAudio() {
        mediaPlayer = null;
        pausado = false;
        posicionPausa = Duration.ZERO;
                try {
            javafx.application.Platform.startup(() -> {});
        } catch (IllegalStateException e) {
        }
    }
    
    public boolean cargarCancion(String rutaArchivo) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.dispose();
            }
            
            File file = new File(rutaArchivo);
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            pausado = false;
            posicionPausa = Duration.ZERO;
            
            mediaPlayer.setOnError(() -> {
                System.out.println("Error al cargar el archivo: " + mediaPlayer.getError());
            });
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al cargar: " + e.getMessage());
            return false;
        }
    }
    
    public void play() {
        if (mediaPlayer != null) {
            if (pausado) {
                mediaPlayer.seek(posicionPausa);
                pausado = false;
            }
            mediaPlayer.play();
        }
    }
    
    public void pause() {
        if (mediaPlayer != null && estaReproduciendo()) {
            posicionPausa = mediaPlayer.getCurrentTime();
            mediaPlayer.pause();
            pausado = true;
        }
    }
    
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.seek(Duration.ZERO);
            pausado = false;
            posicionPausa = Duration.ZERO;
        }
    }
    
    public boolean isPlaying() {
        return mediaPlayer != null && 
               mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }
    
    public void cerrar() {
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
        }
    }
    
    public float getDuracionTotal() {
        if (mediaPlayer != null && mediaPlayer.getMedia() != null &&
            mediaPlayer.getMedia().getDuration() != Duration.UNKNOWN) {
            return (float) mediaPlayer.getMedia().getDuration().toSeconds();
        }
        return 0;
    }
    
    public float getPosicionActual() {
        if (mediaPlayer != null) {
            if (pausado) {
                return (float) posicionPausa.toSeconds();
            } else {
                return (float) mediaPlayer.getCurrentTime().toSeconds();
            }
        }
        return 0;
    }
    
    public void establecerPosicion(float segundos) {
        if (mediaPlayer != null) {
            Duration nuevaPosicion = Duration.seconds(segundos);
            if (pausado) {
                posicionPausa = nuevaPosicion;
            } else {
                mediaPlayer.seek(nuevaPosicion);
            }
        }
    }
    
    public boolean estaReproduciendo() {
        return isPlaying();
    }
}