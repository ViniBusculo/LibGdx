package com.spix.sample.objetoanimado3d;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.spix.sample.objetoanimado3d.ObjetoAnimado3D;
import com.spix.sample.objetoanimado3d.ControlePersonagem3D;
import com.spix.sample.objetoanimado3d.CodigoCara;
import com.spix.sample.objetoanimado3d.Movimentacao;
import com.spix.sample.objetoanimado3d.Colisao;




// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(1280, 720); // o tamanho da janela // (1280, 720)
		config.setForegroundFPS(60);
		config.setTitle("ObjetoAnimado3D");
		// new Lwjgl3Application(new ObjetoAnimado3D(), config);
		// new Lwjgl3Application(new ControlePersonagem3D(), config);
		new Lwjgl3Application(new Movimentacao(), config);
		// new Lwjgl3Application(new Colisao(), config);
	}
}
