package org.yourorghere;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.awt.event.*; 
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import javax.swing.JOptionPane;

import com.sun.opengl.util.*;
import com.sun.opengl.util.texture.TextureData;

public class Renderer extends MouseAdapter implements GLEventListener, KeyListener
{
	// Atributos
	private GL gl;
	private GLU glu;
	private GLUT glut;
	private GLAutoDrawable glDrawable;
	private double angle, fAspect;
	private float rotX, rotY, obsZ; 
	private boolean luz;
	private int idTexture[];
	private int width, height;
	private BufferedImage image;
	private TextureData td;
	private ByteBuffer buffer;	
	
	/**
	 * Construtor da classe Renderer que n�o recebe par�metros.
	 */
	public Renderer()
	{
		// Especifica o �ngulo da proje��o perspectiva  
		angle=50;   
		// Inicializa o valor para corre��o de aspecto   
		fAspect = 1; 
		
		// Inicializa os atributos usados para alterar a posi��o do 
		// observador virtual (=c�mera)
		rotX = 0;
		rotY = 0;
		obsZ = 200; 
	
		luz = true;
	}
	
	/**
	 * M�todo definido na interface GLEventListener e chamado pelo objeto no qual ser� feito o desenho
	 * logo ap�s a inicializa��o do contexto OpenGL. 
	 */    
	public void init(GLAutoDrawable drawable)
	{
		glDrawable = drawable;
		gl = drawable.getGL();      
		glu = new GLU();
		glut = new GLUT();

		drawable.setGL(new DebugGL(gl));        

		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glEnable(GL.GL_DEPTH_TEST);    
		
		gl.glEnable(GL.GL_LIGHT0);
		gl.glEnable(GL.GL_LIGHT1);
		gl.glEnable(GL.GL_LIGHTING);
		
		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE);	
		
		// Habilita o modelo de coloriza��o de Gouraud
		gl.glShadeModel(GL.GL_SMOOTH);
		
		// Comandos de inicializa��o para textura
		loadImage("xadres.jpg");
               
		
		// Gera identificador de textura
		idTexture = new int[10];
		gl.glGenTextures(1, idTexture, 1);
		
		// Especifica qual � a textura corrente pelo identificador 
		gl.glBindTexture(GL.GL_TEXTURE_2D, idTexture[0]);
            
		
		// Envio da textura para OpenGL
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 3, width, 
				height, 0, GL.GL_BGR, GL.GL_UNSIGNED_BYTE, buffer);

		// Define os filtros de magnifica��o e minifica��o 
		gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MIN_FILTER,GL.GL_LINEAR);	
		gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MAG_FILTER,GL.GL_LINEAR);
                
                gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MIN_FILTER,GL.GL_LINEAR); 
                gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MAG_FILTER,GL.GL_LINEAR); 

                gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT); 
                gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT); 

                gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE); 

			
	}
	
	public void loadImage(String fileName)
	{
		// Tenta carregar o arquivo		
		image = null;
		try {
			image = ImageIO.read(new File(fileName));
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(null,"Erro na leitura do arquivo "+fileName);
		}

		// Obt�m largura e altura
		width  = image.getWidth();
		height = image.getHeight();
		// Gera uma nova TextureData...
		td = new TextureData(0,0,false,image);
		// ...e obt�m um ByteBuffer a partir dela
		buffer = (ByteBuffer) td.getBuffer();
	}

	/**
	 * M�todo definido na interface GLEventListener e chamado pelo objeto no qual ser� feito o desenho
	 * para come�ar a fazer o desenho OpenGL pelo cliente.
	 */  
	public void display(GLAutoDrawable drawable)
	{
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
		gl.glLoadIdentity();
		
		if (luz)
			gl.glEnable(GL.GL_LIGHT0);
		else
			gl.glDisable(GL.GL_LIGHT0);
		
		especificaParametrosVisualizacao(); 
		defineIluminacao();
		
		gl.glLineWidth(2);
		
		// Desenha uma esfera azul
                
                gl.glEnable(GL.GL_TEXTURE_2D); 

                float texgen_s[] = {0.5f, 0.0f, 0.0f, 0.5f}; 
                float texgen_t[] = {0.0f, 0.5f, 0.0f, 0.5f}; 

                gl.glTexGeni(GL.GL_S, GL.GL_TEXTURE_GEN_MODE, GL.GL_EYE_LINEAR); 
                gl.glTexGeni(GL.GL_T, GL.GL_TEXTURE_GEN_MODE, GL.GL_EYE_LINEAR); 

                gl.glTexGenfv(GL.GL_S, GL.GL_OBJECT_PLANE, texgen_s, 0); 
                gl.glTexGenfv(GL.GL_T, GL.GL_OBJECT_PLANE, texgen_t, 0);	

                gl.glEnable(GL.GL_TEXTURE_GEN_S); // Habilita a gera��o da textura 
                gl.glEnable(GL.GL_TEXTURE_GEN_T); 

                gl.glColor3f(1.0f, 1.0f, 1.0f); 
                
		//gl.glColor3f(0.0f, 0.0f, 1.0f);
		gl.glPushMatrix();
			gl.glTranslatef(30.0f, 0.0f, 0.0f);
			glut.glutSolidSphere(24, 30, 30);
		gl.glPopMatrix();
		
		// Desenha um cubo no qual a textura � aplicada
                
                gl.glDisable(GL.GL_TEXTURE_GEN_S); 
                gl.glDisable(GL.GL_TEXTURE_GEN_T); 
    
		gl.glEnable(GL.GL_TEXTURE_2D);	// Primeiro habilita uso de textura	  	
		gl.glPushMatrix();
			gl.glTranslatef(-30.0f, 0.0f, 0.0f);
			gl.glScalef(16.0f, 16.0f, 16.0f);
			gl.glColor3f(1.0f, 1.0f, 1.0f);
			gl.glBegin (GL.GL_QUADS );
				// Especifica a coordenada de textura para cada v�rtice
				// Face frontal
				gl.glNormal3f(0.0f,0.0f,-1.0f);
				gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);
				gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);
				gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);
				gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);				
				// Face posterior
				gl.glNormal3f(0.0f,0.0f,1.0f);
				gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);
				gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);
				gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);
				gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);
				// Face superior
				gl.glNormal3f(0.0f,1.0f,0.0f);
				gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);
				gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);
				gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);
				gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);
				// Face inferior
				gl.glNormal3f(0.0f,-1.0f,0.0f);
				gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);
				gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);
				gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);
				gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);
				// Face lateral direita
				gl.glNormal3f(1.0f,0.0f,0.0f);
				gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);
				gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);
				gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);
				gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);
				// Face lateral esquerda
				gl.glNormal3f(-1.0f,0.0f,0.0f);
				gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);
				gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);
				gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);
				gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);
			gl.glEnd();
		gl.glPopMatrix();
		gl.glDisable(GL.GL_TEXTURE_2D);	//	Desabilita uso de textura
	}

	/**
	 * M�todo definido na interface GLEventListener e chamado pelo objeto no qual ser� feito o desenho
	 * depois que a janela foi redimensionada.
	 */  
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
		gl.glViewport(0, 0, width, height);
		fAspect = (float)width/(float)height;      
	}

	/**
	 * M�todo definido na interface GLEventListener e chamado pelo objeto no qual ser� feito o desenho
	 * quando o modo de exibi��o ou o dispositivo de exibi��o associado foi alterado.
	 */  
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) { }

	/**
	 * M�todo usado para especificar a posi��o do observador virtual (=c�mera).
	 */    
	public void posicionaObservador()
	{
		// Especifica sistema de coordenadas do modelo
		gl.glMatrixMode(GL.GL_MODELVIEW);
		// Inicializa sistema de coordenadas do modelo
		gl.glLoadIdentity();
		// Especifica posi��o do observador e do alvo
		gl.glTranslatef(0,0,-obsZ);
		gl.glRotatef(rotX,1,0,0);
		gl.glRotatef(rotY,0,1,0);
	}
	
	/**
	 * M�todo usado para especificar o volume de visualiza��o.
	 */    
	public void especificaParametrosVisualizacao()
	{
		// Especifica sistema de coordenadas de proje��o
		gl.glMatrixMode(GL.GL_PROJECTION);
		// Inicializa sistema de coordenadas de proje��o
		gl.glLoadIdentity();

		// Especifica a proje��o perspectiva(angulo,aspecto,zMin,zMax)
		glu.gluPerspective(angle, fAspect, 0.2, 500);

		posicionaObservador();
	}
	
	/**
	 * M�todo usado para especificar os par�metros de ilumina��o.
	 */    	
	public void defineIluminacao()
	{
		//Define os par�metros atrav�s de vetores RGBA - o �ltimo valor deve ser sempre 1.0f
		float luzAmbiente[]={0.2f, 0.2f, 0.2f, 1.0f}; 
		float luzDifusa[]={1.0f, 1.0f, 1.0f, 1.0f};  
		float luzEspecular[]={1.0f, 1.0f, 1.0f, 1.0f};
		float posicaoLuz[]={40.0f, 60.0f, 0.0f, 1.0f}; // �ltimo par�metro: 0-direcional, 1-pontual/posicional 

		float posicaoLuz2[]={-40.0f, 60.0f, 0.0f, 1.0f};
		float luzEspecular2[]={1.0f, 1.0f, 1.0f, 0.0f};
		float luzDifusa2[]={1.0f, 1.0f, 1.0f, 1.0f};
		
		//Ativa o uso da luz ambiente 
		gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, luzAmbiente, 0);

		//Define os par�metros da luz de n�mero 0
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, luzAmbiente, 0); 
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, luzDifusa, 0 );
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, luzEspecular, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, posicaoLuz, 0 ); 	
		
		//Define os par�metros da luz de n�mero 1
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, luzAmbiente, 0); 
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, luzDifusa2, 0 );
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPECULAR, luzEspecular2, 0);
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, posicaoLuz2, 0 ); 
		
		// Brilho do material
		float especularidade[]={1.0f, 1.0f, 1.0f, 1.0f};
		int especMaterial = 60;

		// Define a reflect�ncia do material 
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, especularidade, 0);
		// Define a concentra��o do brilho
		gl.glMateriali(GL.GL_FRONT, GL.GL_SHININESS, especMaterial);		
	}

	/**
	 * M�todo da classe MouseAdapter que est� sendo sobrescrito para gerenciar os 
	 * eventos de clique de mouse, de maneira que sej� feito zoom in e zoom out.
	 */  
	public void mouseClicked(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1) // Zoom in
		if (angle >= 4) angle -= 4;
		if (e.getButton() == MouseEvent.BUTTON3) // Zoom out
		if (angle <= 72) angle += 4;
		glDrawable.display();
	}

	/**
	 * M�todo definido na interface KeyListener que est� sendo implementado para, 
	 * de acordo com as teclas pressionadas, permitir mover a posi��o do observador
	 * virtual.
	 */        
	public void keyPressed(KeyEvent e)
	{
		switch (e.getKeyCode())
		{ 
			case KeyEvent.VK_LEFT:		rotY--;
										break;
			case KeyEvent.VK_RIGHT:		rotY++;
										break;
			case KeyEvent.VK_UP:		rotX++;
										break;
			case KeyEvent.VK_DOWN:		rotX--;
										break;
			case KeyEvent.VK_HOME:		obsZ++;
										break;
			case KeyEvent.VK_END:		obsZ--;
										break;	
			case KeyEvent.VK_F1:		luz = !luz;
										break;											
			case KeyEvent.VK_ESCAPE:	System.exit(0);
										break;
		}  
		glDrawable.display();
	}

	/**
	 * M�todo definido na interface KeyListener.
	 */      
	public void keyTyped(KeyEvent e) { }

	/**
	 * M�todo definido na interface KeyListener.
	 */       
	public void keyReleased(KeyEvent e) { }
}


