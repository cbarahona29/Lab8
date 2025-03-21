package SpotifyLab8;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
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
    
    // Iconos para los botones
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
        
        // Cargar iconos para los botones
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
        // Intentar cargar la imagen por defecto desde varias ubicaciones
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
        
        // Crear botones con iconos o texto según disponibilidad
        btnPlay = new JButton();
        btnPause = new JButton();
        btnStop = new JButton();
        
        // Establecer los iconos a los botones o usar texto como alternativa
        if (iconoPlay != null) {
            btnPlay.setIcon(iconoPlay);
            System.out.println("Icono Play establecido correctamente");
        } else {
            btnPlay.setText("Play");
            System.out.println("Usando texto 'Play' en lugar de icono");
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
        
        // Ajustar tamaño de los botones
        btnPlay.setPreferredSize(new Dimension(50, 50));
        btnPause.setPreferredSize(new Dimension(50, 50));
        btnStop.setPreferredSize(new Dimension(50, 50));
        
        // Configurar apariencia de los botones
        // Mantener los bordes pintados para mejor visibilidad
        btnPlay.setBorderPainted(true);
        btnPlay.setContentAreaFilled(true);
        btnPlay.setFocusPainted(true);
        
        btnPause.setBorderPainted(true);
        btnPause.setContentAreaFilled(true);
        btnPause.setFocusPainted(true);
        
        btnStop.setBorderPainted(true);
        btnStop.setContentAreaFilled(true);
        btnStop.setFocusPainted(true);
        
        // Agregar los botones al panel de controles
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
        
        // Imprimir información de depuración
        System.out.println("btnPlay es nulo? " + (btnPlay == null));
        System.out.println("btnPlay es visible? " + btnPlay.isVisible());
        System.out.println("btnPause es nulo? " + (btnPause == null));
        System.out.println("btnStop es nulo? " + (btnStop == null));
        
        setVisible(true);
    }
    
    private ImageIcon cargarImagenDefault() {
        ImageIcon icono = null;
        try {
            // Intentar varias ubicaciones
            File file = new File("default_album.png");
            if (file.exists()) {
                icono = new ImageIcon(file.getAbsolutePath());
            } else {
                // Intentar cargar desde recursos
                icono = new ImageIcon(getClass().getResource("/default_album.png"));
                if (icono == null || icono.getIconWidth() <= 0) {
                    // Crear un icono básico como último recurso
                    icono = new ImageIcon(new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB));
                }
            }
            
            // Escalar la imagen
            if (icono != null && icono.getIconWidth() > 0) {
                Image img = icono.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                icono = new ImageIcon(img);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen por defecto: " + e.getMessage());
            // Crear un icono vacío como último recurso
            icono = new ImageIcon(new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB));
        }
        return icono;
    }
    
    private void cargarIconosBotones() {
        try {
            // Primero intentar cargar desde ruta absoluta
            File playFile = new File("images/botonplay.png");
            File pauseFile = new File("images/botonpause.png");
            File stopFile = new File("images/botonstop.png");
            
            boolean iconosEncontrados = false;
            
            if (playFile.exists() && pauseFile.exists() && stopFile.exists()) {
                System.out.println("Cargando iconos desde ruta absoluta");
                iconoPlay = new ImageIcon(playFile.getAbsolutePath());
                iconoPause = new ImageIcon(pauseFile.getAbsolutePath());
                iconoStop = new ImageIcon(stopFile.getAbsolutePath());
                iconosEncontrados = true;
            }
            
            // Segundo intento: ruta "src/images"
            if (!iconosEncontrados) {
                playFile = new File("src/images/botonplay.png");
                pauseFile = new File("src/images/botonpause.png");
                stopFile = new File("src/images/botonstop.png");
                
                if (playFile.exists() && pauseFile.exists() && stopFile.exists()) {
                    System.out.println("Cargando iconos desde src/images");
                    iconoPlay = new ImageIcon(playFile.getAbsolutePath());
                    iconoPause = new ImageIcon(pauseFile.getAbsolutePath());
                    iconoStop = new ImageIcon(stopFile.getAbsolutePath());
                    iconosEncontrados = true;
                }
            }
            
            // Tercer intento: ruta "src/SpotifyLab8/images"
            if (!iconosEncontrados) {
                playFile = new File("src/SpotifyLab8/images/botonplay.png");
                pauseFile = new File("src/SpotifyLab8/images/botonpause.png");
                stopFile = new File("src/SpotifyLab8/images/botonstop.png");
                
                if (playFile.exists() && pauseFile.exists() && stopFile.exists()) {
                    System.out.println("Cargando iconos desde src/SpotifyLab8/images");
                    iconoPlay = new ImageIcon(playFile.getAbsolutePath());
                    iconoPause = new ImageIcon(pauseFile.getAbsolutePath());
                    iconoStop = new ImageIcon(stopFile.getAbsolutePath());
                    iconosEncontrados = true;
                }
            }
            
            // Cuarto intento: intentar con recursos
            if (!iconosEncontrados) {
                System.out.println("Intentando cargar desde recursos");
                iconoPlay = new ImageIcon(getClass().getResource("/images/botonplay.png"));
                iconoPause = new ImageIcon(getClass().getResource("/images/botonpause.png"));
                iconoStop = new ImageIcon(getClass().getResource("/images/botonstop.png"));
                
                // Comprobar si se cargaron los recursos
                if (iconoPlay != null && iconoPlay.getIconWidth() > 0) {
                    iconosEncontrados = true;
                }
            }
            
            // Redimensionar los iconos si se cargaron
            if (iconosEncontrados) {
                iconoPlay = new ImageIcon(iconoPlay.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
                iconoPause = new ImageIcon(iconoPause.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
                iconoStop = new ImageIcon(iconoStop.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
                System.out.println("Iconos cargados y redimensionados correctamente");
            } else {
                System.err.println("No se pudieron encontrar los archivos de iconos");
                iconoPlay = null;
                iconoPause = null;
                iconoStop = null;
            }
        } catch (Exception e) {
            System.err.println("Error al cargar los iconos: " + e.getMessage());
            e.printStackTrace();
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