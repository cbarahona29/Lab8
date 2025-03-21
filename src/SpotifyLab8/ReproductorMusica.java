package SpotifyLab8;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.event.*;

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
    
    private ImageIcon iconoPlay;
    private ImageIcon iconoPause;
    private ImageIcon iconoStop;
    
    private JSlider sliderProgreso;
    private JSlider sliderVolumen;
    private JLabel lblTiempoActual;
    private JLabel lblTiempoTotal;
    private Timer timerActualizacion;
    private boolean actualizandoSlider = false;
    
    public ReproductorMusica() {
        listaReproduccion = new ListaEnlazada();
        reproductor = new ReproductorAudio();
        
        setTitle("Reproductor de Musica");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        cargarIconosBotones();
        
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        modeloLista = new DefaultListModel<>();
        jListCanciones = new JList<>();
        jListCanciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollLista = new JScrollPane(jListCanciones);
        scrollLista.setPreferredSize(new Dimension(300, 400));
        panelIzquierdo.add(new JLabel("Lista de Reproduccion"), BorderLayout.NORTH);
        panelIzquierdo.add(scrollLista, BorderLayout.CENTER);
        
        JPanel panelBotonesLista = new JPanel(new FlowLayout());
        btnAdd = new JButton("Agregar");
        btnRemove = new JButton("Eliminar");
        panelBotonesLista.add(btnAdd);
        panelBotonesLista.add(btnRemove);
        panelIzquierdo.add(panelBotonesLista, BorderLayout.SOUTH);
        
        JPanel panelDerecho = new JPanel(new BorderLayout());
        
        JPanel panelImagen = new JPanel(new BorderLayout());
        iconoDefault = cargarImagenDefault();
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
        
        btnPlay = new JButton();
        btnPause = new JButton();
        btnStop = new JButton();
        
        if (iconoPlay != null) {
            btnPlay.setIcon(iconoPlay);
        } else {
            btnPlay.setText("Play");
        }
        
        if (iconoPause != null) {
            btnPause.setIcon(iconoPause);
        } else {
            btnPause.setText("Pause");
        }
        
        if (iconoStop != null) {
            btnStop.setIcon(iconoStop);
        } else {
            btnStop.setText("Stop");
        }
        
        btnPlay.setPreferredSize(new Dimension(50, 50));
        btnPause.setPreferredSize(new Dimension(50, 50));
        btnStop.setPreferredSize(new Dimension(50, 50));
        
        btnPlay.setBorderPainted(true);
        btnPlay.setContentAreaFilled(true);
        btnPlay.setFocusPainted(true);
        
        btnPause.setBorderPainted(true);
        btnPause.setContentAreaFilled(true);
        btnPause.setFocusPainted(true);
        
        btnStop.setBorderPainted(true);
        btnStop.setContentAreaFilled(true);
        btnStop.setFocusPainted(true);
        
        panelControles.add(btnPlay);
        panelControles.add(btnPause);
        panelControles.add(btnStop);
        
        JPanel panelVolumen = new JPanel(new BorderLayout(5, 5));
        sliderVolumen = new JSlider(0, 100, 100);
        sliderVolumen.setPreferredSize(new Dimension(200, 30));
        
        JLabel lblVolumen = new JLabel("Volumen: ");
        panelVolumen.add(lblVolumen, BorderLayout.WEST);
        panelVolumen.add(sliderVolumen, BorderLayout.CENTER);
        
        JPanel panelControlCompleto = new JPanel(new BorderLayout(5, 5));
        panelControlCompleto.add(panelControles, BorderLayout.NORTH);
        panelControlCompleto.add(panelVolumen, BorderLayout.SOUTH);
        
        panelDerecho.add(panelImagen, BorderLayout.NORTH);
        panelDerecho.add(panelInfo, BorderLayout.CENTER);
        
        JPanel panelControlesPrincipal = new JPanel(new BorderLayout());
        panelControlesPrincipal.add(panelProgreso, BorderLayout.NORTH);
        panelControlesPrincipal.add(panelControlCompleto, BorderLayout.SOUTH);
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
    
    private ImageIcon cargarImagenDefault() {
        ImageIcon icono = null;
        try {
            File file = new File("default_album.png");
            if (file.exists()) {
                icono = new ImageIcon(file.getAbsolutePath());
            } else {
                icono = new ImageIcon(getClass().getResource("/default_album.png"));
            }
            
            if (icono != null && icono.getIconWidth() > 0) {
                Image img = icono.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                icono = new ImageIcon(img);
            }
        } catch (Exception e) {
            icono = null;
        }
        return icono;
    }
    
    private void cargarIconosBotones() {
        try {
            File playFile = new File("images/botonplay.png");
            File pauseFile = new File("images/botonstop.png");
            File stopFile = new File("images/botonpause.png");
            
            boolean iconosEncontrados = false;
            
            if (playFile.exists() && pauseFile.exists() && stopFile.exists()) {
                iconoPlay = new ImageIcon(playFile.getAbsolutePath());
                iconoPause = new ImageIcon(pauseFile.getAbsolutePath());
                iconoStop = new ImageIcon(stopFile.getAbsolutePath());
                iconosEncontrados = true;
            }
            
            if (!iconosEncontrados) {
                playFile = new File("src/images/botonplay.png");
                pauseFile = new File("src/images/botonstop.png");
                stopFile = new File("src/images/botonpause.png");
                
                if (playFile.exists() && pauseFile.exists() && stopFile.exists()) {
                    iconoPlay = new ImageIcon(playFile.getAbsolutePath());
                    iconoPause = new ImageIcon(pauseFile.getAbsolutePath());
                    iconoStop = new ImageIcon(stopFile.getAbsolutePath());
                    iconosEncontrados = true;
                }
            }
            
            if (!iconosEncontrados) {
                playFile = new File("src/SpotifyLab8/images/botonplay.png");
                pauseFile = new File("src/SpotifyLab8/images/botonstop.png");
                stopFile = new File("src/SpotifyLab8/images/botonpause.png");
                
                if (playFile.exists() && pauseFile.exists() && stopFile.exists()) {
                    iconoPlay = new ImageIcon(playFile.getAbsolutePath());
                    iconoPause = new ImageIcon(pauseFile.getAbsolutePath());
                    iconoStop = new ImageIcon(stopFile.getAbsolutePath());
                    iconosEncontrados = true;
                }
            }
            
            if (!iconosEncontrados) {
                iconoPlay = new ImageIcon(getClass().getResource("/images/botonplay.png"));
                iconoPause = new ImageIcon(getClass().getResource("/images/botonstop.png"));
                iconoStop = new ImageIcon(getClass().getResource("/images/botonpause.png"));
                
                if (iconoPlay != null && iconoPlay.getIconWidth() > 0) {
                    iconosEncontrados = true;
                }
            }
            
            if (iconosEncontrados) {
                iconoPlay = new ImageIcon(iconoPlay.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
                iconoPause = new ImageIcon(iconoPause.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
                iconoStop = new ImageIcon(iconoStop.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
            } else {
                iconoPlay = null;
                iconoPause = null;
                iconoStop = null;
            }
        } catch (Exception e) {
            iconoPlay = null;
            iconoPause = null;
            iconoStop = null;
        }
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
        
        sliderVolumen.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                double volumen = sliderVolumen.getValue() / 100.0;
                reproductor.setVolumen(volumen);
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
            
            int result = JOptionPane.showConfirmDialog(this, panel, "Información de la cancion",
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
                lblInfoCancion.setText("No hay cancion seleccionada");
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
            reproductor.setVolumen(sliderVolumen.getValue() / 100.0);
            
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