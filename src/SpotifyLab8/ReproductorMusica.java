/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
*/
package SpotifyLab8;
/**
 *
 * @author danilos
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ReproductorMusica extends JFrame {
    private ListaEnlazada listaReproduccion;
    private ReproductorAudio reproductor;
    private int cancionActualIndice = -1;
    
    private JList<String> jListCanciones;
    private DefaultListModel<String> modeloLista;
    private JLabel lblImagen;
    private JLabel lblInfoCancion;
    private JButton btnPlay, btnPause, btnStop, btnAdd, btnRemove;
    private ImageIcon iconoDefault;
    
    private JSlider sliderProgreso;
    private JLabel lblTiempoActual;
    private JLabel lblTiempoTotal;
    private Timer timerActualizacion;
    private boolean actualizandoSlider = false;
    
    public ReproductorMusica() {
        listaReproduccion = new ListaEnlazada();
        reproductor = new ReproductorAudio();
        
        setTitle("Reproductor de Música");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel izquierdo para la lista de canciones
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        modeloLista = new DefaultListModel<>();
        jListCanciones = new JList<>();
        jListCanciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollLista = new JScrollPane(jListCanciones);
        scrollLista.setPreferredSize(new Dimension(300, 400));
        panelIzquierdo.add(new JLabel("Lista de Reproducción"), BorderLayout.NORTH);
        panelIzquierdo.add(scrollLista, BorderLayout.CENTER);
        
        JPanel panelBotonesLista = new JPanel(new FlowLayout());
        btnAdd = new JButton("Agregar");
        btnRemove = new JButton("Eliminar");
        panelBotonesLista.add(btnAdd);
        panelBotonesLista.add(btnRemove);
        panelIzquierdo.add(panelBotonesLista, BorderLayout.SOUTH);
        
        JPanel panelDerecho = new JPanel(new BorderLayout());
        
        JPanel panelImagen = new JPanel(new BorderLayout());
        iconoDefault = new ImageIcon("default_album.png");  // Imagen por defecto
        Image img = iconoDefault.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        iconoDefault = new ImageIcon(img);
        lblImagen = new JLabel(iconoDefault);
        lblImagen.setHorizontalAlignment(JLabel.CENTER);
        panelImagen.add(lblImagen, BorderLayout.CENTER);
        
        JPanel panelInfo = new JPanel(new BorderLayout());
        lblInfoCancion = new JLabel("No hay canción seleccionada");
        lblInfoCancion.setHorizontalAlignment(JLabel.CENTER);
        panelInfo.add(lblInfoCancion, BorderLayout.CENTER);
        
        JPanel panelProgreso = new JPanel(new BorderLayout(5, 5));
        sliderProgreso = new JSlider(0, 100, 0);
        sliderProgreso.setEnabled(false);
        
        JPanel panelTiempo = new JPanel(new BorderLayout());
        lblTiempoActual = new JLabel("00:00");
        lblTiempoTotal = new JLabel("00:00");
        panelTiempo.add(lblTiempoActual, BorderLayout.WEST);
        panelTiempo.add(lblTiempoTotal, BorderLayout.EAST);
        
        panelProgreso.add(sliderProgreso, BorderLayout.CENTER);
        panelProgreso.add(panelTiempo, BorderLayout.SOUTH);
        
        JPanel panelControles = new JPanel(new FlowLayout());
        btnPlay = new JButton("Play");
        btnPause = new JButton("Pause");
        btnStop = new JButton("Stop");
        panelControles.add(btnPlay);
        panelControles.add(btnPause);
        panelControles.add(btnStop);
        
        panelDerecho.add(panelImagen, BorderLayout.NORTH);
        panelDerecho.add(panelInfo, BorderLayout.CENTER);
        
        JPanel panelControlesPrincipal = new JPanel(new BorderLayout());
        panelControlesPrincipal.add(panelProgreso, BorderLayout.NORTH);
        panelControlesPrincipal.add(panelControles, BorderLayout.SOUTH);
        panelDerecho.add(panelControlesPrincipal, BorderLayout.SOUTH);
        
        panelPrincipal.add(panelIzquierdo, BorderLayout.WEST);
        panelPrincipal.add(panelDerecho, BorderLayout.CENTER);
        
        add(panelPrincipal);
        
        timerActualizacion = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarBarraProgreso();
            }
        });
        
        configurarEventos();
        
        setVisible(true);
    }
    
    private void configurarEventos() {
        btnPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cancionActualIndice != -1) {
                    reproductor.play();
                    timerActualizacion.start();
                }
            }
        });
        
        btnPause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reproductor.pause();
                timerActualizacion.stop();
            }
        });
        
        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reproductor.stop();
                timerActualizacion.stop();
                sliderProgreso.setValue(0);
                lblTiempoActual.setText("00:00");
            }
        });
        
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarCancion();
            }
        });
        
        btnRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarCancion();
            }
        });
        
        jListCanciones.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int indiceSeleccionado = jListCanciones.getSelectedIndex();
                if (indiceSeleccionado != -1) {
                    seleccionarCancion(indiceSeleccionado);
                }
            }
        });
        
        sliderProgreso.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!sliderProgreso.getValueIsAdjusting() && !actualizandoSlider) {
                    int valorSlider = sliderProgreso.getValue();
                    float duracionTotal = reproductor.getDuracionTotal();
                    float nuevaPosicion = (valorSlider / 100.0f) * duracionTotal;
                    reproductor.establecerPosicion(nuevaPosicion);
                    actualizarEtiquetaTiempo(nuevaPosicion, lblTiempoActual);
                }
            }
        });
        
        sliderProgreso.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                timerActualizacion.stop();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (reproductor.estaReproduciendo()) {
                    timerActualizacion.start();
                }
            }
        });
                addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                timerActualizacion.stop();
                reproductor.cerrar();
                System.exit(0);
            }
        });
    }
    
    private void actualizarListaCanciones() {
        modeloLista.clear();
        String[] canciones = listaReproduccion.obtenerArrayCanciones();
        for (String cancion : canciones) {
            modeloLista.addElement(cancion);
        }
        jListCanciones.setModel(modeloLista);
    }
    
    private void agregarCancion() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo de audio");
        int resultado = fileChooser.showOpenDialog(this);
        
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();
            
            JTextField txtNombre = new JTextField(20);
            JTextField txtArtista = new JTextField(20);
            JTextField txtDuracion = new JTextField(20);
            JTextField txtGenero = new JTextField(20);
            
            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Nombre de la canción:"));
            panel.add(txtNombre);
            panel.add(new JLabel("Artista:"));
            panel.add(txtArtista);
            panel.add(new JLabel("Duración (mm:ss):"));
            panel.add(txtDuracion);
            panel.add(new JLabel("Género:"));
            panel.add(txtGenero);
            
            int result = JOptionPane.showConfirmDialog(this, panel, "Información de la canción",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                JFileChooser imageChooser = new JFileChooser();
                imageChooser.setDialogTitle("Seleccionar imagen para la canción");
                int resultadoImagen = imageChooser.showOpenDialog(this);
                
                String rutaImagen = "default_album.png"; 
                
                if (resultadoImagen == JFileChooser.APPROVE_OPTION) {
                    rutaImagen = imageChooser.getSelectedFile().getAbsolutePath();
                }
                
                Cancion nuevaCancion = new Cancion(
                        txtNombre.getText(),
                        txtArtista.getText(),
                        txtDuracion.getText(),
                        rutaImagen,
                        txtGenero.getText(),
                        archivoSeleccionado.getAbsolutePath()
                );
                
                listaReproduccion.agregarCancion(nuevaCancion);
                actualizarListaCanciones();
            }
        }
    }
    
    private void eliminarCancion() {
        int indiceSeleccionado = jListCanciones.getSelectedIndex();
        
        if (indiceSeleccionado != -1) {
            if (indiceSeleccionado == cancionActualIndice) {
                reproductor.stop();
                timerActualizacion.stop();
                cancionActualIndice = -1;
                lblInfoCancion.setText("No hay canción seleccionada");
                lblImagen.setIcon(iconoDefault);
                sliderProgreso.setValue(0);
                sliderProgreso.setEnabled(false);
                lblTiempoActual.setText("00:00");
                lblTiempoTotal.setText("00:00");
            } else if (indiceSeleccionado < cancionActualIndice) {
                cancionActualIndice--;
            }
            
            listaReproduccion.eliminarCancion(indiceSeleccionado);
            actualizarListaCanciones();
        }
    }
    
    private void seleccionarCancion(int indice) {
        Cancion cancionSeleccionada = listaReproduccion.obtenerCancion(indice);
        
        if (cancionSeleccionada != null) {
            reproductor.stop();
            timerActualizacion.stop();
            
            cancionActualIndice = indice;
            
            lblInfoCancion.setText("<html><center>" +
                    cancionSeleccionada.getNombre() + "<br>" +
                    "Artista: " + cancionSeleccionada.getArtista() + "<br>" +
                    "Duración: " + cancionSeleccionada.getDuracion() + "<br>" +
                    "Género: " + cancionSeleccionada.getGenero() +
                    "</center></html>");
            
            try {
                ImageIcon icono = new ImageIcon(cancionSeleccionada.getRutaImagen());
                Image img = icono.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                lblImagen.setIcon(new ImageIcon(img));
            } catch (Exception e) {
                lblImagen.setIcon(iconoDefault);
            }
            
            reproductor.cargarCancion(cancionSeleccionada.getRutaArchivo());
            
            sliderProgreso.setValue(0);
            sliderProgreso.setEnabled(true);
            
            float duracionTotal = reproductor.getDuracionTotal();
            actualizarEtiquetaTiempo(duracionTotal, lblTiempoTotal);
            lblTiempoActual.setText("00:00");
        }
    }
    
    private void actualizarBarraProgreso() {
        if (cancionActualIndice != -1) {
            actualizandoSlider = true;
            
            float posicionActual = reproductor.getPosicionActual();
            float duracionTotal = reproductor.getDuracionTotal();
            
            if (duracionTotal > 0) {
                int porcentaje = (int)((posicionActual / duracionTotal) * 100);
                sliderProgreso.setValue(porcentaje);
                
                actualizarEtiquetaTiempo(posicionActual, lblTiempoActual);
            }
            
            actualizandoSlider = false;
               if (!reproductor.estaReproduciendo() && posicionActual >= duracionTotal) {
                timerActualizacion.stop();
                sliderProgreso.setValue(0);
                lblTiempoActual.setText("00:00");
            }
        }
    }
    
    private void actualizarEtiquetaTiempo(float segundos, JLabel etiqueta) {
        int minutos = (int)(segundos / 60);
        int segs = (int)(segundos % 60);
        etiqueta.setText(String.format("%02d:%02d", minutos, segs));
    }
    
}