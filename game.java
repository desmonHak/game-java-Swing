import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.EventQueue;
import java.awt.event.*;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;



final public class Game {

	final private static Dimension tamanoPantalla = Toolkit.getDefaultToolkit().getScreenSize(); //obtencion de las dimensiones de la pantalla de la pc
	final public static String __autor__ = "Desmon";     //autor: desmon. no tocar por favor

	final static int altura = tamanoPantalla.height+100; //no tocar!!, tamano de la pantalla en altura(y)
	final static int ancho = tamanoPantalla.width+100;   //no tocar!!, tamano de la pantalla en anchura(x)

	final private static int x_max = ancho/2 -50;         //no tocar!!, tamano maximo de colision en la cordenada y
	final private static int y_max = altura/2 -150;       //no tocar!!, tamano maximo de colision en la cordenada x
	final private static Color Backgroound = Color.BLACK; //color de fondo


	private static int actualizadorTiempo = 85;            //se recomienda no tocar, tiempo de actualizacion de los graficos
	private static short enemigosGeneradosEjemplo = 80;    //no tocar!!!, medidor de cantidad de enemigos por fila
	private static int columnas_a_generar = 4;             //columnas a generar

	// ------- variables de Threads/Hilos --------
	private static Thread hilo1;                           // medidor de movimiento enemigo
	private static Thread hilo2;                           // detecion de teclas para los disparos y medidor de localizacion del jugador
	private static Thread hilo3;                           // hilo encargado de actualizar los graficos
	private static Thread hilo4;                           // hilo encargado de medir las colisions entre disparos y enemigos
	// --------------------------------------------

	private static Lamina1 Lamina1;                         //Lamina donde pintar todos los graficos
	private static int x_limiteMovimiento;                  //no tocar
	private static Jugador Jugador = new Jugador();         //intancia del jugador, se recomienda manipular esta variable desde su clase
	private static ArrayList<Disparos>ListaDisparos = new ArrayList<Disparos>(); // aray contenedor de los disparos


	// ------- clase diparo --------
	public static class Disparos{
		Color color = Color.YELLOW;  // color del disparo
		int posicion[] = {Jugador.posicion[0]+24, y_max-25, 5, 20}; //dimensiones y localizacion del diaparo
		boolean existencia = true;   // variable para la eliminacion del disparo tras una colision con un borde o enemigo
		byte velocidad = (byte)((int)Jugador.velocidad+5); //velocidad del disparo, es igual a la verlocidad del jugador + 5
	}

	public static class Enemigo {
		int posicion[] = {0,50,0,0}; //posicion y dimension de los enemigos, se recomiendo no manegar esta variable
		boolean live = true;         //comprobador de si hubo una colision , si la hubo(false) no se imprime al enemigo y se torna del color de fondo
		boolean lado = true;         // comprueba que no a dado con un borde
		static Color color = Color.RED; // color de los enemigos
	}

	public static class Jugador {
		int posicion[] = {x_max/2,y_max}; //localizacion del jugador
		byte live = 3;                    //cantidad de vidas siponibles
		boolean victoria = false;         //variables de victoria
		Color color = Color.BLUE;         //color del jugador
		byte velocidad = 10;              //velocidad del jugador, se recomienda no tocar
	}

	final private static class Windows extends JFrame {
		
		private static final long serialVersionUID = 1L;

		Windows() {

			DesplazamientoEnemigo DesplazamientoEnemigo = new DesplazamientoEnemigo(Lamina1);

			Lamina1 = new Lamina1();
			add(Lamina1);
			
			// ----- arranque/inizializacion de los hilos ----------------
			Runnable funcHilo3 = new ActualizarPantalla();
			hilo3 = new Thread(funcHilo3, "Actualizador de graficos");
			hilo3.setPriority(7);
			hilo3.start();

			Runnable funcHilo1 = new DesplazamientoEnemigo();
			hilo1 =  new Thread(funcHilo1, "DesplazamientoEnemigo");
			hilo1.setPriority(7);
			hilo1.start();

			Runnable funcHilo2 = new MovimientoJugador();
			hilo2 = new Thread(funcHilo2, "Movimiento jugador");
			hilo2.setPriority(7);
			hilo2.start();

			Runnable funcHilo4 = new DetectorDeColisiones();
			//hilo4 = new Thread
			// ------------------------------------------------------------

			setSize(ancho/2, altura/2); //tamano de la pantalla
			System.out.println(ancho/2+"x"+altura/2+" "+x_max+"x"+y_max);
			setLocation(ancho/4, altura/4); //localizacion de la pantalla
			setTitle("juego-01");

			setVisible(true);
			setResizable(true);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		}

		final private class DetectorDeColisiones implements Runnable{

			@Override
			public void run(){

			}

		}

		final private class DesplazamientoEnemigo implements Runnable {

			private Lamina1 classLamina1Enemigos;
			private int velocidadMoverEnemigos = 800;

			DesplazamientoEnemigo(Lamina1 classLamina1Enemigos){
			 	this.classLamina1Enemigos = classLamina1Enemigos;
			} 
			DesplazamientoEnemigo(){
				classLamina1Enemigos = Game.Lamina1;
			}
			int i = 50;

			@Override
			public void run(){

				ArrayList<Enemigo> enemigosActuales = Lamina1.getterEnemigos(); //lista que contiene los enemigos

				while (true){

					for (int i = 0; i < enemigosActuales.size(); i++){
						
						Enemigo enemigoActual = enemigosActuales.get(i);
											
						if (enemigosActuales.get(enemigosGeneradosEjemplo-1).posicion[0] > x_max-50){

							while(true){

								if (enemigosActuales.get(0).posicion[0] <= 50){

									enemigosActuales.get(0).posicion[0] = 100;	
									break;

								} else{

									for (int b = 0; b <= enemigosActuales.size()-1; b++){

										enemigosActuales.get(b).posicion[0] -= 50;						
							
									}

								}

								try{

									Thread.sleep(velocidadMoverEnemigos);

								} catch(InterruptedException e){

									e.printStackTrace();

								}

							}

						} else{

							enemigoActual.posicion[0] += 50;
						}

					}

					try{

						Thread.sleep(velocidadMoverEnemigos);

					} catch(InterruptedException e){

						e.printStackTrace();
					}

				}

			}
			
		}

	}

	final private static class Lamina1 extends JPanel {
	
		private static final long serialVersionUID = 1L;

		private static Enemigo enemigo;
		private ArrayList<Enemigo> enemigos = new ArrayList<Enemigo>();

		private static int posicionUltima[] = {0, 50};
		private static boolean estadoEnemigos = true;
		private static byte columna = 0;
		private static int unidades = 0;
		

		public ArrayList getterEnemigos(){
			return enemigos;
		}

		Lamina1() {

			for(int i = 0; enemigosGeneradosEjemplo > i; i++ ){

				enemigo = new Enemigo();
				posicionUltima[0] += 50; // posicion x
				enemigo.posicion[0] = posicionUltima[0];
				
				if (x_max-100 <= enemigo.posicion[0]){
					x_limiteMovimiento = posicionUltima[0];
					int x_max = ancho/2 -50;

					enemigo.posicion[0] = 50;
					posicionUltima[0] = 50;

					posicionUltima[1] += 50; //posicion y

					columna++;
					if (columna == 1){
						enemigosGeneradosEjemplo = (short)(columnas_a_generar * unidades);
					}
					System.out.println(columna);
					System.out.println(unidades);
					
				}
				if (columna == 0){
					unidades++;
				}

				enemigo.posicion[1] = posicionUltima[1]; 
				
				enemigo.posicion[2] += 50;
				enemigo.posicion[3] += 50;

				enemigos.add(enemigo);
			}

		}


	
		public void paintComponent(Graphics g) {

			setBackground(Backgroound);
	
			super .paintComponent(g);
			Graphics2D g2 = (Graphics2D)g;

			Rectangle2D rectangulo;

			g2.setPaint(Jugador.color);
			g2.fillOval(Jugador.posicion[0], Jugador.posicion[1], 50, 50);

			for(int i = 0; enemigosGeneradosEjemplo > (short)i; i++ ){

				Enemigo enemigoActual = enemigos.get(i);
				int posicion[] = enemigoActual.posicion;
				rectangulo = new Rectangle2D.Double(posicion[0], posicion[1], posicion[2]-10, posicion[3]-10);

				if (true == enemigoActual.live){
					g2.setPaint(Enemigo.color);
					g2.fill(rectangulo);
				} else if (false == enemigoActual.live){
					g2.setPaint(Backgroound);
					g2.fill(rectangulo);
				}

			}

			for(int i = 0; i < ListaDisparos.size() && ListaDisparos.size() != 0; i++){

				rectangulo = new Rectangle2D.Double(ListaDisparos.get(i).posicion[0], ListaDisparos.get(i).posicion[1], ListaDisparos.get(i).posicion[2], ListaDisparos.get(i).posicion[3]);
				g2.setPaint(ListaDisparos.get(i).color);
				g2.fill(rectangulo);
				//System.out.println((ListaDisparos.get(i).posicion[0]+""+ ListaDisparos.get(i).posicion[1]+""+ ListaDisparos.get(i).posicion[2]+""+ ListaDisparos.get(i).posicion[3]));

				if(ListaDisparos.get(i).posicion[1] <= 0 || ListaDisparos.get(i).existencia == false){
					ListaDisparos.remove(i);
				}else{
					Disparos fd = new Disparos();
					ListaDisparos.get(i).posicion[1] -= fd.velocidad;
				}

				System.out.println(ListaDisparos.size());

			}

			try{
				Thread.sleep(actualizadorTiempo);
			} catch(InterruptedException e){
				e.printStackTrace();
			}
	
		}
	
	}

	final private static class MovimientoJugador implements Runnable{

		@Override
		public void run(){

			Lamina1.setLayout(null);
			Lamina1.setFocusable(true);

			KeyListener Teclado = new KeyListener(){

				public void keyTyped(KeyEvent e){}

				public void keyPressed(KeyEvent e){

					if ( e.getKeyCode() == KeyEvent.VK_A ){  //flecha izquierda
						if (Jugador.posicion[0] >= 0){
							if (Jugador.posicion[0] == x_max || Jugador.posicion[0] == x_max+5){
								System.out.println("se alcanzo el limite maximo");
								Jugador.posicion[0] = x_max-10;
							} else{
								Jugador.posicion[0] -= Jugador.velocidad;
								
							}
						} else{
							Jugador.posicion[0] = 0;
						}

					}
					if ( e.getKeyCode() == KeyEvent.VK_S ){ // flecha derecha

						if (Jugador.posicion[0] <= x_max){
							if (Jugador.posicion[0] == x_max || Jugador.posicion[0] == x_max+5){
								System.out.println("se alcanzo el limite 0");
								Jugador.posicion[0] = x_max;
							} else{
								Jugador.posicion[0] += Jugador.velocidad;
							}
						} else{
							Jugador.posicion[0] = x_max;
						}

					}
					if ( e.getKeyCode() == KeyEvent.VK_ESCAPE ){ // boton de escape(esc)
						System.exit(0);
					}

				}
				public void keyReleased(KeyEvent e){
					if ( e.getKeyCode() == KeyEvent.VK_ENTER){ // tecla enter
						System.out.println("disparando prollectil");
						Disparos disparo = new Disparos();
						ListaDisparos.add(disparo);
					}
				}


			};
			
			Lamina1.addKeyListener(Teclado);

		}

	}

	final private static class ActualizarPantalla implements Runnable {
		@Override
		public void run(){
			while (true){
				try {
					Lamina1.paint(Lamina1.getGraphics());
					Thread.sleep(actualizadorTiempo);
				} catch(InterruptedException i){
					i.printStackTrace();
				}
			}
		}
	}


	public static void main(String argv[]) {
		
		Windows windows = new Windows();

	}

}
