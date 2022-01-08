package com.AniMng;

import java.awt.Component;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Señor C. y Jarzta
 * @param <T> (De preferencia Objetos que hereden de Component) (Preferably
 * Objects that extends from Component)
 */
public class AnimationManager<T extends Component> implements Runnable {

    private T objeto;
    private List<Animation> anilist;
    private int currentRate;

    /**
     *
     * @param objeto (De preferencia Objetos que hereden de Component)
     * (Preferably Objects that extends from Component)
     */
    public AnimationManager(T objeto) {
        this.objeto = objeto;
        anilist = new ArrayList<>();
        this.currentRate = 1;
    }

    private static class Animation {

        private int rate, rep;
        private Point orientacion;

        private Animation(Point orientacion, int rep, int rate) {
            this.orientacion = orientacion;
            this.rep = rep;
            this.rate = rate;
        }

        private void move(Component object) {

            try {
                for (int i = 0; i < rep; i++) {
                    object.setLocation(object.getX() + orientacion.x, object.getY() + orientacion.y);
                    Thread.sleep(rate);
                }
            } catch (Exception e) {
            }

        }

    }

    /**
     * este método utiliza un For en el cual la variable rep determina el numero
     * de veces que se reperirá el bucle, las variables "X" y "Y", se le sumaran
     * a la coordenada "X" y "Y" del Objeto que va a animar en cada ciclo del
     * bucle for, y la variable "rate" serán los milisegundos que se detenga el
     * hilo.
     *
     * @param x pixeles en lo que se moverá la coordenada X.
     * @param y pixeles en lo que se moverá la coordenada Y.
     * @param rep numero de veces que se moverán las coordenadas X y Y la
     * cantidad de pixeles indicada.
     * @param rate esta cantidad se le asignará al metodo Sleep.
     * @return retorna este mismo método.
     */
    public AnimationManager<T> addAnimation(int x, int y, int rep, int rate) {
        currentRate = rate;
        anilist.add(new Animation(new Point(x, y), rep, currentRate));
        return this;
    }

    /**
     * este método utiliza un For en el cual la variable rep determina el numero
     * de veces que se reperirá el bucle, las variables "X" y "Y", se le sumaran
     * a la coordenada "X" y "Y" del Objeto que va a animar en cada ciclo del
     * bucle for, no es necesario pasarle el "rate"
     *
     * @param x pixeles en lo que se moverá la coordenada X.
     * @param y pixeles en lo que se moverá la coordenada Y.
     * @param rep numero de veces que se moverán las coordenadas X y Y la.
     * cantidad de pixeles indicada
     * @return retorna este mismo método.
     */
    public AnimationManager<T> addAnimation(int x, int y, int rep) {
        anilist.add(new Animation(new Point(x, y), rep, currentRate));
        return this;
    }

    @Override
    public void run() {
        anilist.forEach((ani) -> {
            ani.move(objeto);
        });
    }

    /**
     * Inicia la animación o las animaciones agregadas.
     */
    public void start() {
        Thread hilo = new Thread(this);
        hilo.start();
    }

}
